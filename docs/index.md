**Advanded RecyclerView** is an extension library of the [`RecyclerView`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html).

## Demo app

<a href='https://play.google.com/store/apps/details?id=com.h6ah4i.android.example.advrecyclerview'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="200" /></a>

## Phirosophies

- **Never inherit `RecyclerView` class**
    - 👉 &nbsp; Reduces library conflictions, easy to integrate with existing code
- **Implement each features as separated modules**
    - 👉 &nbsp; Pick features only what you need
- **Primitive API set rather than user-friendly huge API sets**
    - 👉 &nbsp; Looks difficult at a glance, but gives great flexibility like original `RecyclerView`


## Features

- **Swipe**
    - Swipe dismiss and swipe pinning operation. (like Google's Inbox app)
- **Drag and Drop**
    - Smooth item reordering with linear list ([`LinearLayoutManager`](https://developer.android.com/reference/android/support/v7/widget/LinearLayoutManager.html)). It behaves like the playlist of Google's Play Music app.
    - Also drag & drop work with [`GridLayoutManager`](https://developer.android.com/reference/android/support/v7/widget/GridLayoutManager.html) and [`StaggeredGridLayoutManager`](https://developer.android.com/reference/android/support/v7/widget/StaggeredGridLayoutManager.html).
- **Expand**
    - A list with collapsible groups and its children. This feature is port of the [`ExpandableListView`](https://developer.android.com/reference/android/widget/ExpandableListView.html) of Android framework.
- **Wrapper adapter**
    - Inject additional functionalities to `RecyclerView.Adapter` by using the [Decorator patten](https://en.m.wikipedia.org/wiki/Decorator_pattern)  (Header, Footer, Section, Combining multiple adapters, etc...)
- **Misc.**
    - All swipe, drag and drop, expand and wrapper adapter features work together!
    - An `ItemAnimator`, it behaves exact the same as the default [`SimpleItemAnimator`](https://developer.android.com/reference/android/support/v7/widget/SimpleItemAnimator.html), but its code is refactored. More easy to customize!
    - Some `ItemDecoration`s. Adding drop shadows to each items, drawing separators.
