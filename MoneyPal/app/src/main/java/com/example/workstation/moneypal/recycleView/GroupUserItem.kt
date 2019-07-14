package com.example.workstation.moneypal.recycleView

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.MoneyPalActivity
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.entities.GroupUsers
import com.example.workstation.moneypal.entities.GroupeCreateParameter
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.moneypal.util.DynamicLinkUtil
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_select_user.view.*
import kotlinx.android.synthetic.main.row_group.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

class GroupUserItem (val group:GroupUsers, private val context: Context):
    Item() {
    private lateinit var mDialogView: View
    private lateinit var userSelectSection: Section
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.group_name_row.text=group.groupName
        viewHolder.detail_group_row.text="Nombre de participant(s) : ${group.listOfUsers.size}"
        viewHolder.amount_group_row.text="${group.abjectifAmount}"


        viewHolder.button_add_menber.apply {
            if((FirebaseAuth.getInstance().currentUser!!.phoneNumber.equals(group.creatorPhone))){
                visibility=View.VISIBLE
            }
            else{
                visibility=View.GONE
            }
            setOnClickListener {
                //TODO affichage des utilisateurs et envoi des liens dynamic
                FirestoreUtil.addUserListenerForSelect(group,context, ::updateRecycleViewAlertDialogue)
            }
        }

        viewHolder.button_see_detail.setOnClickListener {
            FirestoreUtil.showDetailOfGroup(group,context)
        }

        if(group.photo!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(group.photo!!))
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(viewHolder.photo_group_row)
        }
        else{
            GlideApp.with(context)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.icone_users_group)
                .error(R.drawable.icone_users_group)
                .into(viewHolder.photo_group_row)
        }
    }

    private fun updateRecycleViewAlertDialogue(items: List<Item>) {
        GroupeCreateParameter.clearAllData()
        mDialogView = LayoutInflater.from(context).inflate(R.layout.layout_select_user, null)
        mDialogView.recyclerView_select_user.apply {
            layoutManager= LinearLayoutManager(context)
            adapter= GroupAdapter<ViewHolder>().apply {
                userSelectSection= Section(items)
                add(userSelectSection)
                //setOnItemClickListener(onItemClick)
            }
        }
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Ajouter ou inviter des participants")
        val  mAlertDialog = mBuilder.show()
        mDialogView.button_share_link.setOnClickListener {
            DynamicLinkUtil.generateContentLink(context,group,onComplete = { shortLink->
                var intent= Intent()
                val msg="Le groupe d'objectif ${group.groupName} vous invite à le rejoindre sur MoneyPal via ce lien :\n$shortLink"
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT,msg)
                intent.type = "text/plain"
                context.startActivity(intent)
                GroupeCreateParameter.clearAllData()
                mAlertDialog.dismiss()
            })
        }
        mDialogView.button_add_user_select.setOnClickListener {
            if(GroupeCreateParameter.listOfMenbersNumber.isNotEmpty()){
                val progressDialog=context.indeterminateProgressDialog("Veillez patienter")
                FirestoreUtil.addMemberOnGroup(GroupeCreateParameter.listOfMenbersNumber,group,onComplete = { success ->
                    if (success){
                        progressDialog.dismiss()
                        context.toast("Membres ajoutés avec succès")
                        GroupParameter.currenGroupUsers=group
                        GroupParameter.currentFragment=2
                        val intent = Intent(context, MoneyPalActivity::class.java)
                        context.startActivity(intent)
                    }
                    else{
                        progressDialog.dismiss()
                        context.toast("Echec d'ajout des membres")
                    }
                    GroupeCreateParameter.clearAllData()
                    mAlertDialog.dismiss()
                })
            }
            else{
                context.toast("Veuillez sélectioner au moins un utilisateur")
            }
        }
        mDialogView.button_cancel_alert_dialog.setOnClickListener {
            GroupeCreateParameter.clearAllData()
            mAlertDialog.dismiss()
        }
    }

    override fun getLayout()= R.layout.row_group
}