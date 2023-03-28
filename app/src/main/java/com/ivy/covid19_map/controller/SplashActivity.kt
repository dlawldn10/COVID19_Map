package com.ivy.covid19_map.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ivy.covid19_map.R
import com.ivy.covid19_map.remoteSource.RequestInterface
import com.ivy.covid19_map.databinding.ActivitySplashBinding
import com.ivy.covid19_map.repository.CenterRepository
import com.ivy.covid19_map.repository.NetworkRepository
import com.ivy.covid19_map.viewModel.ProgressBarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.awaitResponse
import javax.inject.Inject
import kotlin.concurrent.timer

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var networkRepository: NetworkRepository

    @Inject
    lateinit var centerRepository: CenterRepository

    private val progressBarViewModel = ProgressBarViewModel()

    companion object {
        const val PER_PAGE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.viewModel = progressBarViewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        val mapActivityIntent = Intent(this@SplashActivity, MainActivity::class.java)


        GlobalScope.launch {
            centerRepository.deleteAll()

            val getCentersJob = async {
                for (x in 1..10) {
                    networkRepository.getCenters(x).collect{ centerRepository.insert(it) }
                }
                // 진행률 딜레이 테스트 코드
                //delay(4000)
            }

            /* period = 1000 = 1초 마다 반복.
            *  period = 10 = 0.01초 마다 반복.
            *  0.01초를 200번 반복하면 2초.
            *  즉, 200번 반복해서 진행률 100을 만드려면
            *  한번 반복에 0.5씩 증가해야함 */
            var progress = 0.0
            timer(period = 10) {
                // 진행률이 100%가 되면 지도 액티비티로 이동
                if (progressBarViewModel.barProgress.value == 100) {
                    startActivity(mapActivityIntent)
                    cancel()
                    finish()
                }else{
                    progress += 0.5
                }


                // 80% 진행되었을 때 진행상황 확인
                if (progress == 80.0){
                    // 덜 끝났다면
                    // 끝날때까지 대기 후 다시 진행
                    runBlocking {
                        getCentersJob.join()
                    }

                }

                runOnUiThread {
                    progressBarViewModel.setProgressView(progress)
                }
            }

        }


    }


}