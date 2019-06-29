package com.example.workstation.moneypal.recycleView

import android.content.Context
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.GroupUsers
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.whatsup.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_group.*

class GroupUserItem (val group:GroupUsers, private val context: Context):
    Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.group_name_row.text=group.groupName
        viewHolder.detail_group_row.text="Nombre de participant(s) : ${group.listOfUsers.size}"
        viewHolder.amount_group_row.text="${group.abjectifAmount}"

        if(group.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(group.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(viewHolder.photo_group_row)
        }
        else{
            GlideApp.with(context)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(viewHolder.photo_group_row)
        }
    }

    override fun getLayout()= R.layout.row_group
}