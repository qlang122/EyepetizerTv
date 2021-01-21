package com.qlang.eyepetizer.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.ktx.execAsync
import com.qlang.eyepetizer.model.MainViewModel
import com.qlang.eyepetizer.mvvm.BaseVMActivity
import com.qlang.eyepetizer.ui.adapter.HomeListAdapter
import com.qlang.eyepetizer.ui.adapter.TabTitleAdapter
import com.qlang.tvwidget.AutoFocusGridLayoutManager
import com.qlang.tvwidget.BorderEffect
import com.qlang.tvwidget.BorderView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseVMActivity<MainViewModel>() {
    private var lastPressBackTime = 0L

    private var listAdapter: HomeListAdapter? = null

    private var focusEntity = FocusEntity<Any>()
    private var unHasFocusEntity = FocusEntity<Any>()
    private lateinit var currFocusTab: FocusEntity<TabTitleInfo>

    private val viewBorder by lazy {
        BorderView<RecyclerView>(this).apply {
            setBackgroundResource(R.drawable.item_border_bg)
        }
    }
    private val viewBorder2 by lazy {
        BorderView<RecyclerView>(this).apply {
            getEffect<BorderEffect>()?.scale = 1.08f
            setBackgroundResource(R.drawable.item_border_empty_bg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getContentLayoutId() = R.layout.activity_main

    override fun initView() {
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
        rv_title?.apply {
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
                            viewModel.changeTab(currFocusTab.value, rv_list)
                        }
                    }
                }
            }
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    layoutManager?.findViewByPosition(1)?.requestFocus()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewBorder.attachTo(rv_list)
                }
            })
        }
        rv_list?.apply {
            layoutManager = AutoFocusGridLayoutManager(context, 3).also {
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
        //日期：2020-10-27 点击：2144
        viewBorder2.attachTo(rv_title)

        viewModel.onInit(currFocusTab.value)
    }

    override fun bindVM(): Class<MainViewModel> = MainViewModel::class.java

    override fun observe() {
        viewModel.dataUiState.observe(this, Observer {
            it.t?.let { data ->
                execAsync({
                    while (rv_list.isComputingLayout) {
                    }
                }, {
                    if (data.currPage != viewModel.currPage) listAdapter?.notifyDataSetChanged()
                    else listAdapter?.notifyItemRangeChanged(data.oldCount, data.currCount)
                }, this)
            }
        })
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
                    rv_title?.layoutManager?.findViewByPosition(currFocusTab.position)?.requestFocus()
                    return true
                }
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                onBackPressed();return true
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

    override fun onBackPressed() {
        if (focusEntity.value != null) {
            rv_title?.layoutManager?.findViewByPosition(currFocusTab.position)?.requestFocus()
            unHasFocusEntity.clear()
        } else if (currFocusTab.position != 1) {
            rv_title?.layoutManager?.findViewByPosition(1)?.requestFocus()
        } else {
            if (System.currentTimeMillis() - lastPressBackTime > 1500) {
                showToast("再按一次退出")
                lastPressBackTime = System.currentTimeMillis()
            } else finish()
        }
    }
}
