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
                      val context: Context)
    :MessageItem(message){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        FirebaseMLKUtil.translateMsg(language,message.tex, onComplete = { stransletedMessage: String ->
            viewHolder.textView_message_text.text=stransletedMessage
            if(stransletedMessage.equals(message.tex,true)){
                viewHolder.textView_message_text.setTextColor(Color.BLACK)
            }
            else{
                viewHolder.textView_message_text.setTextColor(Color.GRAY)
            }
            // recuperation de la langue du texte
            val languageIdentifier = FirebaseNaturalLanguage
                .getInstance()
                .languageIdentification
            languageIdentifier.identifyLanguage(stransletedMessage)
                .addOnSuccessListener { languageCode ->
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
                            }
                            .addOnFailureListener { exception ->
                                viewHolder.textView_message_text.text=msg

                            }
                    }
                    .addOnFailureListener { exception ->
                        viewHolder.textView_message_text.text=msg
                    }
            }
        }
        super.bind(viewHolder, position)
    }

    private fun translateTexte(textToTranslate: String,language:String): String {

        var text=textToTranslate
        // recuperation de la langue du texte
        val languageIdentifier = FirebaseNaturalLanguage
            .getInstance()
            .languageIdentification
        var srcLanguage=AppConstants.NO_LANGUAGE_CODE
        languageIdentifier.identifyLanguage(textToTranslate)
            .addOnSuccessListener { languageCode ->
                if (languageCode !== "und" &&languageCode.equals("fr")) {
                    // Toast.makeText(context,"$textToTranslate($languageCode)",Toast.LENGTH_SHORT).show()
                    srcLanguage= FirebaseTranslateLanguage.languageForLanguageCode(languageCode)!!
                    text=when(language){
                        AppConstants.NO_LANGUAGE->textToTranslate
                        AppConstants.ENGLISH->{
                            // code de traduction
                            translateMyTextToLanguage(textToTranslate,srcLanguage,FirebaseTranslateLanguage.EN)
                        }
                        else->textToTranslate
                    }
                }
            }
            .addOnFailureListener(
                object:OnFailureListener {
                    override fun onFailure(e:Exception) {
                        Toast.makeText(context,"erreur de détection de langue",Toast.LENGTH_SHORT).show()
                    }
                })
        return text
    }

    private fun translateMyTextToLanguage(textToTranslate: String,
                                          srcLanguageCode: Int,
                                          destLanguaCode: Int): String {
        var finalText=textToTranslate
        var dowlodSucces=false
        if(srcLanguageCode==17){
            // definition de loption de traduction
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(srcLanguageCode)
                .setTargetLanguage(destLanguaCode)
                .build()

            // telechargement de la traduction
            val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    dowlodSucces=true
                }
                .addOnFailureListener { exception ->
                    dowlodSucces=false
                }

            // lancement de la traduction
            translator.translate(textToTranslate)
                .addOnSuccessListener { translatedText ->
                    finalText= translatedText
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context,"erreur de traduction",Toast.LENGTH_SHORT).show()
                    finalText= textToTranslate
                }
            Toast.makeText(context,finalText,Toast.LENGTH_SHORT).show()
        }
        return finalText
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