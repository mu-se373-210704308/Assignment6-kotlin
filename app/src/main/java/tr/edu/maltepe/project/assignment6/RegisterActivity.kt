package tr.edu.maltepe.project.assignment6client

import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import com.android.volley.AuthFailureError
import java.util.HashMap

class RegisterActivity : AppCompatActivity() {
    var editTextUsername: EditText? = null
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var radioGroupGender: RadioGroup? = null
    var progressBar: ProgressBar? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        //if the user is already logged in we will directly start the MainActivity (profile) activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            return
        }
        editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        radioGroupGender = findViewById<RadioGroup>(R.id.radioGender)
        findViewById<View>(R.id.buttonRegister).setOnClickListener(View.OnClickListener { //if user pressed on button register
            //here we will register the user to server
            registerUser()
        })
        findViewById<View>(R.id.textViewLogin).setOnClickListener(View.OnClickListener { //if user pressed on textview that already register open LoginActivity
            finish()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        })
    }

    private fun registerUser() {
        val username: String = editTextUsername.getText().toString().trim { it <= ' ' }
        val email: String = editTextEmail.getText().toString().trim { it <= ' ' }
        val password: String = editTextPassword.getText().toString().trim { it <= ' ' }
        val gender: String =
            (findViewById<View>(radioGroupGender.getCheckedRadioButtonId()) as RadioButton).getText()
                .toString()

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username")
            editTextUsername.requestFocus()
            return
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email")
            editTextEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email")
            editTextEmail.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password")
            editTextPassword.requestFocus()
            return
        }
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_REGISTER.toString() + "?username=" + username + "&password=" + password,
            object : Listener<String?>() {
                fun onResponse(response: String?) {
                    progressBar.setVisibility(View.GONE)
                    try {
                        //converting response to json object
                        val obj = JSONObject(response)
                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(
                                getApplicationContext(),
                                obj.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

                            //getting the user from the response
                            val userJson: JSONObject = obj.getJSONObject("user")

                            //creating a new user object
                            val user = User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("gender")
                            )

                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user)

                            //starting the profile activity
                            finish()
                            startActivity(Intent(getApplicationContext(), MainActivity::class.java))
                        } else {
                            Toast.makeText(
                                getApplicationContext(),
                                obj.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            },
            object : ErrorListener() {
                fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
            @get:Throws(AuthFailureError::class)
            protected val params: Map<String, String>
                protected get() {
                    val params: MutableMap<String, String> = HashMap()
                    params["username"] = username
                    params["email"] = email
                    params["password"] = password
                    params["gender"] = gender
                    return params
                }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
}