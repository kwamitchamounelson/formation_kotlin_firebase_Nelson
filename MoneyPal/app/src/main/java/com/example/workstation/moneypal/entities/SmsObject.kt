package com.example.workstation.moneypal.entities

import com.example.workstation.moneypal.util.SmsUtil
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class SmsObject(
    val senderPhone:String,
    val messageBody:String,
    val dateReceive: Date?,
    val dateSend:Date?
): Serializable {
    constructor():this("","",null,null)

    fun getDetailOperation():DetailOperation{
        var detailOperation= DetailOperation()
        var listOfKeWord:ArrayList<String>
        var isOperation:Boolean
        for(operation in OperationData.listeOfOperation){
            listOfKeWord=SmsUtil.getWordKey(operation)
            isOperation=true
            for(word in listOfKeWord){
                if(!messageBody.contains(word,true)){
                    isOperation=false
                    break
                }
            }
            if(isOperation && !listOfKeWord.isEmpty()){
                detailOperation= DetailOperation(operation,dateReceive,0.0,this)
                break
            }
        }
        return SmsUtil.getAmounOfDetailOperation(detailOperation)
    }

    override fun toString(): String {
        return "SmsObject(" +
                "senderPhone='$senderPhone', " +
                "\nmessageBody='$messageBody', " +
                "\ndateReceive='$dateReceive', " +
                "\ndateSend='$dateSend')"
    }

}