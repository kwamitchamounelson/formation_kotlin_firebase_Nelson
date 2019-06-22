package com.example.workstation.whatsup.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.workstation.whatsup.entities.*
import com.example.workstation.whatsup.recycleview.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {
    private val currentUser=FirebaseAuth.getInstance().currentUser
    private val firestoreInstance:FirebaseFirestore by lazy{FirebaseFirestore.getInstance()}

    //for user
    private val currentUserDocRef:DocumentReference
        get() = firestoreInstance.document("users/${currentUser?.phoneNumber
            ?:throw NullPointerException("UID is null")}")

    private val currentGroupColRef:CollectionReference = firestoreInstance.collection("groups")

    private val chatChannelsCollectionRef= firestoreInstance.collection("chatChannels")

    private val chatGroupCollectionRef= firestoreInstance.collection("groups")

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

    fun CreateGroupe(groupParameter:GroupeCreateParameter,photo:String?=null,onComplete: (groupId:String) -> Unit){
        val newGroup=GroupUser("",groupParameter.groupeName,photo,groupParameter.listOfMenbersNumber)
        val refGroupe = currentGroupColRef.document()
        refGroupe.set(newGroup)
            .addOnSuccessListener {
                // ajout de lid du groupe dans la liste des utilisateurs
                var ref:Any
                for(userPhone in groupParameter.listOfMenbersNumber){
                    ref= firestoreInstance.document("users/$userPhone").collection("groups").document(refGroupe.id).set(mapOf("groupId" to refGroupe.id))
                }

                val refGroup= firestoreInstance.collection("groups").document(refGroupe.id)
                refGroup.update("grpoupId",refGroupe.id)
                    .addOnSuccessListener {
                        onComplete(refGroupe.id)
                    }
                    .addOnFailureListener{
                        onComplete(refGroupe.id)
                    }
            }
    }

    fun getCurrentUser(onComplete: (User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    fun addUserListener(context: Context, onListen:(List<Item>)->Unit):ListenerRegistration{
        return firestoreInstance.collection("users")
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    return@addSnapshotListener
                }
                val items= mutableListOf<Item>()
                val userPhone=FirebaseAuth.getInstance().currentUser?.phoneNumber
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        if(it.id!=FirebaseAuth.getInstance().currentUser?.phoneNumber){
                            items.add(PersonItem(it.toObject(User::class.java)!!,it.id,context))
                        }
                    }

                    //chargement des groupes
                    if(userPhone!=null){
                        val refGroup=firestoreInstance.collection("groups")
                        val ref= firestoreInstance.collection("users").document(userPhone).collection("groups")
                            .addSnapshotListener{querySnapshot2, firebaseFirestoreException2 ->
                                if(querySnapshot2!=null){
                                    querySnapshot2.documents.forEach {groupDoc->
                                        val group=refGroup.document(groupDoc.id)
                                            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                                if(documentSnapshot!=null){
                                                    items.add(GroupItem(documentSnapshot.toObject(GroupUser::class.java)!!,context))
                                                }
                                                onListen(items)
                                            }
                                    }
                                }
                            }
                    }
                    //chargement des groupes

                }
                //  onListen(items)
            }
    }

    fun addUserListenerGroup(context: Context, onListen:(List<Item>)->Unit):ListenerRegistration{
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
                            items.add(PersonItemGroup(it.toObject(User::class.java)!!,it.id,context,false))
                        }
                    }
                }
                onListen(items)
            }
    }

    fun addSearchUserListener(context: Context, text:String, onListen:(List<Item>)->Unit):ListenerRegistration{
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
                                    items.add(PersonItem(it.toObject(User::class.java)!!,it.id,context))
                                }
                            }
                            else{
                                items.add(PersonItem(it.toObject(User::class.java)!!,it.id,context))
                            }
                        }
                    }
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration)=registration.remove()

    fun getOrCreatChatChannel(otherUserId:String,onComplete: (channelId:String) -> Unit){
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if(it.exists()){
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }
                val currentUserId=FirebaseAuth.getInstance().currentUser!!.phoneNumber
                val newChannel= chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId,otherUserId)))
                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId!!)
                    .set(mapOf("channelId" to newChannel.id))
                onComplete(newChannel.id)
            }
    }

    fun addChatMessageListener(language:String,channelId:String,context: Context,onListen:(List<Item>)->Unit):ListenerRegistration{
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    Log.e("FIRESTORE","ChatMessageListener error.",firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items= mutableListOf<Item>()
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        if(it["type"]==MessageType.TEXT){
                            items.add(TextMessageItem(language,it.toObject(TextMessage::class.java)!!,context,""))
                        } else{
                            items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!,context,""))
                        }
                        return@forEach
                    }
                }
                onListen(items)
            }
    }


    // liste des messages de group
    fun addChatGroupMessageListener(language:String,groupId:String,context: Context,onListen:(List<Item>)->Unit):ListenerRegistration{
        return chatGroupCollectionRef.document(groupId).collection("messages")
            .orderBy("time")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    return@addSnapshotListener
                }
                val items= mutableListOf<Item>()
                if (querySnapshot != null) {
                    querySnapshot.documents.forEach {
                        if(it["type"]==MessageType.TEXT){
                            val messageText=it.toObject(TextMessage::class.java)
                            items.add(TextMessageItem(language,messageText!!,context,messageText.senderId))
                        } else{
                            val messageImage=it.toObject(ImageMessage::class.java)
                            items.add(ImageMessageItem(messageImage!!,context,messageImage.senderId))
                        }
                        return@forEach
                    }
                }
                onListen(items)
            }
    }

    fun sendMessage(message:Message,channelId:String){
        chatChannelsCollectionRef.document(channelId).collection("messages").add(message)
    }

    fun sendMessageGroup(message:Message,groupId:String){
        chatGroupCollectionRef.document(groupId).collection("messages").add(message)
    }

    fun updateImagePathGroup(groupId: String, imagePath: String,onComplete: (message:String) -> Unit) {
        val ref= firestoreInstance.collection("groups").document(groupId)
        ref.update("photo",imagePath)
            .addOnSuccessListener {
                ref.update("grpoupId",groupId)
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
}