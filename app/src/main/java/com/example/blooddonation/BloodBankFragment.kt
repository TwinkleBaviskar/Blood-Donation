package com.example.blooddonation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.BloodBankAdapter
import com.example.blooddonation.BloodBankModel

class BloodBankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blood_bank, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewBloodBanks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val bankList = listOf(
            BloodBankModel(R.drawable.google, "Red Cross Blood Bank", "Ahmedabad", "9876543210", 4.6),
            BloodBankModel(R.drawable.profile, "LifeCare Blood Center", "Vadodara", "9823456789", 4.3),
            BloodBankModel(R.drawable.google, "SaveLife Blood Bank", "Surat", "9898123456", 4.8),
            BloodBankModel(R.drawable.profile, "Noble Blood Bank", "Rajkot", "9765432109", 4.5),
            BloodBankModel(R.drawable.google, "Hope Blood Donation Center", "Gandhinagar", "9900123456", 4.4),
            BloodBankModel(R.drawable.google, "Humanity Blood Bank", "Bhavnagar", "9912345678", 4.2)
        )

        recyclerView.adapter = BloodBankAdapter(bankList)
        return view
    }
}
