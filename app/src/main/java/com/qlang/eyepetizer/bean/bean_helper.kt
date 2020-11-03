package com.qlang.eyepetizer.bean

import android.net.Uri

object ItemType {
    //未知类型
    const val UNKNOWN = -1

    //自定义头部类型。
    const val CUSTOM_HEADER = 0
    const val TEXT_CARD_HEADER1 = 1
    const val TEXT_CARD_HEADER2 = 2
    const val TEXT_CARD_HEADER3 = 3

    //type:textCard -> dataType:TextCard,type:header4
    const val TEXT_CARD_HEADER4 = 4

    //type:textCard -> dataType:TextCard -> type:header5
    const val TEXT_CARD_HEADER5 = 5
    const val TEXT_CARD_HEADER6 = 6

    //type:textCard -> dataType:TextCardWithRightAndLeftTitle,type:header7
    const val TEXT_CARD_HEADER7 = 7

    //type:textCard -> dataType:TextCardWithRightAndLeftTitle,type:header8
    const val TEXT_CARD_HEADER8 = 8
    const val TEXT_CARD_FOOTER1 = 9

    //type:textCard -> dataType:TextCard,type:footer2
    const val TEXT_CARD_FOOTER2 = 10

    //type:textCard -> dataType:TextCardWithTagId,type:footer3
    const val TEXT_CARD_FOOTER3 = 11

    //type:banner -> dataType:Banner
    const val BANNER = 12

    //type:banner3-> dataType:Banner
    const val BANNER3 = 13

    //type:followCard -> dataType:FollowCard -> type:video -> dataType:VideoBeanForClient
    const val FOLLOW_CARD = 14

    //type:pictureFollowCard -> dataType:FollowCard -> dataType:UgcPictureBean
    const val PIC_FOLLOW_CARD = 2

    //type:briefCard -> dataType:TagBriefCard
    const val TAG_BRIEFCARD = 15

    //type:briefCard -> dataType:TopicBriefCard
    const val TOPIC_BRIEFCARD = 16

    //type:columnCardList -> dataType:ItemCollection
    const val COLUMN_CARD_LIST = 17

    //type:videoSmallCard -> dataType:VideoBeanForClient
    const val VIDEO_SMALL_CARD = 18

    //type:informationCard -> dataType:InformationCard
    const val INFORMATION_CARD = 19

    //type:autoPlayVideoAd -> dataType:AutoPlayVideoAdDetail
    const val AUTO_PLAY_VIDEO_AD = 20

    //type:horizontalScrollCard -> dataType:HorizontalScrollCard
    const val HORIZONTAL_SCROLL_CARD = 21

    //type:specialSquareCardCollection -> dataType:ItemCollection
    const val SPECIAL_SQUARE_CARD_COLLECTION = 22

    //type:squareCardCollection -> dataType:ItemCollection
    const val SQUARE_CARD_COLLECTION = 29

    //type:ugcSelectedCardCollection -> dataType:ItemCollection
    const val UGC_SELECTED_CARD_COLLECTION = 23

    //reply ->
    const val VIDEO_REPLY = 25

    //type:communityColumnsCard -> dataType:FollowCard
    const val STR_COMMUNITY_COLUMNS_CARD = 26

    //type:autoPlayFollowCard -> dataType:FollowCard
    const val AUTO_PLAY_FOLLOW_CARD = 27

    //type:DynamicInfoCard -> dataType:DynamicInfoCard
    const val DYNAMIC_REPLY = 28

    const val MAX = 100 //避免外部其他类型与此处包含的某个类型重复。
}

