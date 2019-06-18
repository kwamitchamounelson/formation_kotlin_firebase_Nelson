package com.example.workstation.whatsup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.MenuView
import android.support.v7.view.menu.MenuView.ItemView
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

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId:String
    private lateinit var textMessage: TextView
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecycleView=true
    private lateinit var messagesSection:Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var userName=intent.getStringExtra(AppConstants.USER_NAME)
        var userPhone=intent.getStringExtra(AppConstants.USER_PHONE)
        supportActionBar?.title=("$userName($userPhone)")

        val otherUserId=intent.getStringExtra(AppConstants.USER_PHONE)
        FirestoreUtil.getOrCreatChatChannel(otherUserId){channelId ->

            currentChannelId=channelId

            messagesListenerRegistration=FirestoreUtil.addChatMessageListener(AppConstants.NO_LANGUAGE,channelId,this,this::updateRecycleView)

            imageView_send.setOnClickListener {
                val messageToSend=
                    TextMessage(edit_text_message.text.toString(),Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,MessageType.TEXT)
                edit_text_message.setText("")
                if (messageToSend != null) {
                    FirestoreUtil.sendMessage(messageToSend,channelId)
                }
            }
            fab_send_image.setOnClickListener {
                val intent=Intent().apply {
                    type="image/*"
                    action=Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"Choisir une image"),RC_SELECT_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SELECT_IMAGE && resultCode== Activity.RESULT_OK && data!=null && data.data!=null){
            val selectedImagePath=data.data
            val selectedImageBmp= MediaStore.Images.Media.getBitmap(contentResolver,selectedImagePath)
            val outputStream= ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            val selectedImageBytes=outputStream.toByteArray()
            StorageUtil.uploadMessageImage(selectedImageBytes){imagePath ->
                val messageToSend=
                    ImageMessage(imagePath,Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                FirestoreUtil.sendMessage(messageToSend,currentChannelId)
            }
        }
    }

    private fun updateRecycleView(messages:List<Item>){
        fun init(){
            recycleView_message.apply {
                layoutManager=LinearLayoutManager(this@ChatActivity)
                adapter=GroupAdapter<ViewHolder>().apply {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.english -> {
                messagesListenerRegistration=FirestoreUtil.addChatMessageListener(AppConstants.ENGLISH,currentChannelId,
                    this,this::updateRecycleView)
                return true
            }
            R.id.frensh -> {
                messagesListenerRegistration=FirestoreUtil.addChatMessageListener(AppConstants.FRENSH,currentChannelId,
                    this,this::updateRecycleView)
                return true
            }
            R.id.no_traduction -> {
                messagesListenerRegistration=FirestoreUtil.addChatMessageListener(AppConstants.NO_LANGUAGE,currentChannelId,
                    this,this::updateRecycleView)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
