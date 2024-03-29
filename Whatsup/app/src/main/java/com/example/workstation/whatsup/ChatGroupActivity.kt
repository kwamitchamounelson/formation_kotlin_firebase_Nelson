package com.example.workstation.whatsup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.workstation.whatsup.entities.ImageMessage
import com.example.workstation.whatsup.entities.MessageType
import com.example.workstation.whatsup.entities.TextMessage
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_SELECT_IMAGE=2

class ChatGroupActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecycleView=true
    private lateinit var messagesSection: Section
    private lateinit var groupId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var groupNmae=intent.getStringExtra(AppConstants.GROUP_NAME)
        groupId=intent.getStringExtra(AppConstants.GROUP_ID)
        supportActionBar?.title=(groupNmae)

        messagesListenerRegistration=FirestoreUtil.addChatGroupMessageListener(AppConstants.NO_LANGUAGE,groupId,this,this::updateRecycleView)

        imageView_send.setOnClickListener {
            val messageToSend=
                TextMessage(edit_text_message.text.toString(), Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.phoneNumber!!, MessageType.TEXT)
            edit_text_message.setText("")
            if (messageToSend != null) {
                FirestoreUtil.sendMessageGroup(messageToSend,groupId)
            }
        }
        fab_send_image.setOnClickListener {
            val intent= Intent().apply {
                type="image/*"
                action= Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"Choisir une image"),RC_SELECT_IMAGE)
        }
    }

    private fun updateRecycleView(messages:List<Item>){
        fun init(){
            recycleView_message.apply {
                layoutManager= LinearLayoutManager(this@ChatGroupActivity)
                adapter= GroupAdapter<ViewHolder>().apply {
                    messagesSection=Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecycleView=false
        }
        fun updateItems()=messagesSection.update(messages)
        if(shouldInitRecycleView){
            init()
        }
        else{
            updateItems()
        }
        recycleView_message.scrollToPosition(recycleView_message.adapter!!.itemCount-1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SELECT_IMAGE && resultCode== Activity.RESULT_OK && data!=null && data.data!=null){
            val selectedImagePath=data.data
            val selectedImageBmp= MediaStore.Images.Media.getBitmap(contentResolver,selectedImagePath)
            val outputStream= ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            val selectedImageBytes=outputStream.toByteArray()
            StorageUtil.uploadMessageImage(selectedImageBytes){ imagePath ->
                val messageToSend=
                    ImageMessage(imagePath,Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                FirestoreUtil.sendMessageGroup(messageToSend,groupId)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.english -> {
                messagesListenerRegistration=FirestoreUtil.addChatGroupMessageListener(AppConstants.ENGLISH,groupId,
                    this,this::updateRecycleView)
                return true
            }
            R.id.frensh -> {
                messagesListenerRegistration=FirestoreUtil.addChatGroupMessageListener(AppConstants.FRENSH,groupId,
                    this,this::updateRecycleView)
                return true
            }
            R.id.no_traduction -> {
                messagesListenerRegistration=FirestoreUtil.addChatGroupMessageListener(AppConstants.NO_LANGUAGE,groupId,
                    this,this::updateRecycleView)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
