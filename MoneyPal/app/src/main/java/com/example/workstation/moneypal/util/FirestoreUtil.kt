package com.example.workstation.whatsup.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.icu.util.CurrencyAmount
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.example.workstation.moneypal.entities.ContributionUser
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.entities.GroupUsers
import com.example.workstation.moneypal.entities.User
import com.example.workstation.moneypal.recycleView.GroupUserItem
import com.example.workstation.moneypal.recycleView.UserItem
import com.example.workstation.moneypal.recycleView.UserSelectItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

object FirestoreUtil {
    private val currentUser=FirebaseAuth.getInstance().currentUser
    private val firestoreInstance:FirebaseFirestore by lazy{FirebaseFirestore.getInstance()}

    //for user
    private val currentUserDocRef:DocumentReference
        get() = firestoreInstance.document("users/${currentUser?.phoneNumber
            ?:throw NullPointerException("UID is null")}")

    private val currentGroupColRef:CollectionReference = firestoreInstance.collection("groups")

    fun initCurrentUserIfFirstTime(onComplete: ()->Unit){
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()){
                val newUser=User(FirebaseAuth.getInstance().currentUser?.phoneNumber?:"","",null)
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else{
                onComplete()
            }
        }
    }

    fun updateCurrentUser(name:String="",photo:String?=null){
        val userFieldMap= mutableMapOf<String,Any>()
        if(currentUser!=null) {
            userFieldMap["phoneNumber"]= currentUser.phoneNumber!!
        }
        if(name.isNotBlank()) userFieldMap["name"]=name
        if(photo!=null) userFieldMap["photo"]=photo
        currentUserDocRef.update(userFieldMap)
    }


    fun getCurrentUser(onComplete: (User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    fun addUserListener(context: Context, onListen:(List<Item>,Int)->Unit){
        val items= mutableListOf<Item>()
        var contributionUser:ContributionUser
        var user:User
        GroupParameter.currenGroupTotalAmount=0
        val currentGroup=GroupParameter.currenGroupUsers
        for (phone in currentGroup!!.listOfUsers){
            var curentUserDoc=firestoreInstance.collection("users").document(phone!!)
            curentUserDoc.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                curentUserDoc
                    .collection("groups")
                    .document(currentGroup.groupId)
                    .addSnapshotListener { documentSnapshot2, firebaseFirestoreException ->
                        user= documentSnapshot!!.toObject(User::class.java)!!
                        //Toast.makeText(context,"${user}",Toast.LENGTH_SHORT).show()
                        contributionUser= documentSnapshot2!!.toObject(ContributionUser::class.java)!!
                        GroupParameter.currenGroupTotalAmount+=contributionUser.amount
                        //Toast.makeText(context,"${user.name}",Toast.LENGTH_SHORT).show()
                        items.add(UserItem(user,contributionUser,context))
                        onListen(items,GroupParameter.currenGroupTotalAmount)
                    }
            }
        }
    }


    fun addUserListenerForSelect(context: Context, onListen:(List<Item>)->Unit){
        val progressDialog=context.indeterminateProgressDialog("Veillez patienter")
        val currentGroup=GroupParameter.currenGroupUsers
        firestoreInstance.collection("users")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    return@addSnapshotListener
                }
                val items= mutableListOf<Item>()
                var user:User
                val userPhone=FirebaseAuth.getInstance().currentUser?.phoneNumber
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        user=it.toObject(User::class.java)!!
                        if(it.id!=userPhone && !(currentGroup!!.listOfUsers.contains(user.phoneNumber))){
                            items.add(UserSelectItem(user,context,false))
                        }
                    }
                }
                progressDialog.dismiss()
                onListen(items)
            }
    }

    fun addGroupListener2(context: Context, onListen:(List<Item>)->Unit):ListenerRegistration{
        return firestoreInstance.collection("groups")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    return@addSnapshotListener
                }
                val items= mutableListOf<Item>()
                val userPhone=FirebaseAuth.getInstance().currentUser?.phoneNumber
                var currentgroup:GroupUsers
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        currentgroup=it.toObject(GroupUsers::class.java)!!
                        if(currentgroup.listOfUsers.contains(userPhone)){
                            items.add(GroupUserItem(currentgroup ,context))
                        }
                    }
                }
                onListen(items)
            }
    }


    /*fun addSearchUserListener(context: Context, text:String, onListen:(List<Item>)->Unit):ListenerRegistration{
        return firestoreInstance.collection("users")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    Log.e("FIRESTORE","User listener error.",firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items= mutableListOf<Item>()
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        if(it.id!=FirebaseAuth.getInstance().currentUser?.phoneNumber){
                            if(!text.isEmpty()){
                                if(it.id.toUpperCase().contains(text.toUpperCase()) ||
                                    it["name"].toString().toUpperCase().contains(text.toUpperCase())){
                                    items.add(UserItem(it.toObject(User::class.java)!!,context))
                                }
                            }
                            else{
                                items.add(UserItem(it.toObject(User::class.java)!!,context))
                            }
                        }
                    }
                }
                onListen(items)
            }
    }*/


    fun CreateGroupe(group:GroupUsers,photo:String?=null,onComplete: (groupId:String) -> Unit){
        val refGroupe = currentGroupColRef.document()
        refGroupe.set(group)
            .addOnSuccessListener {
                val refGroup= firestoreInstance.collection("groups").document(refGroupe.id)
                refGroup.update("groupId",refGroupe.id)
                    .addOnSuccessListener {
                        addUpdateUserAmountGroup(currentUser!!.phoneNumber!!,0,refGroupe.id,onComplete = {
                            onComplete(refGroupe.id)
                        })
                    }
                    .addOnFailureListener{
                        addUpdateUserAmountGroup(currentUser!!.phoneNumber!!,0,refGroupe.id,onComplete = {
                            onComplete(refGroupe.id)
                        })
                    }
            }
    }


    fun addUpdateUserAmountGroup(userPhone:String,amount: Int,groupId1:String,onComplete: (groupId2:String) -> Unit){
        val contributionUser=ContributionUser(userPhone,groupId1,amount)
        val refGroupe = currentUserDocRef.collection("groups")
            .document(groupId1).set(contributionUser)
            .addOnSuccessListener{
                onComplete(groupId1)
            }
            .addOnFailureListener {
                onComplete(groupId1)
            }
    }

    fun removeListener(registration: ListenerRegistration)=registration.remove()



    fun updateImagePathGroup(groupId: String, imagePath: String,onComplete: (message:String) -> Unit) {
        val ref= firestoreInstance.collection("groups").document(groupId)
        ref.update("photo",imagePath)
            .addOnSuccessListener {
                ref.update("groupId",groupId)
                    .addOnSuccessListener {
                        onComplete("Groupe crée")
                    }
                    .addOnFailureListener{
                        onComplete("Echec de création")
                    }
            }
            .addOnFailureListener{
                onComplete("Echec de création")
            }
    }

    fun showDetailOfGroup(group: GroupUsers, context: Context) {
        val message="${group.groupName} est un groupe dont le montant de l'objectif est de ${group.abjectifAmount} FCFA" +
                ".Ce groupe compte ${group.listOfUsers.size} membre(s) avec ${group.creatorPhone} comme createur." +
                "\n\nDescription du groupe:" +
                "\n${group.descriptionGroup}"
        showAlertDilogue("${group.groupName}",message,context)
    }

    fun showAlertDilogue(title: String, message: String, context: Context) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle(title)
            setMessage(message)
            setIcon(R.drawable.notification_icon_background)
        }
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->{

                }
            }
        }
        builder.setPositiveButton("Ok",dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }
}