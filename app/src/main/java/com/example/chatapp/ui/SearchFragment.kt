package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentSearchBinding
import com.example.chatapp.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding ?= null
    private val binding get() = _binding!!
    private lateinit var listener :ValueEventListener
    private val adapter = SearchAdapter()
    private lateinit var auth :FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private  var list: MutableList<User> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}