package com.qlang.eyepetizer.bean

/**
 * @author Created by qlang on 2018/8/24.
 */
data class ResponeInfo<T>(var count: Int?, var total: Int?, var code: Int?, var message: String?,
                          var nextPageUrl: String?, var itemList: List<T>?, var data: T?){
    override fun toString(): String {
        return "ResponeInfo(count=$count, total=$total, code=$code, message=$message, nextPageUrl=$nextPageUrl, itemList=$itemList, data=$data)"
    }
}