object UrlAction {
    const val TAG = "eyepetizer://tag/"
    const val DETAIL = "eyepetizer://detail/"
    const val RANKLIST = "eyepetizer://ranklist/"
    const val WEBVIEW = "eyepetizer://webview/?title="
    const val REPLIES_HOT = "eyepetizer://replies/hot?"
    const val TOPIC_DETAIL = "eyepetizer://topic/detail?"
    const val UGC_DETAIL = "eyepetizer://pgc/detail/"
    const val COMMON_TITLE = "eyepetizer://common/?title"
    const val LT_DETAIL = "eyepetizer://lightTopic/detail/"
    const val CM_TOPIC_SQUARE = "eyepetizer://community/topicSquare"
    const val HP_NOTIFI_TAB_ZERO = "eyepetizer://homepage/notification?tabIndex=0"
    const val CM_TAGSQUARE_TAB_ZERO = "eyepetizer://community/tagSquare?tabIndex=0"
    const val CM_TOPIC_SQUARE_TAB_ZERO = "eyepetizer://community/tagSquare?tabIndex=0"
    const val HP_SEL_TAB_TWO_NEWTAB_MINUS_THREE =
            "eyepetizer://homepage/selected?tabIndex=2&newTabIndex=-3"
    const val UNKNOW = ""

    fun processAction(actionUrl: String, onProcess: (action: String) -> Unit) {
        print("-----action url----->$actionUrl")
        val action = Uri.decode(actionUrl)
        print("-----action----->$action")
        if (action == null) return

        when {
            action.startsWith(WEBVIEW) -> onProcess(WEBVIEW)
            action == RANKLIST -> onProcess(RANKLIST)
            action.startsWith(TAG) -> onProcess(TAG)
            action == HP_SEL_TAB_TWO_NEWTAB_MINUS_THREE -> onProcess(HP_SEL_TAB_TWO_NEWTAB_MINUS_THREE)
            action == CM_TAGSQUARE_TAB_ZERO -> onProcess(CM_TAGSQUARE_TAB_ZERO)
            action == CM_TOPIC_SQUARE -> onProcess(CM_TOPIC_SQUARE)
            action == CM_TOPIC_SQUARE_TAB_ZERO -> onProcess(CM_TOPIC_SQUARE_TAB_ZERO)
            action.startsWith(COMMON_TITLE) -> onProcess(COMMON_TITLE)
            action == COMMON_TITLE -> onProcess(COMMON_TITLE)
            action.startsWith(TOPIC_DETAIL) -> onProcess(TOPIC_DETAIL)
            action.startsWith(DETAIL) -> onProcess(DETAIL)
            action.startsWith(UGC_DETAIL) -> onProcess(UGC_DETAIL)
            else -> onProcess(UNKNOW)
        }
    }

    fun getWebViewInfo(url: String): Pair<String, String> {
        val title = url.split("title=").last().split("&url").first()
        val _url = url.split("url=").last()
        return Pair(title, _url)
    }

    fun getVideoId(actionUrl: String): Long {
        return actionUrl.split(DETAIL)[1].toLong()
    }
}

object ItemTypeHelper {
    fun getItemViewType(info: Any?): Int {
        var type: String? = ""
        var type1: String? = ""
        var dataType: String? = ""

        when (info) {
            is DiscoverInfo -> {
                type = info.type
                type1 = info.data?.type
                dataType = info.data?.dataType
            }
            is HomeRecommendInfo -> {
                type = info.type
                type1 = info.data?.type
                dataType = info.data?.dataType
            }
            is DailyInfo -> {
                type = info.type
                type1 = info.data?.type
                dataType = info.data?.dataType
            }
            is FollowInfo -> {
                type = info.type
                type1 = info.data?.type
                dataType = info.data?.dataType
            }
            is VideoRelated -> {
                type = info.type
                type1 = info.data?.type
                dataType = info.data?.dataType
            }
            is VideoReplies -> {
                type = info.type
                type1 = info.data?.type
                dataType = info.data?.dataType
            }
            is DynamicInfo -> {
                type = info.type
                type1 = ""
                dataType = info.data?.dataType
            }
//    print("----getItemViewType---->$type $type1 $dataType");
        }
//    print("----getItemViewType---->$type $type1 $dataType");
        return if (type == "textCard") getTextCardType(type1) else getTextViewType(type, dataType)
    }

    fun convertVideoTime(time: Int): String {
        val minute = 1 * 60
        val hour = 60 * minute
        val day = 24 * hour

        return if (time < day)
            String.format("%02d:%02d", time / minute, time % 60)
        else String.format("%02d:%02d:%02d", time / hour, (time % hour) / minute, time % 60)
    }

