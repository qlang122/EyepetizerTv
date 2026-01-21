package com.qlang.eyepetizer.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.bean.FocusEntity
import com.qlang.eyepetizer.bean.ItemTypeHelper
import com.qlang.eyepetizer.bean.LocalVideoInfo
import com.qlang.eyepetizer.bean.VideoRelated
import com.qlang.eyepetizer.databinding.ActivityVideoDetailBinding
import com.qlang.eyepetizer.model.VideoDetailViewModel
import com.qlang.eyepetizer.mvvm.BaseVMActivity
import com.qlang.eyepetizer.net.loadImg
import com.qlang.eyepetizer.ui.adapter.LibraryType
import com.qlang.eyepetizer.ui.adapter.VideoDetailRelatedAdapter
import com.qlang.tvwidget.AutoFocusGridLayoutManager
import com.qlang.tvwidget.BorderEffect
import com.qlang.tvwidget.BorderView
import com.shuyu.gsyvideoplayer.GSYVideoADManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import kotlinx.coroutines.*

class VideoDetailActivity : BaseVMActivity<ActivityVideoDetailBinding, VideoDetailViewModel>() {

    private var relatedAdapter: VideoDetailRelatedAdapter? = null
    private var orientationUtils: OrientationUtils? = null

    private val globalJob by lazy { Job() }

    private val viewBorder by lazy {
        BorderView<ViewGroup>(this).apply {
            getEffect<BorderEffect>()?.scale = 1.0f
        }
    }
    private val viewBorder2 by lazy {
        BorderView<ViewGroup>(this).apply {
            setBackgroundResource(R.drawable.item_border_bg)
        }
    }

    private var hideBottomContainerJob: Job? = null
    private var lastKeyDownTime = System.currentTimeMillis()

    private var isFullVideo = false

    private var isLandScreen = false

