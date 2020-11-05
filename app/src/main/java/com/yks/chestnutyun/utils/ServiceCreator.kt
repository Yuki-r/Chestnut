package com.yks.chestnutyun.utils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @Description:    Retrofit 创建 封装
 * @Author:         Yu ki-r
 * @CreateDate:     2020/11/2 15:54
 */


object ServiceCreator {


    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)






    private val retrofit = Retrofit.Builder()
        .client(okHttpBuilder.build())
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}