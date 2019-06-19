package com.example.workstation.whatsup.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.entities.GroupeCreateParameter
import com.example.workstation.whatsup.glide.GlideApp
import kotlinx.android.synthetic.main.fragment_edit_group_creation.*
import kotlinx.android.synthetic.main.fragment_edit_group_creation.view.*
//import kotlinx.android.synthetic.main.fragment_edit_group_creation.view.*
import java.io.ByteArrayOutputStream

class EditGroupCreationFragment : Fragment() {

    private val RC_SELECT_IMAGE=2
    //private lateinit var selectedImageBytes:ByteArray
    private var pictureJustChanged=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_edit_group_creation, container, false)
        view.apply {
            view.groupe_image_edit.setOnClickListener{
                val intent= Intent().apply {
                    type="image/*"
                    action= Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"Choisir une image"),RC_SELECT_IMAGE)
            }
            view.group_name.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    GroupeCreateParameter.groupeName=p0.toString()
                }

            })
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
            GroupeCreateParameter.selectedImageBytes=outputStream.toByteArray()
            group_name.setText(GroupeCreateParameter.groupeName)
            GlideApp.with(this)
                .load(GroupeCreateParameter.selectedImageBytes)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_group_work_black_24dp)
                .error(R.drawable.ic_group_work_black_24dp)
                .into(groupe_image_edit)

            pictureJustChanged=true
        }
    }

    override fun onStart() {
        super.onStart()
        group_name.setText(GroupeCreateParameter.groupeName)
        if(!pictureJustChanged){
            GlideApp.with(this)
                .load(GroupeCreateParameter.selectedImageBytes)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_group_work_black_24dp)
                .error(R.drawable.ic_group_work_black_24dp)
                .into(groupe_image_edit)
        }
    }

}