    private fun getTextCardType(type: String?): Int {
        return when (type) {
            "header4" -> ItemType.TEXT_CARD_HEADER4
            "header5" -> ItemType.TEXT_CARD_HEADER5
            "header7" -> ItemType.TEXT_CARD_HEADER7
            "header8" -> ItemType.TEXT_CARD_HEADER8
            "footer2" -> ItemType.TEXT_CARD_FOOTER2
            "footer3" -> ItemType.TEXT_CARD_FOOTER3
            else -> ItemType.UNKNOWN
        }
    }

    private fun getTextViewType(type: String?, dataType: String?): Int {
        var rtn = ItemType.UNKNOWN

        when (type) {
            "horizontalScrollCard" -> rtn = if (dataType == "HorizontalScrollCard") ItemType.HORIZONTAL_SCROLL_CARD else ItemType.UNKNOWN
            "specialSquareCardCollection" -> rtn =
                    if (dataType == "ItemCollection") ItemType.SPECIAL_SQUARE_CARD_COLLECTION else ItemType.UNKNOWN
            "squareCardCollection" -> rtn = if (dataType == "ItemCollection") ItemType.SQUARE_CARD_COLLECTION else ItemType.UNKNOWN
            "columnCardList" -> rtn = if (dataType == "ItemCollection") ItemType.COLUMN_CARD_LIST else ItemType.UNKNOWN
            "banner" -> rtn = if (dataType == "Banner") ItemType.BANNER else ItemType.UNKNOWN
            "banner3" -> rtn = if (dataType == "Banner") ItemType.BANNER3 else ItemType.UNKNOWN
            "videoSmallCard" -> rtn = if (dataType == "VideoBeanForClient") ItemType.VIDEO_SMALL_CARD else ItemType.UNKNOWN
            "briefCard" -> rtn = when (dataType) {
                "TagBriefCard", "BriefCard" -> ItemType.TAG_BRIEFCARD
                "TopicBriefCard" -> ItemType.TOPIC_BRIEFCARD
                else -> ItemType.UNKNOWN
            }
            "followCard" -> rtn = if (dataType == "FollowCard") ItemType.FOLLOW_CARD else ItemType.UNKNOWN
            "pictureFollowCard" -> rtn = if (dataType == "FollowCard") ItemType.PIC_FOLLOW_CARD else ItemType.UNKNOWN
            "informationCard" -> rtn = if (dataType == "InformationCard") ItemType.INFORMATION_CARD else ItemType.UNKNOWN
            "ugcSelectedCardCollection" -> rtn = if (dataType == "ItemCollection") ItemType.UGC_SELECTED_CARD_COLLECTION else ItemType.UNKNOWN
            "autoPlayVideoAd" -> rtn = if (dataType == "AutoPlayVideoAdDetail") ItemType.AUTO_PLAY_VIDEO_AD else ItemType.UNKNOWN
            "reply" -> rtn = if (dataType == "ReplyBeanForClient") ItemType.VIDEO_REPLY else ItemType.UNKNOWN
            "leftAlignTextHeader" -> rtn = if (dataType == "LeftAlignTextHeader") ItemType.TEXT_CARD_HEADER4 else ItemType.UNKNOWN
            "communityColumnsCard" -> rtn = if (dataType == "FollowCard") ItemType.STR_COMMUNITY_COLUMNS_CARD else ItemType.UNKNOWN
            "autoPlayFollowCard" -> rtn = if (dataType == "FollowCard") ItemType.AUTO_PLAY_FOLLOW_CARD else ItemType.UNKNOWN
            "DynamicInfoCard" -> rtn = if (dataType == "DynamicReplyCard") ItemType.DYNAMIC_REPLY else ItemType.UNKNOWN
            "video" -> rtn = if (dataType == "VideoBeanForClient") ItemType.AUTO_PLAY_FOLLOW_CARD else ItemType.UNKNOWN
        }
        return rtn
    }
}
