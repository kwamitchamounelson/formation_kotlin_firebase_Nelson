package com.example.workstation.moneypal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.workstation.moneypal.entities.ContributionUser
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.entities.User
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.api.HoverParameters
import kotlinx.android.synthetic.main.activity_user_pay.*
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.ArrayList
import android.widget.Toast
import android.app.Activity
import android.content.Intent



class UserPayActivity : AppCompatActivity(),Hover.DownloadListener{

    private val REQUEST_CODE=0
    var currentUser:User?=null
    val currentGroup=GroupParameter.currenGroupUsers
    var contributionUser:ContributionUser?=null
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
                val amount=amount_contribution.text.toString().toInt()
                val intent=HoverParameters.Builder(this)
                    .request(AppConstants.ACTION_SEND_MONEY_ORANGE_ORANGE)
                    .extra("1",currentGroup!!.creatorPhone!!.substring(4))
                    //.extra("1","+273691621708".substring(4))
                    .extra("2","$amount")
                    .buildIntent()
                startActivityForResult(intent,REQUEST_CODE)
            }catch (e:Exception){
                toast("Veuillez entrer un montant valide pour la contribution")
            }
        }
    }

    //verification de la reponse
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val sessionTextArr = data!!.getStringArrayExtra("ussd_messages")
            //TODO verifier le message et modifier la contribution du user
            //val uuid = data.getStringExtra("uuid")
            /*if(sessionTextArr.isNullOrEmpty()){
                toast(sessionTextArr.last())
            }*/
            val intent = Intent(this, MoneyPalActivity::class.java)
            startActivity(intent)
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            toast("Error: " + data!!.getStringExtra("error"))
        }
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
