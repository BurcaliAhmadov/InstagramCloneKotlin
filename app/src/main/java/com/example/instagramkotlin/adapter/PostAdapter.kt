package com.example.instagramkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.instagramkotlin.databinding.RecyclerRowBinding
import com.example.instagramkotlin.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(val placeList: List<Post>): RecyclerView.Adapter<PostAdapter.PlaceHolder>() {
    class PlaceHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        val binding:RecyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  PlaceHolder(binding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {

        holder.binding.commentView.text=placeList[position].comment
        holder.binding.emailView.text=placeList[position].email
        Picasso.get().load(placeList[position].url).into(holder.binding.imageView)
    }
}