When creating headers & footers, the `AbstractHeaderFooterWrapperAdapter` can be a good alternative to the `ComposedAdapter`. This class provides simple way to creating a wrapper crass for headers & footers by implementing small number of methods.


## Quick quide

### Usage

```java
MyAdapter adapter = new MyAdapter();
MyHeadFootAdapter wrappedAdapter = new MyHeadFootAdapter(adapter);

recyclerView.setAdapter(wrappedAdapter);

```

### Implementation of headers & footers wrapper adapter

```java
class MyHeadFootAdapter 
    extends AbstractHeaderFooterWrapperAdapter<MyHeadFootAdapter.HeaderVH, MyHeadFootAdapter.FooterVH>
{
    static class HeaderVH extends RecylerView.ViewHolder { ... }
    static class FooterVH extends RecylerView.ViewHolder { ... }

    public MyHeadFootAdapter(RecyclerView.Adapter adapter) {
        super.this(adapter);
    }

    @Override
    public int getHeaderItemCount() {
        return <<NUMBER OF HEADER ITEMS GOES HERE>>;
    }

    @Override
    public int getFooterItemCount() {
        return <<NUMBER OF FOOTER ITEMS GOES HERE>>;
    }

    @Override
    public HeaderVH onCreateHeaderItemViewHolder(ViewGroup parent, int viewType) {
        View v = ...;
        return new HeaderVH(v);
    }

    @Override
    public FooterVH onCreateFooterItemViewHolder(ViewGroup parent, int viewType) {
        View v = ...;
        return new FooterVH(v);
    }

    @Override
    public void onBindHeaderItemViewHolder(HeaderVH holder, int localPosition) {
        // bind data to header items views
    }

    @Override
    public void onBindFooterItemViewHolder(FooterVH holder, int localPosition) {
        // bind data to footer items views
    }
}
```

## Click event handling

The `AbstractHeaderFooterWrapperAdapter` extends `ComposedAdapter` so the same click event hadling approach is required.

```java
class MyHeadFootAdapter 
    extends AbstractHeaderFooterWrapperAdapter<MyHeadFootAdapter.HeaderVH, MyHeadFootAdapter.FooterVH>
    implements View.OnClickListener {

    @Override
    public HeaderVH onCreateHeaderItemViewHolder(ViewGroup parent, int viewType) {
        View v = ...;
        return new HeaderVH(v);
    }

    @Override
    public void onClick(View v) {
        RecyclerView rv = RecyclerViewAdapterUtils.getParentRecyclerView(v);
        RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);

        int rootPosition = vh.getAdapterPosition();
        if (rootPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // need to determine adapter local position like this:
        RecyclerView.Adapter rootAdapter = rv.getAdapter();
        int localPosition = WrapperAdapterUtils.unwrapPosition(rootAdapter, this, rootPosition);

        // get segment
        long segmentedPosition = getSegmentedPosition(localPosition);
        int segment = extractSegmentPart(segmentedPosition);
        int offset = extractSegmentOffsetPart(segmentedPosition);

        String message;

        if (segment == SEGMENT_TYPE_HEADER) {
            // Header item is clicked !
        } else if (segment == SEGMENT_TYPE_FOOTER) {
            // Footer item is clicked !
        }
    }
}

```

## Multipe view type handling

Just same as `ComposedAdapter`, so need to use `ItemViewTypeComposer.extractWrappedViewTypePart()` when using view type.

```java
@Override
public int getHeaderItemViewType(int localPosition) {
    return position % 2;
}

@Override
public void onBindHeaderItemViewHolder(HeaderViewHolder holder, int localPosition) {
    int viewType = ItemViewTypeComposer.extractWrappedViewTypePart(holder.getItemViewType());

    if (viewType == 0) {
    	...
    } else {
        ...
    }
}
```

