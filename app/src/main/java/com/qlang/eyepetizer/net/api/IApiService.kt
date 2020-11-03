package com.qlang.eyepetizer.net.api

import com.qlang.eyepetizer.bean.*
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.Url
import okhttp3.ResponseBody
import retrofit2.http.GET

/**
 * @author Created by qlang on 2019/1/9.
 */
interface IApiService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(IApi.UPLOAD_EXCEPTION_LOG)
    suspend fun uploadErrLog(@Body body: RequestBody): ResponeInfo<Any?>?

    @GET(IApi.VIDEO_DETAIL)
    suspend fun getVideoDetail(@Path("id") id: Long): VideoClientInfo?

    @GET(IApi.VIDEO_DETAIL_RELATED)
    suspend fun getVideoRelated(@Query("id") id: Long): ResponeInfo<VideoRelated>?

    @GET
    suspend fun getVideoReplies(@Url url: String): ResponeInfo<VideoReplies>?

    @GET
    suspend fun getDiscovery(@Url url: String): ResponeInfo<DiscoverInfo>?

    @GET
    suspend fun getHomeRecommend(@Url url: String): ResponeInfo<HomeRecommendInfo>?

    @GET
    suspend fun getDaily(@Url url: String): ResponeInfo<DailyInfo>?

    @GET
    suspend fun getUGC(@Url url: String): ResponeInfo<FollowInfo>?

    @GET
    suspend fun getCommunityRecommend(@Url url: String): ResponeInfo<CommunityInfo>?

    @GET
    suspend fun getFollow(@Url url: String): ResponeInfo<FollowInfo>?

    @GET
    suspend fun getDynamic(@Url url: String): ResponeInfo<DynamicInfo>?

    @JvmSuppressWildcards
    @GET
    suspend fun getCategory(@Url url: String, @QueryMap params: Map<String, Any>): ResponeInfo<HomeRecommendInfo>?

    @GET(IApi.HOT_SEARCH)
    suspend fun getHotSearch(): List<String>?

    @GET(IApi.SEARCH_SEARCH)
    suspend fun getSearch(@Path("key") key: String, @Path("index") index: Int,
                          @Path("size") size: Int): ResponeInfo<Any>?

    @GET
    suspend fun download(@Url url: String): ResponseBody
}