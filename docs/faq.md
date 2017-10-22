## Drag & Drop

### Unexpected item animations

#### [:octocat: Repeated animation problem #5](https://github.com/h6ah4i/android-advancedrecyclerview/issues/5)

??? question "Question"
    After Drag&Drop the dragged cell restores to original position and starts the translation animation again to its dropped position. How can I fix this? Thank a lot.

??? info "Answer"
     This library requires stable ID. If the getItemId() method is not implemented properly, will causes exceptions or will animates incorrectly. 


!!! summary "Related issues"
    - [:octocat: Issues with stable ids #7](https://github.com/h6ah4i/android-advancedrecyclerview/issues/7)
    - [:octocat: Position Changing Between List Item #22](https://github.com/h6ah4i/android-advancedrecyclerview/issues/22)

---



## Swipeable

#### [:octocat: [HELP] Swipe animation not like example #199](https://github.com/h6ah4i/android-advancedrecyclerview/issues/199)

??? question "Question"
    Hello !
    I implemented drag&drop Recycler View as in your example and I have some annoying problem. When I swipe some item RecyclerView animates position changing. It looks like playing back my drag gesture.
    
    How to remove this effect ? I need from dropped element to stay where I left it, not animating again.
    
    Here you have a link for YouTube video with described effect : https://youtu.be/V3eBLiAkwws

??? info "Answer"
    This library requires stable ID to work properly. Please call `setHasStableIds(true)` and implement the `getItemId()` method properly.


!!! summary "Related issues"
    - [:octocat: Issues with stable ids #7](https://github.com/h6ah4i/android-advancedrecyclerview/issues/7)
    - [:octocat: Repeated animation problem #5](https://github.com/h6ah4i/android-advancedrecyclerview/issues/5)

---


## Expandable

### Click event handling

#### [:octocat: [Expand Problem] : How to lock expanding of a specific group using onBindGroupViewHolder? #302](https://github.com/h6ah4i/android-advancedrecyclerview/issues/302)

??? question "Question"
    Hi,
    
    i want to lock expanding of some specific groups (ie. if the group has only one child) and want to set the groups other click event listener in my custom ExpandableItemAdapter class.
    
    i think i need to implement in onBindGoupViewHolder, but how?

??? check "Answer"
    If you want to custom click event handling, make the `onCheckCanExpandOrCollapseGroup()` method always returns false, then set click listener in `onCreateGroupViewHolder()` (or in `onBindGroupViewHolder()`). In the click event handler, `collapseGroup()` / `expandGroup()` methods can be used to control the group's expanded state.
    
    ```java
    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
    }
    
    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(..., parent, false);
        MyGroupViewHolder vh = new MyGroupViewHolder(v);
        vh.mSomeClickableView.setOnClickListener(this);
        return vh;
    }
    
    @Override
    public void onClick(View v) {
        RecyclerView.ViewHolder vh = RecyclerViewAdapterUtils.getViewHolder(v);
        int flatPosition = vh.getAdapterPosition();
    
        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }
    
        long expandablePosition = mExpandableItemManager.getExpandablePosition(flatPosition);
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);
    
        // toggle expanded/collapsed
        if (mExpandableItemManager.isGroupExpanded(groupPosition)) {
            mExpandableItemManager.collapseGroup(groupPosition);
        } else {
            mExpandableItemManager.expandGroup(groupPosition);
        }
    }
    ```

---


### Unexpected item animations

#### [:octocat: Position Changing Between List Item #22](https://github.com/h6ah4i/android-advancedrecyclerview/issues/22)

??? question "Question"
    Hi, I have problem implementing expandable list with fixed recyclerview. Please see the video https://www.youtube.com/watch?v=fkcmtkZz3zo
    
    The way I do it is to resize recyclerview and recalculate the height based on how much item is expanded.
    Do you think it's possible to fix this issue?

??? info "Answer"
    Hi. Have you implemented the `getGroupId()` and `getChildId()` methods properly that return stable IDs?


!!! summary "Related issues"
    - [:octocat: Issues with stable ids #7](https://github.com/h6ah4i/android-advancedrecyclerview/issues/7)
    - [:octocat: Repeated animation problem #5](https://github.com/h6ah4i/android-advancedrecyclerview/issues/5)

 ---


