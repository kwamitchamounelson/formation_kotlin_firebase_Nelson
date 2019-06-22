package com.example.workstation.moneypal

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.workstation.moneypal.fragment.GroupFragment
import com.example.workstation.moneypal.fragment.HomeFragment

class MoneyPalActivity : AppCompatActivity() {
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                replaceFragment(GroupFragment())
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money_pal)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        replaceFragment(HomeFragment())
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
}
