package com.qlang.eyepetizer.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.qlang.eyepetizer.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

class MyGsyVideoPlayer : StandardGSYVideoPlayer {

    /**
     *  是否第一次加载视频。用于隐藏进度条、播放按钮等UI。播放完成后，重新加载视频，会重置为true。
     */
    private var initFirstLoad = true

    constructor(context: Context) : super(context)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId() = R.layout.layout_my_gsy_video_player

    fun clickStartBtn() {
        super.clickStartIcon()
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.ic_pause_white_24dp)
                    imageView.setBackgroundResource(R.drawable.sl_pause_white_bg)
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24dp)
                    imageView.setBackgroundResource(R.drawable.sl_play_white_bg)
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageResource(R.drawable.ic_refresh_white_24dp)
                    imageView.setBackgroundResource(0)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24dp)
                    imageView.setBackgroundResource(R.drawable.sl_play_white_bg)
                }
            }

        } else {
            super.updateStartImage()
        }
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        initFirstLoad = true
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        mBottomContainer.visibility = View.GONE
        mStartButton.visibility = View.GONE
    }

    //播放中
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        if (initFirstLoad) {
            mBottomContainer.visibility = View.GONE
            mStartButton.visibility = View.GONE
        }
        initFirstLoad = false
    }

    //开始缓冲
    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
    }

    //暂停
    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        mBottomContainer.visibility = View.GONE
    }

    //错误状态
    override fun changeUiToError() {
        super.changeUiToError()
    }

    fun getBottomContainer() = mBottomContainer
}