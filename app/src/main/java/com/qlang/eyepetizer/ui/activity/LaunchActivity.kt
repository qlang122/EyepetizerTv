package com.qlang.eyepetizer.ui.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.BaseActivity
import com.qlang.eyepetizer.config.startActivity
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Job

class LaunchActivity : BaseActivity() {
    private val TAG = "LaunchActivity"

    private val permissions = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    )

    private val splashDuration = 3 * 1000L

    private val job by lazy { Job() }
    private val alphaAnimation by lazy {
        AlphaAnimation(0.5f, 1.0f).apply {
            duration = splashDuration
            fillAfter = true
        }
    }

    private val scaleAnimation by lazy {
        ScaleAnimation(
            1f, 1.05f, 1f, 1.05f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = splashDuration
            fillAfter = true
        }
    }

    private val PRE_STA = 0x3003
    private val PRE_ACT1 = 0x3004
    private val handler = Handler {
        when (it.what) {
            PRE_STA -> {
                step2()
            }
            PRE_ACT1 -> {
                startActivity<MainActivity>()
                finish()
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            handler.sendEmptyMessageDelayed(PRE_ACT1, splashDuration)
        } else {
            requestPermission()
        }

        findViewById<ImageView>(R.id.iv_logo)?.let {
            it.visibility = View.VISIBLE
            it.startAnimation(alphaAnimation)
        }
        findViewById<ImageView>(R.id.iv_bg)?.startAnimation(scaleAnimation)
    }

    override fun getContentLayoutId() = R.layout.activity_launch

    private fun step2() {
        handler.sendEmptyMessageDelayed(PRE_ACT1, splashDuration)
    }

    private fun requestPermission() {
        PermissionX.init(this).permissions(*permissions)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, checkPermission(deniedList), "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, checkPermission(deniedList), "设置", "取消")
            }
            .request { _, _, _ ->
                handler.sendEmptyMessageDelayed(PRE_STA, splashDuration)
            }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun checkPermission(list: List<String>): String {
        val buffer = StringBuffer()
        list.any { permissions[0] == it }.also { if (it) buffer.append("需要存储权限存入照片！\n") }
        list.any { permissions[1] == it }.also { if (it) buffer.append("需要存储权限读取照片！\n") }
        list.any { permissions[2] == it }.also { if (it) buffer.append("需要读取设备状态权限！\n") }
        return buffer.toString()
    }
}
