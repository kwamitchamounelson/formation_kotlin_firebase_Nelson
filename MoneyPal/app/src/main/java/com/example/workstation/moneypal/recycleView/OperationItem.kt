package com.example.workstation.moneypal.recycleView

import android.content.Context
import android.graphics.Color
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
        /*viewHolder.item_operation.setOnClickListener {
            it.setBackgroundColor(Color.YELLOW)
        }*/
        /*viewHolder.item_operation.setOnFocusChangeListener { view, b ->
            if(b){
                view.setBackgroundColor(Color.YELLOW)
            }
            else{
                view.setBackgroundColor(Color.WHITE)
            }
        }*/
    }

    override fun getLayout()= R.layout.row_operation
}