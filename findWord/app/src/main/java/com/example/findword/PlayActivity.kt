package com.example.findword

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
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
        supportActionBar?.title=("${wordItems.size} mots trouvés")
        supportActionBar?.subtitle="${textBrut.length} carctères dans la grille"
        toast("${wordItems.size} mots trouvés")
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
                    //recherche horizontale de la gauche vers la droite
                    str=""
                    caracters.clear()
                    for (index in column until (column+wordToFind.length)){
                        try {
                            str+=grille[row][index]
                            caracters.add(Caracter(grille[row][index],row,index,false))
                        }catch (e:Exception){
                            break
                        }
                    }
                    if(str.equals(wordToFind,true)){
                        wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.HORIZONTAL_DIRECTION),this))
                        trouve=true
                        break
                    }
                    //recherche horizontale de la gauche vers la droite

                    //TODO implementer tous les autres types de recherches
                    //recherche horizontale de la droite ver la gauche
                    if(!trouve){
                        str=""
                        caracters.clear()
                        for (index in (column-wordToFind.length+1) until column+1){
                            try {
                                str+=grille[row][index]
                                caracters.add(Caracter(grille[row][index],row,index,false))
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind.reversed(),true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.HORIZONTAL_DIRECTION),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche horizontale de la droite ver la gauche


                    //recherche verticale du haut vers le bas
                    if(!trouve){
                        str=""
                        caracters.clear()
                        for (index in row until (row+wordToFind.length)){
                            try {
                                str+=grille[index][column]
                                caracters.add(Caracter(grille[index][column],index,column,false))
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind,true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.VERICAL_DIRECTION),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche verticale haut vers le bas


                    //recherche verticale du bas vers le haut
                    if(!trouve){
                        str=""
                        caracters.clear()
                        for (index in (row-wordToFind.length+1) until row+1){
                            try {
                                str+=grille[index][column]
                                caracters.add(Caracter(grille[index][column],index,column,false))
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind.reversed(),true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.VERICAL_DIRECTION),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche verticale du bas vers le haut


                    //recherche oblique du haut vers le bas(de la gauche ver la droite)
                    if(!trouve){
                        str=""
                        caracters.clear()
                        var k=0
                        for (index in row until (row+wordToFind.length)){
                            try {
                                str+=grille[index][column+k]
                                caracters.add(Caracter(grille[index][column+k],index,column+k,false))
                                k++
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind,true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.OBLIQUE_DIRECTION_GD),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche oblique du haut vers le bas(de la gauche ver la droite)


                    //recherche oblique du bas vers le haut(de la gauche ver la droite)
                    if(!trouve){
                        str=""
                        caracters.clear()
                        var k=wordToFind.length
                        for (index in (row-wordToFind.length+1) until row+1){
                            try {
                                str+=grille[index][column-k+1]
                                caracters.add(Caracter(grille[index][column-k+1],index,column-k+1,false))
                                k--
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind.reversed(),true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.OBLIQUE_DIRECTION_GD),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche oblique du bas vers le haut(de la gauche ver la droite)


                    //recherche oblique du haut vers le bas(de la droite vers la gauche)
                    if(!trouve){
                        str=""
                        caracters.clear()
                        var k=0
                        for (index in row until (row+wordToFind.length)){
                            try {
                                str+=grille[index][column-k]
                                caracters.add(Caracter(grille[index][column-k],index,column-k,false))
                                k++
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind,true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.OBLIQUE_DIRECTION_DG),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche oblique du haut vers le bas(de la droite vers la gauche)


                    //recherche oblique du bas vers le haut(de la droite vers la gauche)
                    if(!trouve){
                        str=""
                        caracters.clear()
                        var k=wordToFind.length
                        for (index in (row-wordToFind.length+1) until row+1){
                            try {
                                str+=grille[index][column+k+1]
                                caracters.add(Caracter(grille[index][column+k+1],index,column+k+1,false))
                                k--
                            }catch (e:Exception){
                                break
                            }
                        }
                        if(str.equals(wordToFind.reversed(),true)){
                            wordItems.add(WordItem(Word(wordToFind,caracters,"definition",AppConstantes.OBLIQUE_DIRECTION_DG),this))
                            trouve=true
                            break
                        }
                    }
                    //recherche oblique du bas vers le haut(de la droite vers la gauche)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu!!.findItem(R.id.search_view_word)
        val searchView = searchItem.actionView as SearchView
        searchView.setSubmitButtonEnabled(true)
        searchView.setQueryHint("Search a word")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                seachWord(newText)
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun seachWord(newText: String) {
        if(newText.isEmpty()){
            recycle_view_word.apply {
                layoutManager= LinearLayoutManager(this@PlayActivity)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSectionWord= Section(wordItems)
                    add(itemSectionWord)
                    setOnItemClickListener(onItemClickWord)
                }
            }
        }
        else{
            var items= mutableListOf<Item>()
            var word:Word
            for(item in wordItems){
                word=(item as WordItem).word
                if((word.text).contains(newText,true)){
                    items.add(item)
                }
            }
            recycle_view_word.apply {
                layoutManager= LinearLayoutManager(this@PlayActivity)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSectionWord= Section(items)
                    add(itemSectionWord)
                    setOnItemClickListener(onItemClickWord)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.original_grill -> {
                //val progressDialog=indeterminateProgressDialog("Recherche des mots en dans la grille...")
                loadData(DICTIONARY.ORIGINAL_GRILLE)
                //progressDialog.dismiss()
                return true
            }
            R.id.my_dictionary -> {
                val intent=Intent(this,WordActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

