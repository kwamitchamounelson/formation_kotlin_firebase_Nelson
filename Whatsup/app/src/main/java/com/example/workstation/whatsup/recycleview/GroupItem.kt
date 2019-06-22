package com.example.workstation.whatsup.recycleview

import android.content.Context
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.entities.GroupUser
import com.example.workstation.whatsup.glide.GlideApp
import com.example.workstation.whatsup.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_user.*

class GroupItem(val group:GroupUser,
                private val context: Context
):
    Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name.text=group.name
        if(group.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(group.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(viewHolder.photo_user)
        }
        else{
            GlideApp.with(context)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(viewHolder.photo_user)
        }
    }

    override fun getLayout()= R.layout.row_user
}