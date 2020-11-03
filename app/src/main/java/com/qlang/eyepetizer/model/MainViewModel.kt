package com.qlang.eyepetizer.model

import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.ktx.execAsync
import com.qlang.eyepetizer.mvvm.BaseViewModel
import com.qlang.eyepetizer.net.api.IApi
import com.qlang.eyepetizer.net.model.MainNet

class MainViewModel : BaseViewModel() {
    private val net by lazy { MainNet() }

    val tabTitles: ArrayList<TabTitleInfo> = arrayListOf(
            TabTitleInfo("发现", 0x100, IApi.DISCOVERY_URL),
            TabTitleInfo("推荐", 0x101, IApi.HOME_RECOMMEND_URL),
            TabTitleInfo("日报", 0x102, IApi.DAILY_URL),
            TabTitleInfo("广告", 14, "${IApi.HOME_OTHER_URL}14"),
            TabTitleInfo("生活", 36, "${IApi.HOME_OTHER_URL}36"),
            TabTitleInfo("动画", 10, "${IApi.HOME_OTHER_URL}10"),
            TabTitleInfo("搞笑", 28, "${IApi.HOME_OTHER_URL}28"),
            TabTitleInfo("开胃", 4, "${IApi.HOME_OTHER_URL}4"),
            TabTitleInfo("创意", 2, "${IApi.HOME_OTHER_URL}2"),
            TabTitleInfo("运动", 18, "${IApi.HOME_OTHER_URL}18"),
            TabTitleInfo("音乐", 20, "${IApi.HOME_OTHER_URL}20"),
            TabTitleInfo("萌宠", 26, "${IApi.HOME_OTHER_URL}26"),
            TabTitleInfo("剧情", 12, "${IApi.HOME_OTHER_URL}12"),
            TabTitleInfo("科技", 32, "${IApi.HOME_OTHER_URL}32"),
            TabTitleInfo("旅行", 6, "${IApi.HOME_OTHER_URL}6"),
            TabTitleInfo("影视", 8, "${IApi.HOME_OTHER_URL}8"),
            TabTitleInfo("记录", 22, "${IApi.HOME_OTHER_URL}22"),
            TabTitleInfo("游戏", 30, "${IApi.HOME_OTHER_URL}30"),
            TabTitleInfo("综艺", 38, "${IApi.HOME_OTHER_URL}38"),
            TabTitleInfo("时尚", 24, "${IApi.HOME_OTHER_URL}24"),
            TabTitleInfo("集锦", 34, "${IApi.HOME_OTHER_URL}34"))

    val currPageUrlsMap: MutableMap<Int, String?> = hashMapOf()
    var currPage: Int = 0x101//默认 "推荐"

    private val datasLock = Any()
    var datas: MutableList<BaseInfo> = ArrayList()
    private val datasMap: MutableMap<Int, MutableList<BaseInfo>> = hashMapOf()

    private val requestParams = MutableLiveData<String>()

    inner class DatasRespone(var currPage: Int, var oldCount: Int, var currCount: Int) {
    }

    val dataUiState: LiveData<UiState<DatasRespone>> = Transformations.switchMap(requestParams) {
        liveData {
            var succ = false
            var count = 0
            var curr = 0
            val page = currPage
            currPageUrlsMap[page]?.let { url ->
                when (page) {
                    0x100 -> getDiscoverData(url).apply {
                        succ = first;count = second;curr = third
                    }
                    0x101 -> getHomeRecommendData(url).apply {
                        succ = first;count = second;curr = third
                    }
                    0x102 -> getDailyData(url).apply {
                        succ = first;count = second;curr = third
                    }
                    -1 -> ""
                    else -> getCategoryData(url).apply {
                        succ = first;count = second;curr = third
                    }
                }
                ""
            }
            emit(UiState(succ, t = DatasRespone(page, count, curr)))
        }
    }

    private suspend fun getCategoryData(url: String): Triple<Boolean, Int, Int> {
        var oldCount = 0
        var currCount = 0
        val result = net.getCategory(url)
        val value = result.value
        val succ = result.isSuccess && value != null
        if (succ) {
            val list = value?.itemList ?: emptyList()
            if (!datasMap.containsKey(currPage)) datasMap[currPage] = arrayListOf()

            val temp: MutableList<BaseInfo> = arrayListOf()
            oldCount = datasMap[currPage]?.size ?: 0
            for (info in list) {
                when (ItemTypeHelper.getItemViewType(info)) {
                    ItemType.FOLLOW_CARD,
                    ItemType.VIDEO_SMALL_CARD -> {
                        temp.add(info.apply { currCount++ })
                    }
                }
            }
            synchronized(datasLock) {
                for (t in temp) {
                    datas.add(t);datasMap[currPage]?.add(t)
                }
            }

            currPageUrlsMap[currPage] = value?.nextPageUrl
        }
        return Triple(succ, oldCount, currCount)
    }

