package com.ivy.covid19_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Toast
import com.ivy.covid19_map.databinding.FragmentMapBinding.inflate


private const val ARG_PARAM1 = "param1"

class MapFragment : Fragment() {
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fragmentBinding = inflate(inflater, container, false)
        Toast.makeText(this.context, "프래그먼트 부착", Toast.LENGTH_SHORT).show()
        return fragmentBinding.root
    }

}