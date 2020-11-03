package com.qlang.tvwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoFocusGridLayoutManager : GridLayoutManager {
    constructor(context: Context, spanCount: Int) : super(context, spanCount) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
    }

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) :
            super(context, spanCount, orientation, reverseLayout) {
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onFocusSearchFailed(focused: View, focusDirection: Int, recycler: RecyclerView.Recycler,
                                     state: RecyclerView.State): View? {
        // Need to be called in order to layout new row/column
        val nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state)
                ?: return null

        val fromPos = getPosition(focused)
        val nextPos = getNextViewPos(fromPos, focusDirection)

        return findViewByPosition(nextPos)
    }

    /**
     * Manually detect next view to focus.
     *
     * @param fromPos   from what position start to seek.
     * @param direction in what direction start to seek. Your regular
     * `View.FOCUS_*`.
     * @return adapter position of next view to focus. May be equal to
     * `fromPos`.
     */
    protected fun getNextViewPos(fromPos: Int, direction: Int): Int {
        val offset = calcOffsetToNextView(direction)

        if (hitBorder(fromPos, offset)) {
            //return fromPos;
        }
        return fromPos + offset
    }

    /**
     * Calculates position offset.
     *
     * @param direction regular `View.FOCUS_*`.
     * @return position offset according to `direction`.
     */
    protected fun calcOffsetToNextView(direction: Int): Int {
        val spanCount = spanCount
        val orientation = orientation

        if (orientation == LinearLayoutManager.VERTICAL) {
            when (direction) {
                View.FOCUS_DOWN -> return spanCount
                View.FOCUS_UP -> return -spanCount
                View.FOCUS_RIGHT -> return 1
                View.FOCUS_LEFT -> return -1
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            when (direction) {
                View.FOCUS_DOWN -> return 1
                View.FOCUS_UP -> return -1
                View.FOCUS_RIGHT -> return spanCount
                View.FOCUS_LEFT -> return -spanCount
            }
        }
        return 0
    }

    /**
     * Checks if we hit borders.
     *
     * @param from   from what position.
     * @param offset offset to new position.
     * @return `true` if we hit border.
     */
    private fun hitBorder(from: Int, offset: Int): Boolean {
        val spanCount = spanCount
        return if (Math.abs(offset) == 1) {
            val spanIndex = from % spanCount
            val newSpanIndex = spanIndex + offset
            newSpanIndex < 0 || newSpanIndex >= spanCount
        } else {
            val newPos = from + offset
            newPos < 0 || newPos >= spanCount
        }
    }
}