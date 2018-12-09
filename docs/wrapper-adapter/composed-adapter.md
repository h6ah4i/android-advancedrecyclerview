
## What's `ComposedAdapter`?

The [`ComposedAdapter`]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.java) is an adapter which aggregates multiple adapters into one.

![Basic usage of ComposedAdapter](../images/block-diagram-composed-adapter.png)

```java
dataSet = new DataSet();

composedAdapter.addAdapter(new AdapterA(new DataSet()));
composedAdapter.addAdapter(new AdapterB(new DataSet()));
composedAdapter.addAdapter(new AdapterC(new DataSet()));
```

!!! info ""
    :octocat: Check the [`ComposedAdapter` implementation on GitHub]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.java)

!!! summary "Related methods"
    - [:blue_book: `ComposedChildAdapterTag ComposedAdapter.addAdapter(RecyclerView.Adapter adapter)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#addAdapter(android.support.v7.widget.RecyclerView.Adapter))
    - [:blue_book: `ComposedChildAdapterTag ComposedAdapter.addAdapter(RecyclerView.Adapter adapter, int position)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#addAdapter(android.support.v7.widget.RecyclerView.Adapter, int))
    - [:blue_book: `boolean ComposedAdapter.removeAdapter(ComposedChildAdapterTag tag)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#removeAdapter(com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedChildAdapterTag))


The `ComposedAdapter` can hold the same child adapter instance multiple times like this;

![ComposedAdapter can hold the same child adapter instance multiple times](../images/block-diagram-composed-adapter-2.png)

```java
dataSet = new DataSet();

adapterA = new AdapterA(dataSet);
composedAdapter.addAdapter(adapterA);
composedAdapter.addAdapter(adapterA);

adapterA2 = new AdapterA(dataSet);
composedAdapter.addAdapter(adapterA2);
```

## Item position handling

The `ComposedAdapter` calls each child adapters as **segment**, also child adapter's local item position are called as **offset**.

![Segments and Offsets](../images/block-diagram-composed-adapter-3.png)

!!! summary "Related methods"
    - [:blue_book: `int ComposedAdapter.getSegment(ComposedChildAdapterTag tag)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#getSegment(com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedChildAdapterTag))
    - [:blue_book: `long ComposedAdapter.getSegmentedPosition(int flatPosition)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#getSegmentedPosition(int))
    - [:blue_book: `int ComposedAdapter.extractSegmentPart(long segmentedPosition)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#extractSegmentPart(long))
    - [:blue_book: `int ComposedAdapter.extractSegmentOffsetPart(long segmentedPosition)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.html#extractSegmentOffsetPart(long))


## Item ID and ViewType handling

When merging adapters, we must take care about item IDs. They have to be unique in entire the dataset, but the problem is child datasets may contains the duplicated IDs. The [`ItemIdComposer`]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.java) is used to manage this problem.

### ItemIdComposer

This utility class provides several static methods to handle the *packed* item ID value.

Item IDs are expressed by 64 bits length integer in RecyclerView, so it can be embed multiple information by using bit operation technique. `ItemIdComposer` divides 64 bits into four chunks; *view type segment*, *group ID*, *child ID* and *reserved bit*.

| Bits       | Usage             |
|------------|-------------------|
| bit 63     | Reserved          |
| bit 62-56  | View type segment |
| bit 55-28  | Group ID          |
| bit 27-0   | Child ID          |


!!! info ""
    :octocat: Check the [`ItemIdComposer` implementation on GitHub]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.java)

!!! summary "Related methods"
    - [:blue_book: `long ItemIdComposer.composeSegment(int segment, long wrappedId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#composeSegment(int, long))
    - [:blue_book: `int ItemIdComposer.extractSegmentPart(long composedId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#extractSegmentPart(long))
    - [:blue_book: `long ItemIdComposer.extractExpandableGroupIdPart(long composedId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#extractExpandableGroupIdPart(long))
    - [:blue_book: `long ItemIdComposer.extractExpandableChildIdPart(long composedId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#extractExpandableChildIdPart(long))
    - [:blue_book: `long ItemIdComposer.extractWrappedIdPart(long composedId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#extractWrappedIdPart(long))
    - [:blue_book: `boolean ItemIdComposer.isExpandableGroup(long composedId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#isExpandableGroup(long))
    - [:blue_book: `long ItemIdComposer.composeExpandableGroupId(long groupId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#composeExpandableGroupId(long))
    - [:blue_book: `long ItemIdComposer.composeExpandableChildId(long groupId, long childId)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemIdComposer.html#composeExpandableChildId(long, long))



### ItemViewTypeComposer

Item view type has similar problem like item ID. The `ItemViewTypeCompser` manages packed item view type value that `ItemIdComposer` doing it for item ID.

Item view types are expressed by 32 bits integer in RecyclerView, and `ItemViewTypeCompser` divides it into three chunks; *expandable group flag*, *view type segment* and *wrapped view type code*.


| Bits       | Usage                  |
|------------|------------------------|
| bit 31     | Expandable group flag  (1: expandable group / 0: normal item) |
| bit 30-24  | View type segment      |
| bit 27-0   | Wrapped view type code |


!!! info ""
    :octocat: Check the [`ItemViewTypeComposer` implementation on GitHub]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemViewTypeComposer.java)

!!! summary "Related methods"
    - [:blue_book: `int ItemViewTypeComposer.composeSegment(int segment, int wrappedViewType)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemViewTypeComposer.html#composeSegment(int, int))
    - [:blue_book: `int ItemViewTypeComposer.extractSegmentPart(int composedViewType)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemViewTypeComposer.html#extractSegmentPart(int))
    - [:blue_book: `int ItemViewTypeComposer.extractWrappedViewTypePart(int composedViewType)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemViewTypeComposer.html#extractWrappedViewTypePart(int))
    - [:blue_book: `boolean ItemViewTypeComposer.isExpandableGroup(int composedViewType)`](/javadoc/reference/com/h6ah4i/android/widget/advrecyclerview/adapter/ItemViewTypeComposer.html#isExpandableGroup(int))


## How to migrate to **WRAPPED** adapter?

Need to change several things to use your adapter wrapped with `ComposedAdapter`. Refer to the [Tweak your Adapter to support adapter wrapping](/wrapper-adapter/migrate-to-wrapped-adapter) page for more details.
