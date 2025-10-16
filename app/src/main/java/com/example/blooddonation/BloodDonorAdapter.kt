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
import java.util.concurrent.TimeUnit

class BloodDonorAdapter(
    private val context: Context,
    private var donorList: List<BloodDonorModel>
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

        val diff = System.currentTimeMillis() - donor.timestamp
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val isExpired = hours > 12 || donor.requestStatus == "expired"

        if (isExpired) {
            holder.donateButton.isEnabled = false
            holder.donateButton.text = "Completed"
            holder.donateButton.setBackgroundColor(Color.parseColor("#BDBDBD")) // grey
            holder.donateButton.setOnClickListener {
                Toast.makeText(context, "❌ Donor ko blood mil gaya hai", Toast.LENGTH_SHORT).show()
            }
        } else {
            holder.donateButton.isEnabled = true
            holder.donateButton.text = "Donate"
            holder.donateButton.setBackgroundColor(Color.parseColor("#F44336")) // red

            holder.donateButton.setOnClickListener {
                showContactDialog(donor)
            }
        }
    }

    // ✅ Updated function (fixed + fully working)
    private fun showContactDialog(donor: BloodDonorModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Contact ${donor.name}")
        builder.setMessage("Choose an option to contact:")

        builder.setPositiveButton("📞 Call") { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${donor.phone}")
            context.startActivity(intent)
        }

        builder.setNegativeButton("💬 Message") { _, _ ->
            // ✅ Open message fragment using HomeScreenActivity function
            Toast.makeText(context, "Opening chat with ${donor.name}", Toast.LENGTH_SHORT).show()
            if (context is com.example.blooddonation.HomeScreenActivity) {
                (context as com.example.blooddonation.HomeScreenActivity)
                    .openMessageFragment(donor.name)
            } else {
                Toast.makeText(context, "Unable to open chat screen", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun getItemCount(): Int = donorList.size

    fun updateList(newList: List<BloodDonorModel>) {
        donorList = newList
        notifyDataSetChanged()
    }
}
