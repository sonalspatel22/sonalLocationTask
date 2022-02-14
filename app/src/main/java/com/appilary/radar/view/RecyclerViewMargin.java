package com.appilary.radar.view;

import android.graphics.Rect;
import androidx.annotation.IntRange;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration {
    private final int columns;
    private int margin;

    /**
     * constructor
     *
     * @param margin desirable margin size in px between the views in the recyclerView
     */
    public RecyclerViewMargin(@IntRange(from = 0) int margin) {
        columns = 1;
        this.margin = margin;

    }

    public RecyclerViewMargin(@IntRange(from = 0) int margin, @IntRange(from = 0) int columns) {
        this.columns = columns;
        this.margin = margin;

    }

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);
        //set right margin to all
        outRect.right = margin;
        //set bottom margin to all
        outRect.bottom = margin;
        //we only add top margin to the first row
        if (position < columns) {
            outRect.top = margin;
        }
        //add left margin only to the first column
        if (position % columns == 0) {
            outRect.left = margin;
        }
    }
}