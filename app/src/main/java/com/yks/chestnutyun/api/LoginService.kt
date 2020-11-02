package com.yks.chestnutyun.api

import androidx.lifecycle.LiveData
import com.yks.chestnutyun.base.BaseBean
import com.yks.chestnutyun.data.bean.LoginData
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @Description:    登录使用的接口调用方法
 * @Author:         Yu ki-r
 * @CreateDate:     2020/11/2 16:01
 */
interface LoginService {

    
    /**
     * 注册接口
     */
    @POST("")
    suspend fun register(@Query("username")username: String,
                         @Query("password")password: String,
                         @Query("verificationCode")verificationCode:String): LiveData<BaseBean<LoginData>>
}