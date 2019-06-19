package com.example.workstation.whatsup.entities

object GroupeCreateParameter {
    var selectedImageBytes: ByteArray? =null
    var groupeName=""
    var listOfMenbersNumber= arrayListOf<String>()

    fun clearAllData(){
        selectedImageBytes=null
        groupeName=""
        listOfMenbersNumber.clear()
    }
}