package com.example.workstation.moneypal.fragment


import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workstation.moneypal.*
import com.example.workstation.moneypal.entities.AcountParameter
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.entities.GroupeCreateParameter
import com.example.workstation.moneypal.entities.OperatorParameter
import com.example.workstation.moneypal.recycleView.UserItem
import com.example.workstation.moneypal.util.DynamicLinkUtil
import com.example.workstation.moneypal.util.SmsUtil
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_group.view.*
import kotlinx.android.synthetic.main.layout_select_user.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import java.lang.Exception

class GroupFragment : Fragment() {

    private lateinit var mDialogView: View
    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecycleview=true
    private lateinit var myView:View
    //var users= mutableListOf<Item>()
    private lateinit var peopleSection: Section
    private lateinit var userSelectSection: Section
    val currentGroup=GroupParameter.currenGroupUsers
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loadData()
        // Inflate the layout for this fragment
        myView= inflater.inflate(R.layout.fragment_group, container, false)
        view.apply {
            myView.info_date_group.text=AcountParameter.infoDayAcount
            myView.info_solde_group.text=AcountParameter.infoSoldeAcount
            if(currentGroup!=null){
                myView.group_name.text=currentGroup.groupName
                myView.amount_group.text=currentGroup.abjectifAmount.toString()
            }
            else{
                myView.group_name.text=""
                myView.amount_group.text=""
            }
            myView.add_member_text_view.setOnClickListener {
                if(currentGroup!=null){
                    if(currentGroup.creatorPhone.equals(FirebaseAuth.getInstance().currentUser!!.phoneNumber,true)){
                        loadUsers()
                    }
                    else{
                        toast("Seule le createur du groupe peut ajouter des membres et vous ne l'etes pas")
                    }
                }
                else{
                    toast("Veuillez choisir un groupe")
                    val intent=Intent(this@GroupFragment.context,GroupActivity::class.java)
                    startActivity(intent)
                }
            }

            myView.pay_member_text_view.setOnClickListener {
                val creatorPhone=currentGroup!!.creatorPhone
                if(!(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.equals(creatorPhone,true))){
                    val operatorOfAdminNumber= SmsUtil.getOperatorOfNumber(this@GroupFragment.context!!,creatorPhone!!)
                    if(operatorOfAdminNumber.equals(OperatorParameter.CURRENT_OPERATOR,true)){
                        beginPay()
                    }
                    else{
                        val message="L'opérateur courrant que vous avez activé est ${OperatorParameter.CURRENT_OPERATOR} " +
                                "et le numéro du createur du groupe au quel vous souhaitez contribuer est $operatorOfAdminNumber ." +
                                "Voullez-vous changer l'operateur courrant vers $operatorOfAdminNumber ?"
                        lateinit var dialog: AlertDialog
                        val builder = AlertDialog.Builder(this@GroupFragment.context!!)
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
                    FirestoreUtil.showAlertDilogue("Attention",message,this@GroupFragment.context!!)
                }
            }


            myView.button_detail_group.setOnClickListener {
                if (currentGroup != null) {
                    FirestoreUtil.showDetailOfGroup(currentGroup, this@GroupFragment.context!!)
                }
            }
            myView.linearLayout_see_detail.apply {
                if (currentGroup != null) {
                    visibility=View.VISIBLE
                    setOnClickListener {
                        FirestoreUtil.showDetailOfGroup(currentGroup,this.context)
                    }
                }
                else{
                    visibility=View.INVISIBLE
                }
            }
        }
        return myView
    }

    private fun manageProgressBar() {
        myView.progressbar_amount.apply {
            max=GroupParameter.currenGroupUsers!!.abjectifAmount
            progress=GroupParameter.currenGroupTotalAmount
        }
    }

    private fun loadData() {
        FirestoreUtil.addUserListener(this@GroupFragment.context!!, this::updateRecycleView)
    }

