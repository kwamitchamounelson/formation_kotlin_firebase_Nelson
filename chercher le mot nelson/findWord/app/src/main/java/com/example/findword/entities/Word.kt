package com.example.findword.entities

data class Word(var text:String,var isValid:Boolean?,var caracters: ArrayList<Caracter>?,var definition:String?) {
    constructor():this("",false, arrayListOf(),"")
}