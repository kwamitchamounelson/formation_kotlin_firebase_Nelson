package com.example.findword

import java.util.*

object DICTIONARY {
    val MY_DICTIONARY=AppConstantes.STR_DICTIONARY.toLowerCase(Locale.ROOT).split("\\n".toRegex()).sorted()
    val ORIGINAL_GRILLE=AppConstantes.STR_GRILLE.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)
}