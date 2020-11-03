package com.qlang.eyepetizer.bean

import android.os.Parcel
import android.os.Parcelable

/// 视频对应的具体信息，响应实体类。
class VideoClientInfo(
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
        val recallSource: Any?,
        val releaseTime: Long?,
        val remark: String?,
        val resourceType: String?,
        val searchWeight: Int?,
        val shareAdTrack: Any?,
        val slogan: Any?,
        val src: Any?,
        val subtitles: List<Any>?,
        val tags: List<Tag>?,
        val thumbPlayUrl: Any?,
        val title: String?,
        val titlePgc: String?,
        val type: String?,
        val waterMarks: Any?,
        val webAdTrack: Any?,
        val webUrl: WebUrl?) : BaseInfo() {
    override fun toString(): String {
        return "VideoClientInfo(ad=$ad, adTrack=$adTrack, author=$author, brandWebsiteInfo=$brandWebsiteInfo," +
                " campaign=$campaign, category=$category, collected=$collected, consumption=$consumption," +
                " cover=$cover, dataType=$dataType, date=$date, description=$description," +
                " descriptionEditor=$descriptionEditor, descriptionPgc=$descriptionPgc," +
                " duration=$duration, favoriteAdTrack=$favoriteAdTrack, id=$id, idx=$idx," +
                " ifLimitVideo=$ifLimitVideo, label=$label, labelList=$labelList, lastViewTime=$lastViewTime," +
                " library=$library, playInfo=$playInfo, playUrl=$playUrl, played=$played," +
                " playlists=$playlists, promotion=$promotion, provider=$provider, reallyCollected=$reallyCollected," +
                " recallSource=$recallSource, releaseTime=$releaseTime, remark=$remark," +
                " resourceType=$resourceType, searchWeight=$searchWeight, shareAdTrack=$shareAdTrack," +
                " slogan=$slogan, src=$src, subtitles=$subtitles, tags=$tags, thumbPlayUrl=$thumbPlayUrl," +
                " title=$title, titlePgc=$titlePgc, type=$type, waterMarks=$waterMarks, webAdTrack=$webAdTrack, webUrl=$webUrl)"
    }

    fun toLocalVideoInfo(): LocalVideoInfo {
        return LocalVideoInfo(id, playUrl, "", title, description, duration, category,
                library, consumption, cover, author, webUrl, tags, playInfo, ad ?: false)
    }
}

class LocalVideoInfo(
        var videoId: Long?,
        val playUrl: String?,
        val playUrlLocal: String?,
        val title: String?,
        val description: String?,
        val duration: Int?,
        val category: String?,
        val library: String?,
        val consumption: Consumption?,
        val cover: Cover?,
        val author: Author?,
        val webUrl: WebUrl?,
        val tags: List<Tag>?,
        val playInfo: List<PlayInfo>?,
        val ad: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Consumption::class.java.classLoader),
            parcel.readParcelable(Cover::class.java.classLoader),
            parcel.readParcelable(Author::class.java.classLoader),
            parcel.readParcelable(WebUrl::class.java.classLoader),
            parcel.createTypedArrayList(Tag),
            parcel.createTypedArrayList(PlayInfo),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(videoId)
        parcel.writeString(playUrl)
        parcel.writeString(playUrlLocal)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(duration)
        parcel.writeString(category)
        parcel.writeString(library)
        parcel.writeParcelable(consumption, flags)
        parcel.writeParcelable(cover, flags)
        parcel.writeParcelable(author, flags)
        parcel.writeParcelable(webUrl, flags)
        parcel.writeTypedList(tags)
        parcel.writeTypedList(playInfo)
        parcel.writeByte(if (ad) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "LocalVideoInfo(videoId=$videoId, playUrl=$playUrl, playUrlLocal=$playUrlLocal," +
                " title=$title, description=$description, duration=$duration, category=$category," +
                " library=$library, consumption=$consumption, cover=$cover, author=$author," +
                " webUrl=$webUrl, tags=$tags, playInfo=$playInfo, ad=$ad)"
    }

    companion object CREATOR : Parcelable.Creator<LocalVideoInfo> {
        override fun createFromParcel(parcel: Parcel): LocalVideoInfo {
            return LocalVideoInfo(parcel)
        }

        override fun newArray(size: Int): Array<LocalVideoInfo?> {
            return arrayOfNulls(size)
        }
    }

}
