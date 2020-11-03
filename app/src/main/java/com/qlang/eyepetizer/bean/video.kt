package com.qlang.eyepetizer.bean

/// 视频详情-相关推荐+评论
class VideoDetailInfo() {
    var videoInfo: VideoClientInfo? = null
    var videoRelated: ResponeInfo<VideoRelated>? = null
    var videoReplies: ResponeInfo<VideoReplies>? = null

}

/// 视频详情-相关推荐
class VideoRelated(
        val data: VideoRelatedData?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {

}

class VideoRelatedData(
        val ad: Boolean?,
        val adTrack: List<Any>?,
        val author: Author?,
        val brandWebsiteInfo: Any?,
        val campaign: Any?,
        val category: String?,
        val collected: Boolean?,
        val consumption: Consumption?,
        val cover: Cover?,
        val dataType: String?,
        val date: Long?,
        val description: String?,
        val descriptionEditor: String?,
        val descriptionPgc: String?,
        val duration: Int?,
        val favoriteAdTrack: Any?,
        val id: Long?,
        val idx: Int?,
        val ifLimitVideo: Boolean?,
        val label: Any?,
        val labelList: List<Any>?,
        val lastViewTime: Any?,
        val library: String?,
        val playInfo: List<PlayInfo>?,
        val playUrl: String?,
        val played: Boolean?,
        val playlists: Any?,
        val promotion: Any?,
        val provider: Provider?,
        val reallyCollected: Boolean?,
        val releaseTime: Long?,
        val remark: String?,
        val resourceType: String?,
        val searchWeight: Int?,
        val shareAdTrack: Any?,
        val slogan: Any?,
        val src: Int?,
        val subTitle: Any?,
        val tags: List<Tag>?,
        val thumbPlayUrl: Any?,
        val title: String?,
        val titlePgc: String?,
        val type: String?,
        val waterMarks: Any?,
        val webAdTrack: Any?,
        val webUrl: WebUrl?,

        val subtitles: List<Any>?,
        val recallSource: String?,
        val text: String?,
        val follow: AuthorFollow?,
        val footer: Any?,
        val header: Header?,
        val count: Int?,
        val actionUrl: String?,
        val itemList: List<VideoRelatedItemX>?) : BaseInfo() {


    fun toFollowCard(): FollowCard {
        val info = FollowCard()
        info.ad = ad;
        info.adTrack = adTrack;
        info.author = author;
        info.brandWebsiteInfo = brandWebsiteInfo;
        info.campaign = campaign;
        info.category = category;
        info.collected = collected;
        info.consumption = consumption;
        info.cover = cover;
        info.dataType = dataType;
        info.date = date;
        info.description = description;
        info.descriptionEditor = descriptionEditor;
        info.descriptionPgc = descriptionPgc;
        info.duration = duration;
        info.favoriteAdTrack = favoriteAdTrack;
        info.id = id;
        info.idx = idx;
        info.ifLimitVideo = ifLimitVideo;
        info.label = label;
        info.labelList = labelList;
        info.lastViewTime = lastViewTime;
        info.library = library;
        info.playInfo = playInfo;
        info.playUrl = playUrl;
        info.played = played;
        info.playlists = playlists;
        info.promotion = promotion;
        info.provider = provider;
        info.reallyCollected = reallyCollected;
        info.releaseTime = releaseTime;
        info.remark = remark;
        info.resourceType = resourceType;
        info.searchWeight = searchWeight;
        info.shareAdTrack = shareAdTrack;
        info.slogan = slogan;
        info.src = src;
        info.subtitles = subtitles;
        info.tags = tags;
        info.thumbPlayUrl = thumbPlayUrl;
        info.title = title;
        info.titlePgc = titlePgc;
        info.type = type;
        info.waterMarks = waterMarks;
        info.webAdTrack = webAdTrack;
        info.webUrl = webUrl;
        return info
    }

    fun toLocalVideoInfo(): LocalVideoInfo {
        return LocalVideoInfo(id, playUrl, "", title, description, duration, category,
                library, consumption, cover, author, webUrl, tags, playInfo, ad ?: false)
    }
}

class VideoRelatedItemX(
        val data: VideoRelatedDataX?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) {

}

class VideoRelatedDataX(
        val actionUrl: Any?,
        val contentType: String?,
        val dataType: String?,
        val message: String?,
        val nickname: String?) {

}

/// 视频详情-评论
class VideoReplies(
        val data: VideoRepliesData?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {

}

class VideoRepliesData(
        val actionUrl: String?,
        val adTrack: Any?,
        val cover: Any?,
        val createTime: Long?,
        val dataType: String?,
        val follow: AuthorFollow?,
        val hot: Boolean?,
        val id: Long?,
        val imageUrl: Any?,
        val likeCount: Int?,
        val liked: Boolean?,
        val message: String?,
        val parentReply: VideoRepliesParentReply?,
        val parentReplyId: Long?,
        val replyStatus: String?,
        val rootReplyId: Long?,
        val sequence: Int?,
        val showConversationButton: Boolean?,
        val showParentReply: Boolean?,
        val sid: String?,
        val subTitle: Any?,
        val text: String?,
        val type: String?,
        val ugcVideoId: Any?,
        val ugcVideoUrl: Any?,
        val user: User?,
        val userBlocked: Boolean?,
        val userType: Any?,
        val videoId: Long?,
        val videoTitle: String?) : BaseInfo() {

}

class VideoRepliesParentReply(
        val id: Long?,
        val imageUrl: Any?,
        val message: String?,
        val replyStatus: String?,
        val user: User?) {

}
