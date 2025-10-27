package com.example.blooddonation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.ReportModel

class ReportAdapter(
    private var reportList: ArrayList<ReportModel>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardReport: CardView = view.findViewById(R.id.cardReport)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvBloodGroup: TextView = view.findViewById(R.id.tvBloodGroup)
        val tvWeight: TextView = view.findViewById(R.id.tvWeight)
        val tvLastDonation: TextView = view.findViewById(R.id.tvLastDonation)
        val tvNextDonation: TextView = view.findViewById(R.id.tvNextDonation)
        val tvHemoglobin: TextView = view.findViewById(R.id.tvHemoglobin)
        val tvEligibility: TextView = view.findViewById(R.id.tvEligibility)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        holder.tvName.text = "👤 ${report.name}"
        holder.tvBloodGroup.text = "🩸 Blood Group: ${report.bloodGroup}"
        holder.tvWeight.text = "⚖️ Weight: ${report.weight}"
        holder.tvLastDonation.text = "🗓️ Last Donation: ${report.lastDonation}"
        holder.tvNextDonation.text = "📆 Next Eligible: ${report.nextDonation}"
        holder.tvHemoglobin.text = "💉 Hemoglobin: ${report.hemoglobin}"

        if (report.eligibility == "Eligible") {
            holder.tvEligibility.text = "✅ Eligible to Donate"
            holder.tvEligibility.setTextColor(Color.parseColor("#2E7D32"))
            holder.cardReport.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.tvEligibility.text = "❌ Not Eligible"
            holder.tvEligibility.setTextColor(Color.parseColor("#C62828"))
            holder.cardReport.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
        }
    }

    override fun getItemCount(): Int = reportList.size

    fun filterList(filteredList: ArrayList<ReportModel>) {
        reportList = filteredList
        notifyDataSetChanged()
    }
}
