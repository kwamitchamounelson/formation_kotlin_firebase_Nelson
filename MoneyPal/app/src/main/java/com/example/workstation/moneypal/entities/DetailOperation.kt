package com.example.workstation.moneypal.entities

data class DetailOperation(val operationName:String,val day:String,val amount:Int) {
    constructor():this("","",0)
}