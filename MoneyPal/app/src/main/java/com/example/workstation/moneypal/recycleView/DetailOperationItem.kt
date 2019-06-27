package com.example.workstation.moneypal.recycleView

import android.content.Context
import android.graphics.Color
import com.example.workstation.moneypal.AppConstants
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.DetailOperation
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_detail_operation.*
import org.jetbrains.anko.textColor

class DetailOperationItem(val detailOperation:DetailOperation, private val context: Context):
Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.name_operation_detail.text=detailOperation.operation?.name
        viewHolder.day_operation_detail.text=detailOperation.date.toString()
        viewHolder.amount_text_view.text="${detailOperation.amount}"
        if(detailOperation.operation!!.name.equals(AppConstants.DEPOTS)){
            viewHolder.amount_text_view.textColor=Color.GREEN
        }
        else{
            viewHolder.amount_text_view.textColor=Color.RED
        }
    }

    override fun getLayout()= R.layout.row_detail_operation
}