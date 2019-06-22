package com.example.workstation.moneypal.fragment


import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.DetailOperation
import com.example.workstation.moneypal.entities.Operation
import com.example.workstation.moneypal.recycleView.DetailOperationItem
import com.example.workstation.moneypal.recycleView.OperationItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {


    var operations= mutableListOf<Item>()
    var detailsOperations= mutableListOf<Item>()
    private lateinit var itemSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        view.apply {
            Glide.with(this)
                .load("")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_person_outline_black_24dp)
                .error(R.drawable.ic_person_outline_black_24dp)
                .into(image_view_user)

            view.recyclerView_operation.apply {
                layoutManager= LinearLayoutManager(this@HomeFragment.context,LinearLayout.HORIZONTAL,false)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSection= Section(operations)
                    add(itemSection)
                }
            }

            view.recyclerView_detail.apply {
                layoutManager= LinearLayoutManager(this@HomeFragment.context)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSection= Section(detailsOperations)
                    add(itemSection)
                }
            }
            val itemDecor = DividerItemDecoration(this@HomeFragment.context, ClipDrawable.HORIZONTAL)
            view.recyclerView_detail.addItemDecoration(itemDecor)
        }
        return view
    }

    private fun loadData() {
        val nbre=1..10
        var nbre2=1..30
        for (i in nbre){
            operations.add(OperationItem(Operation("Operation $i"),this@HomeFragment.context!!))
        }

        for(i in nbre2){
            detailsOperations.add(DetailOperationItem(DetailOperation("Transfere $i","Lundi $i",(100*i))
                ,this@HomeFragment.context!!))
        }
    }


}
