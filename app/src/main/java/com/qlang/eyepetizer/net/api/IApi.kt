package com.qlang.eyepetizer.net.api

/**
 * @author Created by qlang on 2019/3/14.
 */
object IApi {

    const val UPLOAD_EXCEPTION_LOG = "api/v3/devices/logs"

    ///视频详情-视频信息
    const val VIDEO_DETAIL = "api/v2/video/{id}"

    ///视频详情-推荐列表
    const val VIDEO_DETAIL_RELATED = "api/v4/video/related"

    ///视频详情-评论列表
    const val VIDEO_DETAIL_REPLIES = "api/v2/replies/video?videoId="

    /// 首页-发现列表
    const val DISCOVERY_URL = "api/v7/index/tab/discovery" //v5

    /// 首页-推荐列表
    const val HOME_RECOMMEND_URL = "api/v5/index/tab/allRec?page=0"

    /// 首页-日报列表
    const val DAILY_URL = "api/v5/index/tab/feed"

    /// 首页-社区
    const val UGC_URL = "api/v5/index/tab/ugcSelected"

    //14广告 36生活 10动画 搞笑28 开胃4 创意2 运动18 音乐20 萌宠26 剧情12 科技32 旅行6 影视8 记录22 游戏30 综艺38 时尚24 集锦34
    /// 首页-其他
    const val HOME_OTHER_URL = "api/v5/index/tab/category/"// category/{type}

    /// 社区-推荐列表
    const val COMMUNITY_RECOMMEND_URL = "api/v7/community/tab/rec"

    /// 社区-关注列表
    const val FOLLOW_URL = "api/v6/community/tab/follow"

    /// 社区-动态
    const val DYNAMIC_URL = "api/v6/community/tab/dynamicFeeds"

    /// 通知-推送列表
    const val PUSHMESSAGE_URL = "api/v3/messages"

    ///搜索-热搜关键词
    const val HOT_SEARCH = "api/v3/queries/hot"

    ///搜索-搜索关键词
    const val SEARCH_SEARCH = "api/v1/search?query={key}&start={index}&num={size}"
}