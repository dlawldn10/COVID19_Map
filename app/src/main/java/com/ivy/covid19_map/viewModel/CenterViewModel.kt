package com.ivy.covid19_map.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivy.covid19_map.CenterData

class CenterViewModel : ViewModel() {

    var address = MutableLiveData<String>()
    var centerName = MutableLiveData<String>()
    var facilityName = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()
    var updatedAt = MutableLiveData<String>()

    fun setCenterData(centerData: CenterData){
        address.value = centerData.address
        centerName.value = centerData.centerName
        facilityName.value = centerData.facilityName
        phoneNumber.value = centerData.phoneNumber
        updatedAt.value = centerData.updatedAt
    }

}
