package com.example.workstation.moneypal

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.workstation.moneypal.entities.OperatorParameter
import com.example.workstation.moneypal.fragment.GroupFragment
import com.example.workstation.moneypal.fragment.HomeFragment


class MoneyPalActivity : AppCompatActivity() {
    private  var operatorIndicator: MenuItem?=null
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
        changeOperator(OperatorParameter.CURRENT_OPERATOR,HomeFragment())
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        operatorIndicator=menu!!.findItem(R.id.current_operator)
        if(operatorIndicator!=null){
            when(OperatorParameter.CURRENT_OPERATOR){
                AppConstants.ORANGE_MONEY_OPERATOR->{
                    operatorIndicator!!.setIcon(R.drawable.orang)
                }
                AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                    operatorIndicator!!.setIcon(R.drawable.mtn)
                }
            }
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_group -> {
                return true
            }
            R.id.orange_money -> {
                changeOperator(AppConstants.ORANGE_MONEY_OPERATOR,HomeFragment())
                return true
            }
            R.id.mobile_money -> {
                changeOperator(AppConstants.MTN_MOBILE_MONEY_OPERATOR,HomeFragment())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun changeOperator(operatorName:String,fragment: Fragment){
        OperatorParameter.CURRENT_OPERATOR=operatorName
        if(operatorIndicator!=null){
            when(OperatorParameter.CURRENT_OPERATOR){
                AppConstants.ORANGE_MONEY_OPERATOR->{
                    operatorIndicator!!.setIcon(R.drawable.orang)
                }
                AppConstants.MTN_MOBILE_MONEY_OPERATOR->{
                    operatorIndicator!!.setIcon(R.drawable.mtn)
                }
            }
        }
        replaceFragment(fragment)
    }
}
