package com.qlang.eyepetizer.bean

import android.os.Parcel
import android.os.Parcelable

//(int) ([\w]*)([ =\d]*[\d ]*);  val $2:Int?,
//([\w]+) ([\w]*)([ =\d]*[\d ]*);  val $2:$1?,
//List\<([\w]+)\> ([\w]*)([ =\d]*[\d \[\]]*);  val $2:List<$1>?,
//
abstract class BaseInfo {}

data class Content(
        val data: FollowCard?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) {
    override fun toString(): String {
        return "Content(\ndata=$data, type=$type, tag=$tag, id=$id, adIndex=$adIndex)"
    }
}

data class FollowCard(
        var ad: Boolean?,
        var adTrack: List<Any>?,
        var author: Author?,
        var brandWebsiteInfo: Any?,
        var campaign: Any?,
        var category: String?,
        var collected: Boolean?,
        var consumption: Consumption?,
        var cover: Cover?,
        var dataType: String?,
        var date: Long?,
        var description: String?,
        var descriptionEditor: String?,
        var descriptionPgc: Any?,
        var duration: Int?,
        var favoriteAdTrack: Any?,
        var id: Long?,
        var idx: Int?,
        var ifLimitVideo: Boolean?,
        var label: Any?,
        var labelList: List<Any>?,
        var lastViewTime: Any?,
        var library: String?,
        var playInfo: List<PlayInfo>?,
        var playUrl: String?,
        var played: Boolean?,
        var playlists: Any?,
        var promotion: Any?,
        var provider: Provider?,
        var reallyCollected: Boolean?,
        var releaseTime: Long?,
        var remark: String?,
        var resourceType: String?,
        var searchWeight: Int?,
        var shareAdTrack: Any?,
        var slogan: Any?,
        var src: Any?,
        var subtitles: Any?,
        var tags: List<Tag>?,
        var thumbPlayUrl: Any?,
        var title: String?,
        var titlePgc: Any?,
        var type: String?,
        var waterMarks: Any?,
        var webAdTrack: Any?,
        var webUrl: WebUrl?,
        var owner: User?,
        var url: String?,
        var urls: List<String>?) {

    constructor() : this(null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null)

    override fun toString(): String {
        return "FollowCard(ad=$ad, adTrack=$adTrack, \nauthor=$author, brandWebsiteInfo=$brandWebsiteInfo, " +
                "campaign=$campaign, category=$category, collected=$collected, \nconsumption=$consumption, " +
                " \ncover=$cover, dataType=$dataType, date=$date, description=$description, descriptionEditor=$descriptionEditor," +
                " descriptionPgc=$descriptionPgc, duration=$duration, favoriteAdTrack=$favoriteAdTrack," +
                " id=$id, idx=$idx, ifLimitVideo=$ifLimitVideo, label=$label, \nlabelList=$labelList," +
                " lastViewTime=$lastViewTime, library=$library, \nplayInfo=$playInfo, playUrl=$playUrl," +
                " played=$played, playlists=$playlists, promotion=$promotion, \nprovider=$provider," +
                " reallyCollected=$reallyCollected, releaseTime=$releaseTime, remark=$remark," +
                " resourceType=$resourceType, searchWeight=$searchWeight, shareAdTrack=$shareAdTrack," +
                " slogan=$slogan, src=$src, subtitles=$subtitles, \ntags=$tags, thumbPlayUrl=$thumbPlayUrl," +
                " title=$title, titlePgc=$titlePgc, type=$type, waterMarks=$waterMarks, webAdTrack=$webAdTrack," +
                " \nwebUrl=$webUrl, owner=$owner, url=$url, \nurls=$urls)"
    }

    fun toLocalVideoInfo(): LocalVideoInfo {
        return LocalVideoInfo(id, playUrl, "", title, description, duration,
                category, library, consumption, cover, author, webUrl, tags, playInfo, ad ?: false)
    }
}

class Header(
        val actionUrl: String?,
        val cover: Cover?,
        val description: String?,
        val font: Any?,
        val icon: String?,
        val iconType: String?,
        val id: Int?,
        val label: Label?,
        val labelList: List<Label>?,
        val rightText: String?,
        val showHateVideo: Boolean?,
        val subTitle: Any?,
        val subTitleFont: Any?,
        val textAlign: String?,
        val time: Long?,
        val title: String?) {
    override fun toString(): String {
        return "HomeHeader(actionUrl=$actionUrl, cover=$cover, description=$description, font=$font, " +
                "icon=$icon, iconType=$iconType, id=$id, \nlabel=$label, \nlabelList=$labelList, " +
                "rightText=$rightText, showHateVideo=$showHateVideo, subTitle=$subTitle, " +
                "subTitleFont=$subTitleFont, textAlign=$textAlign, time=$time, title=$title)"
    }
}

