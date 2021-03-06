package com.qlang.eyepetizer.bean

/// 首页-发现
class DiscoverInfo(
        val data: DiscoverBody?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {

}

class DiscoverBody(
        val actionUrl: String?,
        val ad: Boolean?,
        val adTrack: Any?,
        val author: Author?,
        val content: Content?,
        val autoPlay: Boolean?,
        val brandWebsiteInfo: Any?,
        val campaign: Any?,
        val category: String?,
        val collected: Boolean?,
        val consumption: Consumption?,
        val count: Int?,
        val cover: Cover?,
        val dataType: String?,
        val date: Long?,
        val description: String?,
        val descriptionEditor: String?,
        val descriptionPgc: Any?,
        val duration: Int?,
        val expert: Boolean?,
        val favoriteAdTrack: Any?,
        val follow: AuthorFollow?,
        val footer: Any?,
        val haveReward: Boolean?,
        val header: Header?,
        val icon: String?,
        val iconType: String?,
        val id: Long?,
        val idx: Int?,
        val ifLimitVideo: Boolean?,
        val ifNewest: Boolean?,
        val ifPgc: Boolean?,
        val ifShowNotificationIcon: Boolean?,
        val image: String?,
        val itemList: List<HomeRecommendInfo>?,
        val label: Label?,
        val labelList: List<Label>?,
        val lastViewTime: Any?,
        val library: String?,
        val medalIcon: Boolean?,
        val newestEndTime: Any?,
        val playInfo: List<PlayInfo>?,
        val playUrl: String?,
        val played: Boolean?,
        val playlists: Any?,
        val promotion: Any?,
        val provider: Provider?,
        val reallyCollected: Boolean?,
        val releaseTime: Long?,
        val remark: Any?,
        val resourceType: String?,
        val rightText: String?,
        val searchWeight: Int?,
        val shade: Boolean?,
        val shareAdTrack: Any?,
        val slogan: Any?,
        val src: Any?,
        val subTitle: Any?,
        val subtitles: List<Any>?,
        val switchStatus: Boolean?,
        val tags: List<Tag>?,
        val text: String?,
        val thumbPlayUrl: Any?,
        val title: String?,
        val titlePgc: Any?,
        val type: String?,
        val uid: Int?,
        val waterMarks: Any?,
        val webAdTrack: Any?,
        val webUrl: WebUrl?,
        val detail: AutoPlayVideoAdDetail?) : BaseInfo() {

    fun toLocalVideoInfo(): LocalVideoInfo {
        val card = content?.data
        return LocalVideoInfo(id ?: card?.id,
                playUrl ?: card?.playUrl,
                "",
                title ?: card?.title,
                description ?: card?.description,
                duration ?: card?.duration,
                category ?: card?.category,
                library ?: card?.library,
                consumption ?: card?.consumption,
                cover ?: card?.cover,
                author ?: card?.author,
                webUrl ?: card?.webUrl,
                tags ?: card?.tags,
                playInfo ?: card?.playInfo,
                ad ?: false)
    }
}

class AutoPlayVideoAdDetail(
        val actionUrl: String?,
        val adTrack: List<Any>?,
        val adaptiveImageUrls: String?,
        val adaptiveUrls: String?,
        val canSkip: Boolean?,
        val categoryId: Int?,
        val countdown: Boolean?,
        val cycleCount: Int?,
        val description: String?,
        val displayCount: Int?,
        val displayTimeDuration: Int?,
        val icon: String?,
        val id: Long?,
        val ifLinkage: Boolean?,
        val imageUrl: String?,
        val iosActionUrl: String?,
        val linkageAdId: Int?,
        val loadingMode: Int?,
        val openSound: Boolean?,
        val position: Int?,
        val showActionButton: Boolean?,
        val showImage: Boolean?,
        val showImageTime: Int?,
        val timeBeforeSkip: Int?,
        val title: String?,
        val url: String?,
        val videoAdType: String?,
        val videoType: String?) {

}
