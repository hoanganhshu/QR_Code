package com.example.qrscan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qrscan.databinding.BottomSelectItemsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSelectItems(private val onSelect: (Int) ->Unit ) : BottomSheetDialogFragment(){

    private  lateinit var binding : BottomSelectItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSelectItemsBinding.inflate(layoutInflater,container,false)

        binding.delete.setOnClickListener {
            onSelect(1)

        }
        binding.download.setOnClickListener {
            onSelect(2)

        }








        return binding.root
    }
}