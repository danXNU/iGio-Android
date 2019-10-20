package com.danitox.igio_android

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        /*val position = parent.getChildAdapterPosition(view)
        val isLast = position == state.itemCount - 1
        if (isLast) {
            outRect.bottom = space
            outRect.top = 0 //don't forget about recycling...
        }
        if (position == 0) {
            outRect.top = space
            // don't recycle bottom if first item is also last
            // should keep bottom padding set above
            if (!isLast)
                outRect.bottom = 0
        }*/

        outRect.top = space
        outRect.bottom = space
        outRect.left = space
        outRect.right = space
    }
}