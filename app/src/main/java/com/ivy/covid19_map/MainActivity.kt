package com.ivy.covid19_map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ivy.covid19_map.databinding.ActivityMainBinding
import com.naver.maps.map.MapFragment.newInstance

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(binding.mapFragmentContainer.id) as MapFragment?
            ?: newInstance().also {
                fm.beginTransaction().add(binding.mapFragmentContainer.id, it).commit()
            }

    }
}