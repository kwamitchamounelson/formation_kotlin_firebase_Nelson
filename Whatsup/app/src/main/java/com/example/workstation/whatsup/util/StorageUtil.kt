package com.example.workstation.whatsup.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val currentUserRef:StorageReference
    get() = storageInstance
        .reference
        .child(FirebaseAuth.getInstance().currentUser?.phoneNumber?:throw NullPointerException("utilisateur inexistant."))

    fun uploadprofilePhoto(imageByte:ByteArray,
                           onSuccess:(imagePath:String)->Unit){
        val ref= currentUserRef.child("ProfilePicture/${FirebaseAuth.getInstance().currentUser?.phoneNumber?:throw NullPointerException("utilisateur inexistant.")}")
        ref.putBytes(imageByte).addOnSuccessListener {
            onSuccess(ref.path)
        }
    }

    fun uploadMessageImage(imageByte:ByteArray,
                           onSuccess:(imagePath:String)->Unit){
        val ref= currentUserRef.child("messages/${UUID.nameUUIDFromBytes(imageByte)}")
        ref.putBytes(imageByte).addOnSuccessListener {
            onSuccess(ref.path)
        }
    }

    fun pathToReference(path:String)= storageInstance.getReference(path)
}