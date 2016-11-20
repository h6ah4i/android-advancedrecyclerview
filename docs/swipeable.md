![Block Diagram - Swipeable](/images/block-diagram-swipe.png)


[TOC]

# Tutorial

## Step 1. Make the adapter supports stable ID

**This step is very important. If adapter does not return stable & unique ID, it will cause weird behaviors (wrong animation, NPE, etc...)**

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


## Step 2. Implement the `SwipeableItemAdapter` interface

```java
class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    static class MyViewHolder extends RecyclerView.ViewHolder {
        ...
    }
    ...
}
```

⏬ &nbsp; ⏬ &nbsp; ⏬

```java
class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> 
        extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
        implements SwipeableItemAdapter<MyAdapter.MyViewHolder> {

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        // TODO implement here later
        return Swipeable.REACTION_CAN_NOT_SWIPE_ANY;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        // TODO implement here later
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, final int position, int result) {
        // TODO implement here later
        return new SwipeResultActionDefault();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ...
    }
}
```

## Step 3. Extend the `AbstractSwipeableItemViewHolder` instead of the `RecyclerView.ViewHolder`

```java
class MyAdapter ... {
    static class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View v) {
            super(v);
        }
    }
    ...
}
```

⏬ &nbsp; ⏬ &nbsp; ⏬

```java
class MyAdapter ... {
    static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        MyViewHolder(View v) {
            super(v);
        }

        @Override
        View getSwipeableContainerView() {
            // TODO implement here later
            return null;
        }
    }
}
```

## Step 4. Modify layout file of item views

```xml
<!-- Content View -->
<TextView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/text1"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:gravity="center"/>
```

⏬ &nbsp; ⏬ &nbsp; ⏬ 

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

        <!-- Content View -->
        <TextView
            android:id="@android:id/text1"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:layout_gravity="top|left"
            android:gravity="center"/>

    </FrameLayout>
</FrameLayout>
```

## Step 5. Update ViewHolder

```java
class MyAdapter ... {
    static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        TextView mText;

        public MyViewHolder(View v) {
            super(v);
            mText = (TextView) v;
        }

        @Override
        public View getSwipeableContainerView() {
            return null;
        }
    }
}
```

⏬ &nbsp; ⏬ &nbsp; ⏬

```java
class MyAdapter ... {
    static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        TextView mText;
        FrameLayout mContainer;

        public MyViewHolder(View v) {
            super(v);
            mText = (TextView) v.findViewById(android.R.id.text1);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }
}
```

## Step 6. Update Adapter

```java
class MyAdapter ... {

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) { ... }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) { ... }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result) { ... }
}
```

⏬ &nbsp; ⏬ &nbsp; ⏬ 

```java

class MyAdapter ... {

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
        // Base (abstract) classes are
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

## Step 7. Modify initialization process of RecyclerView in Activity / Fragment

```java
void onCreate() {
    ...

    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    MyAdapter adapter = new MyAdapter();

    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
}
```

⏬ &nbsp; ⏬ &nbsp; ⏬ 

```java
void onCreate() {
    ...

    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();


    MyAdapter adapter = new MyAdapter();
    RecyclerView.Adapter wrappedAdapter = swipeManager.createWrappedAdapter(adapter);

    recyclerView.setAdapter(wrappedAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    swipeManager.attachRecyclerView(recyclerView);
}
```

## Step 8. Custom more and details of the implementation

Please refer to [the demo app implementation](https://github.com/h6ah4i/android-advancedrecyclerview/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_s_basic) for more details.
