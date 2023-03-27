package com.ivy.covid19_map

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment.newInstance
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.Overlay.OnClickListener
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var centerRepository: CenterRepository

    val markerCenterMap = mutableMapOf<Marker, CenterData>()
    private lateinit var naverMap: NaverMap

    lateinit var markerOnClickListener: Overlay.OnClickListener

    private val centerTypeMap = mapOf(
        "중앙/권역" to 1, "지역" to 2
    )

    var nowShowingMarker: Marker? = null

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

            // 선택한 마커 재선택
            if (nowShowingMarker == clickedMarker){
                nowShowingMarker = null
                clickedMarker.captionText = ""
            }
            // 선택한 상태에서 다른 마커 선택
            else if(nowShowingMarker != null){
                nowShowingMarker!!.captionText = ""
                nowShowingMarker = clickedMarker
                clickedMarker.captionText = clickedCenter.centerName
                moveCamera(clickedCenter.lat.toDouble(), clickedCenter.lng.toDouble())
            }
            // 미선택 상태에서 마커 선택
            else if (nowShowingMarker == null){
                nowShowingMarker = clickedMarker
                clickedMarker.captionText = clickedCenter.centerName
                moveCamera(clickedCenter.lat.toDouble(), clickedCenter.lng.toDouble())
            }

            true
        }


    }

    /* 카메라 이동 */
    fun moveCamera(lat: Double, lng: Double){
        val cameraUpdate = CameraUpdate
            .scrollTo(LatLng(lat, lng))
            .animate(CameraAnimation.Easing, 1000)
        naverMap.moveCamera(cameraUpdate)
    }

    private suspend fun getCenterFromDB() = flow {
        centerRepository.selectAll().forEach { emit(it) }
    }


    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.setOnMapClickListener { point, coord ->
            nowShowingMarker?.captionText = ""
            nowShowingMarker = null
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