package com.qlang.eyepetizer.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    var nextPageUrl: String? = null

    private val _detailUiState: MutableLiveData<UiState<Any>> = MutableLiveData()
    val detailUiState: LiveData<UiState<Any>> = _detailUiState
    private val _relateUiState: MutableLiveData<UiState<Any>> = MutableLiveData()
    val relateUiState: LiveData<UiState<Any>> = _relateUiState

    fun onInit() = launchIO {
        videoId?.let {
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
            _relateUiState.postValue(UiState<Any>(succ))
        }
    }

    fun getVideoDetail() = launchIO {
        videoId?.let {
            val result = videoId?.let { id -> net.getVideoDetail(id) }
            val value = result?.value
            val succ = result?.isSuccess == true && value != null
            if (succ) {
                videoInfo = value?.toLocalVideoInfo()
            }
            _detailUiState.postValue(UiState<Any>())
        }
    }
}