package com.ivy.covid19_map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ivy.covid19_map.CenterData
import com.ivy.covid19_map.CenterRepository
import kotlinx.coroutines.launch

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
