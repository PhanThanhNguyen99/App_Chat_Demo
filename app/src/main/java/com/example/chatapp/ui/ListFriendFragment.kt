package com.example.chatapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentListFriendBinding
import com.example.chatapp.user.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ListFriendFragment : Fragment() {
     private var _binding : FragmentListFriendBinding ?= null
    private val binding get() = _binding!!
    private  var adapter = ListFriendAdapter()
    private lateinit var auth: FirebaseAuth
    private  var  list :MutableList<User> = ArrayList()
    private lateinit var viewRecyclerView : RecyclerView
    private lateinit var listFriend: ValueEventListener
    private lateinit var database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListFriendBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        list.clear()
        getAvatar()
        viewRecyclerView = binding.revFriend
        getList()
        binding.searchListFriend.setOnClickListener {
            findNavController().navigate(R.id.action_listFriendFragment_to_searchFragment)
        }
        viewRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    private fun getList() {
        val user = auth.currentUser!!
         database  = FirebaseDatabase.getInstance().reference.child("User").child(user.uid).child("listFriend")
       listFriend =  database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (postSnapshot in snapshot.children) {
                    val database1 = FirebaseDatabase.getInstance().getReference("User").child(postSnapshot.key.toString())
                    Log.i("GG", "onDataChange:  ${postSnapshot.key.toString()}")
                    database1.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nUser = snapshot.child("info").getValue(User::class.java)
                            list.add(nUser!!)
                            Log.i("gg",list.toString())

                            adapter.setData(list)
                            viewRecyclerView.adapter = adapter


                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })




    }

     private fun getAvatar(){
         val user = auth.currentUser!!
         val database = FirebaseDatabase.getInstance().getReference("User").child(user.uid).child("info")

         database.get().addOnSuccessListener {
             binding.nameUser.text = it.child("name").value.toString()
             Glide.with(requireActivity()).load(it.child("image").value.toString()).into(binding.circleAvarta)
         }.addOnFailureListener {
             Toast.makeText(requireContext(),"Failure",Toast.LENGTH_SHORT).show()
         }

     }

    override fun onPause() {
        super.onPause()
       database.removeEventListener(listFriend)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
