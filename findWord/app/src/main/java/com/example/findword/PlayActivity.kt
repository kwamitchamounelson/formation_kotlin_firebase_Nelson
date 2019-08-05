package com.example.findword

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.example.findword.entities.Caracter
import com.example.findword.entities.Word
import com.example.findword.recycleView.CharItem
import com.example.findword.recycleView.WordItem
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.content_play.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PlayActivity : AppCompatActivity() {
    private var grille= arrayListOf<CharArray>()
    private val REQUEST_IMAGE_CAPTURE = 1

    var wordItems= mutableListOf<Item>()
    private lateinit var itemSectionWord: Section

    var CharItems= mutableListOf<Item>()
    private lateinit var itemSectionChar: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(toolbar)
        loadData(AppConstantes.DEFAULT_TEXT)
        fab.setOnClickListener { view ->
            dispatchTakePictureIntent()
        }
    }

    private fun loadData(text:String) {
        wordItems.clear()
        CharItems.clear()

        //convertion de la chaine en miniscule pour faciliter les comparaisons
        val textBrut=text.toLowerCase(Locale.ROOT)

        //debut de linitialisation et de laffichage dans la grille
        //decoupage de la chaine de caractere en des morceau de 31 caracteres
        var textSpliter=""
        val taille=textBrut.length
        val nbreTab=((taille/AppConstantes.MAX_COLUMN)+1).toInt()
        var indice=0
        for (num in 0 until nbreTab){
            try {
                indice=(num*AppConstantes.MAX_COLUMN)
                textSpliter+=(textBrut.substring(indice,(indice+AppConstantes.MAX_COLUMN))+AppConstantes.MY_SPLITER)
            }catch (e:Exception){
                textSpliter+=(textBrut.substring(indice))
            }
        }
        var strTabs=textSpliter.split(AppConstantes.MY_SPLITER).toMutableList()
        if(strTabs.isEmpty()){
            strTabs= mutableListOf(textBrut)
        }
        //initialisation de la grille avec 16 lignes et 31 colones
        var lasElement=strTabs.last()
        val sizeOfLasArray=lasElement.length
        for (i in sizeOfLasArray until AppConstantes.MAX_COLUMN){
            lasElement+=" "
        }
        strTabs.set(strTabs.lastIndex,lasElement)
        grille= arrayListOf<CharArray>()
        for(str in strTabs){
            grille.add(str.toCharArray())
        }
        //afficharge de la grille
        for (row in 0 until grille.size){
            for (column in 0 until AppConstantes.MAX_COLUMN){
                CharItems.add(CharItem(Caracter(grille[row][column],row,column,false),this))
            }
        }
        grille_recycleView.apply {
            layoutManager= GridLayoutManager(this@PlayActivity,AppConstantes.MAX_COLUMN)
            adapter= GroupAdapter<ViewHolder>().apply {
                itemSectionChar= Section(CharItems)
                add(itemSectionChar)
                //setOnItemClickListener(onItemClickDetailOperation)
            }
        }
        //fin de linitialisation et de laffichage dans la grille


        //debut de la recherche des mots
        //val progressDialog=indeterminateProgressDialog("Recherche des mots en dans la grille...")
        for (wordToFind in DICTIONARY.MY_DICTIONARY){
            //FONCTION PRINCIPALE DE RECHERCHE
            findAllValidWord(wordToFind)
        }
        recycle_view_word.apply {
            layoutManager= LinearLayoutManager(this@PlayActivity)
            adapter= GroupAdapter<ViewHolder>().apply {
                itemSectionWord= Section(wordItems)
                add(itemSectionWord)
                setOnItemClickListener(onItemClickWord)
            }
        }
        //progressDialog.dismiss()
        toast("fin de la recherche")
        //fin de la recherche des mots
    }


    //FONCTION PRINCIPALE DE RECHERCHE
    private fun findAllValidWord(wordToFind: String) {
        var caracters= arrayListOf<Caracter>()
        var trouve=false
        val firsChar=wordToFind.first()
        var str=""
        for (row in 0 until grille.size){
            for (column in 0 until AppConstantes.MAX_COLUMN){
                if(grille[row][column]==firsChar){
                    str=""
                    caracters.clear()
                    //recherche horizontale de la gauche vers la droite
                    for (index in column until (column+wordToFind.length)){
                        try {
                            str+=grille[row][index]
                            caracters.add(Caracter(grille[row][index],row,index,false))
                        }catch (e:Exception){
                            break
                        }
                    }
                    //recherche horizontale de la gauche vers la droite

                    //TODO implementer tous les autres types de recherches

                    //toast("recherche de $wordToFind:\n$str")
                    if(str.equals(wordToFind,true)){
                        wordItems.add(WordItem(Word(wordToFind,caracters,"definition"),this))
                        trouve=true
                        break
                    }
                }
            }
            if (trouve){
                break
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //recuperation de limage capturee
            val imageBitmap = data!!.extras!!.get("data") as Bitmap

            //ici on passe limage a la fonction firebase pour extraction de text
            val image = FirebaseVisionImage.fromBitmap(imageBitmap)
            val detector = FirebaseVision.getInstance()
                .onDeviceTextRecognizer
            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    //la detection de texte a reussi
                    //recuperation du texte detecte avec elimination des espaces pour plus de clarte
                    val text=firebaseVisionText.text.replace("\\s".toRegex(), "").toLowerCase(Locale.ROOT)
                    //le traitement du text recupere se fait dans cette fonction
                    loadData(text)
                }
                .addOnFailureListener {
                    //la detection de texte a echoue
                    Toast.makeText(this,"Erreur de detection de text",Toast.LENGTH_SHORT).show()
                }
        }
    }

    private val onItemClickWord= OnItemClickListener{ item, view ->
        if(item is WordItem){
            val caracters=item.word.caracters
            finWord(caracters)
        }
    }

    private fun finWord(caracters: ArrayList<Caracter>?) {
        var charItem:CharItem
        for (item in CharItems){
            charItem=item as CharItem
            charItem.caracter.isSelected=false
        }
        if (caracters != null) {
            var itemIndexe:Int
            var item:Any
            for(caracter in caracters){
                try {
                    itemIndexe=CharItems.indexOf(CharItem(caracter,this))
                    caracter.isSelected=true
                    CharItems.set(itemIndexe,CharItem(caracter,this))
                }catch (e:Exception){}
            }
            grille_recycleView.apply {
                layoutManager= GridLayoutManager(this@PlayActivity,AppConstantes.MAX_COLUMN)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSectionChar= Section(CharItems)
                    add(itemSectionChar)
                    //setOnItemClickListener(onItemClickDetailOperation)
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

}

