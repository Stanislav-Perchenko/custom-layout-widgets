package com.alperez.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by stanislav.perchenko on 5/2/2018
 */
public class UniformVerticalRecyclerItemSpace extends RecyclerView.ItemDecoration {

    private final int itemSpaceHorizontal, itemSpaceVertical;
    private final int itemSpaceVerticalHalf;

    public UniformVerticalRecyclerItemSpace(int itemSpace) {
        itemSpaceHorizontal = itemSpace;
        itemSpaceVertical = itemSpace;
        itemSpaceVerticalHalf = Math.round((float) itemSpace/2f);
    }

    public UniformVerticalRecyclerItemSpace(int itemSpaceHorizontal, int itemSpaceVertical) {
        this.itemSpaceVertical = itemSpaceVertical;
        this.itemSpaceHorizontal = itemSpaceHorizontal;
        itemSpaceVerticalHalf = Math.round((float) itemSpaceVertical/2f);
    }

    public int getItemSpaceHorizontal() {
        return itemSpaceHorizontal;
    }

    public int getItemSpaceVertical() {
        return itemSpaceVertical;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.set(itemSpaceHorizontal, itemSpaceVertical, itemSpaceHorizontal, itemSpaceVerticalHalf);
        } else if (position == (parent.getAdapter().getItemCount() - 1)) {
            outRect.set(itemSpaceHorizontal, itemSpaceVerticalHalf, itemSpaceHorizontal, itemSpaceVertical);
        } else {
            outRect.set(itemSpaceHorizontal, itemSpaceVerticalHalf, itemSpaceHorizontal, itemSpaceVerticalHalf);
        }


    }
}
