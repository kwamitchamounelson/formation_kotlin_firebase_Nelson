package com.example.workstation.whatsup

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.workstation.whatsup.entities.User
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.TimeUnit
import com.example.workstation.whatsup.recycleview.PersonItem
import com.xwray.groupie.OnItemClickListener
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecycleview=true
    private lateinit var peopleSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        userListenerRegistration=FirestoreUtil.addUserListener(this, this::updateRecycleView)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }


    private fun updateRecycleView(items: List<Item>){
        fun init(){
            recyclerView_list_0f_user.apply {
                layoutManager=LinearLayoutManager(this@MainActivity)
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
        val itemDecor = DividerItemDecoration(this, HORIZONTAL)
        recyclerView_list_0f_user.addItemDecoration(itemDecor)
    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecycleview=true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu!!.findItem(R.id.search_view_user)
        val searchView = searchItem.actionView as SearchView
        searchView.setSubmitButtonEnabled(true)
        searchView.setQueryHint("Search")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                userListenerRegistration=FirestoreUtil.addSearchUserListener(this@MainActivity
                    ,newText
                    ,this@MainActivity::updateRecycleView)
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_group -> {
                val intent = Intent(this, CreatChatGroupActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.new_diffusion -> true
            R.id.whatsup_web -> true
            R.id.message_important -> true
            R.id.parametres -> {
                FirestoreUtil.initCurrentUserIfFirstTime {
                    val intent = Intent(this, MyAccountActivity::class.java)
                    startActivity(intent)
                }
                return true
            }
            R.id.deconnexion -> signOut()
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        return
    }*/

    private fun signOut():Boolean {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Déconnexion")
        builder.setMessage("Voullez-vous vous déconnecter?")
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, UserAuthActivity::class.java)
                    startActivity(intent)
                }
                DialogInterface.BUTTON_NEUTRAL -> {

                }
            }
        }
        builder.setPositiveButton("Oui",dialogClickListener)
        builder.setNeutralButton("Annuler",dialogClickListener)
        dialog = builder.create()
        dialog.show()
        return true
    }

    private val onItemClick=OnItemClickListener{ item, view ->
        if(item is PersonItem){
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(AppConstants.USER_NAME,item.person.name)
            intent.putExtra(AppConstants.USER_PHONE,item.person.phoneNumber)
            intent.putExtra(AppConstants.USER_ID,item.userId)
            startActivity(intent)
        }

    }
}

