package com.animall.assignemnt.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animall.assignemnt.databinding.SmsCardBinding
import com.animall.assignemnt.model.SMS
import com.animall.assignemnt.views.cards.SmsCard

class SmsAdapter(val activity : Activity, var senderId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var smsList : ArrayList<SMS> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = SmsCardBinding.inflate(LayoutInflater.from(activity))
        return SmsCard(binding)
    }

    override fun getItemCount(): Int {
        return smsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SmsCard) {
            val isNew = position == 0 && !senderId.isNullOrEmpty()
            holder.setUI(smsList.get(position), isNew)
        }
    }

    fun addSms(smsList : ArrayList<SMS>) {
        this.smsList.addAll(smsList)
        notifyDataSetChanged()
    }

}