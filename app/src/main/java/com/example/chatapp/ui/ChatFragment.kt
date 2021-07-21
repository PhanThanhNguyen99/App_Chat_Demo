package com.example.chatapp.ui

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ui.ChatFragmentArgs
import com.example.chatapp.ListFragment
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.databinding.FragmentListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment(){
    private val args by navArgs<ChatFragmentArgs>()
   private var _binding : FragmentChatBinding ?= null
    private val binding  get() = _binding!!
    private lateinit var auth :FirebaseAuth
  private  val adapter = ChatAdapter()
    private lateinit var recyclerView : RecyclerView
    private lateinit var database2 : DatabaseReference
    private lateinit var seenMessage: ValueEventListener


    private  var list :MutableList<Chat> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        Glide.with(requireActivity()).load(args.curretUser.image).into(binding.circleAvatarChat)
        binding.nameUserChat.text = args.curretUser.name
        auth = FirebaseAuth.getInstance()
        recyclerView = binding.rcvChat
         seenMessage()
        readMessage()
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
            reverseLayout = false
        }

        binding.txtSend.setOnClickListener{
            sendMessage()
            binding.edtMessage.setText("")
        }

        return binding.root
    }

    private fun readMessage() {
        val user =auth.currentUser!!
        val database = FirebaseDatabase.getInstance().getReference("User")
                .child(user.uid)
                .child("listFriend")
                .child(args.curretUser.id.toString())
                .child("message")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (postSnapshot in snapshot.children){
                    val sChat = postSnapshot.child("sent").value.toString()
                    val tChat = postSnapshot.child("text").value.toString()
                    val iChat = args.curretUser.image
                    val seChat = postSnapshot.child("seen").value.toString()

                        list.add(Chat(iChat,sChat,tChat,seChat))

                }

                adapter.setData(list)
                recyclerView.adapter =adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
     private fun seenMessage(){
         val user = auth.currentUser!!
          database2 = FirebaseDatabase.getInstance().getReference("User")
                 .child(args.curretUser.id.toString())
                 .child("listFriend")
                 .child(user.uid)
                 .child("message")
         seenMessage = database2.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 for (postSnap in snapshot.children){
                     val hashMap1 : HashMap<String, String> = HashMap()
                     hashMap1["seen"] = "true"
                     database2.child(postSnap.key.toString()).updateChildren(hashMap1 as Map<String, Any>)
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }
         })
     }
    private fun sendMessage() {
        val user = auth.currentUser!!
        val database = FirebaseDatabase.getInstance().getReference("User")
                .child(user.uid)
                .child("listFriend")
                .child(args.curretUser.id.toString())

        val hashMap : HashMap<String, String> = HashMap()
        hashMap["seen"] = "false"
        hashMap["sent"] = "true"
        hashMap["text"] = binding.edtMessage.text.toString()
        database.child("message").push().setValue(hashMap)

        val database1 = FirebaseDatabase.getInstance().getReference("User")
                .child(args.curretUser.id.toString())
                .child("listFriend")
                .child(user.uid)

        val hashMap1 : HashMap<String, String> = HashMap()
        hashMap1["seen"] = "false"
        hashMap1["sent"] = "false"
        hashMap1["text"] = binding.edtMessage.text.toString()
        database1.child("message").push().setValue(hashMap1)

    }

    override fun onPause() {
        super.onPause()
         database2.removeEventListener(seenMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}