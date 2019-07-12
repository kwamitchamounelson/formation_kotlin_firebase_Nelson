package com.example.workstation.moneypal.fragment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.workstation.moneypal.AppConstants
import com.example.workstation.moneypal.DetailOperationActivity
import com.example.workstation.moneypal.R
import com.example.workstation.moneypal.entities.*
import com.example.workstation.moneypal.glide.GlideApp
import com.example.workstation.moneypal.recycleView.DetailOperationItem
import com.example.workstation.moneypal.recycleView.OperationItem
import com.example.workstation.moneypal.util.SmsUtil
import com.example.workstation.whatsup.util.FirestoreUtil
import com.example.workstation.whatsup.util.StorageUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast


class HomeFragment : Fragment() {


    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS=0
    var operations= mutableListOf<Item>()
    var detailsOperationsItems= mutableListOf<Item>()
    private lateinit var itemSectionOperation: Section
    private lateinit var itemSectionDetailOperation: Section
    private var listOfMessages = ArrayList<SmsObject>()
    private var currentListOfDetailOperation=ArrayList<DetailOperation>()
    private lateinit var myView:View
    private var shouldInitRecycleview=true
    private var currentOperation:Operation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView= inflater.inflate(R.layout.fragment_home, container, false)
        myView.apply {
            try {
                FirestoreUtil.getCurrentUser { user ->
                    if(user.photo!=null){
                        GlideApp.with(this@HomeFragment)
                            .load(StorageUtil.pathToReference(user.photo!!))
                            .transform(CircleCrop())
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .error(R.drawable.ic_account_circle_black_24dp)
                            .into(myView.image_view_user)
                    }
                    else{
                        GlideApp.with(this@HomeFragment)
                            .load("")
                            .transform(CircleCrop())
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .error(R.drawable.ic_account_circle_black_24dp)
                            .into(myView.image_view_user)
                    }
                }
            }catch (e:Exception){}
        }
        myView.button_see_all.setOnClickListener {
            initAllOperationTypes()
            loadData(null)
        }
        myView.textView_last_transaction.setOnClickListener {
            loadLasOperation()
        }
        initAllOperationTypes()
        if (ContextCompat.checkSelfPermission(this@HomeFragment.context!!, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@HomeFragment.act, Manifest.permission.READ_SMS)) {
                loadData(null)
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@HomeFragment.act,
                    arrayOf(Manifest.permission.READ_SMS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS
                )
            }
        }
        else{
            loadData(null)
        }
        val itemDecor = DividerItemDecoration(this@HomeFragment.context, ClipDrawable.HORIZONTAL)
        myView.recyclerView_detail.addItemDecoration(itemDecor)
        return myView
    }

    private fun loadLasOperation() {
        if(currentListOfDetailOperation.isNotEmpty()){
            currentListOfDetailOperation.sortByDescending {
                return@sortByDescending it.date
            }
            detailsOperationsItems.clear()
            detailsOperationsItems.add(DetailOperationItem(currentListOfDetailOperation[0],this@HomeFragment.context!!))
            updateRecycleViewDetailOperation()
        }
    }

    //chargement des donnee dans le recycleView
    private fun updateRecycleViewDetailOperation() {
        try {
            fun init(){
                myView.recyclerView_detail.apply {
                    layoutManager= LinearLayoutManager(this@HomeFragment.context)
                    adapter= GroupAdapter<ViewHolder>().apply {
                        itemSectionDetailOperation= Section(detailsOperationsItems)
                        add(itemSectionDetailOperation)
                        setOnItemClickListener(onItemClickDetailOperation)
                    }
                }
                shouldInitRecycleview=false
            }
            fun updateItem()=itemSectionDetailOperation.update(detailsOperationsItems)

            if(shouldInitRecycleview){
                init()
            }
            else{
                updateItem()
            }

            var str=""
            val count=detailsOperationsItems.size
            if(count==0){
                str="Aucune Opération"
            }
            else if(count==1){
                str="Une Opération"
            }
            else{
                str="${detailsOperationsItems.size} Opérations"
            }
            toast(str)
        }catch (e:Exception){}
    }

    private fun initAllOperationTypes() {
        operations.clear()
        for (op in OperationData.listeOfOperation){
            operations.add(OperationItem(op,this@HomeFragment.context!!))
        }
        myView.recyclerView_operation.apply {
            layoutManager= LinearLayoutManager(this@HomeFragment.context,LinearLayout.HORIZONTAL,false)
            adapter= GroupAdapter<ViewHolder>().apply {
                itemSectionOperation= Section(operations)
                add(itemSectionOperation)
                setOnItemClickListener(onItemClickOperation)
            }
        }
    }

    private fun loadData(operation: Operation?) {
        val progressDialog=indeterminateProgressDialog("Récupération des messages...")
        currentOperation=operation
        detailsOperationsItems.clear()
        currentListOfDetailOperation.clear()
        listOfMessages=SmsUtil.getAllSms(this@HomeFragment.context!!,operation)
        var op:Operation?
        var detailOperation:DetailOperation?
        for (sms in listOfMessages){
            op= sms.getDetailOperation().operation
            if(op!=null){
                detailOperation=sms.getDetailOperation()
                currentListOfDetailOperation.add(detailOperation)
                detailsOperationsItems.add(DetailOperationItem(detailOperation,this@HomeFragment.context!!))
            }
        }
        if(currentOperation==null){
            if(currentListOfDetailOperation.isNotEmpty()){
                detailOperation=currentListOfDetailOperation[0]
                val date=detailOperation.date
                myView.info_date.text="Solde du compte a la date du ${java.sql.Date(date!!.time)}"
                myView.info_solde.text="${SmsUtil.getTheSolde(detailOperation)} FCFA"
            }
            else{
                myView.info_date.text="Solde du compte"
                myView.info_solde.text="0 FCFA"
            }
        }
        updateRecycleViewDetailOperation()
        progressDialog.dismiss()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadData(null)
                } else {
                    toast("Vous devez accepter la permission")
                }
                return
            }
            else -> return
        }
    }

    private val onItemClickOperation= OnItemClickListener{ item, view ->
        if(item is OperationItem){
            loadData(item.operation)
        }
    }

    private val onItemClickDetailOperation= OnItemClickListener{ item, view ->
        if(item is DetailOperationItem){
            val intent=Intent(this@HomeFragment.context,DetailOperationActivity::class.java)
            intent.putExtra(AppConstants.DETAIL_OPERATION,item.detailOperation)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AcountParameter.infoDayAcount= myView.info_date.text.toString()
        AcountParameter.infoSoldeAcount= myView.info_solde.text.toString()
    }
}
