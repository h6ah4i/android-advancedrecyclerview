package com.h6ah4i.android.widget.advrecyclerview.selectable;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class WeakHolderTracker<VH extends RecyclerView.ViewHolder> {
    private SparseArray<WeakReference<VH>> mHoldersByPosition =
            new SparseArray<>();

    /**
     * Returns the holder with a given position. If non-null, the returned
     * holder is guaranteed to have getAdapterPosition() == position.
     *
     * @param position
     * @return
     */
    public VH getHolder(int position) {
        WeakReference<VH> holderRef = mHoldersByPosition.get(position);
        if (holderRef == null) {
            return null;
        }

        VH holder = holderRef.get();
        if (holder == null || holder.getAdapterPosition() != position) {
            mHoldersByPosition.remove(position);
            return null;
        }

        return holder;
    }

    public void bindHolder(VH holder, int position) {
        mHoldersByPosition.put(position, new WeakReference<>(holder));
    }

    public List<VH> getTrackedHolders() {
        List<VH> holders = new ArrayList<VH>();

        for (int i = 0; i < mHoldersByPosition.size(); i++) {
            int position = mHoldersByPosition.keyAt(i);
            VH holder = getHolder(position);

            if (holder != null) {
                holders.add(holder);
            }
        }

        return holders;
    }


}
