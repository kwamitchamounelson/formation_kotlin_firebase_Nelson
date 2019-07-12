package com.example.workstation.moneypal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.api.HoverParameters
import kotlinx.android.synthetic.main.activity_user_pay.*
import org.jetbrains.anko.toast
import java.lang.Exception
import android.widget.Toast
import android.app.Activity
import android.content.Intent
import com.example.workstation.moneypal.entities.*
import com.example.workstation.moneypal.util.SmsUtil
import com.example.workstation.whatsup.util.FirestoreUtil
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import java.util.*


class UserPayActivity : AppCompatActivity(),Hover.DownloadListener{

    private val REQUEST_CODE=0
    var currentUser:User?=null
    val currentGroup=GroupParameter.currenGroupUsers
    var contributionUser:ContributionUser?=null
    var amount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pay)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Contribution"
        Hover.initialize(this);

        currentUser= intent.getSerializableExtra(AppConstants.USER) as User?
        contributionUser= intent.getSerializableExtra(AppConstants.CONTRIBUTION) as ContributionUser?
        initData()
        button_pay_contribution.setOnClickListener {
            try {
                amount=amount_contribution.text.toString().toInt()
                if(OperatorParameter.CURRENT_OPERATOR.equals(AppConstants.ORANGE_MONEY_OPERATOR,true)){
                    //transfert Orange-Orange
                    val intent=HoverParameters.Builder(this)
                        .request(AppConstants.ACTION_SEND_MONEY_ORANGE_ORANGE)
                        .extra("1",currentGroup!!.creatorPhone!!.substring(4))
                        .extra("2","$amount")
                        .buildIntent()
                    startActivityForResult(intent,REQUEST_CODE)
                }
                else{
                    //Transfert MTN-MTN
                    val intent=HoverParameters.Builder(this)
                        .request(AppConstants.ACTION_SEND_MONEY_MTN_MTN)
                        .setEnvironment(1)
                        .setEnvironment(1)
                        .extra("phone",currentGroup!!.creatorPhone!!.substring(4))
                        .setEnvironment(1)
                        .extra("montant","$amount")
                        .buildIntent()
                    startActivityForResult(intent,REQUEST_CODE)
                }
            }catch (e:Exception){
                toast("Veuillez entrer un montant valide pour la contribution")
                toast(e.toString())
            }
        }
    }

    //verification de la reponse
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //TODO verifier le message et modifier la contribution du user
            val sessionTextArr = data!!.getStringArrayExtra("ussd_messages")
            var strMessage=""
            for (str in sessionTextArr){
                strMessage+=str
            }
            if(OperatorParameter.CURRENT_OPERATOR.equals(AppConstants.ORANGE_MONEY_OPERATOR,true)){
                val keyWord="Transfert initie"
                // traiter le resultat Orange
                if(strMessage.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)
                        .contains(keyWord.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT))){
                    updateUserAmounContribution()
                }
                else{
                    toast("Paiement non validé")
                    exitSession()
                }
            }
            else{
                //TODO mettre le mot cle dune transaction MTN reussie
                val keyWord="i"
                if(strMessage.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)
                        .contains(keyWord.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT))){
                    updateUserAmounContribution()
                }
                else{
                    toast("Paiement non validé")
                    exitSession()
                }
            }
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            toast("Error: " + data!!.getStringExtra("error"))
            exitSession()
        }
    }

    private fun updateUserAmounContribution() {
        val totalAmount=(amount+contributionUser!!.amount)
        val progressDialog=indeterminateProgressDialog("Veillez patienter...")
        FirestoreUtil.addUpdateUserAmountGroup(currentUser!!.phoneNumber,totalAmount,currentGroup!!.groupId,onComplete = {
            progressDialog.dismiss()
            exitSession()
        })
    }

    private fun exitSession() {
        val intent = Intent(this, MoneyPalActivity::class.java)
        startActivity(intent)
    }

    private fun initData() {
        group_name_contribution.text="${currentGroup!!.groupName}" +
                "\nObjectif : ${currentGroup.abjectifAmount} FCFA" +
                "\nReste : ${(currentGroup.abjectifAmount-GroupParameter.currenGroupTotalAmount)} FCFA"

        current_amoun_contribution.text="Ma contribution actuelle : ${contributionUser!!.amount} FCFA"
        description_text.text=currentGroup.descriptionGroup
    }

    override fun onSuccess(p0: ArrayList<HoverAction>?) {
        toast("transaction reussie")
    }

    override fun onError(p0: String?) {
        toast("echec de la transaction")
    }
}
