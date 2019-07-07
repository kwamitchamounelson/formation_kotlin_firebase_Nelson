package com.example.workstation.moneypal.recycleView

import android.content.Context
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.GroupeCreateParameter
import com.example.workstation.moneypal.entities.User
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.whatsup.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_select_user.*

class UserSelectItem (val user: User,
                      private val context: Context,
                      var selected:Boolean
):
    Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var userName=user.name
        if(userName.isEmpty()){
            userName=user.phoneNumber
        }
        if(GroupeCreateParameter.listOfMenbersNumber.contains(user.phoneNumber)){
            viewHolder.checkBox_group.isChecked=true
        }
        //userName=person.phoneNumber
        viewHolder.user_name_group.text=userName
        viewHolder.checkBox_group.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                //ajouter le user dans la liste
                GroupeCreateParameter.listOfMenbersNumber.add(user.phoneNumber)
            }
            else{
                //retirer le user de la liste
                GroupeCreateParameter.listOfMenbersNumber.remove(user.phoneNumber)
            }
            //Toast.makeText(context,"${GroupeCreateParameter.listOfMenbersNumber.size}", Toast.LENGTH_SHORT).show()
        }
        if(user.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(user.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user_group)
        }
        else{
            GlideApp.with(context)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user_group)
        }
    }

    override fun getLayout()= R.layout.row_select_user
}