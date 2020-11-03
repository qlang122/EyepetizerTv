package com.libs.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.graphics.Rect
import android.graphics.Bitmap

/**
 * @author Created by qlang on 2017/7/24.
 */
class FaceMaskView : View {
    private var paint = Paint()
    private var textPaint = Paint()
    private var pointPaint = Paint()
    private var frameRect = Rect()

    private var isMirror = false
    private var isLandscape = true
    private var isShowTxt = false

    private var faceBitmap: Bitmap? = null
    private var faceResId = -1
    private var faceInfos: ArrayList<FaceInfo> = arrayListOf()

    private var frameWidth = 0
    private var frameHeight = 0
    private var viewWidth = 0
    private var viewHeight = 0

    private var scale_X = 1.0f
    private var scale_Y = 1.0f

    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context?) {
        paint.color = 0xFFFD7C36.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = if (null != context) dp2px(context, 2f).toFloat() else 4f

        pointPaint.color = 0xFFFD7C36.toInt()
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeWidth = if (null != context) dp2px(context, 8f).toFloat() else 8f

        textPaint.color = 0xFFFF0011.toInt()
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        textPaint.textSize = if (null != context) dp2px(context, 16f).toFloat() else 24f
    }

    fun setFaceInfo(info: List<FaceInfo>) {
        synchronized(faceInfos) { faceInfos.clear();faceInfos.addAll(info) }
        this.postInvalidate()
    }

    fun setFrameResId(var1: Int) {
        faceResId = var1
    }

    fun getFaceInfos(): ArrayList<FaceInfo>? {
        return faceInfos
    }

    fun setFrameColor(v: Int) {
        if (v > 0) paint.color = v
    }

    fun setFrameColor(v: String) {
        if (v.matches(Regex("^#(([\\da-fA-F]{6})|([\\da-fA-F]{8}))")))
            paint.color = Color.parseColor(v)
    }

    fun setReflection(var1: Boolean) {
        isMirror = var1
    }

    fun setShowTips(var1: Boolean) {
        isShowTxt = var1
    }

    fun setScreenLandscape(`val`: Boolean) {
        isLandscape = `val`
    }

    fun setFrameSize(width: Int, height: Int) {
        frameWidth = width
        frameHeight = height

        scale_X = if (isLandscape) viewWidth.toFloat() / frameWidth else viewHeight.toFloat() / frameWidth
        scale_Y = if (isLandscape) viewHeight.toFloat() / frameHeight else viewWidth.toFloat() / frameHeight
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = measuredWidth
        viewHeight = measuredHeight
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        faceInfos.forEach { face ->
            if (null == face) return
            val left = face.x.times(scale_X).toInt()
            val top = face.y.times(scale_Y).toInt()
            val w = face.width.times(scale_X).toInt()
            val h = face.height.times(scale_Y).toInt()

            if (isLandscape) {
                if (isMirror) {
                    val fw = frameWidth.times(scale_X).toInt()
                    frameRect.set(fw - left - w, top, fw - left, top.plus(h))
                } else {
                    frameRect.set(left, top, left.plus(w), top.plus(h))
                }
            } else {
                if (isMirror) {
//                    val fh = frameWidth.times(scale_X).toInt()
                    val fw = frameHeight.times(scale_Y).toInt()
                    frameRect.set(fw - top - w, /*fh - */left, fw - top, /*fh - */left + h)
                } else {
                    frameRect.set(top, left, top.plus(h), left.plus(w))
                }
            }
//            canvas?.drawText("[$viewWidth $viewHeight $frameWidth $frameHeight $left $top $w $h $scale_X]",
//                    20f, 150f, textPaint)
//            canvas?.drawText("[${frameRect.left} ${frameRect.top} ${frameRect.width()} ${frameRect.height()}]",
//                    frameRect.left.toFloat(), frameRect.top.toFloat() - 40, textPaint)

            if (isShowTxt) canvas?.drawText(face.text, frameRect.left.toFloat(), frameRect.top.toFloat() - 20, textPaint)

            face.points?.forEach { canvas?.drawPoint(it.x.times(scale_X), it.y.times(scale_Y), pointPaint) }

            canvas?.drawRect(frameRect, paint)
        }
    }

    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    class FaceInfo {
        var id: Int = 0
        var x: Int = 0
        var y: Int = 0
        var width: Int = 0
        var height: Int = 0
        var degree: Int = 0
        var text = ""
        var points: List<PointF>? = null

        constructor(x: Int, y: Int, width: Int, height: Int) {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }

        constructor(id: Int, x: Int, y: Int, width: Int, height: Int, degree: Int, text: String) {
            this.id = id
            this.x = x
            this.y = y
            this.width = width
            this.height = height
            this.degree = degree
            this.text = text
        }
    }
}