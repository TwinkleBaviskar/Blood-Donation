package com.example.blooddonation.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.BloodDonorModel
import java.util.concurrent.TimeUnit

class BloodDonorAdapter(
    private val context: Context,
    private var donorList: List<BloodDonorModel>,
    private val onDonateClick: (BloodDonorModel) -> Unit
) : RecyclerView.Adapter<BloodDonorAdapter.DonorViewHolder>() {

    inner class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtDonorName)
        val bloodGroup: TextView = itemView.findViewById(R.id.txtBloodGroup)
        val location: TextView = itemView.findViewById(R.id.txtLocation)
        val timeAgo: TextView = itemView.findViewById(R.id.txtTimeAgo)
        val donateButton: Button = itemView.findViewById(R.id.btnDonate)
        val card: CardView = itemView.findViewById(R.id.cardDonor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blood_donor, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donorList[position]

        holder.name.text = donor.name
        holder.bloodGroup.text = donor.bloodGroup
        holder.location.text = donor.location
        holder.timeAgo.text = donor.timeAgo

        // Urgent = light-red background
        holder.card.setCardBackgroundColor(
            if (donor.isUrgent) Color.parseColor("#FFF1F1") else Color.WHITE
        )

        // ⏰ 12 hours expiry check
        val now = System.currentTimeMillis()
        val isExpired = donor.timestamp <= 0L ||
                (now - donor.timestamp) > TimeUnit.HOURS.toMillis(12)

        when {
            donor.requestStatus.equals("completed", ignoreCase = true) -> {
                holder.donateButton.text = "Completed"
                holder.donateButton.isEnabled = false
                holder.donateButton.setBackgroundColor(Color.parseColor("#BDBDBD"))
                holder.donateButton.alpha = 0.7f
                holder.donateButton.setOnClickListener(null)
            }

            isExpired -> {
                holder.donateButton.text = "Expired"
                holder.donateButton.isEnabled = false
                holder.donateButton.setBackgroundColor(Color.parseColor("#BDBDBD"))
                holder.donateButton.alpha = 0.7f
                holder.donateButton.setOnClickListener {
                    Toast.makeText(context, "This request has expired", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                holder.donateButton.text = "Donate"
                holder.donateButton.isEnabled = true
                holder.donateButton.setBackgroundColor(Color.parseColor("#F44336"))
                holder.donateButton.alpha = 1f

                holder.donateButton.setOnClickListener {
                    showContactDialog(donor)
                }
            }
        }
    }

    override fun getItemCount(): Int = donorList.size

    fun updateList(newList: List<BloodDonorModel>) {
        donorList = newList
        notifyDataSetChanged()
    }

    // ================== DIALOG ==================

    private fun showContactDialog(donor: BloodDonorModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Contact ${donor.name}")
        builder.setMessage("Choose how you want to contact:")

        // LEFT: CALL  -> NEUTRAL
        builder.setNeutralButton("CALL") { _, _ ->
            val phone = donor.phone
            if (phone.isNotBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                context.startActivity(intent)
                // agar chaho to yahi pe mark complete wala logic use kar sakti ho
                onDonateClick(donor)
            } else {
                Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show()
            }
        }

        // RIGHT: CANCEL -> POSITIVE
        builder.setPositiveButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}
