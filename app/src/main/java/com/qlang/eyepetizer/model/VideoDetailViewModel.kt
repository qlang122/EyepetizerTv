package com.qlang.eyepetizer.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.mvvm.BaseViewModel
import com.qlang.eyepetizer.net.model.MainNet

class VideoDetailViewModel : BaseViewModel() {
    val net by lazy { MainNet() }

    var relatedDatas = ArrayList<VideoRelated>()

    var videoInfo: LocalVideoInfo? = null

    private var _videoId: Long? = null
    var videoId: Long?
        get() = videoInfo?.videoId ?: _videoId
        set(value) {
            _videoId = value
        }

    private var videoDetailParams = MutableLiveData<String>()
    private var relateParams = MutableLiveData<String>()

    var nextPageUrl: String? = null

    val detailUiState: LiveData<UiState<Any>> = Transformations.switchMap(videoDetailParams) {
        liveData {
            val result = videoId?.let { id -> net.getVideoDetail(id) }
            val value = result?.value
            val succ = result?.isSuccess == true && value != null
            if (succ) {
                videoInfo = value?.toLocalVideoInfo()
            }
            emit(UiState<Any>())
        }
    }

    val relateUiState: LiveData<UiState<Any>> = Transformations.switchMap(relateParams) {
        liveData {
            val result = videoId?.let { id -> net.getVideoRelated(id) }
            val value = result?.value
            val succ = result?.isSuccess == true && value != null
            if (succ) {
                val list = value?.itemList ?: emptyList()
                for (info in list) {
                    when (ItemTypeHelper.getItemViewType(info)) {
                        ItemType.FOLLOW_CARD,
                        ItemType.VIDEO_SMALL_CARD -> {
                            relatedDatas.add(info)
                        }
                    }
                }
            }
            emit(UiState<Any>(succ))
        }
    }

    fun onInit() {
        videoId?.let { relateParams.value = "" }
    }

    fun getVideoDetail() {
        videoId?.let { videoDetailParams.value = "" }
    }
}