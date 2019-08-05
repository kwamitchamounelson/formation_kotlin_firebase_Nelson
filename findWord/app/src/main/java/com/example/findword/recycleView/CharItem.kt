package com.example.findword.recycleView

import android.content.Context
import android.graphics.Color
import com.example.findword.R
import com.example.findword.entities.Caracter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_char.*

class CharItem (val caracter: Caracter, private val context: Context): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.char_textView.text="${caracter.char}"
        if (caracter.isSelected){
            viewHolder.card_char.apply {
                setCardBackgroundColor(Color.BLACK)
            }
            viewHolder.char_textView.apply {
                setTextColor(Color.WHITE)
            }
        }
    }
    override fun getLayout()= R.layout.row_char
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharItem

        if (caracter != other.caracter) return false

        return true
    }

    override fun hashCode(): Int {
        return caracter.hashCode()
    }

}