package com.example.workstation.whatsup.recycleview

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import com.example.workstation.whatsup.AppConstants
import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.entities.TextMessage
import com.example.workstation.whatsup.util.FirebaseMLKUtil
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*


class TextMessageItem(val language:String,
                      val message:TextMessage,
                      val context: Context,
                      val senderId:String)
    :MessageItem(message){
    private val colorTranslateText=Color.GRAY
    private val colorSrcText=Color.BLACK
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.sender_name.text=senderId
        var origineLanguageCode="X"
        FirebaseMLKUtil.translateMsg(language,message.tex, onComplete = { stransletedMessage: String ->
            viewHolder.textView_message_text.text=stransletedMessage
            if(stransletedMessage.equals(message.tex,true)){
                viewHolder.textView_message_text.setTextColor(colorSrcText)
            }
            else{
                viewHolder.textView_message_text.setTextColor(colorTranslateText)
            }
            // recuperation de la langue du texte
            val languageIdentifier = FirebaseNaturalLanguage
                .getInstance()
                .languageIdentification
            languageIdentifier.identifyLanguage(stransletedMessage)
                .addOnSuccessListener { languageCode ->
                    origineLanguageCode=showLanguageCode(languageCode)
                    if(!languageCode.equals("und",true)){
                        var code=languageCode
                        if(languageCode.equals("en",true)){
                            code="fr"
                        }
                        else if(languageCode.equals("fr",true)){
                            code="en"
                        }
                        viewHolder.button_translate_item.text=code
                    }
                    else{
                        viewHolder.button_translate_item.text="X"
                    }
                }
                .addOnFailureListener(
                    object:OnFailureListener {
                        override fun onFailure(e:Exception) {
                            viewHolder.button_translate_item.text="X"
                        }
                    })
        })
        viewHolder.button_translate_item.setOnClickListener {
            val langCode=viewHolder.button_translate_item.text.toString()
            val msg=viewHolder.textView_message_text.text.toString()
            var srcCodeLanguage=-1
            var destCodeLanguage=-1
            var newCode="en"
            when(langCode){
                "fr"->{
                    srcCodeLanguage=FirebaseTranslateLanguage.EN
                    destCodeLanguage=FirebaseTranslateLanguage.FR
                    newCode="en"
                }
                "en"->{
                    srcCodeLanguage=FirebaseTranslateLanguage.FR
                    destCodeLanguage=FirebaseTranslateLanguage.EN
                    newCode="fr"
                }
            }
            if(srcCodeLanguage==-1 || destCodeLanguage==-1){
                Toast.makeText(context,"Langue non prise en charge",Toast.LENGTH_SHORT).show()
            }
            else{
                val options = FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(srcCodeLanguage)
                    .setTargetLanguage(destCodeLanguage)
                    .build()
                val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
                translator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        translator.translate(msg)
                            .addOnSuccessListener { translatedText ->
                                viewHolder.textView_message_text.text=translatedText
                                viewHolder.button_translate_item.text=newCode
                                viewHolder.textView_message_text.setTextColor(colorTranslateText)
                            }
                            .addOnFailureListener { exception ->
                                viewHolder.textView_message_text.text=msg
                                viewHolder.textView_message_text.setTextColor(colorSrcText)

                            }
                    }
                    .addOnFailureListener { exception ->
                        viewHolder.textView_message_text.text=msg
                        viewHolder.textView_message_text.setTextColor(colorSrcText)
                    }
            }
        }
        viewHolder.button_no_translate_item.setOnClickListener {
            viewHolder.textView_message_text.text=message.tex
            viewHolder.textView_message_text.setTextColor(colorSrcText)
            viewHolder.button_translate_item.text=origineLanguageCode
        }
        super.bind(viewHolder, position)
    }

    private fun showLanguageCode(languageCode: String): String {
        return when(languageCode){
            "und"->"X"
            "en"->"fr"
            "fr"->"en"
            else->languageCode
        }
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