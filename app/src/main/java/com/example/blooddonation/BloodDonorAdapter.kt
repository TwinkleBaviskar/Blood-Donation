package com.example.blooddonation.adapter

import android.app.AlertDialog
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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.BloodDonorModel

class BloodDonorAdapter(
    private val context: Context,
    private var donorList: List<BloodDonorModel>,
    private val onDonateClick: (String) -> Unit
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

        // Button behavior
        if (donor.requestStatus.equals("completed", ignoreCase = true)) {
            holder.donateButton.text = "Completed"
            holder.donateButton.isEnabled = false
            holder.donateButton.setBackgroundColor(Color.parseColor("#BDBDBD"))
            holder.donateButton.setOnClickListener(null)
        } else {
            holder.donateButton.text = "Donate"
            holder.donateButton.isEnabled = true
            holder.donateButton.setBackgroundColor(Color.parseColor("#F44336"))

            holder.donateButton.setOnClickListener {
                showContactDialog(donor)
            }
        }
    }

    private fun showContactDialog(donor: BloodDonorModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Contact ${donor.name}")
        builder.setMessage("Choose how you want to contact:")

        // Call
        builder.setPositiveButton("📞 Call") { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
            context.startActivity(intent)
            // invoke callback to mark completed (as you used previously)
            onDonateClick(donor.requestId)
        }

        // Message
        builder.setNegativeButton("💬 Message") { _, _ ->
            Toast.makeText(context, "Opening chat with ${donor.name}", Toast.LENGTH_SHORT).show()
            onDonateClick(donor.requestId)
        }

        // Cancel
        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun getItemCount(): Int = donorList.size

    // Now updateList simply replaces list (no forced sorting here)
    fun updateList(newList: List<BloodDonorModel>) {
        donorList = newList
        notifyDataSetChanged()
    }
}
