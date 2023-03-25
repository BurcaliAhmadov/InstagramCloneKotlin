package com.example.instagramkotlin.view


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instagramkotlin.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class UploadActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUploadBinding
    private lateinit var activityResulLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var db:FirebaseFirestore
    private lateinit var auth:FirebaseAuth
    private lateinit var  storage:FirebaseStorage
    var selectedPicture: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadBinding.inflate(layoutInflater)
        val view: View =binding.root
        setContentView(view)
        registerLauncher()
        db=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        storage=FirebaseStorage.getInstance()



    }
    fun image(view: View){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission
                .READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission
                    .READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }.show()
            }
            else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //startActivity
            activityResulLauncher.launch(intentToGallery)
        }
    }
    fun registerLauncher(){
        activityResulLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode== RESULT_OK){
                val intentFromData= result.data
                if(intentFromData!=null){
                    selectedPicture=intentFromData.data
                    binding.imageView.setImageURI(selectedPicture)
                }

            }

        }
        permissionLauncher=registerForActivityResult(RequestPermission()){result->
            if(result){
                //permission granted
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResulLauncher.launch(intentToGallery)
            }
            else{
                //permission denied
                Toast.makeText(this@UploadActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }

        }
    }
    fun upload(view:View){
        val uuid=UUID.randomUUID()
        val imageName="$uuid.jpg"
        val storageReference=storage.reference
        val imageReference=storageReference.child("images").child(imageName)
        if(selectedPicture!=null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {task->
                //downloadUrl
                val uploadedPicturesReference=storage.reference.child("images").child(imageName)
                uploadedPicturesReference.downloadUrl.addOnSuccessListener {uri->
                    val downloadUri=uri.toString()
                    val hashMap= hashMapOf<String,Any>()
                    hashMap["downloadUrl"] = downloadUri
                    hashMap["userEmail"]=auth.currentUser!!.email.toString()
                    hashMap["comment"]=binding.comment.text.toString()
                    hashMap["time"]=Timestamp.now()
                    db.collection("Posts").add(hashMap).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener { e->
                        Toast.makeText(this@UploadActivity,e.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }.addOnFailureListener { e->
                    Toast.makeText(this@UploadActivity,e.localizedMessage,Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener { e->
                Toast.makeText(this@UploadActivity,e.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }
}