package com.example.workstation.whatsup

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.workstation.whatsup.fragment.AddMemberFragment
import com.example.workstation.whatsup.fragment.EditGroupCreationFragment
import com.example.workstation.whatsup.util.FirestoreUtil

class CreatChatGroup2Activity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.edit_group -> {
                replaceFragment(EditGroupCreationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.add_menbers -> {
                replaceFragment(AddMemberFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout,fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_creaat_group, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_group_buton -> {
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creat_chat_group2)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        replaceFragment(EditGroupCreationFragment())
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
}
