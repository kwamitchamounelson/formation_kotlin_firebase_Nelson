package com.example.workstation.whatsup.util

import android.widget.Toast
import com.example.workstation.whatsup.AppConstants
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import org.jetbrains.anko.indeterminateProgressDialog

object FirebaseMLKUtil {
    fun translateMsg(language:String,textToTranslate: String, onComplete: (translatedMessage:String) -> Unit){

        var srcCodeLanguage=-1
        var destCodeLanguage=-1
        if(language.equals(AppConstants.ENGLISH)){
            srcCodeLanguage=FirebaseTranslateLanguage.FR
            destCodeLanguage=FirebaseTranslateLanguage.EN
        }
        else if(language.equals(AppConstants.FRENSH)){
            srcCodeLanguage=FirebaseTranslateLanguage.EN
            destCodeLanguage=FirebaseTranslateLanguage.FR
        }

        if(srcCodeLanguage==-1 || destCodeLanguage==-1){
            onComplete(textToTranslate)
        }
        else{
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(srcCodeLanguage)
                .setTargetLanguage(destCodeLanguage)
                .build()
            val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(textToTranslate)
                        .addOnSuccessListener { translatedText ->
                            onComplete(translatedText)
                        }
                        .addOnFailureListener { exception ->
                            onComplete(textToTranslate)

                        }
                }
                .addOnFailureListener { exception ->
                    onComplete(textToTranslate)
                }
        }
    }
}