package com.qlang.eyepetizer.net.model

import com.qlang.eyepetizer.base.MyApp
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.net.api.IApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainNet : BaseNet(MyApp.instance) {
    suspend fun getVideoDetail(id: Long): Result<VideoClientInfo?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getVideoDetail(id) }
        }
    }

    suspend fun getVideoRelated(id: Long): Result<ResponeInfo<VideoRelated>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getVideoRelated(id) }
        }
    }

    suspend fun getVideoReplies(id: Long, url: String? = null): Result<ResponeInfo<VideoReplies>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getVideoReplies(if (url.isNullOrEmpty()) "${IApi.VIDEO_DETAIL_REPLIES}$id" else url) }
        }
    }

    suspend fun getDiscovery(url: String = IApi.DISCOVERY_URL): Result<ResponeInfo<DiscoverInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getDiscovery(url) }
        }
    }

    suspend fun getHomeRecommend(url: String = IApi.HOME_RECOMMEND_URL): Result<ResponeInfo<HomeRecommendInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getHomeRecommend(url) }
        }
    }

    suspend fun getDaily(url: String = IApi.DAILY_URL): Result<ResponeInfo<DailyInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getDaily(url) }
        }
    }

    suspend fun getUGC(url: String = IApi.UGC_URL): Result<ResponeInfo<FollowInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getUGC(url) }
        }
    }

    suspend fun getCommunityRecommend(url: String = IApi.COMMUNITY_RECOMMEND_URL): Result<ResponeInfo<CommunityInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getCommunityRecommend(url) }
        }
    }

    suspend fun getFollow(url: String = IApi.FOLLOW_URL): Result<ResponeInfo<FollowInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getFollow(url) }
        }
    }

    suspend fun getDynamic(url: String = IApi.DYNAMIC_URL): Result<ResponeInfo<DynamicInfo>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getDynamic(url) }
        }
    }

    suspend fun getCategory(url: String): Result<ResponeInfo<HomeRecommendInfo>?> {
        return withContext(Dispatchers.IO) {
            val params = HashMap<String, Any>().apply {
                put("v", "4.4.0");put("vc", 4811);put("f", "iphone");put("net", "wifi")
                put("isOldUser", "1");put("p_product", "EYEPETIZER_IOS")
                put("u", "eba4cea101acbc893766b92720dff2b345983704")
                put("_s", "0926bf9aa896522b33f7b34b20497028")
            }
            apiServer.safeCall { this.getCategory(url, params) }
        }
    }

    suspend fun getHotSearch(): Result<List<String>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getHotSearch() }
        }
    }

    suspend fun getSearch(key: String, index: Int, size: Int): Result<ResponeInfo<Any>?> {
        return withContext(Dispatchers.IO) {
            apiServer.safeCall { this.getSearch(key, index, size) }
        }
    }
}