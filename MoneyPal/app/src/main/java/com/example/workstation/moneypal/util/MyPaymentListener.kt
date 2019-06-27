package com.example.workstation.moneypal.util

import android.content.Context
import android.util.Log
import com.boorgeon.monetbil.android.PaymentListener
import com.boorgeon.monetbil.android.PaymentResponse
import com.example.workstation.moneypal.YourPaymentResultActivity


class MyPaymentListener(context: Context) : PaymentListener(context) {

    override fun onPaymentSuccess(paymentResponse: PaymentResponse?) {
        super.onPaymentSuccess(paymentResponse)

        Log.d("listener", "onPaymentSuccess")

        val transaction = paymentResponse!!.getTransaction_UUID()
        val item_ref = paymentResponse.getItem_ref()
        val payment_ref = paymentResponse.getPayment_ref()
        val msisdn = paymentResponse.getMsisdn()
        val amount = paymentResponse.getAmount()
        val success = paymentResponse.isSuccess

        val intent = this.intent
        intent.putExtra("transaction", transaction)
        intent.putExtra("item_ref", item_ref)
        intent.putExtra("payment_ref", payment_ref)
        intent.putExtra("msisdn", msisdn)
        intent.putExtra("amount", amount)
        intent.putExtra("success", success)

        this.startActivity(YourPaymentResultActivity::class.java)
    }

    override fun onPaymentFailed(paymentResponse: PaymentResponse?) {
        super.onPaymentFailed(paymentResponse)

        Log.d("listener", "onPaymentFailed")

        val transaction = paymentResponse!!.getTransaction_UUID()
        val item_ref = paymentResponse.getItem_ref()
        val payment_ref = paymentResponse.getPayment_ref()
        val msisdn = paymentResponse.getMsisdn()
        val amount = paymentResponse.getAmount()
        val success = paymentResponse.isSuccess

        val intent = this.intent
        intent.putExtra("transaction", transaction)
        intent.putExtra("item_ref", item_ref)
        intent.putExtra("payment_ref", payment_ref)
        intent.putExtra("msisdn", msisdn)
        intent.putExtra("amount", amount)
        intent.putExtra("success", success)
        this.startActivity(YourPaymentResultActivity::class.java)
    }

    override fun onPaymentWidgetClosed() {
        super.onPaymentWidgetClosed()
        Log.d("listener", "onPaymentWidgetClosed")
    }

}