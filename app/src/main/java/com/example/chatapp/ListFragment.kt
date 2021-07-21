package com.example.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.databinding.FragmentListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ListFragment : AppCompatActivity() {
    private lateinit var binding : FragmentListBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide();
        // Inflate the layout for this fragment
        val bottomNavigationView = binding.bottomMenu

        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)
        auth = FirebaseAuth.getInstance()

    }

    private fun status(status :String){
        val user = auth.currentUser!!
        val database = FirebaseDatabase.getInstance().getReference("User").child(user.uid).child("info")

        val hasmap  : HashMap<String,String> = HashMap()
        hasmap["status"] = status
        database.updateChildren(hasmap as Map<String, Any>)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()

        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        status(currentDate)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return onSupportNavigateUp()|| navController.navigateUp()
    }


}