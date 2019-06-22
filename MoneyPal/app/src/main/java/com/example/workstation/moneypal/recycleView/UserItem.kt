package com.example.workstation.moneypal.recycleView

import android.content.Context
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_users.*

class UserItem (val user:User, private val context: Context):
Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name.text=user.userName
        viewHolder.day.text="Lundi 5"
        viewHolder.amount_text_user.text="0000"
    }

    override fun getLayout()= R.layout.row_users
}