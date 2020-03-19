package com.sergiomanrique.blogapp.Activities.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sergiomanrique.blogapp.R
import java.util.jar.Manifest

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

    private fun openGallery(){
        val  galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent,1)
    }

    private fun checkAndRequestForPermission(){
        if(ContextCompat.checkSelfPermission(this@RegisterActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageUserPhoto = findViewById<ImageView>(R.id.regUserPhoto)

        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null){
            // the user has succesfully picked an image
            // we need to sabe its reference to a uri variable
            val pickedImgUri = data.data;
            imageUserPhoto.setImageURI(pickedImgUri)
        }
    }
}


