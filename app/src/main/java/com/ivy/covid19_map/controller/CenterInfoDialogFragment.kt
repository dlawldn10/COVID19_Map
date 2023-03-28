package com.ivy.covid19_map.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ivy.covid19_map.dataClass.CenterData
import com.ivy.covid19_map.viewModel.CenterViewModel
import com.ivy.covid19_map.databinding.DialogCenterInfoBinding

class CenterInfoDialogFragment: DialogFragment() {
    /* 사용자가 선택한 센터의 데이터 */
    var centerData: CenterData? = null

    /* 뷰 모델 설정 */
    private val centerViewModel : CenterViewModel = CenterViewModel()

    /* 뷰 바인딩 */
    private lateinit var fragmentBinding: DialogCenterInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* 다이얼로그 바깥 부분 클릭 시 cancel */
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* 뷰 모델 설정 */
        fragmentBinding = DialogCenterInfoBinding.inflate(inflater, container, false)
        fragmentBinding.viewModel = centerViewModel
        fragmentBinding.lifecycleOwner = this
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* 센터 데이터에 따라 뷰 갱신 */
        centerData?.let {
            centerViewModel.setCenterData(it)
        }

    }
}