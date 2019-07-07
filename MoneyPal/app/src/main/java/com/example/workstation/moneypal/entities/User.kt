package com.example.workstation.moneypal.entities

import java.io.Serializable

data class User(val phoneNumber:String, val name:String, var photo:String?):Serializable {

    constructor():this("","",null)
}