package com.ivy.covid19_map.controller

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.R
import com.ivy.covid19_map.dataClass.CenterData
import com.ivy.covid19_map.databinding.DialogCenterInfoBinding
import com.ivy.covid19_map.viewModel.CenterViewModel

class CenterInfoDialog(private val activity: AppCompatActivity) {

    /* 커스텀 할 dialog */
    private val dialog = Dialog(activity)

    /* 사용자가 선택한 센터의 데이터 */
    var centerData : CenterData? = null

    /* 뷰 모델 설정 */
    private val centerViewModel : CenterViewModel = CenterViewModel()

    init {
        /* 뷰 바인딩 */
        var dialogBinding = DialogCenterInfoBinding.inflate(activity.layoutInflater)
        /* 뷰 모델 설정 */
        dialogBinding.viewModel = centerViewModel
        dialogBinding.lifecycleOwner = activity
        dialog.setContentView(dialogBinding.root)

        /* 다이얼로그 바깥 부분 클릭 시 cancel */
        dialog.setCancelable(true)

        /* 배경 투명 설정*/
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    fun show(){
        /* 센터 데이터에 따라 뷰 갱신 */
        centerData?.let {
            centerViewModel.setCenterData(it)
        }

        dialog.show()
    }
}