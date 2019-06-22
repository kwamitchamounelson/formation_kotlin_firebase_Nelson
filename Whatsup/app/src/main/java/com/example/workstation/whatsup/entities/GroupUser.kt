package com.example.workstation.whatsup.entities

data class GroupUser(val grpoupId:String, val name:String, var photo:String?,
                     var members: ArrayList<String>) {
    constructor():this("","",null, arrayListOf())
}