package com.example.workstation.whatsup.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.CircleCrop

import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.glide.GlideApp
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_edit_group_creation.*
import java.io.ByteArrayOutputStream

class EditGroupCreationFragment : Fragment() {

    private val RC_SELECT_IMAGE=2
    private lateinit var selectedImageBytes:ByteArray
    private var pictureJustChanged=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_edit_group_creation, container, false)
        view.apply {
            groupe_image.setOnClickListener{
                val intent= Intent().apply {
                    type="image/*"
                    action= Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"Choisir une image"),RC_SELECT_IMAGE)
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SELECT_IMAGE && resultCode== Activity.RESULT_OK && data!=null && data.data!=null){
            val selectedImagePath=data.data
            val selectedImageBmp= MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImagePath)
            val outputStream= ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImageBytes=outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_group_work_black_24dp)
                .error(R.drawable.ic_group_work_black_24dp)
                .into(groupe_image)

            pictureJustChanged=true
        }
    }

    override fun onStart() {
        super.onStart()
        GlideApp.with(this)
            .load("//http")
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_group_work_black_24dp)
            .error(R.drawable.ic_group_work_black_24dp)
            .into(groupe_image)
    }


}
