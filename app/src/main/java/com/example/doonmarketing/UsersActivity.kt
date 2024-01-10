package com.example.doonmarketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doonmarketing.databinding.ActivityUsersBinding
import com.example.kaamwaale.daos.FirebaseDao
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class UsersActivity : AppCompatActivity(),UserFunctionListener {
    lateinit var mBinding:ActivityUsersBinding
    lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityUsersBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val query: Query = FirebaseDao.userCollection
        val options: FirestoreRecyclerOptions<UserModel> = FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel::class.java).build()
        adapter= UserAdapter(options,this)

        mBinding.apply {
            recyclerView.adapter=adapter
            recyclerView.layoutManager= LinearLayoutManager(this@UsersActivity)
            logoutBtn.setOnClickListener { FirebaseDao.logout(this@UsersActivity);finishAffinity() }
        }

    }

    override fun onUserClick(user: UserModel) {
        startActivity(Intent(this, VisitsActivity::class.java)
            .putExtra("id",user.uid))
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}
 interface UserFunctionListener{
     fun onUserClick(user:UserModel)
 }
