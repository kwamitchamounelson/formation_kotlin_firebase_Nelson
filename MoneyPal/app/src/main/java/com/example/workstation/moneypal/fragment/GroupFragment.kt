package com.example.workstation.moneypal.fragment


import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.recycleView.UserItem
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecycleview=true
    //var users= mutableListOf<Item>()
    private lateinit var peopleSection: Section
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loadData()
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_group, container, false)
        view.apply {

        }
        return view
    }

    private fun loadData() {
        userListenerRegistration= FirestoreUtil.addUserListener(this@GroupFragment.context!!, this::updateRecycleView)
    }

    private fun updateRecycleView(items: List<Item>){
        fun init(){
            recyclerView_users.apply {
                layoutManager=LinearLayoutManager(this@GroupFragment.context)
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
        val itemDecor = DividerItemDecoration(this@GroupFragment.context, ClipDrawable.HORIZONTAL)
        recyclerView_users.addItemDecoration(itemDecor)
    }

    private val onItemClick= OnItemClickListener{ item, view ->
        if(item is UserItem){
            //action here
        }
    }


}
