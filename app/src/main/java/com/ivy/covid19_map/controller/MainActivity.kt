package com.ivy.covid19_map.controller

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.dataClass.CenterData
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
    /* 뷰 바인딩 */
    private lateinit var binding: ActivityMainBinding

    /* room 데이터 접근 관련 레포지토리 */
    @Inject
    lateinit var centerRepository: CenterRepository

    /* 마커와 센터 객체를 매핑하여 저장 */
    private val markerCenterMap = mutableMapOf<Marker, CenterData>()

    /* 네이버 맵 객체 */
    private lateinit var naverMap: NaverMap

    /* 마커 클릭 시 이벤트 처리 지정 */
    private lateinit var markerOnClickListener: Overlay.OnClickListener

    /* 타입 별 색 구분을 위한 맵 */
    private val centerTypeMap = mapOf("중앙/권역" to 1, "지역" to 2)

    /* 현재 사용자가 선택한 마커 객체 */
    private var nowShowingMarker: Marker? = null

    /* 마커 선택 시 센터의 정보를 표시하는 DialogFragment */
    private lateinit var customDialog: CenterInfoDialog

    /* 사용자의 현재 위치를 제공하는 객체 */
    private lateinit var locationSource: FusedLocationSource

    companion object{
        /* 사용자 위치 정보 허용 request code */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customDialog = CenterInfoDialog(this)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(binding.mapFragmentContainer.id) as MapFragment?
            ?: newInstance().also {
                fm.beginTransaction().add(binding.mapFragmentContainer.id, it).commit()
            }.getMapAsync(this)


        /* 마커 선택 시 동작 설정 */
        markerOnClickListener = Overlay.OnClickListener { overlay ->
            val clickedMarker = overlay as Marker
            val clickedCenter = markerCenterMap[clickedMarker]!!

            updateNowShowingMarker(clickedMarker, clickedCenter)

            true
        }


        /* 사용자 위치 정보 가져오기 */
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)


    }

    /* 사용자 위치 정보 사용 권한 설정 */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /* 마커 선택 시 카메라 이동 */
    fun moveCamera(lat: Double, lng: Double){
        val cameraUpdate = CameraUpdate
            .scrollTo(LatLng(lat, lng))
            .animate(CameraAnimation.Easing, 1000)
        naverMap.moveCamera(cameraUpdate)
    }

    /* 마커 선택 시 동작 설정 */
    private fun updateNowShowingMarker(clickedMarker: Marker, clickedCenter: CenterData){

        // 선택한 마커 재선택
        if (nowShowingMarker == clickedMarker){
            nowShowingMarker = null
            clickedMarker.captionText = ""
            return
        }
        // 선택한 상태에서 다른 마커 선택
        else if(nowShowingMarker != null){
            nowShowingMarker!!.captionText = ""
        }
        
        // 갱신 작업
        nowShowingMarker = clickedMarker
        clickedMarker.captionText = clickedCenter.centerName
        moveCamera(clickedCenter.lat.toDouble(), clickedCenter.lng.toDouble())
        customDialog.centerData = clickedCenter
        
        // dialog 띄우기
        customDialog.show()
    }

    /* room을 통해 저장된 센터 데이터 가져오기 */
    private suspend fun getCenterFromDB() = flow {
        centerRepository.selectAll().forEach { emit(it) }
    }


    /* 네이버 지도 객체가 준비되었을 때 실행되는 callback */
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        /* 마커 객체가 아닌 곳을 클릭하면 초기화 */
        naverMap.setOnMapClickListener { point, coord ->
            nowShowingMarker?.captionText = ""
            nowShowingMarker = null
        }

        /* fab버튼 클릭 시 카메라가 사용자 현재 위치로 이동 */
        binding.moveToCurrentLocationFab.setOnClickListener {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }


        GlobalScope.launch {

            getCenterFromDB().collect {
                /* 마커 생성 */
                val marker = Marker()
                marker.position = LatLng(it.lat.toDouble(), it.lng.toDouble())
                marker.onClickListener = markerOnClickListener
                marker.icon = MarkerIcons.BLACK
                marker.captionRequestedWidth = 200

                /* 타입에 따라 마커 색상 구분 */
                val type = centerTypeMap[it.centerType]
                if ( type == 1) marker.iconTintColor = Color.GREEN
                else if(type == 2) marker.iconTintColor = Color.YELLOW

                /* 마커 객체와 센터 데이터를 매핑 */
                markerCenterMap[marker] = it
            }

            /* 생성한 마커를 지도에 띄우기 */
            runOnUiThread {
                markerCenterMap.keys.forEach { it.map = naverMap }
            }

        }

    }


}