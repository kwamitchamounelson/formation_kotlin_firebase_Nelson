package com.example.workstation.moneypal

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.entities.OperatorParameter
import com.example.workstation.moneypal.fragment.GroupFragment
import com.example.workstation.moneypal.fragment.HomeFragment
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.toast


class MoneyPalActivity : AppCompatActivity() {
    private  var operatorIndicator: MenuItem?=null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                GroupParameter.currentFragment=1
                replaceFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                if(GroupParameter.currenGroupUsers!=null){
                    GroupParameter.currentFragment=2
                    replaceFragment(GroupFragment())
                }
                else{
                    toast("Veuillez choisir un groupe")
                    val intent=Intent(this,GroupActivity::class.java)
                    startActivity(intent)
                }
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
        getDynamicLink()
        changeOperator(OperatorParameter.CURRENT_OPERATOR)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun getDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener {
                if(it!=null){
                    var isMember=true
                    var deepLink=it.link.toString()
                    //toast(deepLink)
                    val tab=deepLink.split(AppConstants.SPLITOR_OF_LINK)
                    if(tab.size==2){
                        val groupId=tab[1]
                        FirestoreUtil.getGroupById(groupId,onComplete = {group ->
                            FirestoreUtil.getCurrentUser {user ->
                                if (group != null) {
                                    //val progressDialog=indeterminateProgressDialog("Ajout dans le groupe ${group.groupName}")
                                    FirestoreUtil.addMemberOnGroup(arrayListOf(user.phoneNumber),group,onComplete = {success ->
                                        var title=""
                                        var message=""
                                        if(success){
                                            title="Groupe ${group.groupName}"
                                            message="Salut ${user.name} vous faites desormais partir du groupe d'objectif" +
                                                    " ${group.groupName}." +
                                                    "\n\nDescription du groupe:" +
                                                    "\n${group.descriptionGroup}"
                                        }
                                        else{
                                            title="Echec"
                                            message="Erreur d'ajout dans le groupe d'objectif ${group.groupName}"
                                        }
                                        isMember=false
                                        FirestoreUtil.showAlertDilogue(title,message,this)
                                        GroupParameter.currentFragment=2
                                        GroupParameter.currenGroupUsers=group
                                        //progressDialog.dismiss()
                                    })
                                }
                            }
                        })
                    }
                    if(isMember){
                        toast("Vous faites déja partir de ce groupe")
                    }
                }
            }
            .addOnFailureListener {
                toast(it.toString())
            }
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
            R.id.current_operator -> {
                //GroupParameter.currentFragment=1
                if(OperatorParameter.CURRENT_OPERATOR.equals(AppConstants.ORANGE_MONEY_OPERATOR,true)){
                    changeOperator(AppConstants.MTN_MOBILE_MONEY_OPERATOR)
                }
                else{
                    changeOperator(AppConstants.ORANGE_MONEY_OPERATOR)
                }
                return true
            }
            R.id.group_manager -> {
                val intent = Intent(this, GroupActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.statistic -> {
                val intent = Intent(this, StatisticActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.orange_money -> {
                //GroupParameter.currentFragment=1
                changeOperator(AppConstants.ORANGE_MONEY_OPERATOR)
                return true
            }
            R.id.mobile_money -> {
                //GroupParameter.currentFragment=1
                changeOperator(AppConstants.MTN_MOBILE_MONEY_OPERATOR)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun changeOperator(operatorName:String){
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
        if(GroupParameter.currentFragment==2){
            replaceFragment(GroupFragment())
        }
        else{
            replaceFragment(HomeFragment())
        }
    }
}
