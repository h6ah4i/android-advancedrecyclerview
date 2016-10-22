---
date: 2016-03-08T21:07:13+01:00
title: Advanced RecyclerView
type: index
weight: 0
---

**Advanded RecyclerView** is an extension library of the [`RecyclerView`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html).

## Phirosophies

- **Never inherit `RecyclerView` class**
  - :arrow_forward: &nbsp; Reduces library conflictions, easy to integrate with existing code
- **Implement each features ass separated modules**
  - :arrow_forward: &nbsp; Pick features only what you need
- **Primitive API set rather than user-friendly huge API sets**
  - :arrow_forward: &nbsp; Looks difficult at a glance, but gives great flexibility like `RecyclerView`


## Features

- **Swipe.**
  - Swipe dismiss and swipe pinning operation. (like Google's Inbox app)
- **Drag and Drop.**
  - Smooth item reordering with linear list ([`LinearLayoutManager`](https://developer.android.com/reference/android/support/v7/widget/LinearLayoutManager.html)). It behaves like the playlist of Google's Play Music app.
  - Also drag & drop work with [`GridLayoutManager`](https://developer.android.com/reference/android/support/v7/widget/GridLayoutManager.html) and [`StaggeredGridLayoutManager`](https://developer.android.com/reference/android/support/v7/widget/StaggeredGridLayoutManager.html).
- **Expand.**
  - A list with collapsible groups and its children. This feature is port of the [`ExpandableListView`](https://developer.android.com/reference/android/widget/ExpandableListView.html) of Android framework.
- **Wrapper adapter**
  - Inject additional functionalities to `RecyclerView.Adapter` by using the [Decorator patten](https://en.m.wikipedia.org/wiki/Decorator_pattern)  (Header, Footer, Section, Combining multiple adapters, etc...)
- **Misc.**
  - All swipe, drag and drop, expand and wrapper adapter features work together!
  - An `ItemAnimator`, it behaves exact the same as the default [`SimpleItemAnimator`](https://developer.android.com/reference/android/support/v7/widget/SimpleItemAnimator.html), but its code is refactored. More easy to customize!
  - Some `ItemDecoration`s. Adding drop shadows to each items, drawing separators.