    private fun updateRecycleView(items: List<Item>,totalAmount:Int){
        try {
            fun init(){
                myView.recyclerView_users.apply {
                    layoutManager=LinearLayoutManager(this@GroupFragment.context)
                    adapter= GroupAdapter<ViewHolder>().apply {
                        peopleSection=Section(items)
                        add(peopleSection)
                        setOnItemClickListener(onItemClick)
                    }
                }
                shouldInitRecycleview=false
            }

            fun updateItem()=peopleSection.update(items)

            if(shouldInitRecycleview){
                init()
            }
            else{
                updateItem()
            }
            val itemDecor = DividerItemDecoration(this@GroupFragment.context, ClipDrawable.HORIZONTAL)
            myView.recyclerView_users.addItemDecoration(itemDecor)
            GroupParameter.currenGroupTotalAmount=0
            for (item in items){
                if(item is UserItem){
                    GroupParameter.currenGroupTotalAmount+=item.contributionUser.amount
                }
            }
            //gestion du progress bar
            manageProgressBar()
            myView.amount_current_group.text=GroupParameter.currenGroupTotalAmount.toString()
        }catch (e:Exception){}
    }

    private val onItemClick= OnItemClickListener{ item, view ->
        if(item is UserItem){
            //action here
        }
    }

    private fun loadUsers() {
        FirestoreUtil.addUserListenerForSelect(GroupParameter.currenGroupUsers,this@GroupFragment.context!!, this::updateRecycleViewAlertDialogue)
    }

    private fun updateRecycleViewAlertDialogue(items: List<Item>) {
        GroupeCreateParameter.clearAllData()
        mDialogView = LayoutInflater.from(this@GroupFragment.context).inflate(R.layout.layout_select_user, null)
        mDialogView.recyclerView_select_user.apply {
            layoutManager=LinearLayoutManager(this@GroupFragment.context)
            adapter= GroupAdapter<ViewHolder>().apply {
                userSelectSection=Section(items)
                add(userSelectSection)
                //setOnItemClickListener(onItemClick)
            }
        }
        val mBuilder = AlertDialog.Builder(this@GroupFragment.context!!)
            .setView(mDialogView)
            .setTitle("Ajouter ou inviter des participants")
        val  mAlertDialog = mBuilder.show()
        mDialogView.button_share_link.setOnClickListener {
            DynamicLinkUtil.generateContentLink(this@GroupFragment.context!!,currentGroup!!,onComplete = {shortLink->
                var intent= Intent()
                val msg="Le groupe d'objectif ${currentGroup.groupName} vous invite à le rejoindre sur MoneyPal via ce lien :\n$shortLink"
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT,msg)
                intent.type = "text/plain"
                startActivity(intent)
                GroupeCreateParameter.clearAllData()
                mAlertDialog.dismiss()
            })
        }
        mDialogView.button_add_user_select.setOnClickListener {
            if(GroupeCreateParameter.listOfMenbersNumber.isNotEmpty()){
                val progressDialog=indeterminateProgressDialog("Veillez patienter")
                FirestoreUtil.addMemberOnGroup(GroupeCreateParameter.listOfMenbersNumber,currentGroup!!,onComplete = {success ->
                    if (success){
                        progressDialog.dismiss()
                        toast("Membres ajoutés avec succès")
                        loadData()
                    }
                    else{
                        progressDialog.dismiss()
                        toast("Echec d'ajout des membres")
                    }
                    GroupeCreateParameter.clearAllData()
                    mAlertDialog.dismiss()
                })
            }
            else{
                toast("Veuillez sélectioner au moins un utilisateur")
            }
        }
        mDialogView.button_cancel_alert_dialog.setOnClickListener {
            GroupeCreateParameter.clearAllData()
            mAlertDialog.dismiss()
        }
    }

    private fun beginPay() {
        FirestoreUtil.getCurrentUser {user ->
            FirestoreUtil.getContributionOfUser(user,currentGroup!!,onComplete = {contributionUser ->
                val intent=Intent(context, UserPayActivity::class.java)
                intent.putExtra(AppConstants.USER,user)
                intent.putExtra(AppConstants.CONTRIBUTION,contributionUser)
                startActivity(intent)
            })
        }
    }


}
