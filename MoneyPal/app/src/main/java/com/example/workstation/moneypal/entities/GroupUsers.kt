package com.example.workstation.moneypal.entities

data class GroupUsers(
    val groupId:String, val groupName:String, val abjectifAmount:Int, var photo:String?,
    val creatorPhone:String?, val descriptionGroup:String, val listOfUsers: ArrayList<String?>
) {
    constructor():this("","",0,null,null,"", arrayListOf())
}