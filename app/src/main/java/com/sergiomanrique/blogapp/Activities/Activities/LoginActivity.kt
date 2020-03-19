package com.sergiomanrique.blogapp.Activities.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

import com.sergiomanrique.blogapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userMail = findViewById<EditText>(R.id.login_mail)
        val userPassword = findViewById<EditText>(R.id.login_password)
        val btnLogin = findViewById<Button>(R.id.login_button)
        val loginProgress = findViewById<ProgressBar>(R.id.login_progress)
        val loginPhoto = findViewById<ImageView>(R.id.login_photo)

        // when the user clicks the login button
        loginProgress.visibility = View.INVISIBLE;
        btnLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                loginProgress.visibility = View.VISIBLE
                btnLogin.visibility = View.INVISIBLE

                val mail = userMail.text.toString()
                val password = userPassword.text.toString()

                if (mail.isEmpty() || password.isEmpty()){
                    showMessage("Pleas verify all the fields")
                    btnLogin.visibility = View.VISIBLE
                    loginProgress.visibility = View.INVISIBLE
                } else {
                    signIn(mail, password)
                }
            }
        });

        // when the user clicks the login photo
        loginPhoto.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val registerActivity = android.content.Intent(applicationContext, RegisterActivity::class.java)
                startActivity(registerActivity)
                finish()
            }
        })

    }

    fun signIn(mail: String, password: String){

        val loginProgress = findViewById<ProgressBar>(R.id.login_progress)
        val btnLogin = findViewById<Button>(R.id.login_button)
        val mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(this){ task ->

            if(task.isSuccessful){
                loginProgress.visibility = View.INVISIBLE;
                btnLogin.visibility = View.VISIBLE
                updateUI()
            } else {
                showMessage(task.exception?.message.toString())
                btnLogin.visibility = View.VISIBLE
                loginProgress.visibility = View.INVISIBLE
            }
        }

    }


    fun updateUI(){

        val homeActivity = android.content.Intent(this, HomeActivity::class.java);
        startActivity(homeActivity)
        finish()
    }

    fun showMessage(text:String){
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            // user is already connected so we need to redirect him to the home page
            updateUI()
        }

    }
}
