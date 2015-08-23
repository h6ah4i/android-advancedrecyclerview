package com.h6ah4i.android.widget.advrecyclerview.selectable;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.h6ah4i.android.widget.advrecyclerview.R;

/**
 * <p>
 * A holder extended to support having a selectable mode with a state list animator.</p>
 * <p/>
 * <p>When {@link #setSelectable(boolean)} is set to true, itemView's
 * StateListAnimator is set to selectionModeStateListAnimator.
 * When it is set to false defaultModeStateListAnimator are used.</p>
 * <p/>
 * <p> selectionModeStateListAnimator defaults to a raise animation that animates selection
 * items to a 12dp translationZ.</p>
 * <p/>
 * <p>(Thanks to <a href="https://github.com/kurtisnelson/">Kurt Nelson</a> for examples and discussion on approaches here.)</p>
 */
public  class ElevatingSelectableViewHolder extends RecyclerView.ViewHolder implements SelectableItemViewHolder {

    private boolean mIsSelectable = false;
    private StateListAnimator mSelectionModeStateListAnimator;
    private StateListAnimator mDefaultModeStateListAnimator;

    /**
     * <p>Construct a new standalone SelectableHolder.</p>
     * <p/>
     * <p>Selectable state can be controlled manually by setting {@link #setSelectable(boolean)}.</p>
     *
     * @param itemView Item view for this ViewHolder
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ElevatingSelectableViewHolder(View itemView) {
        super(itemView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSelectionModeStateListAnimator(getRaiseStateListAnimator(itemView.getContext()));
            setDefaultModeStateListAnimator(itemView.getStateListAnimator());
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static StateListAnimator getRaiseStateListAnimator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return AnimatorInflater.loadStateListAnimator(context, R.anim.raise);
        } else {
            return null;
        }
    }

    /**
     * <p>State list animator to use when in selection mode. This defaults
     * to an animator that raises the view when <code>state_activated==true</code>.</p>
     *
     * @return A state list animator
     */
    public StateListAnimator getSelectionModeStateListAnimator() {
        return mSelectionModeStateListAnimator;
    }

    /**
     * Set the state list animator to use when in selection mode.
     *
     * @param selectionModeStateListAnimator A state list animator
     */
    public void setSelectionModeStateListAnimator(StateListAnimator selectionModeStateListAnimator) {
        mSelectionModeStateListAnimator = selectionModeStateListAnimator;
    }

    /**
     * <p>Set the state list animator to use when in selection mode. If not run
     * on a Lollipop device, this method is a no-op.</p>
     *
     * @param resId A state list animator resource id. Ignored prior to Lollipop.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setSelectionModeStateListAnimator(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator animator =
                    AnimatorInflater.loadStateListAnimator(itemView.getContext(), resId);

            setSelectionModeStateListAnimator(animator);
        }
    }

    /**
     * Get the state list animator to use when not in selection mode.
     * This value defaults to the animator set on {@link #itemView} at
     * construction time.
     *
     * @return A state list animator
     */
    public StateListAnimator getDefaultModeStateListAnimator() {
        return mDefaultModeStateListAnimator;
    }

    /**
     * Set the state list animator to use when in default mode. If not run
     * on a Lollipop device, this method is a no-op.
     *
     * @param resId A state list animator resource id. Ignored prior to Lollipop.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setDefaultModeStateListAnimator(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator animator =
                    AnimatorInflater.loadStateListAnimator(itemView.getContext(), resId);

            setDefaultModeStateListAnimator(animator);
        }

    }

    /**
     * Set the state list animator to use when not in selection mode.
     *
     * @param defaultModeStateListAnimator A state list animator
     */
    public void setDefaultModeStateListAnimator(StateListAnimator defaultModeStateListAnimator) {
        mDefaultModeStateListAnimator = defaultModeStateListAnimator;
    }

    /**
     * Whether this holder is currently activated/selected.
     * <p/>
     * Calls through to {@link android.view.View#setActivated(boolean)} on {@link #itemView}.
     *
     * @return True if the view is activated.
     */
    public boolean isActivated() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB){
            return itemView.isSelected();
        } else {
            return itemView.isActivated();
        }
    }

    /**
     * Activate/select this holder.
     * <p/>
     * Calls through to {@link android.view.View#isActivated()} on {@link #itemView}.
     *
     * @param isActivated True to activate the view.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setActivated(boolean isActivated) {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP && isActivated){
            Animation animation= AnimationUtils.loadAnimation(itemView.getContext(), R.anim.raise_prelollipop);
           itemView.startAnimation(animation);
        }

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
            itemView.setSelected(isActivated);
        } else {
            itemView.setActivated(isActivated);
        }
    }

    /**
     * Returns whether {@link #itemView} is currently in a
     * selectable mode.
     *
     * @return True if selectable.
     */
    public boolean isSelectable() {
        return mIsSelectable;
    }

    /**
     * Turns selection mode on and off.
     * If in Lollipop or greater versions, the same applies to
     * {@link #getSelectionModeStateListAnimator()} and
     * {@link #getDefaultModeStateListAnimator()}.
     *
     * @param isSelectable True if selectable.
     */
    public void setSelectable(boolean isSelectable) {
        boolean changed = isSelectable != mIsSelectable;
        mIsSelectable = isSelectable;

        if (changed) {
            refreshChrome();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void refreshChrome() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator animator = mIsSelectable ? mSelectionModeStateListAnimator
                    : mDefaultModeStateListAnimator;

            itemView.setStateListAnimator(animator);

            if (animator != null) {
                animator.jumpToCurrentState();
            }
        }
    }


}
