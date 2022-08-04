package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
 *网络层，同意的网络数据源 访问入口，对所有的网络请求API封装，协程的回调简化
 */
object SunnyWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>()//retrofit动态代理对象
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()//调用方法发起请求
    //协程简化回调
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}