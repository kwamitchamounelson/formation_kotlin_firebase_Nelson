package com.example.workstation.whatsup.recycleview

import android.content.Context
import com.example.workstation.whatsup.AppConstants
import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.entities.TextMessage
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.indeterminateProgressDialog
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification
import android.widget.Toast


class TextMessageItem(val language:String,
                      val message:TextMessage,
                      val context: Context)
    :MessageItem(message){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        var text= translateTexte(message.tex,language)

        viewHolder.textView_message_text.text=text
        super.bind(viewHolder, position)
    }

    private fun translateTexte(textToTranslate: String,language:String): String {

        val languageIdentifier = FirebaseNaturalLanguage
            .getInstance()
            .languageIdentification
        languageIdentifier.identifyLanguage(textToTranslate)
            .addOnSuccessListener { languageCode ->
                Toast.makeText(context,"$textToTranslate($languageCode)",Toast.LENGTH_SHORT).show()
                if (languageCode !== "und") {

                } else {

                }
            }
            .addOnFailureListener(
                object:OnFailureListener {
                    override fun onFailure(e:Exception) {
                        Toast.makeText(context,"erreure de detection",Toast.LENGTH_SHORT).show()
                    }
                })

        var str=when(language){
            AppConstants.NO_LANGUAGE->textToTranslate
            AppConstants.ENGLISH->{
                // code de traduction

                return textToTranslate
            }
            else->textToTranslate
        }
        return str
    }

    override fun getLayout()= R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        return super.isSameAs(other)
        if(other !is TextMessageItem)
            return false
        if(this.message!=other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

}