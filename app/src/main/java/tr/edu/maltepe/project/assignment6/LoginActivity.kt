package tr.edu.maltepe.project.assignment6

import android.view.View
import com.android.volley.AuthFailureError
import java.util.HashMap

class LoginActivity : AppCompatActivity() {
    var etName: EditText? = null
    var etPassword: EditText? = null
    var progressBar: ProgressBar? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        etName = findViewById<EditText>(R.id.etUserName)
        etPassword = findViewById<EditText>(R.id.etUserPassword)


        //calling the method userLogin() for login the user
        findViewById<View>(R.id.btnLogin).setOnClickListener(View.OnClickListener { userLogin() })

        //if user presses on textview not register calling RegisterActivity
        findViewById<View>(R.id.tvRegister).setOnClickListener(View.OnClickListener {
            finish()
            startActivity(Intent(getApplicationContext(), RegisterActivity::class.java))
        })
    }

    private fun userLogin() {
        //first getting the values
        val username: String = etName.getText().toString()
        val password: String = etPassword.getText().toString()
        //validating inputs
        if (TextUtils.isEmpty(username)) {
            etName.setError("Please enter your username")
            etName.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password")
            etPassword.requestFocus()
            return
        }

        //if everything is fine
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST,
            URLs.URL_LOGIN.toString() + "?username=" + username + "&password=" + password,
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
                    params["password"] = password
                    return params
                }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
}