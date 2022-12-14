package com.sunnyweather.android.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val drawerLayout: DrawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawerLayout) }
    private val swipeRefresh: SwipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.swipeRefresh) }
    private val weatherLayout: ScrollView by lazy { findViewById<ScrollView>(R.id.weatherLayout) }
    private val nowLayout: RelativeLayout by lazy { findViewById<RelativeLayout>(R.id.nowLayout) }
    private val titleLayout: FrameLayout by lazy { findViewById<FrameLayout>(R.id.titleLayout) }
    private val navBtn: Button by lazy { findViewById<Button>(R.id.navBtn) }
    private val placeName: TextView by lazy { findViewById<TextView>(R.id.placeName) }
    private val bodyLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.bodyLayout) }
    private val currentTemp: TextView by lazy { findViewById<TextView>(R.id.currentTemp) }
    private val currentSky: TextView by lazy { findViewById<TextView>(R.id.currentSky) }
    private val currentAQI: TextView by lazy { findViewById<TextView>(R.id.currentAQI) }
    private val forecastLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.forecastLayout) }
    private val coldRiskImg: ImageView by lazy { findViewById<ImageView>(R.id.coldRiskImg) }
    private val coldRiskText: TextView by lazy { findViewById<TextView>(R.id.coldRiskText) }
    private val dressingImg: ImageView by lazy { findViewById<ImageView>(R.id.dressingImg) }
    private val dressingText: TextView by lazy { findViewById<TextView>(R.id.dressingText) }
    private val ultravioletImg: ImageView by lazy { findViewById<ImageView>(R.id.ultravioletImg) }
    private val ultravioletText: TextView by lazy { findViewById<TextView>(R.id.ultravioletText) }
    private val carWashingImg: ImageView by lazy { findViewById<ImageView>(R.id.carWashingImg) }
    private val carWashingText: TextView by lazy { findViewById<TextView>(R.id.carWashingText) }

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val decorView = window.decorView
//        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerStateChanged(newState: Int) {}

        })
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        }
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener { refreshWeather() }
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // ??????now.xml???????????????
        val currentTempText = "${realtime.temperature.toInt()} ???"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "???????????? ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // ??????forecast.xml??????????????????
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ???"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // ??????life_index.xml??????????????????
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}