package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.databinding.FragmentLoginBinding
import com.example.chatapp.user.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Transaction
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class LoginFragment : AppCompatActivity() {

    private lateinit var binding  :FragmentLoginBinding
    private lateinit var database :DatabaseReference
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
           val intent  = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
            listFragment()


    }

    private fun listFragment() {
        val user = auth.currentUser
       Handler().postDelayed({
           if (user != null){
               val intent = Intent(this,ListFragment::class.java)
               startActivity(intent)

           }
       },2000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task =  GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {

                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebase(account.idToken!!)

                } catch (e: Exception) {
                    Log.d(TAG, "Google sign in failed", e)
                }
            }else{
                Toast.makeText(this,exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun firebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser!!
                    val mUser = User(user.uid,user.displayName,user.photoUrl.toString(),"online")
                    database = FirebaseDatabase.getInstance().getReference("User")
                    database.child(user.uid).get().addOnSuccessListener {
                        if (it.exists()){
                            val intent = Intent(this,ListFragment::class.java)
                            startActivity(intent)
                        }
                        else{
                            database.child("info").setValue(mUser)
                            val intent = Intent(this,ListFragment::class.java)
                            startActivity(intent)
                        }
                    }
////                    database.child(user.uid).setValue(mUser)
//                    val intent = Intent(this,ListFragment::class.java)
//                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }

}