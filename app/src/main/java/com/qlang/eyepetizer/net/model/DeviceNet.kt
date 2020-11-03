package com.qlang.eyepetizer.net.model

import com.qlang.eyepetizer.base.MyApp
import com.qlang.eyepetizer.bean.ResponeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *
 * @author Created by qlang on 2018/9/4.
 */
class DeviceNet : BaseNet(MyApp.instance) {
    suspend fun uploadErrLog(params: Any): Result<ResponeInfo<Any?>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { uploadErrLog(toGsonRequestBody(params)) }
        }
    }
}