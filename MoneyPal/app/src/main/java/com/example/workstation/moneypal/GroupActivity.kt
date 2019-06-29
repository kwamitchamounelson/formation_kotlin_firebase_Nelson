package com.example.workstation.moneypal

import android.content.Intent
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.recycleView.GroupUserItem
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.content_group.*

class GroupActivity : AppCompatActivity() {

    private lateinit var groupListenerRegistration: ListenerRegistration
    private var shouldInitRecycleview=true
    //var users= mutableListOf<Item>()
    private lateinit var peopleSection: Section
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        groupListenerRegistration= FirestoreUtil.addGroupListener2(this, this::updateRecycleView)!!

        fab_new_group.setOnClickListener { view ->
            val intent = Intent(this, CreatGroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateRecycleView(items: List<Item>){
        fun init(){
            recycle_view_groups.apply {
                layoutManager= LinearLayoutManager(this@GroupActivity)
                adapter= GroupAdapter<ViewHolder>().apply {
                    peopleSection= Section(items)
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
        val itemDecor = DividerItemDecoration(this, ClipDrawable.HORIZONTAL)
        recycle_view_groups.addItemDecoration(itemDecor)
    }

    private val onItemClick= OnItemClickListener{ item, view ->
        if(item is GroupUserItem){
            GroupParameter.currenGroupUsers=item.group
            GroupParameter.currentFragment=2
            val intent = Intent(this, MoneyPalActivity::class.java)
            startActivity(intent)
        }
    }

}
