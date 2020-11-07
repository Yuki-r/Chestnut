package com.yks.chestnutyun.viewmodels

import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.yks.chestnutyun.BR
import com.yks.chestnutyun.base.BaseBean
import com.yks.chestnutyun.common.ResultState
import com.yks.chestnutyun.data.repositories.RegisterRepository
import com.yks.chestnutyun.utils.RegExpUtils
import com.yks.chestnutyun.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.regex.Pattern

/**
 * @Description:    注册功能的ViewModel类
 * @Author:         Yu ki-r
 * @CreateDate:     2020/10/29 20:24
 */


class RegisterViewModel @ViewModelInject  constructor(
    private val registerRepository: RegisterRepository,
): ViewModel()  {
    private companion object

    val TAG: String = "RegisterViewModel"
    val errorResult = MutableLiveData<String>()
    val successResult = MutableLiveData<Boolean>()

    val registerResult = MutableLiveData<ResultState<Boolean>>()

    /**
     * 注册
     */
 /*  suspend fun toRegister(name: String,password:String,verificationCode:String) :ResultState {

        return withContext<ResultState>(context = Dispatchers.IO){
            val baseBean = registerRepository.register(name,password,verificationCode)
            if (baseBean.code==1){
                return@withContext ResultState.Success
            }else{
                return@withContext ResultState.Failure("注册失败")
            }
        }
    }*/

    fun toRegister(name: String,password:String,verificationCode:String){
        viewModelScope.launch(Dispatchers.IO){
          val result =  try {
              registerRepository.toRegister(name,password,verificationCode)
          }catch (e: Exception){
              ResultState.Error(Exception(e.message))
          }

            when(result){
                is ResultState.Success<Boolean> -> Log.d(TAG, "注册成功")
                    else -> Log.d(TAG, "注册失败")
            }
        }
    }

    /**
     * 获取验证码
     */
    fun getCode(userName:String):LiveData<Boolean> = liveData{
        val code = registerRepository.getCode(userName)
        emit(code)
    }
}




