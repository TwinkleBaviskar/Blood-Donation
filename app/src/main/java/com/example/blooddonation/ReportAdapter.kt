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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(
    private var reportList: ArrayList<ReportModel>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val inputFormats = listOf(
        SimpleDateFormat("d/M/yyyy", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    )

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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        holder.tvName.text = "👤 ${report.name ?: "-"}"
        holder.tvBloodGroup.text = "🩸 Blood Group: ${report.bloodGroup ?: "-"}"
        holder.tvWeight.text = "⚖️ Weight: ${report.weight ?: "-"}"
        holder.tvLastDonation.text = "🗓️ Last Donation: ${formatDisplayDate(report.lastDonation)}"
        holder.tvHemoglobin.text = "💉 Hemoglobin: ${report.hemoglobin ?: "-"}"
        setupEligibilityAndNextDate(holder, report.lastDonation)
    }

    private fun setupEligibilityAndNextDate(holder: ReportViewHolder, lastDonationStr: String?) {
        if (lastDonationStr.isNullOrEmpty()) {
            holder.tvNextDonation.text = "📆 Next Eligible: -"
            holder.tvEligibility.text = "❓ Not enough data"
            holder.tvEligibility.setTextColor(Color.parseColor("#616161"))
            holder.cardReport.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            return
        }

        val lastDonationDate = parseFlexibleDate(lastDonationStr.trim())
        if (lastDonationDate == null) {
            holder.tvNextDonation.text = "📆 Next Eligible: -"
            holder.tvEligibility.text = "❓ Invalid date"
            holder.tvEligibility.setTextColor(Color.parseColor("#616161"))
            holder.cardReport.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            return
        }

        val cal = Calendar.getInstance()
        cal.time = lastDonationDate
        cal.add(Calendar.MONTH, 3)
        val nextEligibleDate = cal.time
        val today = Date()
        val isEligible = !nextEligibleDate.after(today)
        holder.tvNextDonation.text = "📆 Next Eligible: ${displayFormat.format(nextEligibleDate)}"

        if (isEligible) {
            holder.tvEligibility.text = "✅ Eligible to Donate"
            holder.tvEligibility.setTextColor(Color.parseColor("#2E7D32"))
            holder.cardReport.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.tvEligibility.text = "❌ Not Eligible"
            holder.tvEligibility.setTextColor(Color.parseColor("#C62828"))
            holder.cardReport.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
        }
    }

    private fun parseFlexibleDate(dateStr: String): Date? {
        for (format in inputFormats) {
            try {
                return format.parse(dateStr)
            } catch (_: ParseException) {
            }
        }
        return null
    }

    private fun formatDisplayDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return "-"

        val parsed = parseFlexibleDate(dateStr.trim()) ?: return dateStr
        return displayFormat.format(parsed)
    }

    override fun getItemCount(): Int = reportList.size

    fun filterList(filteredList: ArrayList<ReportModel>) {
        reportList = filteredList
        notifyDataSetChanged()
    }
}
