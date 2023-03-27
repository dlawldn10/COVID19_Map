package com.ivy.covid19_map

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapFragment.newInstance
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
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
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(binding.mapFragmentContainer.id) as MapFragment?
            ?: newInstance().also {
                fm.beginTransaction().add(binding.mapFragmentContainer.id, it).commit()
            }.getMapAsync(this)


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
                marker.icon = MarkerIcons.BLACK
                marker.iconTintColor = Color.RED
                markerList.add(marker)
            }
            //println("3=========")
            runOnUiThread {
                markerList.forEach { it.map = naverMap }
            }
            //println("4=========")

        }
        //println("5=========")
    }


}