![Block Diagram - Expandable](/images/block-diagram-expand.png)

## Tutorial

### Step 1. Extend `AbstractExpandableItemAdapter` instead of `RecyclerView.Adapter`

First, you need to extend `AbstractExpandableItemAdapter` to use expandable items feature. This class implements `RecyclerView.Adapter`'s methods and some of them are sealed via `final` keyword. Instead, it provides some `Group` / `Child` prefixed version of `RecyclerView.Adapter' methods.

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


### Implement `getGroupCount()` / `getChildCount()`

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


### Implement `getGroupId()` / `getChildId()`

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


### Implement `onCreateGroupViewHolder()` / `onCreateChildViewHolder()`

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

### Implement `onBindGroupViewHolder()` / `onBindChildViewHolder()`

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

### Implement `onCheckCanExpandOrCollapseGroup()`

TODO

