package com.example.qrscan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<VB : ViewBinding> : Fragment(){
    abstract fun inflate(layoutInflater : LayoutInflater,container: ViewGroup?) : VB
   lateinit var mBinding: VB



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = inflate(inflater, container)
        return mBinding.root

    }




}
