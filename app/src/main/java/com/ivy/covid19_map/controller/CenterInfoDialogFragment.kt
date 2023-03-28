package com.ivy.covid19_map.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ivy.covid19_map.CenterData
import com.ivy.covid19_map.viewModel.CenterViewModel
import com.ivy.covid19_map.databinding.DialogCenterInfoBinding

class CenterInfoDialogFragment: DialogFragment() {
    var centerData: CenterData? = null
    private val centerViewModel : CenterViewModel = CenterViewModel()
    private lateinit var fragmentBinding: DialogCenterInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = DialogCenterInfoBinding.inflate(inflater, container, false)
        fragmentBinding.viewModel = centerViewModel
        fragmentBinding.lifecycleOwner = this
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        centerData?.let {
            centerViewModel.setCenterData(it)
        }

    }
}