package com.example.workstation.moneypal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import kotlinx.android.synthetic.main.activity_my_account.*
import java.io.ByteArrayOutputStream

class MyAccountActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE=2
    private lateinit var selectedImageBytes:ByteArray
    private var pictureJustChanged=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        // Inflate the layout for this fragment
        photo_user_account2.setOnClickListener{
            val intent= Intent().apply {
                type="image/*"
                action= Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"Choisir une image"),RC_SELECT_IMAGE)
        }

        next_buttom_account.setOnClickListener {
            if(::selectedImageBytes.isInitialized){
                StorageUtil.uploadprofilePhoto(selectedImageBytes){ imagePath->
                    FirestoreUtil.updateCurrentUser(name_user_accoun.text.toString(),imagePath)
                }
            }
            else{
                FirestoreUtil.updateCurrentUser(name_user_accoun.text.toString(),null)
            }
            val intent = Intent(this, MoneyPalActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SELECT_IMAGE && resultCode== Activity.RESULT_OK && data!=null && data.data!=null){
            val selectedImagePath=data.data
            val selectedImageBmp= MediaStore.Images.Media.getBitmap(this?.contentResolver,selectedImagePath)
            val outputStream= ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImageBytes=outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(photo_user_account2)

            pictureJustChanged=true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            name_user_accoun.setText(user.name)
            Toast.makeText(this,user.phoneNumber, Toast.LENGTH_LONG).show()
            if(!pictureJustChanged && user.photo!=null){

                GlideApp.with(this)
                    .load(StorageUtil.pathToReference(user.photo!!))
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_account_circle_black_24dp)
                    .into(photo_user_account2)
            }
            else{
                GlideApp.with(this)
                    .load("")
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_account_circle_black_24dp)
                    .into(photo_user_account2)
            }
        }
    }
}
