package com.example.workstation.moneypal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.workstation.moneypal.entities.DetailOperation
import kotlinx.android.synthetic.main.activity_detail_operation.*

class DetailOperationActivity : AppCompatActivity() {
    private lateinit var detailOperation:DetailOperation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_operation)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        detailOperation = intent.getSerializableExtra(AppConstants.DETAIL_OPERATION) as DetailOperation
        supportActionBar?.title=("${detailOperation.operation!!.name}")

        initData()
    }

    private fun initData() {
        sms_body.text=detailOperation.smsObject!!.messageBody
        val str="Opération: ${detailOperation.operation!!.name}\n" +
                "Montant: ${detailOperation.amount}\n" +
                "Date: ${detailOperation.date}\n"
        detail_operation_textview.text=str
    }
}
