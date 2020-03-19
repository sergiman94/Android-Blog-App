package com.sergiomanrique.blogapp.Activities.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sergiomanrique.blogapp.R
import kotlinx.android.synthetic.main.activity_register.*
import java.util.jar.Manifest

class RegisterActivity : AppCompatActivity() {

    lateinit var pickedImgUri: Uri;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userEmail = findViewById<EditText>(R.id.regMail)
        val userPassword = findViewById<EditText>(R.id.regPassword)
        val userPassword2 = findViewById<EditText>(R.id.regPassword2)
        val userName = findViewById<EditText>(R.id.regName)
        val progressBar = findViewById<ProgressBar>(R.id.reg_progressBar)
        val regButton = findViewById<Button>(R.id.regButton)

        regButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                regButton.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE

                val mail = userEmail.text.toString()
                val password = userPassword.text.toString()
                val password2 = userPassword2.text.toString()
                val username = userName.text.toString()


                if( mail.isEmpty() || password.isEmpty() || username.isEmpty() || !password.equals(password2)) {
                    // There's something wrong
                    // show a message that everything is not good
                    showMessage("Please verify all fields")
                    regButton.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                } else {
                    // Everything is ok so we can create the account
                    // createAccount method will create the user if the email is valid

                    createAccount(mail, username, password )
                }
            }
        })

        // Request permission to gallery
        val imageUserPhoto = findViewById<ImageView>(R.id.regUserPhoto)
        imageUserPhoto.setOnClickListener(object : View.OnClickListener {
            @Override
            override fun onClick(v: View?) {

                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission()
                } else {
                    openGallery()
                }
            }
        })

    }

    fun createAccount(mail: String, username: String, password: String){

        val regButton = findViewById<Button>(R.id.regButton)
        val progressBar = findViewById<ProgressBar>(R.id.reg_progressBar)
        val mAuth = FirebaseAuth.getInstance()

        if ( pickedImgUri != null ) {

            mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(this){ task ->

                if (task.isSuccessful){
                    showMessage("Account created")

                    // after the user has created the account we need to update the picture and name
                    updateUserInfo(username, pickedImgUri, mAuth.currentUser)

                } else {
                    showMessage("Account creation failed" +  task.exception?.message)
                    regButton.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
            }
        } else {
            showMessage("Load a profile photo please")
        }

    }

    // update user name and photo
    fun updateUserInfo(username: String, pickedImgUri: Uri?, mAuth: FirebaseUser?){

        // first we need to upload the user photo to firebase storage to get the url
        val mStorage = FirebaseStorage.getInstance().getReference().child("user_photos")
        var imageFilePath = mStorage.child(pickedImgUri?.lastPathSegment!!)
        val currentUser = FirebaseAuth.getInstance().currentUser

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener{taskSnapshot: UploadTask.TaskSnapshot? ->

            // Image uploaded succesfully
            // now we can get our imageurl
            imageFilePath.downloadUrl.addOnSuccessListener { uri ->

                //  uri contain  the image url

                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(uri)
                    .build()

                currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        showMessage("Register Complete")
                        updateUI();
                    }
                }
            }
        }
    }

    fun updateUI(){

        val homeActivity = android.content.Intent(this, HomeActivity::class.java);
        startActivity(homeActivity)
        finish()
    }

    private fun openGallery(){
        val  galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent,1)
    }

    private fun checkAndRequestForPermission(){

        if( ContextCompat.checkSelfPermission(this@RegisterActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@RegisterActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this@RegisterActivity, "Please accept for required permission",Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this@RegisterActivity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        } else {
            openGallery()
        }
    }

    // This triggers when the user succesfully has picked an image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageUserPhoto = findViewById<ImageView>(R.id.regUserPhoto)

        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null){
            // the user has succesfully picked an image
            // we need to sabe its reference to a uri variable
            pickedImgUri = data.data!!;
            imageUserPhoto.setImageURI(pickedImgUri)
        }
    }

    fun showMessage(text: String){
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }
}


