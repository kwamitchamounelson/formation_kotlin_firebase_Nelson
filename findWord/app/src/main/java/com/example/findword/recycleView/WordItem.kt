package com.example.findword.recycleView

import android.content.Context
import com.example.findword.R
import com.example.findword.entities.Word
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_word.*

class WordItem(val word: Word, private val context: Context): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.word_text.text=word.text
        viewHolder.definition.text=word.definition
        viewHolder.button_direction.text=word.directtion
    }
    override fun getLayout()= R.layout.row_word
}