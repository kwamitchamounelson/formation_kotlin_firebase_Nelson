package com.example.findword.entities

data class Caracter(var char: Char,var x:Int,var y:Int,var isSelected:Boolean) {
    constructor():this(' ',0,0,false)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Caracter

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

}