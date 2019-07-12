package com.example.workstation.moneypal.recycleView

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.view.View
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.AppConstants
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.UserPayActivity
import com.example.workstation.moneypal.entities.ContributionUser
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.entities.OperatorParameter
import com.example.workstation.moneypal.entities.User
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.moneypal.util.SmsUtil
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_users.*
import org.jetbrains.anko.backgroundColor

class UserItem (val user:User,val contributionUser: ContributionUser, private val context: Context):
Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name.text=user.name
        viewHolder.day.text="Lundi 5"
        viewHolder.amount_text_user.text="0000"
        val currentGroup= GroupParameter.currenGroupUsers
        viewHolder.buton_is_admin.apply {
            if(user.phoneNumber.equals(currentGroup!!.creatorPhone,true)){
                visibility=View.VISIBLE
            }
            else{
                visibility=View.GONE
            }
        }
        if(user.phoneNumber.equals(FirebaseAuth.getInstance().currentUser!!.phoneNumber,true)){
            var color=Color.parseColor("#f6f399")
            if(OperatorParameter.CURRENT_OPERATOR.equals(AppConstants.ORANGE_MONEY_OPERATOR)){
                color=Color.parseColor("#f6b080")
            }
            viewHolder.linearLayout_row_user.backgroundColor=color
            viewHolder.button_edit_amount_row.apply {
                visibility=View.VISIBLE
                setOnClickListener {
                    //implementation de la participation du user(utilisation d'un alertDialog ou utilisation de monetbilUI)
                    if(currentGroup!=null){
                        val creatorPhone=currentGroup.creatorPhone
                        if(!(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.equals(creatorPhone,true))){
                            val operatorOfAdminNumber=SmsUtil.getOperatorOfNumber(context,creatorPhone!!)
                            if(operatorOfAdminNumber.equals(OperatorParameter.CURRENT_OPERATOR,true)){
                                beginPay()
                            }
                            else{
                                val message="L'opérateur courrant que vous avez activé est ${OperatorParameter.CURRENT_OPERATOR} " +
                                        "et le numéro du createur du groupe au quel vous souhaitez contribuer est $operatorOfAdminNumber ." +
                                        "Voullez-vous changer l'operateur courrant vers $operatorOfAdminNumber ?"
                                lateinit var dialog: AlertDialog
                                val builder = AlertDialog.Builder(context)
                                builder.apply {
                                    setTitle("Changement d'opérateur")
                                    setMessage(message)
                                    setIcon(com.google.firebase.firestore.R.drawable.notification_icon_background)
                                }
                                val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                                    when(which){
                                        DialogInterface.BUTTON_POSITIVE ->{
                                            OperatorParameter.CURRENT_OPERATOR=operatorOfAdminNumber
                                            beginPay()
                                        }
                                        DialogInterface.BUTTON_NEGATIVE ->{
                                        }
                                    }
                                }
                                builder.setPositiveButton("Ok",dialogClickListener)
                                builder.setNegativeButton("Annuler",dialogClickListener)
                                dialog = builder.create()
                                dialog.show()
                            }
                        }
                        else{
                            val message="Le créateur du groupe ne peut contribuer lui meme " +
                                    "car les contributions sont faites dans son compte"
                            FirestoreUtil.showAlertDilogue("Attention",message,context)
                        }
                    }
                }
            }
        }
        else{
            viewHolder.button_edit_amount_row.apply {
                visibility=View.GONE
            }
        }
        viewHolder.amount_text_user.text=contributionUser.amount.toString()
        viewHolder.day.text=contributionUser.date.toString()

        if(user.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(user.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user)
        }
        else{
            GlideApp.with(context)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.photo_user)
        }
    }

    private fun beginPay() {
        val intent=Intent(context,UserPayActivity::class.java)
        intent.putExtra(AppConstants.USER,user)
        intent.putExtra(AppConstants.CONTRIBUTION,contributionUser)
        context.startActivity(intent)
    }

    override fun getLayout()= R.layout.row_users
}