package com.example.workstation.moneypal.entities

import java.io.Serializable
import java.util.*

data class DetailOperation(
    val operation: Operation?,
    val date:Date?,
    var amount:Double,
    val smsObject: SmsObject?) :Serializable {
    constructor() : this(null,null,0.0,null)
}