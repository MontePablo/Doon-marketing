package com.example.doonmarketing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doonmarketing.databinding.CustomviewVisitBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class VisitAdapter(options:FirestoreRecyclerOptions<VisitModel>, val listener:VisitFunctionListener):
    FirestoreRecyclerAdapter<VisitModel, VisitAdapter.ViewHolder>(options) {

    inner class ViewHolder(val binding: CustomviewVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(position: Int, model: VisitModel) {
            binding.apply {
                place.text=model.place
                time.text=model.time
                remarks.text=model.remarks
                deleteBtn.setOnClickListener { listener.delete(model) }
                editBtn.setOnClickListener { listener.edit(model) }
                mapBtn.setOnClickListener { listener.gotoMap(model) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CustomviewVisitBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: VisitModel) {
        holder.bindItem(position,model)
    }
}

