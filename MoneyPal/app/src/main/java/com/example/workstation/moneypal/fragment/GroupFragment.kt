package com.example.workstation.moneypal.fragment


import android.content.Intent
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workstation.moneypal.GroupActivity
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.AcountParameter
import com.example.workstation.moneypal.entities.GroupParameter
import com.example.workstation.moneypal.recycleView.UserItem
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.fragment_group.view.*
import org.jetbrains.anko.support.v4.toast

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
            view.info_date_group.text=AcountParameter.infoDayAcount
            view.info_solde_group.text=AcountParameter.infoSoldeAcount
            val currentGroup=GroupParameter.currenGroupUsers
            if(currentGroup!=null){
                view.group_name.text=currentGroup.groupName
                view.amount_group.text=currentGroup.abjectifAmount.toString()
                //gestion du progress bar
                view.progressbar_amount.apply {
                    max=currentGroup.abjectifAmount
                    progress=GroupParameter.currenGroupTotalAmount
                }

            }
            else{
                view.group_name.text=""
                view.amount_group.text=""
            }
            view.add_member_text_view.setOnClickListener {
                if(currentGroup!=null){
                    //gestion de dynamique link
                }
                else{
                    toast("Veuillez choisir un groupe")
                    val intent=Intent(this@GroupFragment.context,GroupActivity::class.java)
                    startActivity(intent)
                }
            }
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
