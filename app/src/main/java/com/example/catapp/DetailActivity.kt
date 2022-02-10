package com.example.catapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.delete


class DetailActivity : AppCompatActivity() {
    companion object {
        const val CAT_FACT_TEXT = "com.example.catapp.cat_fact_text"
        const val CAT_FACT_IMAGE = "com.example.catapp.cat_fact_image"
    }
    private var btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupActionBar()
        setText()
    }

    private fun setupButton(text: String, img: String){
        btn = findViewById(R.id.AddDelFav)
        val cats = showListFromDB().toMutableList()
        var inDB = false
        for (i in 0..cats.size-1){
            if (cats.get(i).text == text && cats.get(i).img == img){
                btn?.text = "Убрать из избранного"
                inDB = true
                break
            }
        }
        if (!inDB){
            btn?.text = "Добавить в избранное"
        }

        btn?.setOnClickListener {
            if (!inDB){
                val cat = Cat()
                cat.text = text
                cat.img = img
                cats.add(cat)
                cats.toList()
                saveIntoDB(cats)
                btn?.text = "Убрать из избранного"
            }
            else{
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                val ct = realm.where(Cat::class.java).equalTo("text", text)
                ct.findFirst()?.deleteFromRealm()
                realm.commitTransaction()
                btn?.text = "Добавить в избранное"
            }
            setText()
        }
    }

    private fun saveIntoDB(cats: List<Cat>){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(cats)
        realm.commitTransaction()
    }

    private fun loadFromDB(): List<Cat>{
        val realm = Realm.getDefaultInstance()
        return realm.where(Cat::class.java).findAll()
    }

    private fun showListFromDB(): List<Cat> {
        val cats = loadFromDB()
        return cats
    }

    private fun setupActionBar(){
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = "Detail"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setText(){
        val text = intent?.extras?.getString(CAT_FACT_TEXT)
        val img = intent?.extras?.getString(CAT_FACT_IMAGE)

        if (text != null) {
            if (img != null) {
                setupButton(text, img)
            }
        }

        val txtView = findViewById<TextView>(R.id.detailText)
        txtView.text = text

        val imgView = findViewById<ImageView>(R.id.imageView2)
        Glide.with(this).load(img).into(imgView)
    }
}