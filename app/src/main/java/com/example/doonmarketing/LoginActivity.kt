package com.example.doonmarketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.doonmarketing.databinding.ActivityLoginBinding
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.FirebaseDao.auth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    lateinit var mBinding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.apply {
            loginBtn.setOnClickListener {
                if(!email.text!!.isEmpty() || !password.text!!.isEmpty()){
                    loginBtn.visibility= View.GONE
                    signIn(email.text.toString(),password.text.toString())
                }
            }
        }
    }
    private fun signIn(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAGG", "signInWithEmail:success")
                    updateUI(auth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAGG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                    mBinding.loginBtn.visibility= View.VISIBLE
                }
            }.addOnFailureListener {
                mBinding.loginBtn.visibility= View.VISIBLE
                Log.d("TAGG",it.localizedMessage)
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }
    }
    fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null){
            if (firebaseUser.email!!.contains("admin"))
                startActivity(Intent(this,UsersActivity::class.java))
            else{

                FirebaseDao.getUser(firebaseUser!!.uid).addOnSuccessListener { document->
                    val user=UserModel(uid = firebaseUser.uid, name = firebaseUser.displayName?:"" , email = firebaseUser.email?:"")
                    if(document.exists()){
                        Toast.makeText(this,"welcome Back!",Toast.LENGTH_SHORT).show()
                    }else{
                        FirebaseDao.addUser(user)
                    }
                    MyPreferences.storeUser(user)

                    startActivity(Intent(this, VisitsActivity::class.java).putExtra("id",firebaseUser.uid))
                }.addOnFailureListener { exception-> Log.d("TAGG","updateUI:onFailure:"+exception.localizedMessage) }
            }
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null){
            if(MyPreferences.fetchUser()!!.email.contains("admin"))
                startActivity(Intent(this, UsersActivity::class.java))
            else
                startActivity(Intent(this, VisitsActivity::class.java))
            finishAffinity()
        }
    }
}