    private suspend fun getDailyData(url: String): Triple<Boolean, Int, Int> {
        var oldCount = 0
        var currCount = 0
        val result = net.getDaily(url)
        val value = result.value
        val succ = result.isSuccess && value != null
        if (succ) {
            val list = value?.itemList ?: emptyList()
            if (!datasMap.containsKey(currPage)) datasMap[currPage] = arrayListOf()

            val temp: MutableList<BaseInfo> = arrayListOf()
            oldCount = datasMap[currPage]?.size ?: 0
            for (info in list) {
                when (ItemTypeHelper.getItemViewType(info)) {
                    ItemType.FOLLOW_CARD,
                    ItemType.VIDEO_SMALL_CARD -> {
                        temp.add(info.apply { currCount++ })
                    }
                }
            }
            synchronized(datasLock) {
                for (t in temp) {
                    datas.add(t);datasMap[currPage]?.add(t)
                }
            }

            currPageUrlsMap[currPage] = value?.nextPageUrl
        }
        return Triple(succ, oldCount, currCount)
    }

    private suspend fun getDiscoverData(url: String): Triple<Boolean, Int, Int> {
        var oldCount = 0
        var currCount = 0
        val result = net.getDiscovery(url)
        val value = result.value
        val succ = result.isSuccess && value != null
        if (succ) {
            val list = value?.itemList ?: emptyList()
            if (!datasMap.containsKey(currPage)) datasMap[currPage] = arrayListOf()

            val temp: MutableList<BaseInfo> = arrayListOf()
            oldCount = datasMap[currPage]?.size ?: 0
            for (info in list) {
                when (ItemTypeHelper.getItemViewType(info)) {
                    ItemType.SQUARE_CARD_COLLECTION,
                    ItemType.HORIZONTAL_SCROLL_CARD -> {
                        info.data?.itemList?.filter {
                            when (ItemTypeHelper.getItemViewType(it)) {
                                ItemType.FOLLOW_CARD,
                                ItemType.VIDEO_SMALL_CARD -> true
                                else -> false
                            }
                        }?.forEach {
                            temp.add(it.apply { currCount++ })
                        }
                    }
                    ItemType.FOLLOW_CARD,
                    ItemType.VIDEO_SMALL_CARD -> {
                        temp.add(info.apply { currCount++ })
                    }
                }
            }
            synchronized(datasLock) {
                for (t in temp) {
                    datas.add(t);datasMap[currPage]?.add(t)
                }
            }

            currPageUrlsMap[currPage] = value?.nextPageUrl
        }
        return Triple(succ, oldCount, currCount)
    }

    private suspend fun getHomeRecommendData(url: String): Triple<Boolean, Int, Int> {
        var oldCount = 0
        var currCount = 0

        val result = net.getHomeRecommend(url)
        val value = result.value
        val succ = result.isSuccess && value != null
        if (succ) {
            val list = value?.itemList ?: emptyList()
            if (!datasMap.containsKey(currPage)) datasMap[currPage] = arrayListOf()

            val temp: MutableList<BaseInfo> = arrayListOf()
            oldCount = datasMap[currPage]?.size ?: 0
            for (info in list) {
                when (ItemTypeHelper.getItemViewType(info)) {
                    ItemType.SQUARE_CARD_COLLECTION,
                    ItemType.HORIZONTAL_SCROLL_CARD -> {
                        info.data?.itemList?.filter {
                            when (ItemTypeHelper.getItemViewType(it)) {
                                ItemType.FOLLOW_CARD,
                                ItemType.VIDEO_SMALL_CARD -> true
                                else -> false
                            }
                        }?.forEach {
                            temp.add(it.apply { currCount++ })
                        }
                    }
                    ItemType.FOLLOW_CARD,
                    ItemType.VIDEO_SMALL_CARD -> {
                        temp.add(info.apply { currCount++ })
                    }
                }
            }
            synchronized(datasLock) {
                for (t in temp) {
                    datas.add(t);datasMap[currPage]?.add(t)
                }
            }

            currPageUrlsMap[currPage] = value?.nextPageUrl
        }
        return Triple(succ, oldCount, currCount)
    }

    fun changeTab(tab: TabTitleInfo?, recyclerView: RecyclerView?) {
        tab ?: return
//        Log.e("QL", "------>$currPage ${tab.tag} ${tab.pageUrl}")
        if (currPage != tab.tag) {
            currPage = tab.tag
            execAsync({
                while (recyclerView?.isComputingLayout == true) {
                }
            }, {
                synchronized(datasLock) {
                    datas.clear()
                    datasMap[currPage]?.let { datas.addAll(it) }
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            })

            if (!datasMap.containsKey(currPage)) {
                currPageUrlsMap[currPage] = tab.pageUrl
                requestParams.value = ""
            }
        }
    }

    fun onInit(tab: TabTitleInfo?) {
        tab ?: return
        currPage = tab.tag
        if (!currPageUrlsMap.containsKey(tab.tag)) currPageUrlsMap[tab.tag] = tab.pageUrl
        requestParams.value = ""
    }

    fun loadMore() {
        requestParams.value = ""
    }
}