package com.example.findword

import android.content.Context
import java.util.*

object DICTIONARY {
    var MY_DICTIONARY= listOf<String>()
    val ORIGINAL_GRILLE=AppConstantes.STR_GRILLE.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)

    //fonction retournant la liste des mots contenue dans le fichier du dictionnaire
    public fun getAllWord(context: Context){
        if(MY_DICTIONARY.isEmpty()){
            var str=""
            context.applicationContext.assets.open(AppConstantes.FILE_NAME_DICTIONARY).bufferedReader().use {
                str+=it.readText()
            }
            MY_DICTIONARY= str.toLowerCase(Locale.ROOT).split("\\n".toRegex()).sorted()
        }
    }
}