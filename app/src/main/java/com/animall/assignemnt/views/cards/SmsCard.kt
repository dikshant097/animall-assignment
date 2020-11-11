package com.animall.assignemnt.views.cards

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.animall.assignemnt.R
import com.animall.assignemnt.databinding.SmsCardBinding
import com.animall.assignemnt.model.SMS

class SmsCard(val binding: SmsCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setUI(sms: SMS, isNew: Boolean) {
        binding.sms = sms

        if(isNew) {
            itemView.background = itemView.context.resources.getDrawable(R.drawable.ic_launcher_background)
        } else {
            itemView.background = null
        }
    }
}