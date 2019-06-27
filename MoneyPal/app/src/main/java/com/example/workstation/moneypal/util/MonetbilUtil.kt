package com.example.workstation.moneypal.util

import android.app.Activity
import android.content.Context
import com.boorgeon.monetbil.android.PaymentRequest



object MonetbilUtil {

    fun testPayement(amount:Int,activity: Activity){
        val service_key = "pjSDhGBiejY6PiBPVc0yxwqNNYeUVX06"

        // Initialize a payment
        val paymentRequest = PaymentRequest(service_key)
        paymentRequest.setAmount(amount) // Amount to be paid

        paymentRequest.setPayment_listener(MyPaymentListener::class.java!!)

        // Start a payment
        paymentRequest.startPayment(activity)
    }
}