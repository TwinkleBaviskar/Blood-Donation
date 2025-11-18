package com.example.blooddonation

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DonorAdapter(
    private val context: Context,
    private val donorList: List<DonorModel>
) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorImage: ImageView = itemView.findViewById(R.id.donorImage)
        val donorName: TextView = itemView.findViewById(R.id.donorName)
        val donorLocation: TextView = itemView.findViewById(R.id.donorLocation)
        val donorBlood: TextView = itemView.findViewById(R.id.donorBlood)
        val donorLastDonation: TextView = itemView.findViewById(R.id.donorLastDonation)
        val donorLivesSaved: TextView = itemView.findViewById(R.id.donorLivesSaved)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donor, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donorList[position]

        // 🩸 Correct Firebase mapping
        holder.donorName.text = donor.fullName ?: "Unknown"
        holder.donorLocation.text = donor.mobile ?: "No Contact"
        holder.donorBlood.text = donor.bloodGroup ?: "-"
        holder.donorLivesSaved.text = "Lives Saved: ${donor.livesSaved ?: 0}"

        // 🕒 Calculate last donation duration dynamically
        holder.donorLastDonation.text = "Last Donation: ${calculateTimeAgo(donor.lastDonation)}"

        // 👤 Profile image (placeholder if null)
        if (!donor.profileImage.isNullOrEmpty()) {
            Glide.with(context)
                .load(donor.profileImage)
                .placeholder(R.drawable.profile)
                .into(holder.donorImage)
        } else {
            holder.donorImage.setImageResource(R.drawable.profile)
        }

        // 🔗 Click → open DonorProfileActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DonorProfileActivity::class.java)
            intent.putExtra("donorData", donor)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = donorList.size

    // 🧠 Utility: Convert date to "x days/months ago"
    private fun calculateTimeAgo(dateString: String?): String {
        if (dateString.isNullOrEmpty() || dateString == "-") return "-"

        return try {
            val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val lastDate = sdf.parse(dateString)
            val diff = Date().time - (lastDate?.time ?: 0)
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            when {
                days < 1 -> "Today"
                days == 1L -> "1 day ago"
                days < 30 -> "$days days ago"
                days < 365 -> "${days / 30} months ago"
                else -> "${days / 365} years ago"
            }
        } catch (e: Exception) {
            "-"
        }
    }
}
