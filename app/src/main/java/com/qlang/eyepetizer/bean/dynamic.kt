package com.qlang.eyepetizer.bean

class DynamicInfo(
        val data: DynamicBody?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {

}

class DynamicBody(
        val dataType: String?,
        val dynamicType: String?,
        val text: String?,
        val actionUrl: String?,
        val mergeNickName: String?,
        val mergeSubTitle: String?,
        val merge: Boolean?,
        val createDate: Long?,
        val user: User?,
        val simpleVideo: VideoClientInfo?,
        val reply: DynamicReply?) : BaseInfo() {

}

class DynamicReply(
        val id: Long?,
        val videoId: Long?,
        val videoTitle: String?,
        val message: String?,
        val likeCount: Int?,
        val showConversationButton: Boolean?,
        val parentReplyId: Int?,
        val rootReplyId: Long?,
        val ifHotReply: Boolean?,
        val liked: Boolean?,
        val parentReply: Any?,
        val user: User?) {

}
