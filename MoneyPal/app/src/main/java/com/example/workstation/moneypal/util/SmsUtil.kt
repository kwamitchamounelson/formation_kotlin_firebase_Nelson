package com.example.workstation.moneypal.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Telephony
import com.example.workstation.moneypal.AppConstants
import com.example.workstation.moneypal.entities.DetailOperation
import com.example.workstation.moneypal.entities.Operation
import com.example.workstation.moneypal.entities.OperatorParameter
import com.example.workstation.moneypal.entities.SmsObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


object SmsUtil {

    @SuppressLint("NewApi")
    fun getAllSms(context: Context,operation: Operation?): ArrayList<SmsObject> {
        val lstSms = ArrayList<SmsObject>()
        val c = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(
                Telephony.Sms.Inbox.ADDRESS,
                Telephony.Sms.Inbox.BODY,
                Telephony.Sms.Inbox.DATE,
                Telephony.Sms.Inbox.DATE_SENT
            ),
            null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )
        val totalSMS = c!!.count
        var sms:SmsObject
        var senderPhone:String
        var messageBody:String
        var listOfKeWord:ArrayList<String>
        var isOperation:Boolean
        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {
                senderPhone=c.getString(0)
                if(senderPhone.equals(OperatorParameter.CURRENT_OPERATOR,true)){
                    messageBody=c.getString(1)
                    if(operation==null){
                        sms= SmsObject(
                            senderPhone,
                            messageBody,
                            Date((c.getString(2)).toLong()),
                            Date((c.getString(3)).toLong())
                        )
                        lstSms.add(sms)
                    }
                    else{
                        //get operations of specific operation
                        listOfKeWord= getWordKey(operation)
                        isOperation=true
                        for(word in listOfKeWord){
                            if(!(messageBody.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT))
                                    .contains(word.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT))){
                                isOperation=false
                                break
                            }
                        }
                        if(isOperation && !listOfKeWord.isEmpty()){
                            sms= SmsObject(
                                senderPhone,
                                messageBody,
                                Date((c.getString(2)).toLong()),
                                Date((c.getString(3)).toLong())
                            )
                            lstSms.add(sms)
                        }
                    }
                }
                c.moveToNext();
            }
        }
        c.close()
        return lstSms
    }

    //fonction permettant de definir des mots cles en fonction dune operation
    fun getWordKey(operation: Operation):ArrayList<String>{
        var list=ArrayList<String>()
        when(operation.name){
            AppConstants.TRANSFERE_D_ARGENT->{
                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{

                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                    }
                }
            }
            AppConstants.ACHAT_DE_CREDIT->{
                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{

                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                    }
                }
            }
            AppConstants.FACTURE_ENEO->{

                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{

                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                        list= arrayListOf(
                            "Votre paiement de",
                            "ENEO",
                            "a ete effectue"
                        )
                    }
                }
            }
            AppConstants.REATRAIT_D_ARGENT->{

                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{
                        list= arrayListOf(
                            "Retrait d'argent reussi",
                            "informations detaillees",
                            "montant net debite"
                        )
                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                    }
                }
            }
            AppConstants.DEPOTS->{
                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{
                        list= arrayListOf(
                            "Depot effectue par",
                            "informations detaillees",
                            "Montant Net du Credit",
                            "Nouveau Solde"
                        )
                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                        list= arrayListOf(
                            "Vous avez recu",
                            "sur votre compte Mobile Money",
                            "Message de l'expediteur"
                        )
                    }
                }
            }
            else->{
                list= arrayListOf()
            }
        }
        return list
    }

    //FONCION RETOURNANT LE SOLDE D'UN UTILISATEUR A PARTIR DE SA DERNIERE OPERATION
    fun getTheSolde(detailOperation: DetailOperation?):Int{
        var solde:Double= 0.0
        val operation=detailOperation!!.operation
        val smsObjet=detailOperation!!.smsObject
        val messageBody=smsObjet!!.messageBody.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)
        when(operation!!.name){
            AppConstants.TRANSFERE_D_ARGENT->{

                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{

                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                    }
                }

            }
            AppConstants.ACHAT_DE_CREDIT->{

                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{

                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                    }
                }

            }
            AppConstants.FACTURE_ENEO->{

                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{

                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                        val splitor1="nouveausolde:"
                        val tab=messageBody.split(splitor1)
                        if(tab.isNotEmpty()){
                            val splitor2="fcfa"
                            val tab2=(tab[1]).split(splitor2)
                            if(tab2.isNotEmpty()){
                                solde=try {
                                    tab2[0].toDouble()
                                }catch (e:Exception){
                                    0.0
                                }
                            }
                        }
                    }
                }

            }
            AppConstants.REATRAIT_D_ARGENT->{

                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{
                        val splitor1="nouveausolde:"
                        val tab=messageBody.split(splitor1)
                        if(tab.isNotEmpty()){
                            val splitor2="fcfa"
                            val tab2=(tab[1]).split(splitor2)
                            if(tab2.isNotEmpty()){
                                solde=try {
                                    tab2[0].toDouble()
                                }catch (e:Exception){
                                    0.0
                                }
                            }
                        }
                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                    }
                }
            }
            AppConstants.DEPOTS->{
                when(OperatorParameter.CURRENT_OPERATOR){
                    AppConstants.ORANGE_MONEY_OPERATOR->{
                        val splitor1="nouveausolde:"
                        val tab=messageBody.split(splitor1)
                        if(tab.isNotEmpty()){
                            val splitor2="fcfa"
                            val tab2=(tab[1]).split(splitor2)
                            if(tab2.isNotEmpty()){
                                solde=try {
                                    tab2[0].toDouble()
                                }catch (e:Exception){
                                    0.0
                                }
                            }
                        }
                    }
                    AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                        val splitor1="nouveausoldeestde:"
                        val tab=messageBody.split(splitor1)
                        if(tab.isNotEmpty()){
                            val splitor2="fcfa"
                            val tab2=(tab[1]).split(splitor2)
                            if(tab2.isNotEmpty()){
                                solde=try {
                                    tab2[0].toDouble()
                                }catch (e:Exception){
                                    0.0
                                }
                            }
                        }
                    }
                }
            }
            else->{
                solde=0.0
            }
        }
        return solde.toInt()
    }


    //FONCION RETOURNANT LE MONTANT DUNE OPERATION
    fun getAmounOfDetailOperation(detailOperation: DetailOperation?):DetailOperation{
        var solde:Double= 0.0
        val operation=detailOperation!!.operation
        if(detailOperation!=null){
            val smsObjet=detailOperation!!.smsObject
            if(detailOperation!=null && smsObjet!=null){
                val messageBody=smsObjet!!.messageBody.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)
                when(operation!!.name){
                    AppConstants.TRANSFERE_D_ARGENT->{

                        when(OperatorParameter.CURRENT_OPERATOR){
                            AppConstants.ORANGE_MONEY_OPERATOR->{

                            }
                            AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                            }
                        }

                    }
                    AppConstants.ACHAT_DE_CREDIT->{

                        when(OperatorParameter.CURRENT_OPERATOR){
                            AppConstants.ORANGE_MONEY_OPERATOR->{

                            }
                            AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                            }
                        }

                    }
                    AppConstants.FACTURE_ENEO->{

                        when(OperatorParameter.CURRENT_OPERATOR){
                            AppConstants.ORANGE_MONEY_OPERATOR->{

                            }
                            AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                                var splitor1="fcfa"
                                var tab=messageBody.split(splitor1)
                                if(tab.isNotEmpty()){
                                    var splitor2="de"
                                    var tab2=(tab[0]).split(splitor2)
                                    if(tab2.isNotEmpty()){
                                        solde=try {
                                            tab2[1].toDouble()
                                        }catch (e:Exception){
                                            0.0
                                        }
                                        splitor1="frais:"
                                        tab=messageBody.split(splitor1)
                                        if(tab.isNotEmpty()){
                                            splitor2="fcfa"
                                            tab2=(tab[1]).split(splitor2)
                                            if(tab2.isNotEmpty()){
                                                solde+=try {
                                                    tab2[0].toDouble()
                                                }catch (e:Exception){
                                                    0.0
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    AppConstants.REATRAIT_D_ARGENT->{

                        when(OperatorParameter.CURRENT_OPERATOR){
                            AppConstants.ORANGE_MONEY_OPERATOR->{
                                val splitor1="netdebite"
                                val tab=messageBody.split(splitor1)
                                if(tab.isNotEmpty()){
                                    val splitor2="fcfa"
                                    val tab2=(tab[1]).split(splitor2)
                                    if(tab2.isNotEmpty()){
                                        solde=try {
                                            tab2[0].toDouble()
                                        }catch (e:Exception){
                                            0.0
                                        }
                                    }
                                }
                            }
                            AppConstants.MTN_MOBILE_MONEY_OPERATOR->{

                            }
                        }
                    }
                    AppConstants.DEPOTS->{

                        when(OperatorParameter.CURRENT_OPERATOR){
                            AppConstants.ORANGE_MONEY_OPERATOR->{
                                val splitor1="fcfa"
                                val tab=messageBody.split(splitor1)
                                if(tab.isNotEmpty()){
                                    val splitor2="montantdetransaction:"
                                    val tab2=(tab[0]).split(splitor2)
                                    if(tab2.isNotEmpty()){
                                        solde=try {
                                            tab2[1].toDouble()
                                        }catch (e:Exception){
                                            0.0
                                        }
                                    }
                                }
                            }
                            AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                                val splitor1="fcfa"
                                val tab=messageBody.split(splitor1)
                                if(tab.isNotEmpty()){
                                    val splitor2="recu"
                                    val tab2=(tab[0]).split(splitor2)
                                    if(tab2.isNotEmpty()){
                                        solde=try {
                                            tab2[1].toDouble()
                                        }catch (e:Exception){
                                            0.0
                                        }
                                    }
                                }
                            }
                        }

                    }
                    else->{
                        solde=0.0
                    }
                }
            }
        }
        detailOperation.amount= solde.toInt()
        return detailOperation
    }

}