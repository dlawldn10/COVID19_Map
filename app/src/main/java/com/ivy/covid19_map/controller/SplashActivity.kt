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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import retrofit2.awaitResponse
import javax.inject.Inject
import kotlin.concurrent.timer

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    /* 뷰 바인딩 */
    lateinit var binding: ActivitySplashBinding

    /* api 데이터 접근 관련 레포지토리 */
    @Inject
    lateinit var networkRepository: NetworkRepository

    /* room 데이터 접근 관련 레포지토리 */
    @Inject
    lateinit var centerRepository: CenterRepository

    /* 본 액티비티와 바인딩 된 뷰모델 */
    private val progressBarViewModel = ProgressBarViewModel()

    companion object {
        /* 한 페이지 당 센터 수 */
        const val PER_PAGE = 10
        const val REQUIRED_ALL_DATA = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* 뷰 바인딩 및 뷰모델 적용 */
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.viewModel = progressBarViewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)


        GlobalScope.launch {
            // 테스트용 코드
            //centerRepository.deleteAll()

            /* 로컬 DB를 확인하여 데이터가 이미 있다면 다음 화면으로 */
            val getCountJob = async {
                if (centerRepository.selectCount() == REQUIRED_ALL_DATA){
                    startMapActivity(this@launch)
                }
            }

            getCountJob.await()

            /* API 데이터 저장 */
            val getCentersJob = async {
                for (x in 1..10) {
                    networkRepository.getCenters(x).collect{ centerRepository.insert(it) }
                }
            }

            /* period=10 으로 설정하여 0.01초 마다 반복.
            *  2초 동안 200번 반복하므로,
            *  2초만에 진행률 100을 맞추기 위해 0.5씩 증가 하도록 함. */
            var progress = 0.0
            timer(period = 10) {

                /* 조건 달성 시 지도 화면으로 이동 */
                if (progressBarViewModel.barProgress.value == 100) {
                    startMapActivity(this@launch)
                    this@timer.cancel()
                }else{
                    progress += 0.5
                }


                /* 80%에서 진행상황 동기화 */
                if (progress == 80.0){
                    runBlocking {
                        getCentersJob.join()
                    }
                }

                /* 뷰 모델을 통해 UI 업데이트 */
                runOnUiThread {
                    progressBarViewModel.setProgressView(progress)
                }
            }

        }


    }

    private fun startMapActivity(coroutineScope: CoroutineScope){
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
        coroutineScope.cancel()
    }


}