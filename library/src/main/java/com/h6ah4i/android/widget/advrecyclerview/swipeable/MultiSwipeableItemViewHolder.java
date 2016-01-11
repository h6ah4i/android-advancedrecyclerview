package com.h6ah4i.android.widget.advrecyclerview.swipeable;

import android.view.View;

public interface MultiSwipeableItemViewHolder {
    int getCurrentSwipeLevel();
    int getMaxSwipeLevel();
    View getSwipeableContainerView(int level);
}
