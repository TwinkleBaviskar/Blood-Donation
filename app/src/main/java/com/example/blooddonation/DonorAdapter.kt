package com.example.blooddonation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonorAdapter(private val donorList: List<DonorModel>) :
    RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorImage: ImageView = itemView.findViewById(R.id.donorImage)
        val donorName: TextView = itemView.findViewById(R.id.donorName)
        val donorLocation: TextView = itemView.findViewById(R.id.donorLocation)
        val donorBlood: TextView = itemView.findViewById(R.id.donorBlood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donor, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donorList[position]
        holder.donorImage.setImageResource(donor.imageRes)
        holder.donorName.text = donor.name
        holder.donorLocation.text = donor.location
        holder.donorBlood.text = donor.bloodGroup
    }

    override fun getItemCount() = donorList.size
}
