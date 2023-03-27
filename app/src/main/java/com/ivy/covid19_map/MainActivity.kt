package com.ivy.covid19_map

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapFragment.newInstance
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
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

    private val markerList = arrayListOf<Marker>()
    private val centerList = arrayListOf<CenterData>()
    val markerCenterMap = mutableMapOf<Marker, CenterData>()
    private lateinit var naverMap: NaverMap

    lateinit var markerOnClickListener: Overlay.OnClickListener

    private val centerTypeMap = mapOf(
        "중앙/권역" to 1, "지역" to 2
    )

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

            // 안눌려진 상태
            if (clickedMarker.captionText.isEmpty()){
                // 정보 보이기
                clickedMarker.captionText = clickedCenter.centerName
                Toast.makeText(this, clickedCenter.toString(), Toast.LENGTH_SHORT).show()

                // 카메라 이동
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(clickedCenter.lat.toDouble(), clickedCenter.lng.toDouble()))
                naverMap.moveCamera(cameraUpdate)
            }
            // 눌려진 상태
            else {
                // 정보 숨기기
                clickedMarker.captionText = ""
            }

            true
        }


    }

    private suspend fun getCenterFromDB() = flow {
        centerRepository.selectAll().forEach { emit(it) }
    }


    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

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