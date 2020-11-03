package com.qlang.eyepetizer.bean

///首页-推荐
class HomeRecommendInfo(
        val data: HomeBody?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {
    override fun toString(): String {
        return "HomeRecommendInfo(\ndata=$data, type=$type, tag=$tag, id=$id, adIndex=$adIndex)"
    }
}

class HomeBody(
        val actionUrl: String?,
        val ad: Boolean?,
        val adTrack: Any?,
        val author: Author?,
        val backgroundImage: String?,
        val brandWebsiteInfo: Any?,
        val campaign: Any?,
        val category: String?,
        val collected: Boolean?,
        val consumption: Consumption?,
        var content: Content?,
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
        val refreshUrl: String?,
        val releaseTime: Long?,
        val remark: Any?,
        val resourceType: String?,
        val rightText: String?,
        val searchWeight: Int?,
        val shareAdTrack: Any?,
        val showHotSign: Boolean?,
        val showImmediately: Boolean?,
        val slogan: Any?,
        val src: Int?,
        val subTitle: Any?,
        val subtitles: List<HomeSubTitle>?,
        val switchStatus: Boolean?,
        val tags: List<Tag>?,
        val text: String?,
        val thumbPlayUrl: Any?,
        val title: String?,
        val titleList: List<String>?,
        val titlePgc: Any?,
        val topicTitle: String?,
        val type: String?,
        val uid: Int?,
        val waterMarks: Any?,
        val webAdTrack: Any?,
        val webUrl: WebUrl?,
        val detail: AutoPlayVideoAdDetail?) : BaseInfo() {
    constructor() : this(null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null, null)

    override fun toString(): String {
        return "HomeBody(actionUrl=$actionUrl, ad=$ad, adTrack=$adTrack, author=$author, " +
                "backgroundImage=$backgroundImage, brandWebsiteInfo=$brandWebsiteInfo, campaign=$campaign, " +
                "category=$category, collected=$collected, \nconsumption=$consumption, \ncontent=$content, " +
                "count=$count, cover=$cover, dataType=$dataType, date=$date, description=$description, " +
                "descriptionEditor=$descriptionEditor, descriptionPgc=$descriptionPgc, duration=$duration, " +
                "expert=$expert, favoriteAdTrack=$favoriteAdTrack, \nfollow=$follow, footer=$footer, " +
                "haveReward=$haveReward, header=$header, icon=$icon, iconType=$iconType, id=$id, idx=$idx, " +
                "ifLimitVideo=$ifLimitVideo, ifNewest=$ifNewest, ifPgc=$ifPgc, " +
                "ifShowNotificationIcon=$ifShowNotificationIcon, image=$image, \nitemList=$itemList, " +
                "label=$label, labelList=$labelList, lastViewTime=$lastViewTime, library=$library, " +
                "medalIcon=$medalIcon, newestEndTime=$newestEndTime, \nplayInfo=$playInfo, playUrl=$playUrl, " +
                "played=$played, playlists=$playlists, promotion=$promotion, \nprovider=$provider, " +
                "reallyCollected=$reallyCollected, refreshUrl=$refreshUrl, releaseTime=$releaseTime, " +
                "remark=$remark, resourceType=$resourceType, rightText=$rightText, searchWeight=$searchWeight, " +
                "shareAdTrack=$shareAdTrack, showHotSign=$showHotSign, showImmediately=$showImmediately, " +
                "slogan=$slogan, src=$src, \nsubTitle=$subTitle, subtitles=$subtitles, switchStatus=$switchStatus, " +
                "\ntags=$tags, text=$text, thumbPlayUrl=$thumbPlayUrl, title=$title, \ntitleList=$titleList, " +
                "titlePgc=$titlePgc, topicTitle=$topicTitle, type=$type, uid=$uid, waterMarks=$waterMarks, " +
                "webAdTrack=$webAdTrack, \nwebUrl=$webUrl, \ndetail=$detail)"
    }

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

class HomeSubTitle(
        val type: String?,
        val url: String?) {
    override fun toString(): String {
        return "HomeSubTitle(type=$type, url=$url)"
    }
}
