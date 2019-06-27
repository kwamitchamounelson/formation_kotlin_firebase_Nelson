package com.example.workstation.moneypal.entities

import java.io.Serializable

data class Operation(val name:String): Serializable {
    constructor():this("")
}