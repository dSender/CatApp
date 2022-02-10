package com.example.catapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.catapp.DetailActivity.Companion.CAT_FACT_IMAGE
import com.example.catapp.DetailActivity.Companion.CAT_FACT_TEXT
import org.w3c.dom.Text

class CatsAdapter(private val cats: List<Cat>): RecyclerView.Adapter<CatsListHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatsListHolder {
        val rootView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.cat_item, parent, false)
        return CatsListHolder(rootView)
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    override fun onBindViewHolder(holder: CatsListHolder, position: Int) {
        holder.bind(cats.get(position))
    }
}


class CatsListHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val textView: TextView = itemView.findViewById(R.id.textView)
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(cat: Cat){
        textView.text = cat.text

        Glide.with(itemView).load(cat.img).into(imageView)


        itemView.setOnClickListener{
            openDetailActivity(itemView.context, cat)
        }
    }

    private fun openDetailActivity(context: Context, cat: Cat){
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(CAT_FACT_TEXT, cat.text)
        intent.putExtra(CAT_FACT_IMAGE, cat.img)
        context.startActivity(intent)
    }
}