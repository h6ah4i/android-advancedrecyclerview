
!!! tip "Just looking for a sample code?"
    :octocat: Check the [minimal expand & collapse sample code on GitHub](https://github.com/h6ah4i/android-advancedrecyclerview/blob/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_minimal/MinimalExpandableExampleActivity.java).

## Tutorial

### Step 1. Extend `AbstractExpandableItemAdapter` instead of `RecyclerView.Adapter`

First, you need to extend [`AbstractExpandableItemAdapter`](https://github.com/h6ah4i/android-advancedrecyclerview/blob/master/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/utils/AbstractExpandableItemAdapter.java) to use expandable items feature. This class implements `RecyclerView.Adapter`'s methods and some of them are sealed via `final` keyword. Instead, it provides some `Group` / `Child` prefixed version of `RecyclerView.Adapter` methods.

```java
class MyChildItem {
    public long id;
}

class MyGroupItem {
    public long id;
    List<MyChildItem> children;
}

class MyAdapter extends AbstractExpandableItemAdapter<MyAdapter.MyGroupVH, MyAdapter.MyChildVH> {
    List<MyGroupItem> items;

    public MyAdapter() {
        setHasStableIds(true); // this is required for expandable feature.
    }

    @Override
    public int getGroupCount() { ... }

    @Override
    public int getChildCount(int groupPosition) { ... }

    @Override
    public long getGroupId(int groupPosition) { ... }

    @Override
    public long getChildId(int groupPosition, int childPosition) { ... }

    @Override
    public MyGroupVH onCreateGroupViewHolder(ViewGroup parent, int viewType) { ... }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) { ... }

    @Override
    public void onBindGroupViewHolder(MyGroupVH holder, int groupPosition, int viewType) { ... }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) { ... }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupVH holder, int groupPosition, int x, int y, boolean expand) { ... }
}
```

### Step 2. Implement each method of `AbstractExpandableItemAdapter`


### Step 3. Implement `getGroupCount()` / `getChildCount()`

!!! info ""
    The corresponding `RecyclerView.Adapter`'s method is [`getItemCount()`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemCount()).

```java
@Override
public int getGroupCount() {
    return items.size();
}

@Override
public int getChildCount(int groupPosition) {
    reuturn items.get(groupPosition).size();
}
```


### Step 4. Implement `getGroupId()` / `getChildId()`

!!! info ""
    The corresponding `RecyclerView.Adapter`'s method is [`getItemId()`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)).

```java
@Override
public int getGroupId(int groupPosition) {
    return items.get(groupPosition).id;
}

@Override
public int getChildId(int groupPosition, int childPosition) {
    reuturn items.get(groupPosition).children.get(childPosition).id;
}
```


### Step 5. Implement `onCreateGroupViewHolder()` / `onCreateChildViewHolder()`

!!! info ""
    The corresponding `RecyclerView.Adapter`'s method is [`onCreateViewHolder()`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#onCreateViewHolder(android.view.ViewGroup, int)).

```java
@Override
public MyGroupVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInfrater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
    return new MyGroupVH(v);
}

@Override
public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInfrater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
    return new MyChildVH(v);
}
```

### Step 6. Implement `onBindGroupViewHolder()` / `onBindChildViewHolder()`

!!! info ""
    The corresponding `RecyclerView.Adapter`'s method is [`onBindViewHolder()`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#onBindViewHolder(VH, int)).

```java
@Override
public void onBindGroupViewHolder(MyGroupVH holder, int groupPosition, int viewType) {
    MyGroupItem item = items.get(groupPosition);
    holder.text.setText(...);
}

@Override
public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
    MyChildItem item = items.get(groupPosition).children.get(childPosition);
    holder.text.setText(...);
}
```

### Step 7. Implement `onCheckCanExpandOrCollapseGroup()`

!!! info ""
    Just returns `true` for make group items automatically respond to click events.

```java
@Override
public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
    return true;
}
```

!!! tip "[Tip] If you want to handle expand/colappse action manually"
    Make this method reutrns `false`. Then call [`RecyclerViewExpandableItemManager.expandGroup()`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/expandable/RecyclerViewExpandableItemManager.html#expandGroup(int)) / [`collapseGroup()`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/expandable/RecyclerViewExpandableItemManager.html#collapseGroup(int)) on click item events.

    **&raquo; Sample code:**  [:octocat: AddRemoveExpandableExampleAdapter#handleOnClickGroupItemContainerView()](https://github.com/h6ah4i/android-advancedrecyclerview/blob/4df3d7e3deddd002800124612aa9527771f80967/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_add_remove/AddRemoveExpandableExampleAdapter.java#L345)

    **&raquo; Related issue:** [:octocat: Force expand/colapse #11](https://github.com/h6ah4i/android-advancedrecyclerview/issues/11)


### Step 8. Custom more and details of the implementation

Please refer to [the demo app implementation :octocat:](https://github.com/h6ah4i/android-advancedrecyclerview/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo_e_basic) for more details.
