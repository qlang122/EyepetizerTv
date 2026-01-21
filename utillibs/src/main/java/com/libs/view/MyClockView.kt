package com.libs.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import com.libs.utils.R
import java.util.*

/**
 * @author Created by qlang on 2018/11/19.
 */
class MyClockView : View, Runnable {
    private var numModel = 0
    private var textSize = 16f
    private var textColor = Color.DKGRAY
    private var textOffset = 0f
    private val textBounds = Rect()

    private val hand_path = Path()

    private var bitmap_clock_bg: Bitmap? = null
    private var bitmap_second_hand: Bitmap? = null
    private var bitmap_minute_hand: Bitmap? = null
    private var bitmap_hour_hand: Bitmap? = null
    private var bitmap_centre_point: Bitmap? = null

    private var clock_bg_rect = Rect()
    private var centre_point_rect = Rect()

    private var handSecond_width = 12f
    private var handMinute_width = 8f
    private var handHour_width = 4f

    private val bitmap_matrix = Matrix()
    private val paint = Paint().apply {
        color = Color.DKGRAY
        isAntiAlias = true
        style = Paint.Style.FILL
        isDither = true
        strokeWidth = 4f
        textSize = 24f
    }

    private val calendar = Calendar.getInstance()

    private var thread: Thread? = null
    private var isRuning = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ClockView)
        numModel = attributes.getInt(R.styleable.ClockView_num_model, 0)

        textSize = attributes.getDimension(R.styleable.ClockView_textSize, 16f)
        textColor = attributes.getColor(R.styleable.ClockView_textColor, Color.DKGRAY)
        textOffset = attributes.getDimension(R.styleable.ClockView_textTopOffset, 0f)

        handSecond_width = attributes.getDimension(R.styleable.ClockView_hand_second_width, 12f)
        handMinute_width = attributes.getDimension(R.styleable.ClockView_hand_minute_width, 8f)
        handHour_width = attributes.getDimension(R.styleable.ClockView_hand_hour_width, 4f)

        bitmap_clock_bg = attributes.getDrawable(R.styleable.ClockView_clock_background)?.let {
            (it as? BitmapDrawable)?.bitmap
        }
        bitmap_second_hand = attributes.getDrawable(R.styleable.ClockView_hand_second)?.let {
            (it as? BitmapDrawable)?.bitmap
        }
        bitmap_minute_hand = attributes.getDrawable(R.styleable.ClockView_hand_minute)?.let {
            (it as? BitmapDrawable)?.bitmap
        }
        bitmap_hour_hand = attributes.getDrawable(R.styleable.ClockView_hand_hour)?.let {
            (it as? BitmapDrawable)?.bitmap
        }
        bitmap_centre_point = attributes.getDrawable(R.styleable.ClockView_centre_point)?.let {
            (it as? BitmapDrawable)?.bitmap
        }
        attributes.recycle()

        paint.textSize = textSize

        thread = Thread(this)
        thread?.start()

    }

    fun stop() {
        isRuning = false
    }

    override fun run() {
        isRuning = true
        while (isRuning) {
            postInvalidate()
            SystemClock.sleep(500)
        }
        isRuning = false
    }

    override fun onDraw(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()

        canvas?.let {
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            it.drawColor(Color.TRANSPARENT)
            if (bitmap_clock_bg == null) {
                paint.color = Color.WHITE
                it.drawCircle(width / 2f, height / 2f, width / 2f, paint)
                paint.color = Color.WHITE
                paint.setShadowLayer(2f, 0f, 0f, Color.GRAY)
                it.drawCircle(width / 2f, height / 2f, width / 4f, paint)
                paint.clearShadowLayer()
            } else {
                clock_bg_rect.set(0, 0, width, height)
                it.drawBitmap(bitmap_clock_bg!!, null, clock_bg_rect, paint)
            }
            paint.color = textColor
            for (i in 0..60) {
                if (i % (if (numModel == 1) 15 else 5) == 0) {
                    val txt = if (i == 0) "12" else "${i / 5}"
                    textBounds.setEmpty()
                    paint.getTextBounds(txt, 0, txt.length, textBounds)
                    val length = (width / 2f) - textOffset - textBounds.height() / 2f
                    val dx = ((width / 2f) + Math.cos(Math.toRadians((360 - ((i * 6.0) - 90)) % 360)) * length).toFloat()
                    val dy = ((height / 2f) - Math.sin(Math.toRadians((360 - ((i * 6.0) - 90)) % 360)) * length).toFloat()
                    it.drawText(txt, dx - (textBounds.width() / 2f), dy + (textBounds.width() / 2f), paint)
                }
            }

            val secondRadian = Math.toRadians((360 - ((second * 6.0) - 90)) % 360)
            val minuteRadian = Math.toRadians((360 - ((minute * 6.0) - 90)) % 360)
            val hourRadian = Math.toRadians((360 - ((hour * 30.0) - 90)) % 360 - (30.0 * minute / 60))

            paint.color = Color.DKGRAY
            paint.strokeWidth = handHour_width
            drawHands(it, bitmap_hour_hand, hourRadian, ((width / 2) * (1f / 2f)))
            paint.strokeWidth = handMinute_width
            drawHands(it, bitmap_minute_hand, minuteRadian, ((width / 2) * (4f / 5f)))

            bitmap_centre_point?.let { bitmap ->
                it.save()
                it.translate(width / 2f - bitmap.width / 2f, height / 2f - bitmap.height / 2f)
                centre_point_rect.set(0, 0, bitmap.width, bitmap.height)
                it.drawBitmap(bitmap, null, centre_point_rect, paint)
                it.restore()
            }
            paint.color = Color.RED
            paint.strokeWidth = handSecond_width
            drawHands(it, bitmap_second_hand, secondRadian, ((width / 2) * (9f / 10f)))

        }
    }

    private fun drawHands(canvas: Canvas, bitmap: Bitmap?, radian: Double, length: Float) {
        val c_x = width.toFloat() / 2
        val c_y = height.toFloat() / 2

        if (bitmap == null) {
            val dx = (c_x + Math.cos(radian) * length).toFloat()
            val dy = (c_x - Math.sin(radian) * length).toFloat()
            canvas.drawLine(c_x, c_y, dx, dy, paint)
        } else {
            canvas.save()
            bitmap_matrix.apply {
                val scale = if ((width / 2f) > bitmap.width)
                    (bitmap.width / (width / 2f))
                else ((width / 2f) / bitmap.width)
                postScale(scale, scale, 0f, bitmap.height / 2f)
                postRotate(-Math.toDegrees(radian).toFloat(), bitmap.width * 1f / 9f, bitmap.height / 2f)
            }
            canvas.translate(width / 2f - bitmap.width * 1f / 9f, height / 2f - bitmap.height / 2f)
            canvas.drawBitmap(bitmap, bitmap_matrix, paint)
            bitmap_matrix.reset()
            canvas.restore()
        }
    }
}