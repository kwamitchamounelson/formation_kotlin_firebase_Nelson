package com.example.workstation.whatsup.fragment


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.workstation.whatsup.AppConstants

import com.example.workstation.whatsup.R
import com.example.workstation.whatsup.recycleview.PersonItem
import com.example.workstation.whatsup.recycleview.PersonItemGroup
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_add_member.*
import org.jetbrains.anko.backgroundColor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AddMemberFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecycleview=true
    private lateinit var peopleSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userListenerRegistration= FirestoreUtil.addUserListenerGroup(this.activity!!, this::updateRecycleView)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_member, container, false)
    }

    private fun updateRecycleView(items: List<Item>){
        fun init(){
            recycle_view_add_member.apply {
                layoutManager= LinearLayoutManager(this@AddMemberFragment.context)
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
        val itemDecor = DividerItemDecoration(this@AddMemberFragment.context, ClipDrawable.HORIZONTAL)
        recycle_view_add_member.addItemDecoration(itemDecor)
    }

    private val onItemClick= OnItemClickListener{ item, view ->
        if(item is PersonItemGroup){
            //Toast.makeText(this@AddMemberFragment.context,item.person.phoneNumber,Toast.LENGTH_SHORT).show()
        }

    }


}
