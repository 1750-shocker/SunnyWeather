package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/*
 *网络层，retrofit构建器
 */
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    //接收泛型，从而可以通过不同的接口类创建不同的动态代理对象
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    //泛型实化，简化写法，没有这个需要接收类的参数，有了这个写在<>中
    inline fun <reified T> create(): T = create(T::class.java)
}
