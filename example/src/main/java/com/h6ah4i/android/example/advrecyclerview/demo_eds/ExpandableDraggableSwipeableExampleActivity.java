/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.example.advrecyclerview.demo_eds;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.fragment.ExpandableItemPinnedMessageDialogFragment;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractExpandableDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.fragment.ExampleExpandableDataProviderFragment;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

public class ExpandableDraggableSwipeableExampleActivity extends ActionBarActivity implements ExpandableItemPinnedMessageDialogFragment.EventListener {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ExampleExpandableDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecyclerListViewFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    /**
     * This method will be called when a group item is removed
     *
     * @param groupPosition
     */
    public void onGroupItemRemoved(int groupPosition) {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(R.string.snack_bar_text_group_item_removed)
                        .actionLabel(R.string.snack_bar_action_undo)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                onItemUndoActionClicked();
                            }
                        })
                        .actionColorResource(R.color.snackbar_action_color_done)
                        .duration(5000)
                        .type(SnackbarType.SINGLE_LINE)
                        .swipeToDismiss(false)
                , this);
    }

    /**
     * This method will be called when a child item is removed
     *
     * @param groupPosition
     * @param childPosition
     */
    public void onChildItemRemoved(int groupPosition, int childPosition) {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(R.string.snack_bar_text_child_item_removed)
                        .actionLabel(R.string.snack_bar_action_undo)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                onItemUndoActionClicked();
                            }
                        })
                        .actionColorResource(R.color.snackbar_action_color_done)
                        .duration(5000)
                        .type(SnackbarType.SINGLE_LINE)
                        .swipeToDismiss(false)
                , this);
    }

    /**
     * This method will be called when a group item is pinned
     *
     * @param groupPosition
     */
    public void onGroupItemPinned(int groupPosition) {
        final DialogFragment dialog = ExpandableItemPinnedMessageDialogFragment.newInstance(groupPosition, RecyclerView.NO_POSITION);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    /**
     * This method will be called when a child item is pinned
     *
     * @param groupPosition
     * @param childPosition
     */
    public void onChildItemPinned(int groupPosition, int childPosition) {
        final DialogFragment dialog = ExpandableItemPinnedMessageDialogFragment.newInstance(groupPosition, childPosition);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    public void onGroupItemClicked(int groupPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractExpandableDataProvider.GroupData data = getDataProvider().getGroupItem(groupPosition);

        if (data.isPinnedToSwipeLeft()) {
            // unpin if tapped the pinned item
            data.setPinnedToSwipeLeft(false);
            ((RecyclerListViewFragment) fragment).notifyGroupItemChanged(groupPosition);
        }
    }

    public void onChildItemClicked(int groupPosition, int childPosition) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractExpandableDataProvider.ChildData data = getDataProvider().getChildItem(groupPosition, childPosition);

        if (data.isPinnedToSwipeLeft()) {
            // unpin if tapped the pinned item
            data.setPinnedToSwipeLeft(false);
            ((RecyclerListViewFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    private void onItemUndoActionClicked() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        final long result = getDataProvider().undoLastRemoval();

        if (result == RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION) {
            return;
        }

        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(result);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(result);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            ((RecyclerListViewFragment) fragment).notifyGroupItemRestored(groupPosition);
        } else {
            // child item
            ((RecyclerListViewFragment) fragment).notifyChildItemRestored(groupPosition, childPosition);
        }
    }

    // implements ExpandableItemPinnedMessageDialogFragment.EventListener
    @Override
    public void onNotifyExpandableItemPinnedDialogDismissed(int groupPosition, int childPosition, boolean ok) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);

        if (childPosition == RecyclerView.NO_POSITION) {
            // group item
            getDataProvider().getGroupItem(groupPosition).setPinnedToSwipeLeft(ok);
            ((RecyclerListViewFragment) fragment).notifyGroupItemChanged(groupPosition);
        } else {
            // child item
            getDataProvider().getChildItem(groupPosition, childPosition).setPinnedToSwipeLeft(ok);
            ((RecyclerListViewFragment) fragment).notifyChildItemChanged(groupPosition, childPosition);
        }
    }

    public AbstractExpandableDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleExpandableDataProviderFragment) fragment).getDataProvider();
    }
}
