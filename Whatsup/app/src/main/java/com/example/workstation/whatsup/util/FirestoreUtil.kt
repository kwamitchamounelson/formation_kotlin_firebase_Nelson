package com.example.workstation.whatsup.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.workstation.whatsup.entities.*
import com.example.workstation.whatsup.recycleview.ImageMessageItem
import com.example.workstation.whatsup.recycleview.PersonItem
import com.example.workstation.whatsup.recycleview.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {
    private val currentUser=FirebaseAuth.getInstance().currentUser
    private val firestoreInstance:FirebaseFirestore by lazy{FirebaseFirestore.getInstance()}
    private val currentUserDocRef:DocumentReference
    get() = firestoreInstance.document("users/${currentUser?.phoneNumber
        ?:throw NullPointerException("UID is null")}")

    private val chatChannelsCollectionRef= firestoreInstance.collection("chatChannels")

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

    fun addUserListener(context: Context, onListen:(List<Item>)->Unit):ListenerRegistration{
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
                            items.add(PersonItem(it.toObject(User::class.java)!!,it.id,context))
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
                            items.add(TextMessageItem(language,it.toObject(TextMessage::class.java)!!,context))
                        } else{
                            items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!,context))
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
}