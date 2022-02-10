package com.example.catapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.realm.Realm
import io.realm.RealmConfiguration
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private val apiUrl = "https://cat-fact.herokuapp.com/facts"
    private val imageUrl = "https://aws.random.cat/meow"
    private var catImagesList: MutableList<String> = mutableListOf()
    private var completedCatList: MutableList<String> = mutableListOf()
    private var cats_: MutableList<Cat> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InitRealm()

        val queue = Volley.newRequestQueue(this)
        getCatsFromServer(queue)
    }

    private fun saveIntoDB(cats: List<Cat>){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(cats)
        realm.commitTransaction()
    }

    override fun onResume() {
        super.onResume()
        setList(showListFromDB())
    }

    private fun loadFromDB(): List<Cat>{
        val realm = Realm.getDefaultInstance()
        return realm.where(Cat::class.java).findAll()
    }

    private fun showListFromDB(): List<Cat> {
        val cats = loadFromDB()
        return cats
    }

    private fun clearDB(){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    private fun clearRecyclerView(){
        val recyclerView: RecyclerView = findViewById(R.id.recyclerId)
        val clearedCats: List<Cat> = mutableListOf()
        val adapter = CatsAdapter(clearedCats)
        recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

    }

    private fun getCatsFromServer(queue: RequestQueue){
        val stringRequest = StringRequest(
            Request.Method.GET,
            apiUrl,
            { response ->
                completedCatList = parseJsonData(response)
                for (i in 0..completedCatList.size-1) {
                    getImageCatFromServer(queue, i)
                }
            },
            {
                Toast.makeText(this, "Ошибка запроса", Toast.LENGTH_SHORT).show()
                val cts = showListFromDB()
                setList(cts)
            }
        )
        queue.add(stringRequest)
    }

    private fun parseJsonData(responseText: String): MutableList<String> {
        val jsonArray = JSONArray(responseText)
        val catList: MutableList<String> = mutableListOf()
        for (index in 0 until jsonArray.length()){
            val jsonObj = jsonArray.getJSONObject(index)
            val catText = jsonObj.getString("text")
            catList.add(catText)
        }
        return catList
    }

    private fun getImageCatFromServer(queue: RequestQueue, counter: Int){

        val stringRequest = StringRequest(
            Request.Method.GET,
            imageUrl,
            { response ->
                val img = JSONObject(response).getString("file")
                catImagesList.add(img)
                val ct = Cat()
                ct.text = completedCatList.get(counter)
                ct.img = img
                cats_.add(ct)
                if (counter == completedCatList.size-1){
                    setList(cats_)
                }
            },
            {
                Toast.makeText(this, "Ошибка запроса", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }

    private fun setList(cats: List<Cat>){
        val recyclerView: RecyclerView = findViewById(R.id.recyclerId)

        val adapter = CatsAdapter(cats)
        recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
    }

    private fun InitRealm(){
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}