The `WrapperAdapter` is a wrapper of another adapter(s). This is a core mechanism of Advanced RecyclerView library. Drag & Drop, Swipe, Expand, Headers and Footers, etc... major features of this library are using it. We can implement additional functionalities by using [Decorator pattern](https://en.wikipedia.org/wiki/Decorator_pattern) through the mechanism.

![Block Diagram - Wrapper Adapter](../images/block-diagram-wrapper-adapter.png)


The `WrapperAdapter` is an interface and it is indended to be combined with `RecyclerView.Adapter`. The most important role of `WrapperAdapter` is converting `position` of items by using `wrapPosition()` and `unwrapPosition()` methods.

![wrapPosition() and unwrapPosition()](../images/block-diagram-wrapper-adapter-2.png)

**Subclasses:**

- [:octocat: `ComposedAdapter`]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/composedadapter/ComposedAdapter.java)
- [:octocat: `SimpleWrapperAdapter`]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/adapter/SimpleWrapperAdapter.java)
- [:octocat: `AbstractHeaderFooterWrapperAdapter`]({{ library.repo_blob_base }}/library/src/main/java/com/h6ah4i/android/widget/advrecyclerview/headerfooter/AbstractHeaderFooterWrapperAdapter.java)
