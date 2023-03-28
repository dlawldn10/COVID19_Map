package com.ivy.covid19_map.viewModel

import androidx.lifecycle.MutableLiveData

/* 프록래스바의 진행률을 설정하는 뷰 모델 */
class ProgressBarViewModel {

    /* 프로그래스바의 progress */
    var barProgress = MutableLiveData<Int>()
    /* 프로그래스바 상단 진행률 텍스트 */
    var progressTextView = MutableLiveData<String>()

    /* UI 업데이트 */
    fun setProgressView(progress: Double){
        barProgress.value = progress.toInt()
        progressTextView.value = if (progress >= 100) "100" else progress.toInt().toString()
    }
}