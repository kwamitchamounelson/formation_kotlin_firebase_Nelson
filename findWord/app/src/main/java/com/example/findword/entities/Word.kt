package com.example.findword.entities

data class Word(var text:String,var caracters: ArrayList<Caracter>?,var definition:String?) {
    constructor():this("", arrayListOf(),"definition")
}