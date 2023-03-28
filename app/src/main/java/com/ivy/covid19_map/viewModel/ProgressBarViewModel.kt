package com.ivy.covid19_map.viewModel

import androidx.lifecycle.MutableLiveData

class ProgressBarViewModel {

    var barProgress = MutableLiveData<Int>()
    var progressTextView = MutableLiveData<String>()

    fun setProgressView(progress: Double){
        barProgress.value = progress.toInt()
        progressTextView.value = if (progress >= 100) "100" else progress.toInt().toString()
    }
}