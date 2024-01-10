package com.example.kaamwaale.daos

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.doonmarketing.LoginActivity
import com.example.doonmarketing.MyPreferences
import com.example.doonmarketing.UserModel
import com.example.doonmarketing.VisitModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseDao {
    val db = Firebase.firestore
    val auth: FirebaseAuth = Firebase.auth
    val userCollection=db.collection("users")
    val visitCollection=db.collection("visits")


    fun addVisit(visit: VisitModel): Task<Void> {
        var v= visitCollection.document().set(visit)
        Log.d("TAGG", "add gig:success")
        return v
    }
    fun getVisit(id:String): Task<DocumentSnapshot> {
        return visitCollection.document(id).get()
    }
    fun addUser(user: UserModel): Task<Void> {
        var v= userCollection.document(user.uid).set(user)
        Log.d("TAGG", "add user:success")
        v.addOnSuccessListener { saveUserInLocal(user) }
        return v
    }
    fun getUser(userId:String): Task<DocumentSnapshot> {
        return userCollection.document(userId).get()
    }
    fun saveUserInLocal(user: UserModel){
        MyPreferences.storeUser(user)
    }
    fun logout(context: Context){
        FirebaseDao.auth.signOut()
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
}