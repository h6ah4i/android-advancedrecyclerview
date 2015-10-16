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

package com.h6ah4i.android.widget.advrecyclerview.selectable;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ListView;

import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;
import java.util.List;

class SelectableItemWrapperAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> {
    private static final String TAG = "ARVSelectableWrapper";

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private SelectableItemAdapter mSelectableItemAdapter;

    private RecyclerViewSelectionManager mSelectionManager;

    private static final String SELECTION_POSITIONS = "position";
    private static final String SELECTIONS_STATE = "state";
    private SparseBooleanArray mSelections = new SparseBooleanArray();
    private WeakHolderTracker<VH> mTracker = new WeakHolderTracker<VH>();

    private int mChoiceMode = ListView.CHOICE_MODE_SINGLE;
    private boolean mCheckable =false;

    public void setCheckable(boolean checkable) {
        boolean changed = mCheckable != checkable;
        mCheckable =checkable;

        if (changed) {
            refreshAllHolders();
        }
    }

    public boolean isCheckable() {
        return mCheckable && isSelectable();
    }

    public boolean isSelectable(int position) {
        VH holder = mTracker.getHolder(position);
        return isSelectable(holder);
    }

    public boolean isSelectable(VH holder) {
        if (holder instanceof SelectableItemViewHolder) {
            return isSelectable() && ((SelectableItemViewHolder)holder).isSelectable();
        } else {
            return false;
        }
    }

    public int getChoiceMode() {
        return mChoiceMode;
    }

    /**
     * <p>Toggle whether this MultiSelector is in selection mode or not.
     * {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemViewHolder#setSelectable(boolean)}
     * will be called on any attached holders as well.</p>
     *
     * @param mChoiceMode
     */
    public void setChoiceMode(int mChoiceMode) {
        this.mChoiceMode = mChoiceMode;

        refreshAllHolders();
    }

    /**
     *
     * @param position
     * @param isSelected Whether the item should be selected.
     */
    public void setSelected(int position, boolean isSelected) {
        VH holder = mTracker.getHolder(position);
        if (holder !=null) {
            setSelected(holder, isSelected);
        } else {
            if (mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
                for (int i = 0; i < mSelections.size(); i++) {
                    if (mSelections.valueAt(i)) {
                        mSelections.put(i, false);
                        notifyItemChanged(i);
                    }
                }
                mSelections.clear();
            }
            mSelections.put(position,isSelected);

            notifyItemChanged(position);
        }

    }

    /**
     *
     * @param holder     Holder to set selection value for.
     * @param isSelected Whether the item should be selected.
     */
    public void setSelected(VH holder, boolean isSelected) {
        if (mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
            for (SelectableItemViewHolder selectedViewHolder : getSelectedViewHolders()) {
                if (selectedViewHolder.getPosition() != holder.getAdapterPosition()) {
                    if (selectedViewHolder instanceof RecyclerView.ViewHolder) {
                        setSelectedInternal((VH)selectedViewHolder, false);
                    }
                }
            }
        }
        setSelectedInternal(holder, isSelected);
    }


    private void setSelectedInternal(VH holder, boolean isSelected) {
        mSelections.put(holder.getAdapterPosition(), isSelected);
        refreshHolder(mTracker.getHolder(holder.getAdapterPosition()));

        mSelectableItemAdapter.onItemSelected(holder, isSelected);
    }



    /**
     * <p>Returns whether a particular item is selected.</p>
     *
     * @param position The position to test selection for.
     * @return Whether the item is selected.
     */
    public boolean isSelected(int position) {
        return mSelections.get(position);
    }

    /**
     * <p>Returns whether a particular item is selected.</p>
     *
     * @param holder The holder to test selection for.
     * @return Whether the item is selected.
     */
    public boolean isSelected(VH holder) {
        return isSelected(holder.getPosition());
    }

    /**
     * <p>Sets selected to false for all positions. Will refresh
     * all bound holders.</p>
     */
    public void clearSelections() {
        mSelections.clear();
        refreshAllHolders();
    }

    /**
     * <p>Return a list of selected positions.</p>
     *
     * @return A list of the currently selected positions in reverse order.
     */
    public List<Integer> getSelectedPositions() {
        List<Integer> positions = new ArrayList<Integer>();

        for (int i = mSelections.size() -1; i >=0; i--) {
            if (mSelections.valueAt(i)) {
                positions.add(mSelections.keyAt(i));
            }
        }

        return positions;
    }

    /**
     * <p>Return a list of selected items.</p>
     *
     * @return A list of the currently selected items in reverse order.
     */
    public List<Object> getSelectedItems() {
        List<Object> items = new ArrayList<Object>();

        for (int i = mSelections.size() -1; i >=0; i--) {
            if (mSelections.valueAt(i)) {
                items.add(mSelectableItemAdapter.getItem(mSelections.keyAt(i)));
            }
        }

        return items;
    }

    /**
     * <p>Return a list of selected view holder.</p>
     *
     * @return A list of the currently selected view holders in reverse order.
     */
    public List<SelectableItemViewHolder> getSelectedViewHolders() {
        List<SelectableItemViewHolder> positions = new ArrayList<SelectableItemViewHolder>();

        for (int i = mSelections.size() -1; i >=0; i--) {
            if (mSelections.valueAt(i)) {
                VH viewHolder = mTracker.getHolder(mSelections.keyAt(i));
                if (viewHolder instanceof  SelectableItemViewHolder) {
                    positions.add((SelectableItemViewHolder)viewHolder);
                }
            }
        }

        return positions;
    }

