package com.example.instagramkotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.instagramkotlin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var  auth:FirebaseAuth
    var email: String?=null
    var password :String ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view:View=binding.root
        setContentView(view)
        auth= FirebaseAuth.getInstance()
        val currentUser=auth.currentUser

        if(currentUser!=null){
            val intent=Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signIn(view: View){
        email=binding.emailText.text.toString()
        password=binding.password.text.toString()
        if(email!=null && password!=null){
            println("test 1")
            if (email!!.isNotEmpty() && password!!.isNotEmpty()){
                println("test 2")
                auth.signInWithEmailAndPassword(email!!,password!!).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_LONG).show()
                    val intent=Intent(this@MainActivity, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {exception->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }


    }
    fun signUp(view: View){
        email=binding.emailText.text.toString()
        password=binding.password.text.toString()
       if(email!=null && password!=null){
           if (email!!.isNotEmpty() && password!!.isNotEmpty()){

               auth.createUserWithEmailAndPassword(email!!,password!!).addOnCompleteListener {task->
                   if(task.isSuccessful){
                       Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_LONG).show()
                       val intent=Intent(this@MainActivity, FeedActivity::class.java)
                       startActivity(intent)
                       finish()
                   }


               }.addOnFailureListener { exception->
                   Toast.makeText(applicationContext,"exception.localizedMessage",Toast.LENGTH_LONG).show()
               }
           }
           else{
               Toast.makeText(applicationContext,"Enter email and pasword",Toast.LENGTH_LONG).show()
           }


       }


    }

}