data class Author(val adTrack: Any?,
                  val approvedNotReadyVideoCount: Int?,
                  val description: String?,
                  val expert: Boolean?,
                  val follow: AuthorFollow?,
                  val icon: String?,
                  val id: Int?,
                  val ifPgc: Boolean?,
                  val latestReleaseTime: Long?,
                  val link: String?,
                  val name: String?,
                  val recSort: Int?,
                  val shield: AuthorShield?,
                  val videoNum: Int?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readParcelable(AuthorFollow::class.java.classLoader),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readParcelable(AuthorShield::class.java.classLoader),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(adTrack.toString())
        parcel.writeValue(approvedNotReadyVideoCount)
        parcel.writeString(description)
        parcel.writeValue(expert)
        parcel.writeParcelable(follow, flags)
        parcel.writeString(icon)
        parcel.writeValue(id)
        parcel.writeValue(ifPgc)
        parcel.writeValue(latestReleaseTime)
        parcel.writeString(link)
        parcel.writeString(name)
        parcel.writeValue(recSort)
        parcel.writeParcelable(shield, flags)
        parcel.writeValue(videoNum)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Author(adTrack=$adTrack, approvedNotReadyVideoCount=$approvedNotReadyVideoCount, description=$description, expert=$expert, \nfollow=$follow, icon=$icon, id=$id, ifPgc=$ifPgc, latestReleaseTime=$latestReleaseTime, link=$link, name=$name, recSort=$recSort, \nshield=$shield, videoNum=$videoNum)"
    }

    companion object CREATOR : Parcelable.Creator<Author> {
        override fun createFromParcel(parcel: Parcel): Author {
            return Author(parcel)
        }

        override fun newArray(size: Int): Array<Author?> {
            return arrayOfNulls(size)
        }
    }

}

data class AuthorFollow(
        val followed: Boolean?,
        val itemId: Int?,
        val itemType: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(followed)
        parcel.writeValue(itemId)
        parcel.writeString(itemType)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "AuthorFollow(followed=$followed, itemId=$itemId, itemType=$itemType)"
    }

    companion object CREATOR : Parcelable.Creator<AuthorFollow> {
        override fun createFromParcel(parcel: Parcel): AuthorFollow {
            return AuthorFollow(parcel)
        }

        override fun newArray(size: Int): Array<AuthorFollow?> {
            return arrayOfNulls(size)
        }
    }
}

data class AuthorShield(
        val itemId: Int?,
        val itemType: String?,
        val shielded: Boolean?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(itemId)
        parcel.writeString(itemType)
        parcel.writeValue(shielded)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "AuthorShield(itemId=$itemId, itemType=$itemType, shielded=$shielded)"
    }

    companion object CREATOR : Parcelable.Creator<AuthorShield> {
        override fun createFromParcel(parcel: Parcel): AuthorShield {
            return AuthorShield(parcel)
        }

        override fun newArray(size: Int): Array<AuthorShield?> {
            return arrayOfNulls(size)
        }
    }
}

