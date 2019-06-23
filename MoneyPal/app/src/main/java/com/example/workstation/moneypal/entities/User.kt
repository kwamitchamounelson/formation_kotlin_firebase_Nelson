package com.example.workstation.moneypal.entities

data class User(val phoneNumber:String, val name:String, var photo:String?) {

    constructor():this("","",null)
}