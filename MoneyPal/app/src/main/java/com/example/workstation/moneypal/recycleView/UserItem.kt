package com.example.workstation.moneypal.recycleView

import android.content.Context
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.User
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.whatsup.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_users.*

class UserItem (val user:User, private val context: Context):
Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name.text=user.name
        viewHolder.day.text="Lundi 5"
        viewHolder.amount_text_user.text="0000"

        if(user.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(user.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user)
        }
        else{
            GlideApp.with(context)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user)
        }
    }

    override fun getLayout()= R.layout.row_users
}