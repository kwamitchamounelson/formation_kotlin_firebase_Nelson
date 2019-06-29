package com.example.workstation.moneypal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.entities.GroupUsers
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_creat_group.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream

class CreatGroupActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE=2
    private  var selectedImageBytes: ByteArray?=null
    private var pictureJustChanged=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creat_group)

        photo_group_creation.setOnClickListener{
            val intent= Intent().apply {
                type="image/*"
                action= Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"Choisir une image"),RC_SELECT_IMAGE)
        }

        button_creat_group.setOnClickListener {
            if(validateForm()){
                val currentUserPhone=FirebaseAuth.getInstance().currentUser?.phoneNumber
                val newGroup=GroupUsers("",
                    objectitl_group.text.toString(),
                    (amount_objectif_group_creat.text.toString()).toInt(),
                    null,
                    currentUserPhone,
                    desciption_group.text.toString(),
                    arrayListOf(currentUserPhone)
                )
                createNewGroup(newGroup)
            }
            else{
                toast("Veuillez entrer toutes les informations")
            }
        }

        button_cancel.setOnClickListener {
            val intent=Intent(this,GroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateForm(): Boolean {
        return (objectitl_group.text.toString().isNotEmpty() && amount_objectif_group_creat.text.toString().isNotEmpty() && desciption_group.text.toString().isNotEmpty())
    }

    private fun createNewGroup(group:GroupUsers) {
        if(selectedImageBytes!=null){
            val progressDialog=indeterminateProgressDialog("Création en cours...")
            FirestoreUtil.CreateGroupe(group,"",onComplete = {
                StorageUtil.uploadprofilePhotoGroup(selectedImageBytes!!,it){ imagePath->
                    FirestoreUtil.updateImagePathGroup(it,imagePath,onComplete = { message->
                        progressDialog.dismiss()
                        val intent=Intent(this,GroupActivity::class.java)
                        startActivity(intent)
                        toast(message)
                    })
                }
            })

        }
        else{
            val progressDialog=indeterminateProgressDialog("Création en cours...")
            FirestoreUtil.CreateGroupe(group,null,onComplete = {
                progressDialog.dismiss()
                val intent=Intent(this,GroupActivity::class.java)
                startActivity(intent)
            })
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
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(photo_group_creation)

            pictureJustChanged=true
        }
    }

    override fun onStart() {
        super.onStart()
        if(!pictureJustChanged){
            GlideApp.with(this)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(photo_group_creation)
        }
    }
}
