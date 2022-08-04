package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
/*
 *仓库层 判断数据请求方向，从网络数据源还是本地缓存数据源获取并返回
 * 该项目作者未实现本地缓存，liveData
 */
object Repository {
    //livaData()函数创建并返回一个liveData对象，然后在他的代码块中提供一个挂起函数的上下文，从而可以调用协程简化的挂起函数
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {//指定线程参数类型为这个，从而使其在子线程中运行
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)//专用于该函数的setValue()，用于通知数据变化
    }

}