data class PlayInfo(
        val name: String?,
        val type: String?,
        val url: String?,
        val urlList: List<Url>?,
        val width: Int?,
        val height: Int?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(Url),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(url)
        parcel.writeTypedList(urlList)
        parcel.writeValue(width)
        parcel.writeValue(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "PlayInfo(name=$name, type=$type, url=$url, \nurlList=$urlList, width=$width, height=$height)"
    }

    companion object CREATOR : Parcelable.Creator<PlayInfo> {
        override fun createFromParcel(parcel: Parcel): PlayInfo {
            return PlayInfo(parcel)
        }

        override fun newArray(size: Int): Array<PlayInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class Url(
        val name: String?,
        val size: Int?,
        val url: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(size)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Url(name=$name, size=$size, url=$url)"
    }

    companion object CREATOR : Parcelable.Creator<Url> {
        override fun createFromParcel(parcel: Parcel): Url {
            return Url(parcel)
        }

        override fun newArray(size: Int): Array<Url?> {
            return arrayOfNulls(size)
        }
    }
}

data class Label(
        val actionUrl: String?,
        val text: String?,
        val card: String?,
        val detail: Any?) {
    override fun toString(): String {
        return "Label(actionUrl=$actionUrl, text=$text, card=$card, detail=$detail)"
    }
}

data class Provider(
        val alias: String?,
        val icon: String?,
        val name: String?) {
    override fun toString(): String {
        return "Provider(alias=$alias, icon=$icon, name=$name)"
    }
}

data class Tag(
        val actionUrl: String?,
        val adTrack: Any?,
        val bgPicture: String?,
        val childTagIdList: Any?,
        val childTagList: Any?,
        val communityIndex: Int?,
        val desc: String?,
        val haveReward: Boolean?,
        val headerImage: String?,
        val id: Int?,
        val ifNewest: Boolean?,
        val name: String?,
        val newestEndTime: Any?,
        val tagRecType: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(actionUrl)
        parcel.writeString(adTrack.toString())
        parcel.writeString(bgPicture)
        parcel.writeString(childTagIdList.toString())
        parcel.writeString(childTagList.toString())
        parcel.writeValue(communityIndex)
        parcel.writeString(desc)
        parcel.writeValue(haveReward)
        parcel.writeString(headerImage)
        parcel.writeValue(id)
        parcel.writeValue(ifNewest)
        parcel.writeString(name)
        parcel.writeString(newestEndTime.toString())
        parcel.writeString(tagRecType)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Tag(actionUrl=$actionUrl, adTrack=$adTrack, bgPicture=$bgPicture, childTagIdList=$childTagIdList, childTagList=$childTagList, communityIndex=$communityIndex, desc=$desc, haveReward=$haveReward, headerImage=$headerImage, id=$id, ifNewest=$ifNewest, name=$name, newestEndTime=$newestEndTime, tagRecType=$tagRecType)"
    }

    companion object CREATOR : Parcelable.Creator<Tag> {
        override fun createFromParcel(parcel: Parcel): Tag {
            return Tag(parcel)
        }

        override fun newArray(size: Int): Array<Tag?> {
            return arrayOfNulls(size)
        }
    }
}

data class WebUrl(
        val forWeibo: String?,
        val raw: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(forWeibo)
        parcel.writeString(raw)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "WebUrl(forWeibo=$forWeibo, raw=$raw)"
    }

    companion object CREATOR : Parcelable.Creator<WebUrl> {
        override fun createFromParcel(parcel: Parcel): WebUrl {
            return WebUrl(parcel)
        }

        override fun newArray(size: Int): Array<WebUrl?> {
            return arrayOfNulls(size)
        }
    }
}

data class Cover(
        val blurred: String?,
        val detail: String?,
        val feed: String?,
        val homepage: String?,
        val sharing: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(blurred)
        parcel.writeString(detail)
        parcel.writeString(feed)
        parcel.writeString(homepage)
        parcel.writeString(sharing)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Cover(blurred=$blurred, detail=$detail, feed=$feed, homepage=$homepage, sharing=$sharing)"
    }

    companion object CREATOR : Parcelable.Creator<Cover> {
        override fun createFromParcel(parcel: Parcel): Cover {
            return Cover(parcel)
        }

        override fun newArray(size: Int): Array<Cover?> {
            return arrayOfNulls(size)
        }
    }

}

data class Consumption(
        val collectionCount: Int?,
        val realCollectionCount: Int?,
        val replyCount: Int?,
        val shareCount: Int?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(collectionCount)
        parcel.writeValue(realCollectionCount)
        parcel.writeValue(replyCount)
        parcel.writeValue(shareCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Consumption(collectionCount=$collectionCount, realCollectionCount=$realCollectionCount, replyCount=$replyCount, shareCount=$shareCount)"
    }

    companion object CREATOR : Parcelable.Creator<Consumption> {
        override fun createFromParcel(parcel: Parcel): Consumption {
            return Consumption(parcel)
        }

        override fun newArray(size: Int): Array<Consumption?> {
            return arrayOfNulls(size)
        }
    }

}

data class User(
        val actionUrl: String?,
        val area: Any?,
        val avatar: String?,
        val birthday: Long?,
        val city: Any?,
        val country: Any?,
        val cover: String?,
        val description: String?,
        val expert: Boolean?,
        val followed: Boolean?,
        val gender: String?,
        val ifPgc: Boolean?,
        val job: Any?,
        val library: String?,
        val limitVideoOpen: Boolean?,
        val nickname: String?,
        val registDate: Long?,
        val releaseDate: Long?,
        val uid: Int?,
        val university: Any?,
        val userType: String?) {
    override fun toString(): String {
        return "User(actionUrl=$actionUrl, area=$area, avatar=$avatar, birthday=$birthday, city=$city, country=$country, cover=$cover, description=$description, expert=$expert, followed=$followed, gender=$gender, ifPgc=$ifPgc, job=$job, library=$library, limitVideoOpen=$limitVideoOpen, nickname=$nickname, registDate=$registDate, releaseDate=$releaseDate, uid=$uid, university=$university, userType=$userType)"
    }
}