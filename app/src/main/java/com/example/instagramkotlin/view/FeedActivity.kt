package com.example.instagramkotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramkotlin.R
import com.example.instagramkotlin.adapter.PostAdapter
import com.example.instagramkotlin.databinding.ActivityFeedBinding
import com.example.instagramkotlin.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var binding: ActivityFeedBinding
    private lateinit var list: ArrayList<Post>
    var adapter:PostAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view: View =binding.root
        setContentView(view)
        auth=FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        list=ArrayList<Post>()
        getData()
        binding.recyeclerView.layoutManager=LinearLayoutManager(this)
        adapter=PostAdapter(list)
        binding.recyeclerView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.out_next,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.logout){
            auth.signOut()
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId== R.id.upload){
            val intent=Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    fun getData(){
        db.collection("Posts").orderBy("time",Query.Direction.DESCENDING).addSnapshotListener { snapShot, e ->
            if(e!=null){
                Toast.makeText(this@FeedActivity,e.localizedMessage,Toast.LENGTH_LONG).show()
            }
            else{
                if(snapShot!=null){
                    if(!snapShot.isEmpty){
                        val documants=snapShot.documents
                        list.clear()

                        for(document in documants){
                            val email=document.get("userEmail") as String
                            val comment=document.get("comment") as String
                            val downloadUrl=document.get("downloadUrl") as String
                            val posts= Post(email,downloadUrl,comment)
                            list.add(posts)
                        }
                        adapter!!.notifyDataSetChanged()

                    }
                }
            }
        }
    }

}