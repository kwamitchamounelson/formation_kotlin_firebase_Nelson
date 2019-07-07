package com.example.workstation.moneypal.entities

import java.io.Serializable
import java.util.*

data class ContributionUser(var userPhone:String,var groupId:String,var amount:Int,val date:Date=Calendar.getInstance().time):Serializable {
    constructor():this("","",0)
}