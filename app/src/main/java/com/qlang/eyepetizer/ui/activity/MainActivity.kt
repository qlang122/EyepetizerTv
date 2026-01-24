package com.qlang.eyepetizer.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.libs.utils.UiUtils
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.insetToSystemStatusBar
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.databinding.ActivityMainBinding
import com.qlang.eyepetizer.ktx.execAsync
import com.qlang.eyepetizer.model.MainViewModel
import com.qlang.eyepetizer.mvvm.BaseVMActivity
import com.qlang.eyepetizer.ui.adapter.HomeListAdapter
import com.qlang.eyepetizer.ui.adapter.TabTitleAdapter
import com.qlang.tvwidget.AutoFocusGridLayoutManager
import com.qlang.tvwidget.BorderEffect
import com.qlang.tvwidget.BorderView
import kotlinx.coroutines.delay

class MainActivity : BaseVMActivity<ActivityMainBinding, MainViewModel>() {
    private var lastPressBackTime = 0L

    private var listAdapter: HomeListAdapter? = null

    private var focusEntity = FocusEntity<Any>()
    private var unHasFocusEntity = FocusEntity<Any>()
    private lateinit var currFocusTab: FocusEntity<TabTitleInfo>

    private val viewBorder by lazy {
        BorderView<RecyclerView>(this).apply {
            getEffect<BorderEffect>()?.scale = 1.03f
            setBackgroundResource(R.drawable.item_border_bg)
        }
    }
    private val viewBorder2 by lazy {
        BorderView<RecyclerView>(this).apply {
            getEffect<BorderEffect>()?.scale = 1.1f
            setBackgroundResource(R.drawable.item_border_empty_bg)
        }
    }

    private var isLandScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        window.insetToSystemStatusBar()
        super.onCreate(savedInstanceState)
    }

    override fun getContentLayoutId() = R.layout.activity_main

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isLand = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandScreen != isLand) {
            isLandScreen = isLand
            binding?.rvList?.layoutManager = AutoFocusGridLayoutManager(context, if (isLandScreen) 3 else 1)
        }
        if (isLandScreen) {
            viewBorder.attachTo(binding?.rvList)
            viewBorder2.attachTo(binding?.rvTitle)
        } else {
            viewBorder.detachFrom(binding?.rvList)
            viewBorder2.detachFrom(binding?.rvTitle)
        }
    }

    override fun initView() {
        binding?.vStatusBar?.let {
            UiUtils.setStatusBarColorAndHeight(it, resources.getColor(R.color.color_14a8a8a8), 0)
        }
        isLandScreen = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        currFocusTab = FocusEntity(viewModel.tabTitles[1]).apply { position = 1 }//默认选中的tab

        listAdapter = HomeListAdapter(this, viewModel.datas).apply {
            setOnItemClickListener { _, _, t ->
                doFocusAction(t)
            }
            setOnItemFocusListener { position, _, hasFocus, t ->
                if (hasFocus) focusEntity.setCurrent(t, position)
                else unHasFocusEntity.setCurrent(t, position)
            }
            setHasStableIds(true)
        }
        binding?.rvTitle?.run {
            isFocusable = false
            layoutManager = AutoFocusGridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false)
            adapter = TabTitleAdapter(this@MainActivity, viewModel.tabTitles).apply {
                setOnItemFocusListener { position, _, hasFocus, t ->
                    if (hasFocus) {
                        focusEntity.clear()
                        if (unHasFocusEntity.position >= 3) {//如果是焦点乱跳导致的
                            layoutManager?.findViewByPosition(currFocusTab.position)?.requestFocus()
                            unHasFocusEntity.clear()
                        } else {
                            currFocusTab.setCurrent(t, position)
                            viewModel.changeTab(currFocusTab.value, binding?.rvList)
                        }
                    }
                }
            }
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    layoutManager?.findViewByPosition(1)?.requestFocus()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    if (isLandScreen) {
                        viewBorder.attachTo(binding?.rvList)
                    } else {
                        viewBorder.detachFrom(binding?.rvList)
                    }
                }
            })
        }
        binding?.rvList?.run {
            layoutManager = AutoFocusGridLayoutManager(context, if (isLandScreen) 3 else 1).apply {
                //                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//                    override fun getSpanSize(position: Int): Int {
//                        return when (listAdapter?.getItemViewType(position)) {
//                            ItemType.SQUARE_CARD_COLLECTION,
//                            ItemType.HORIZONTAL_SCROLL_CARD -> 3
//                            else -> 1
//                        }
//                    }
//                }
            }
            adapter = listAdapter
            isFocusable = false
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as? GridLayoutManager
                    val position = layoutManager?.findLastVisibleItemPosition()
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && position == viewModel.datas.size - 1) {
                        if (!viewModel.currPageUrlsMap[viewModel.currPage].isNullOrEmpty()) {
                            viewModel.loadMore()
                        }
                    }
                }
            })
        }
        if (isLandScreen) {
            viewBorder2.attachTo(binding?.rvTitle)
        } else {
            viewBorder2.detachFrom(binding?.rvTitle)
        }

        onBackPressedDispatcher.addCallback(this) {
            doBack()
        }

        viewModel.onInit(currFocusTab.value)
    }

    override fun bindVM(): Class<MainViewModel> = MainViewModel::class.java

    override fun observe() {
        viewModel.dataUiState.observe(this) {
            it.t?.let { data ->
                execAsync({
                    if (data.currPage != viewModel.currPage) {
                        listAdapter?.notifyDataSetChanged()
                    } else listAdapter?.notifyItemRangeChanged(data.oldCount, data.currCount)
                }, this) {
                    while (binding?.rvList?.isComputingLayout == true) {
                        delay(1)
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val c = event?.unicodeChar ?: 0
//        Log.e("QL", "----->$keyCode $c ${Integer.toHexString(c)} ${c.toChar()} ")

        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                doFocusAction()
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                val pos = focusEntity.position
                if (pos < 3) {//焦点在第一排时
                    binding?.rvTitle?.layoutManager?.findViewByPosition(currFocusTab.position)
                        ?.requestFocus()
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

    private fun doFocusAction(t: Any? = null) {
//        Log.e("QL", "---1--->$t $currentFouseEntry")
        var info: LocalVideoInfo? = null
        when (val value = t ?: focusEntity.value) {
            is HomeRecommendInfo -> {
                info = value.data?.toLocalVideoInfo()
            }
            is DiscoverInfo -> {
                info = value.data?.toLocalVideoInfo()
            }
            is DailyInfo, is FollowInfo -> {
                info = when (value) {
                    is DailyInfo -> value.data?.content?.data
                    is FollowInfo -> value.data?.content?.data
                    else -> null
                }?.toLocalVideoInfo()
            }
        }

        val url = info?.playUrl
        if (url.isNullOrEmpty()) info?.videoId?.let {
            VideoDetailActivity.start(this, it)
        } else VideoDetailActivity.start(this, info!!)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun doBack() {
        if (focusEntity.value != null) {
            binding?.rvTitle?.layoutManager?.findViewByPosition(currFocusTab.position)
                ?.requestFocus()
            unHasFocusEntity.clear()
        } else if (currFocusTab.position != 1) {
            binding?.rvTitle?.smoothScrollToPosition(0)
            execAsync({
                binding?.rvTitle?.layoutManager?.findViewByPosition(1)?.requestFocus()
            }) { delay(100) }
        } else {
            if (System.currentTimeMillis() - lastPressBackTime > 1500) {
                showToast("再按一次退出")
                lastPressBackTime = System.currentTimeMillis()
            } else finish()
        }
    }
}
