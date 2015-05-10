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

package com.h6ah4i.android.example.advrecyclerview.demo_us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.fragment.ExampleDataProviderFragment;
import com.h6ah4i.android.example.advrecyclerview.common.fragment.ItemPinnedMessageDialogFragment;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;


public class UnderSwipeableExampleActivity extends AppCompatActivity implements ItemPinnedMessageDialogFragment.EventListener {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ExampleDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecyclerListViewFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    /**
     * This method will be called when a list item is removed
     *
     * @param position The position of the item within data set
     */
    public void onItemRemoved(int position) {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(R.string.snack_bar_text_item_removed)
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
     * This method will be called when a list item is pinned
     *
     * @param position The position of the item within data set
     */
    public void onItemPinned(int position) {
    }

    /**
     * This method will be called when a list item is clicked
     *
     * @param position The position of the item within data set
     */
    public void onItemClicked(int position) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractDataProvider.Data data = getDataProvider().getItem(position);

        if (data.isPinnedToSwipeLeft()) {
            // unpin if tapped the pinned item
            data.setPinnedToSwipeLeft(false);
            ((RecyclerListViewFragment) fragment).notifyItemChanged(position);
        }
    }
    /**
     * This method will be called when a "button placed under the swipeable view" is clicked
     *
     * @param position The position of the item within data set
     */
    public void onItemButtonClicked(int position) {
        String text = getString(R.string.snack_bar_text_button_clicked, position);

        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(text)
                        .type(SnackbarType.SINGLE_LINE)
                        .swipeToDismiss(true)
                , this);
    }

    private void onItemUndoActionClicked() {
        int position = getDataProvider().undoLastRemoval();
        if (position >= 0) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
            ((RecyclerListViewFragment) fragment).notifyItemInserted(position);
        }
    }

    // implements ItemPinnedMessageDialogFragment.EventListener
    @Override
    public void onNotifyItemPinnedDialogDismissed(int itemPosition, boolean ok) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);

        getDataProvider().getItem(itemPosition).setPinnedToSwipeLeft(ok);
        ((RecyclerListViewFragment) fragment).notifyItemChanged(itemPosition);
    }

    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleDataProviderFragment) fragment).getDataProvider();
    }
}
