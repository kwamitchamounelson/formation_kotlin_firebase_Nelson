package com.example.findword

import java.util.*

object DICTIONARY {
    val MY_DICTIONARY=AppConstantes.STR_DICTIONARY.toLowerCase(Locale.ROOT).split("\\n".toRegex()).sorted()
}