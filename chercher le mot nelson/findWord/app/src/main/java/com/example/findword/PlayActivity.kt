package com.example.findword

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.findword.entities.Word
import com.example.findword.recycleView.WordItem
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.content_play.*
import java.lang.Exception

class PlayActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1

    var wordItems= mutableListOf<Item>()
    private lateinit var itemSectionWord: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(toolbar)
        loadData(null,0,"")

        fab.setOnClickListener { view ->
            dispatchTakePictureIntent()
        }
    }

    private fun loadData(grill:Array<CharArray>?,sizeRow:Int,text:String) {
        wordItems.clear()
        if(grill!=null){
            //TODO parcourir la matrice et recuperer les mots
            var wordText= arrayListOf<Word>()
            text.forEach {
                wordText=formerLesMot(it,text)
                wordText.forEach {word->
                    wordItems.add(WordItem(word,this))
                }
            }

            var row=0..sizeRow
            var colum=0..30
            row.forEach {r->
                colum.forEach {c->
                    wordItems.add(WordItem(Word("${grill[r][c]}",null,null,"definition"),this))
                }
            }

        }
        else{
            wordItems.add(WordItem(Word("Liste des mot ici",null,null,""),this))
        }

        recycle_view_word.apply {
            layoutManager= LinearLayoutManager(this@PlayActivity)
            adapter= GroupAdapter<ViewHolder>().apply {
                itemSectionWord= Section(wordItems)
                add(itemSectionWord)
                //setOnItemClickListener(onItemClickDetailOperation)
            }
        }
    }

    private fun formerLesMot(char: Char, text: String): ArrayList<Word> {
        var list= arrayListOf<Word>()
        var index=text.indexOf(char)
        val count=index..text.length
        var str=""
        count.forEach {
            try {
                if(it!=index){
                    str=""
                    str+=text.substring(index,it)
                    if (str.length in 3..28){
                        list.add(Word(str,false,null,"Definition du mot"))
                    }
                }
            }catch (e:Exception){}
        }
        return list
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap

            val image = FirebaseVisionImage.fromBitmap(imageBitmap)
            val detector = FirebaseVision.getInstance()
                .onDeviceTextRecognizer
            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    val text=firebaseVisionText.text.replace("\\s".toRegex(), "")
                    if (text.isNotEmpty()){
                        //TODO traitement du texte recupere
                        var grill=Array(16) { CharArray(31) }
                        var colum=0
                        var row=0
                        var index=0
                        for(char in text){
                            grill[row][colum]=char
                            if(index!=0 && (index%31)==0){
                                row++
                                colum=0
                            }
                            index++
                        }
                        loadData(grill,0,text)
                    }
                    else{
                        loadData(null,0,"")
                    }
                    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Erreur de detection de text",Toast.LENGTH_SHORT).show()
                }
            grille_imageView.setImageBitmap(imageBitmap)
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

/*
private class YourImageAnalyzer : ImageAnalysis.Analyzer {
    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    override fun analyze(imageProxy: ImageProxy?, degrees: Int) {
        val mediaImage = imageProxy?.image
        val imageRotation = degreesToFirebaseRotation(degrees)
        if (mediaImage != null) {
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            // Pass image to an ML Kit Vision API
            // ...
        }
    }
}
*/