    private var focusEntity = FocusEntity<Any>()
    private val focusListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus) focusEntity.setCurrent(v)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getContentLayoutId() = R.layout.activity_video_detail

    override fun bindVM(): Class<VideoDetailViewModel> = VideoDetailViewModel::class.java

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        initParams()
        startVideoPlayer()
        viewModel.onInit()
    }

    override fun onPause() {
        super.onPause()
        binding?.videoPlayer?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding?.videoPlayer?.onVideoResume()
    }

    override fun onDestroy() {
        GSYVideoADManager.releaseAllVideos()
        orientationUtils?.releaseListener()
        binding?.videoPlayer?.release()
        binding?.videoPlayer?.setVideoAllCallBack(null)
        globalJob.cancel()
        super.onDestroy()
    }

    private fun doBack() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            binding?.btnFull?.requestFocus()
            isFullVideo = false
            return
        }
        binding?.btnFull?.let {
            if (it.isVisible && !it.isFocused) {
                it.requestFocus()
                return
            }
        }

        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding?.videoPlayer?.onConfigurationChanged(this, newConfig, orientationUtils, true, true)

        val isLand = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandScreen != isLand) {
            isLandScreen = isLand
            binding?.rvList?.layoutManager = AutoFocusGridLayoutManager(context, if (isLandScreen) 4 else 1)
        }
    }

    override fun initView() {
        super.initView()
        initParams()

        binding?.videoPlayer?.let { view ->
            orientationUtils = OrientationUtils(this, view)
        }

        relatedAdapter = VideoDetailRelatedAdapter(this, viewModel.relatedDatas).apply {
            setOnItemClickListener { position, _, t ->
                focusEntity.setCurrent(t, position)
                if (!isFullVideo) doFocusAction()
            }
            setOnItemFocusListener { position, _, hasFocus, t ->
                if (hasFocus) focusEntity.setCurrent(t, position)
            }
        }
        binding?.rvList?.run {
            isFocusable = false
            isLandScreen = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            layoutManager = AutoFocusGridLayoutManager(context, if (isLandScreen) 4 else 1)
            adapter = relatedAdapter
            setHasFixedSize(true)
        }
        startVideoPlayer()

        binding?.run {
            viewBorder.attachTo(lyHead)
            viewBorder2.attachTo(rvList)

            setOnClickListener(btnFull)
            setOnFocusListener(btnFull, btnCache, btnFavorites, btnFollow, lyPlayer)
        }

        onBackPressedDispatcher.addCallback(this) {
            doBack()
        }

        viewModel.onInit()
    }

    private fun setOnFocusListener(vararg v: View?) {
        v.forEach { it?.onFocusChangeListener = focusListener }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_full -> {
                binding?.lyPlayer?.clearFocus()
                showFull()
            }
            R.id.ly_player -> {
                binding?.videoPlayer?.clickStartBtn()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val c = event?.unicodeChar ?: 0
//        Log.e("QL", "----->$keyCode $c ${Integer.toHexString(c)} ${c.toChar()} ")
//        val count = event?.repeatCount ?: 0
//        Log.e("QL", "---repeatCount--->$count")
        val curr = System.currentTimeMillis()
        if (curr - lastKeyDownTime < 150) return true
        lastKeyDownTime = curr

        when (keyCode) {
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                doFocusAction()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFullVideo) {
//                    event?.startTracking()
                    doVideoAction(1)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (isFullVideo) {
//                    event?.startTracking()
                    doVideoAction(2)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                val pos = focusEntity.position
                if (pos < 4) {//焦点在第一排时
                    binding?.btnFull?.requestFocus()
                    return true
                }
            }
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_ESCAPE -> {
                doBack();return true
            }
        }
        return if (event == null) false else super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
//        val c = event?.unicodeChar ?: 0
//        Log.e("QL", "---onKeyLongPress-->$keyCode $c ${Integer.toHexString(c)} ${c.toChar()} ")
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> doVideoAction(1)
            KeyEvent.KEYCODE_DPAD_RIGHT -> doVideoAction(2)
        }
        return super.onKeyLongPress(keyCode, event)
    }

    private fun doVideoAction(action: Int) {
        binding?.videoPlayer?.run {
            val total = duration
            var curr = currentPositionWhenPlaying
            when (action) {
                1 -> {
                    if (curr > 0) curr -= 10000;if (curr < 0) curr = 0
                    seekTo(curr.toLong())
                }
                2 -> {
                    if (curr < total) curr += 10000;if (curr > total) curr = total
                    seekTo(curr.toLong())
                }
            }
        }
    }

    private fun doFocusAction() {
        if (isFullVideo) {
            binding?.videoPlayer?.clickStartBtn()
            return
        }
        when (val value = focusEntity.value) {
            is View -> onClick(value)
            is VideoRelated -> {
                val info = value.data?.toLocalVideoInfo()
                val url = info?.playUrl
                if (url.isNullOrEmpty()) info?.videoId?.let {
                    VideoDetailActivity.start(this, it)
                } else VideoDetailActivity.start(this, info)
            }
        }
    }

    private fun initParams() {
        viewModel.videoInfo = intent?.getParcelableExtra(EXTRA_VIDEOINFO)
        intent?.getLongExtra(EXTRA_VIDEO_ID, 0L)?.let { viewModel.videoId = it }
        if (viewModel.videoInfo == null)
            viewModel.getVideoDetail()
    }

    private fun startVideoPlayer() {
        setInfoData()
        viewModel.videoInfo?.run {
            binding?.videoPlayer?.startPlay()
        }
        binding?.btnFull?.requestFocus()
    }

    private fun setInfoData() {
        viewModel.videoInfo?.let { info ->
            info.cover?.blurred?.let { binding?.ivBlurredBg?.loadImg(it) }
            binding?.lyInfo?.tvTitle?.text = info.title ?: ""
            val cate = "${info.category ?: ""} / ${
                ItemTypeHelper.convertVideoTime(
                    info.duration
                        ?: 0
                )
            }${if (info.library == LibraryType.TYPE_DAILY) " / 开眼精选" else ""}"
            binding?.lyInfo?.tvCategory?.text = cate
            binding?.lyInfo?.tvDescription?.text = info.description ?: ""
            binding?.lyInfo?.tvCollectionCount?.text = "${info.consumption?.collectionCount ?: "0"}"
            binding?.lyInfo?.tvShareCount?.text = "${info.consumption?.shareCount ?: "0"}"

            binding?.lyInfo?.tvAuthorName?.text = info.author?.name ?: ""
            binding?.lyInfo?.tvAuthorDescription?.text = info.author?.description ?: ""
            info.author?.icon?.let { url ->
                binding?.lyInfo?.ivAvatar?.loadImg(url, { binding?.lyInfo?.ivAvatar?.setImageBitmap(it) })
            }
        }
    }

    override fun observe() {
        if (!viewModel.detailUiState.hasObservers()) {
            viewModel.detailUiState.observe(this, Observer {
                startVideoPlayer()
            })
        }
        if (!viewModel.relateUiState.hasObservers()) {
            viewModel.relateUiState.observe(this, Observer {
                relatedAdapter?.notifyDataSetChanged()
            })
        }
    }

    private fun GSYVideoPlayer.startPlay() {
        viewModel.videoInfo?.let {
            //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
            fullscreenButton.setOnClickListener { showFull() }
            //防止错位设置
            playTag = TAG
            //音频焦点冲突时是否释放
            isReleaseWhenLossAudio = false
            //增加封面
            val imageView = ImageView(this@VideoDetailActivity)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            it.cover?.detail?.let { it1 -> imageView.loadImg(it1) }
            thumbImageView = imageView
            thumbImageView.setOnClickListener { }
            //是否开启自动旋转
            isRotateViewAuto = false
            //是否需要全屏锁定屏幕功能
            isNeedLockFull = true
            //是否可以滑动调整
            setIsTouchWiget(true)
            //设置触摸显示控制ui的消失时间
            dismissControlTime = 5000
            //设置播放过程中的回调
            setVideoAllCallBack(VideoCallPlayBack())
            //设置播放URL
            it.playUrl?.let { url ->
                setUp(url, false, it.title)
                //开始播放
                startPlayLogic()
            }
        }
    }

    private fun delayHideBottomContainer() {
        hideBottomContainerJob?.cancel()
        hideBottomContainerJob = CoroutineScope(globalJob).launch(Dispatchers.Main) {
            binding?.videoPlayer?.dismissControlTime?.toLong()?.let { delay(it) }
            binding?.videoPlayer?.getBottomContainer()?.visibility = View.GONE
            binding?.videoPlayer?.startButton?.visibility = View.GONE
        }
    }

    private fun showFull() {
        if (isFullVideo) {
            binding?.videoPlayer?.clickStartBtn()
            return
        }

        orientationUtils?.run { if (isLand != 1) resolveByClick() }
        binding?.videoPlayer?.startWindowFullscreen(this, true, false)
        isFullVideo = true
    }

    inner class VideoCallPlayBack : GSYSampleCallBack() {
        override fun onStartPrepared(url: String?, vararg objects: Any?) {
            super.onStartPrepared(url, *objects)
        }

        override fun onClickBlank(url: String?, vararg objects: Any?) {
            super.onClickBlank(url, *objects)
        }

        override fun onClickStop(url: String?, vararg objects: Any?) {
            super.onClickStop(url, *objects)
            delayHideBottomContainer()
        }

        override fun onAutoComplete(url: String?, vararg objects: Any?) {
            super.onAutoComplete(url, *objects)
            if (isFullVideo) doBack()
        }
    }

    companion object {
        const val TAG = "NewDetailActivity"

        const val EXTRA_VIDEOINFO = "videoInfo"
        const val EXTRA_VIDEO_ID = "videoId"

        fun start(context: Context, videoInfo: LocalVideoInfo) {
            val intent = Intent(context, VideoDetailActivity::class.java)
            intent.putExtra(EXTRA_VIDEOINFO, videoInfo)
            context.startActivity(intent)
        }

        fun start(context: Context, videoId: Long) {
            val intent = Intent(context, VideoDetailActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_ID, videoId)
            context.startActivity(intent)
        }
    }
}