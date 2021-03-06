package com.yks.chestnutyun.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yks.chestnutyun.data.bean.User
import com.yks.chestnutyun.data.repositories.UserRepository
import com.yks.chestnutyun.utils.ListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

/**
 * @Description:    修改用户信息的ViewModel
 * @Author:         Yu ki-r
 * @CreateDate:     2020/11/10 11:23
 */
class UserViewModel@ViewModelInject constructor(
    private val userRepository: UserRepository
): ViewModel()  {



    val mModifyResultStatus = MutableLiveData<ListModel<Int>>()
    val mGetUserInfoResultStatus = MutableLiveData<ListModel<User>>()
    val mPostPortraitResultStatus = MutableLiveData<ListModel<String>>()


    /**
     * 上传用户头像
     *
     * @param portrait
     */
    fun postPortrait(part: MultipartBody.Part){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.postPortrait(
                part,
                mPostPortraitResultStatus
            )
        }
    }


    /**
     * 获取用户信息
     *
     * @param name
     */
    fun getUserInfo(name:String){
        viewModelScope.launch(Dispatchers.IO) {
             userRepository.getUserInfo(name, mGetUserInfoResultStatus
             )
        }
    }


    /**
     *  修改用户信息
     *
     * @param user 用户实例
     */
    fun modifyUserMessages(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.modifyUserMessages(user,mModifyResultStatus)
        }
    }
}