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
import com.example.workstation.moneypal.entities.User
import com.example.workstation.moneypal.recycleView.UserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_group.view.*

class GroupFragment : Fragment() {

    var users= mutableListOf<Item>()
    private lateinit var itemSection: Section
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loadData()
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_group, container, false)
        view.apply {
            view.recyclerView_users.apply {
                layoutManager= LinearLayoutManager(this@GroupFragment.context)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSection= Section(users)
                    add(itemSection)
                }
            }
            val itemDecor = DividerItemDecoration(this@GroupFragment.context, ClipDrawable.HORIZONTAL)
            view.recyclerView_users.addItemDecoration(itemDecor)
        }
        return view
    }

    private fun loadData() {
        val nbre=1..30
        for(i in nbre){
            users.add(UserItem(User("User Name $i"),this@GroupFragment.context!!))
        }
    }


}
