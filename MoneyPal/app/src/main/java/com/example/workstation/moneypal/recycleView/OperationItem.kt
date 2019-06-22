package com.example.workstation.moneypal.recycleView

import android.content.Context
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.Operation
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_operation.*

class OperationItem (val operation:Operation,
                     private val context: Context):
    Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.text_operation_item.text=operation.name
    }

    override fun getLayout()= R.layout.row_operation
}