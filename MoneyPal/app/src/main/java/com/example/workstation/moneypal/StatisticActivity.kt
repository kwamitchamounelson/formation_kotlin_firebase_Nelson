package com.example.workstation.moneypal

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.text.Layout
import com.example.workstation.moneypal.entities.Operation
import com.example.workstation.moneypal.entities.OperationData
import com.example.workstation.moneypal.entities.OperatorParameter
import com.example.workstation.moneypal.util.SmsUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

import kotlinx.android.synthetic.main.activity_statistic.*
import kotlinx.android.synthetic.main.content_statistic.*

class StatisticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        managePiedChart()
    }

    private fun managePiedChart() {
        val listOfMessages= SmsUtil.getAllSms(this,null)
        var yValues= arrayListOf<PieEntry>()
        var count=0
        var op: Operation?
        for (operation in OperationData.listeOfOperation){
            count=0
             for (smsObject in listOfMessages){
                 op= smsObject.getDetailOperation().operation
                 if(op!=null){
                     if(op.equals(operation)){
                         count++
                     }
                 }
             }
            yValues.add(PieEntry(count.toFloat(),operation.name))
        }
        pie_chart.apply {
            setUsePercentValues(false)
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef=0.99f
            isDrawHoleEnabled=false
            setHoleColor(Color.WHITE)
            //transparentCircleRadius=61f
            animateY(3000,Easing.EasingOption.EaseInOutCubic)

            var dataSet=PieDataSet(yValues,"")
            dataSet.apply {
                sliceSpace=1f
                selectionShift=5f
                setColors(ColorTemplate.MATERIAL_COLORS,Color.BLUE)
            }
            var data=PieData(dataSet)
            data.apply {
                setValueTextSize(8f)
                setValueTextColor(Color.BLACK)
            }

            legend.apply {
                isWordWrapEnabled=true
            }
            description.apply {
                isEnabled=true
                text="Statistiques des Transactions ${OperatorParameter.CURRENT_OPERATOR}"
                textSize=9f
            }
            //apply all modification
            setData(data)
        }
    }

}
