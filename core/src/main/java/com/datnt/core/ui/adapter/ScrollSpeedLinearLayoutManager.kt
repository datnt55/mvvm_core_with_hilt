package com.datnt.core.ui.adapter

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView


class ScrollSpeedLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                val MILLISECONDS_PER_INCH = 50f
                override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                    return this@ScrollSpeedLinearLayoutManager
                        .computeScrollVectorForPosition(targetPosition)
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }
            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}