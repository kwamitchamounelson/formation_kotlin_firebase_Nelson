package com.example.workstation.moneypal.recycleView

import android.content.Context
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.DetailOperation
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_detail_operation.*

class DetailOperationItem(val detailOperation:DetailOperation, private val context: Context):
Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.name_operation_detail.text=detailOperation.operationName
        viewHolder.day_operation_detail.text=detailOperation.day
        viewHolder.amount_text_view.text="${detailOperation.amount}"
    }

    override fun getLayout()= R.layout.row_detail_operation
}