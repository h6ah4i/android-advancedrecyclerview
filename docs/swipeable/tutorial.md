!!! tip "Just looking for a sample code?"
    :octocat: Check the [minimal swipe sample code on GitHub](https://github.com/h6ah4i/android-advancedrecyclerview/blob/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_minimal/MinimalSwipeableExampleActivity.java).

## Tutorial

### Step 1. Make the adapter supports stable IDs

!!! attention
    **This step is very important. If adapter does not return stable & unique IDs, that will cause some weird behaviors (wrong animations, NPE, etc...)**

```java
class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    MyAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        // requires static value, it means need to keep the same value
        // even if the item position has been changed.
        return mItems.get(position).getId();
    }
}
```

### Step 2. Modify layout file of item views

Wrap content views with another `FrameLayout` whitch has `@+id/container` ID.

```xml
<!-- for itemView -->
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="56dp">
    <!-- Content View(s) -->
    <TextView
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"/>
</FrameLayout>
```


```xml
<!-- for itemView -->
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="56dp">

    <!-- for getSwipeableContainerView() -->
    <FrameLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <!-- Content View(s) -->
        <TextView
            android:id="@android:id/text1"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"/>

    </FrameLayout>
</FrameLayout>
```

### Step 3. Modify ViewHolder

1. Change parent class to [`AbstractSwipeableItemViewHolder`](https://github.com/h6ah4i/android-advancedrecyclerview/blob/master/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractSwipeableItemViewHolder.java).
2. Implement `getSwipeableContainerView()` method


!!! note
    The [`AbstractSwipeableItemViewHolder`](https://github.com/h6ah4i/android-advancedrecyclerview/blob/master/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractSwipeableItemViewHolder.java) class is a convenience class which implements boilerplace methods of [`SwipeableItemViewHolder`](https://github.com/h6ah4i/android-advancedrecyclerview/blob/master/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/swipeable/SwipeableItemViewHolder.java).


```java
class MyAdapter ... {
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        MyViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(android.R.id.text1);
        }
    }
    ...
}
```


```java
class MyAdapter ... {
    static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        TextView textView;
        FrameLayout containerView;

        public MyViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(android.R.id.text1);
            containerView = (FrameLayout) v.findViewById(R.id.container);
        }

        @Override
        public View getSwipeableContainerView() {
            return containerView;
        }
    }
}
```

### Step 4. Implement the `SwipeableItemAdapter` interface

```java
class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    ...
}
```



```java

class MyAdapter
        extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
        implements SwipeableItemAdapter<MyAdapter.MyViewHolder> {

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        // Make swipeable to LEFT direction
        return Swipeable.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        // You can set background color/resource to holder.itemView.
        
        // The argument "type" can be one of the followings;
        // - Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND
        // - Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND
        // (- Swipeable.DRAWABLE_SWIPE_UP_BACKGROUND)
        // (- Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND)
        // (- Swipeable.DRAWABLE_SWIPE_DOWN_BACKGROUND)

        if (type == Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result) {
        // Return sub class of the SwipeResultAction.
        //
        // Available base (abstract) classes are;
        // - SwipeResultActionDefault
        // - SwipeResultActionMoveToSwipedDirection
        // - SwipeResultActionRemoveItem
        // - SwipeResultActionDoNothing

        // The argument "result" can be one of the followings;
        // 
        // - Swipeable.RESULT_CANCELED
        // - Swipeable.RESULT_SWIPED_LEFT
        // (- Swipeable.RESULT_SWIPED_UP)
        // (- Swipeable.RESULT_SWIPED_RIGHT)
        // (- Swipeable.RESULT_SWIPED_DOWN)

        if (result == Swipeable.RESULT_LEFT) {
            return new SwipeResultActionMoveToSwipedDirection() {
                // Optionally, you can override these three methods
                // - void onPerformAction()
                // - void onSlideAnimationEnd()
                // - void onCleanUp()
            };
        } else {
            return new SwipeResultActionDoNothing();
        }
    }
}
```

### Step 5. Modify initialization process of RecyclerView


Put some additional initialization process in your Activity / Fragment.

1. Instantiate `RecyclerViewSwipeManager`
2. Create a wrapped adapter and set it to `RecyclerView`
3. Attach `RecyclerView` to `RecyclerViewSwipeManager`


```java
void onCreate() {
    ...

    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    MyAdapter adapter = new MyAdapter();

    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
}
```

:arrow_down_small: :arrow_down_small: :arrow_down_small:

```java
void onCreate() {
    ...

    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();

    MyAdapter adapter = new MyAdapter();
    RecyclerView.Adapter wrappedAdapter = swipeManager.createWrappedAdapter(adapter);

    recyclerView.setAdapter(wrappedAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // disable change animations
    ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    swipeManager.attachRecyclerView(recyclerView);
}
```

### Step 6. Custom more and details of the implementation

Please refer to [the demo app implementation :octocat:](https://github.com/h6ah4i/android-advancedrecyclerview/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_basic) for more details.
