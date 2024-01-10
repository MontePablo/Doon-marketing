package com.example.doonmarketing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doonmarketing.databinding.CustomviewUserBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class UserAdapter(options:FirestoreRecyclerOptions<UserModel>,val listener:UserFunctionListener):
    FirestoreRecyclerAdapter<UserModel, UserAdapter.ViewHolder>(options) {

    inner class ViewHolder(val binding: CustomviewUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(position: Int, model: UserModel) {
            binding.apply {
                name.text=model.name
                email.text=model.email
                rootView.setOnClickListener { listener.onUserClick(model) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CustomviewUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserModel) {
        holder.bindItem(position,model)
    }
}

