package com.example.workstation.whatsup.recycleview

import android.content.Context
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.entities.GroupeCreateParameter
import com.example.workstation.whatsup.entities.User
import com.example.workstation.whatsup.glide.GlideApp
import com.example.workstation.whatsup.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_user_group.*

class PersonItemGroup (val person: User,
                       val userId:String,
                       private val context: Context,
                       var selected:Boolean
):
    Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var userName=person.name
        if(userName.isEmpty()){
            userName=person.phoneNumber
        }
        if(GroupeCreateParameter.listOfMenbersNumber.contains(person.phoneNumber)){
            viewHolder.checkBox_group.isChecked=true
        }
        //userName=person.phoneNumber
        viewHolder.user_name_group.text=userName
        viewHolder.checkBox_group.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                //ajouter le user dans la liste
                GroupeCreateParameter.listOfMenbersNumber.add(person.phoneNumber)
            }
            else{
                //retirer le user de la liste
                GroupeCreateParameter.listOfMenbersNumber.remove(person.phoneNumber)
            }
            Toast.makeText(context,"${GroupeCreateParameter.listOfMenbersNumber.size}",Toast.LENGTH_SHORT).show()
        }
        if(person.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user_group)
        }
    }

    override fun getLayout()= R.layout.row_user_group
}