    /**
     * <p>Bind a holder to a specific position/id. This implementation ignores the id.</p>
     * <p/>
     * <p>Bound holders will receive calls to {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemViewHolder#setSelectable(boolean)}
     * and {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemViewHolder#setActivated(boolean)} when
     * {@link #setChoiceMode(int)} is called, or when {@link #setSelected(VH, boolean)} is called for the
     * associated position, respectively.</p>
     *
     * @param holder   A holder to bind.
     * @param position Position the holder will be bound to.
     * @param id       Item id the holder will be bound to. Ignored in this implementation.
     */
    public void bindHolder(VH holder, int position, long id) {
        mTracker.bindHolder(holder, position);
        refreshHolder(holder);
    }

    /**
     * <p>Convenience method to ease invoking selection logic.
     * If its selectable, this method toggles selection
     * for the specified item and returns true. Otherwise, it returns false
     * and does nothing.</p>
     * <p/>
     * <p>Equivalent to:</p>
     * <pre>
     * {@code
     * if (multiSelector.isSelectable()) {
     *     boolean isSelected = isSelected(position, itemId);
     *     setSelected(position, itemId, !isSelected);
     *     return true;
     * } else {
     *     return false;
     * }
     * }
     * </pre>
     *
     * @return True if the item was toggled.
     */
    public boolean toggleSelection(VH holder) {
        if (isSelectable(holder)) {
            setSelected(holder, !isSelected(holder));
            return true;
        } else {
            return false;
        }
    }

    public boolean toggleSelection(int position) {
        VH holder = mTracker.getHolder(position);
        if (holder!=null) {
            return toggleSelection(holder);
        } else {
            if (isSelectable(position)) {
                setSelected(position, !isSelected(position));
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean isSelectable() {
        return mChoiceMode != ListView.CHOICE_MODE_NONE;
    }

    public void refreshAllHolders() {
        for (VH holder : mTracker.getTrackedHolders()) {
            refreshHolder(holder);
        }
    }

    private void refreshHolder(VH holder) {
        if (holder == null) {
            return;
        }

        if (holder instanceof SelectableItemViewHolder) {

            SelectableItemViewHolder selectableHolder = (SelectableItemViewHolder) holder;
            selectableHolder.setSelectable(isSelectable());
            selectableHolder.setCheckable(isCheckable());
            if (selectableHolder.isSelectable()) {
                boolean isActivated = mSelections.get(holder.getPosition());
                if (isActivated != selectableHolder.isActivated()) {
                    selectableHolder.setActivated(isActivated);
                    mSelectableItemAdapter.onItemSelected(holder, selectableHolder.isActivated());
                }

            } else {
                if (selectableHolder.isActivated()) {
                    selectableHolder.setActivated(false);
                    mSelectableItemAdapter.onItemSelected(holder, selectableHolder.isActivated());
                }
            }

            if (holder.itemView instanceof CheckableState) {
                CheckableState checkable = (CheckableState) holder.itemView;
                checkable.setCheckable(selectableHolder.isCheckable());
            }

            if (holder.itemView instanceof Checkable) {
                Checkable checkable = (Checkable) holder.itemView;
                checkable.setChecked(selectableHolder.isActivated());
            }


        }

    }


    /**
     * @return Bundle containing the states of the selection and a flag indicating if the multiselection is in
     * selection mode or not
     */

    public Bundle saveSelectionStates() {
        Bundle information = new Bundle();
        information.putIntegerArrayList(SELECTION_POSITIONS, (ArrayList<Integer>) getSelectedPositions());
        information.putInt(SELECTIONS_STATE, mChoiceMode);
        return information;
    }

    /**
     * restore the selection states of the multiselector and the ViewHolder Trackers
     *
     * @param savedStates
     */

    public void restoreSelectionStates(Bundle savedStates) {
        List<Integer> selectedPositions = savedStates.getIntegerArrayList(SELECTION_POSITIONS);
        restoreSelections(selectedPositions);
        mChoiceMode = savedStates.getInt(SELECTIONS_STATE);

    }

    private void restoreSelections(List<Integer> selected) {
        if (selected == null) return;
        int position;
        mSelections.clear();
        for (int i = 0; i < selected.size(); i++) {
            position = selected.get(i);
            mSelections.put(position, true);
        }
        refreshAllHolders();
    }

    public SelectableItemWrapperAdapter(RecyclerViewSelectionManager manager, RecyclerView.Adapter<VH> adapter) {
        super(adapter);

        mSelectableItemAdapter = getSelectableItemAdapter(adapter);
        if (mSelectableItemAdapter == null) {
            throw new IllegalArgumentException("adapter does not implement SelectableItemAdapter");
        }

        if (manager == null) {
            throw new IllegalArgumentException("manager cannot be null");
        }

        mSelectionManager = manager;
    }

    @Override
    protected void onRelease() {
        super.onRelease();

        mSelectableItemAdapter = null;
        mSelectionManager = null;

    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);

        // reset SelectableHolder state
        if (holder instanceof SelectableItemViewHolder) {
            ((SelectableItemViewHolder) holder).setSelectable(isSelectable());
            ((SelectableItemViewHolder) holder).setActivated(false);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH holder = super.onCreateViewHolder(parent, viewType);

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        super.onBindViewHolder(holder, position);

        bindHolder(holder,position,getItemId(position));
    }

    private static SelectableItemAdapter getSelectableItemAdapter(RecyclerView.Adapter adapter) {
        return WrapperAdapterUtils.findWrappedAdapter(adapter, SelectableItemAdapter.class);
    }

}
