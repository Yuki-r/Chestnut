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

/**
 * @Description:    修改用户信息的ViewModel
 * @Author:         Yu ki-r
 * @CreateDate:     2020/11/10 11:23
 */
class UserViewModel@ViewModelInject constructor(
    private val userRepository: UserRepository
): ViewModel()  {

    val user: User =User()

    val id: Int? = null
    var portrait: String? = null   //头像
    var nickname  = user.nickname
    val email = user.email
    val phoneNumber = user.phoneNumber
    val personalizedSignature = user.personalizedSignature
    val password = user.password

    val mModifyResultStatus = MutableLiveData<ListModel<Int>>()

    fun modifyUserMessages(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.modifyUserMessages(user,mModifyResultStatus)
        }
    }
}