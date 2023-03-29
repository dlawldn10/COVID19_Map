# COVID19_Map
## .gitignore
    /app/src/main/res/values/key_strings.xml  // API key string

## 프로젝트 구조
com.ivy.covid19_map

  .controller
  
    CenterInfoDialog  // 예방 접종 센터의 정보를 표시하는 dialog 관리
    MainActivity  // 지도 화면 액티비티
    SplashActivity  // 스플래시 화면 액티비티
    
  .dataClass
  
    CenterData  // 예방 접종 센터 데이터 객체
    GetCenterResponseData // API response 데이터객체
    
  .remoteSource
  
    NetworkModule // retrofit hilt 모듈
    RequestInterface  // retrofit request interface
    
  .repository
  
    CenterRepository   // room repository
    NwrworkRepository // retrofit repository
    
  .room
  
    CenterDAO // room DAO
    CenterDB  // room DB
    RoomModule  // room hilt 모듈
    
  .viewModel
  
    CenterViewModel // 예방 접종 센터 정보 설정 뷰모델
    ProgressBarViewModel  // 프로그레스바 설정 뷰모델
    
   CenterApplication
