package tr.edu.maltepe.project.assignment6

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.String
import kotlin.TODO

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            var id = findViewById<TextView>(R.id.textViewId)
            var userName = findViewById<TextView>(R.id.textViewUsername)
            var userEmail = findViewById<TextView>(R.id.textViewEmail)
            var gender = findViewById<TextView>(R.id.textViewGender)
            var btnLogout = findViewById<Button>(R.id.buttonLogout)
            val user: User = SharedPrefManager.getInstance(this).getUser()
            id.setText(String.valueOf(user.getId()))
            userEmail.setText(user.getEmail())
            gender.setText(user.getGender())
            userName.setText(user.getName())
            btnLogout.setOnClickListener(this)
        } else {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onClick(view: View) {
        if (view == btnLogout) {
            SharedPrefManager.getInstance(applicationContext).logout()
        }
    }
}