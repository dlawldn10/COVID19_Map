package com.ivy.covid19_map.controller

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.CenterData
import com.ivy.covid19_map.repository.CenterRepository
import com.ivy.covid19_map.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment.newInstance
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var centerRepository: CenterRepository

    private val markerCenterMap = mutableMapOf<Marker, CenterData>()
    private lateinit var naverMap: NaverMap

    private lateinit var markerOnClickListener: Overlay.OnClickListener

    private val centerTypeMap = mapOf("중앙/권역" to 1, "지역" to 2)

    private var nowShowingMarker: Marker? = null

    private val customDialog = CenterInfoDialogFragment()

    private lateinit var locationSource: FusedLocationSource

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(binding.mapFragmentContainer.id) as MapFragment?
            ?: newInstance().also {
                fm.beginTransaction().add(binding.mapFragmentContainer.id, it).commit()
            }.getMapAsync(this)


        markerOnClickListener = Overlay.OnClickListener { overlay ->
            val clickedMarker = overlay as Marker
            val clickedCenter = markerCenterMap[clickedMarker]!!

            updateNowShowingMarker(clickedMarker, clickedCenter)


            true
        }


        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /* 카메라 이동 */
    fun moveCamera(lat: Double, lng: Double){
        val cameraUpdate = CameraUpdate
            .scrollTo(LatLng(lat, lng))
            .animate(CameraAnimation.Easing, 1000)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun updateNowShowingMarker(clickedMarker: Marker, clickedCenter: CenterData){

        // 선택한 마커 재선택
        if (nowShowingMarker == clickedMarker){
            nowShowingMarker = null
            clickedMarker.captionText = ""
        }
        // 선택한 상태에서 다른 마커 선택
        else if(nowShowingMarker != null){
            nowShowingMarker!!.captionText = ""
        }
        // 미선택 상태에서 마커 선택
        
        // 갱신 작업
        nowShowingMarker = clickedMarker
        clickedMarker.captionText = clickedCenter.centerName
        moveCamera(clickedCenter.lat.toDouble(), clickedCenter.lng.toDouble())
        customDialog.centerData = clickedCenter
        
        // dialog 띄우기
        customDialog.show(supportFragmentManager, "CenterInfoDialog")
    }

    private suspend fun getCenterFromDB() = flow {
        centerRepository.selectAll().forEach { emit(it) }
    }


    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource


        naverMap.setOnMapClickListener { point, coord ->
            nowShowingMarker?.captionText = ""
            nowShowingMarker = null
        }

        // fab버튼 클릭 시 사용자 현재 위치로 이동
        binding.moveToCurrentLocationFab.setOnClickListener {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }

        // 1->5->2->3->4
        //println("1=========")
        GlobalScope.launch {
            //println("2=========")
            getCenterFromDB().collect {
                val marker = Marker()
                marker.position = LatLng(it.lat.toDouble(), it.lng.toDouble())
                marker.onClickListener = markerOnClickListener
                marker.icon = MarkerIcons.BLACK
                marker.captionRequestedWidth = 200

                val type = centerTypeMap[it.centerType]
                if ( type == 1) marker.iconTintColor = Color.GREEN
                else if(type == 2) marker.iconTintColor = Color.YELLOW

                markerCenterMap[marker] = it
                println(it)
            }
            //println("3=========")
            runOnUiThread {
                markerCenterMap.keys.forEach { it.map = naverMap }
            }
            //println("4=========")

        }
        //println("5=========")
    }


}