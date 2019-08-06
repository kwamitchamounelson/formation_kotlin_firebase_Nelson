package com.example.findword

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.findword.entities.Word
import com.example.findword.recycleView.WordItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.content_word.*
import org.jetbrains.anko.find

class WordActivity : AppCompatActivity() {

    var wordItems= mutableListOf<Item>()
    private lateinit var itemSectionWord: Section
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Mon dictionnaire"
        loadData(DICTIONARY.MY_DICTIONARY)
    }

    private fun loadData(dictionary: List<String>) {
        var indexe=0
        for(str in dictionary){
            wordItems.add(WordItem(Word(str,null,"definition","${indexe+1}"),this))
            indexe++
        }
        supportActionBar?.subtitle="${wordItems.size} mots enregistrés"
        recycleView_word2.apply {
            layoutManager= LinearLayoutManager(this@WordActivity)
            adapter= GroupAdapter<ViewHolder>().apply {
                itemSectionWord= Section(wordItems)
                add(itemSectionWord)
                //setOnItemClickListener(onItemClickWord)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu!!.removeItem(R.id.my_dictionary)
        menu!!.removeItem(R.id.original_grill)
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
            recycleView_word2.apply {
                layoutManager= LinearLayoutManager(this@WordActivity)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSectionWord= Section(wordItems)
                    add(itemSectionWord)
                    //setOnItemClickListener(onItemClickWord)
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
            recycleView_word2.apply {
                layoutManager= LinearLayoutManager(this@WordActivity)
                adapter= GroupAdapter<ViewHolder>().apply {
                    itemSectionWord= Section(items)
                    add(itemSectionWord)
                    //setOnItemClickListener(onItemClickWord)
                }
            }
        }
    }
}
