package com.example.chatapp

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.FragmentInfoBinding
import com.example.chatapp.ui.constact.IMAGE_REQUEST
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class InfoFragment : Fragment() {
   private var _binding :FragmentInfoBinding ?=null
    private val binding  get() =  _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var storage :StorageReference
    private  var imageUri : Uri ?=null
    private lateinit var database : DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        getAvatar()
        storage = FirebaseStorage.getInstance().getReference("upload")
        binding.avatarInfo.setOnClickListener {
            openImage()
        }
        return binding.root
    }

    private fun openImage() {
       val intent  =Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,IMAGE_REQUEST)
    }

    private fun upload(){
        val user = auth.currentUser!!
        if (imageUri !=null){
            val firebaseStorage = storage.child(System.currentTimeMillis().toString())
            firebaseStorage.putFile(imageUri!!).continueWithTask { p0 ->
                if (!p0.isSuccessful) {
                    throw  p0.exception!!
                }
                firebaseStorage.downloadUrl
            }.addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val downloadUri = p0.result
                    val uriStr = downloadUri.toString()

                    database = FirebaseDatabase.getInstance().getReference("User").child(user.uid).child("info")
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["image"] = uriStr
                    database.updateChildren(hashMap as Map<String, Any>)

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST){
              imageUri = data?.data

                upload()

        }

    }
    fun getAvatar(){
        val user = auth.currentUser!!
        val database = FirebaseDatabase.getInstance().getReference("User").child(user.uid).child("info")
        database.get().addOnSuccessListener {
            binding.nameInfo.text = it.child("name").value.toString()
            Glide.with(requireActivity()).load(it.child("image").value.toString()).into(binding.avatarInfo)
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Failure",Toast.LENGTH_SHORT).show()
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }

}