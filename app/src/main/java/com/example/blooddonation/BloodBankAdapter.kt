package com.example.blooddonation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BloodBankAdapter(private val bankList: List<BloodBankModel>) :
    RecyclerView.Adapter<BloodBankAdapter.BloodBankViewHolder>() {

    class BloodBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankImage: ImageView = itemView.findViewById(R.id.bankImage)
        val bankName: TextView = itemView.findViewById(R.id.bankName)
        val bankLocation: TextView = itemView.findViewById(R.id.bankLocation)
        val bankContact: TextView = itemView.findViewById(R.id.bankContact)
        val bankRating: TextView = itemView.findViewById(R.id.bankRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodBankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blood_bank, parent, false)
        return BloodBankViewHolder(view)
    }

    override fun onBindViewHolder(holder: BloodBankViewHolder, position: Int) {
        val bank = bankList[position]
        holder.bankImage.setImageResource(bank.imageResId)
        holder.bankName.text = bank.name
        holder.bankLocation.text = bank.location
        holder.bankContact.text = bank.contact
        holder.bankRating.text = bank.rating.toString()
    }

    override fun getItemCount(): Int = bankList.size
}
