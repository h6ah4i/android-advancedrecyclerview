---
date: 2016-03-09T00:11:02+01:00
title: Getting started
weight: 10
---

## Installation

Add the following lines of code into your `build.gradle`.


```gradle
dependencies {
    compile ('com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:{{< library_version_code >}}@aar'){
        transitive=true
    }
}
```

**NOTE:**
This library is served on [jCenter](https://bintray.com/h6ah4i/maven/android-advancedrecyclerview/{{< library_version_code >}}/view). If the above gradle setting not getting work, try adding the following lines.

```gradle
repositories {
    jcenter()
}
```

## What's next?

First, I recommend you to learn about plain [`RecyclerView`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html). Basic knowledges about `RecyclerView` is required before using this library. The following tutorial is good for starting point;

- [Creating Lists and Cards | Android Developers - Training](https://developer.android.com/training/material/lists-cards.html)


If you've already know well about plain `RecyclerView`, proceed to each sections written about what you want to use:

- [Wrapper Adapter](/wrapper-adapter/)
  - [`ComposedAdapter`](/wrapper-adapter/#composedadapter)
  - [Headers & Footers](/wrapper-adapter/#headers-footers)
  - [Insertion](/wrapper-adapter/#insertion)
  - [Filtering](/wrapper-adapter/#filtering)
- [Drag & Drop](/draggable)
- [Swipeable](/swipeable)
- [Expandable](/expandable)

## Other references

1. Demo app code

  - [Install the demo app from Google Play](https://play.google.com/store/apps/details?id=com.h6ah4i.android.example.advrecyclerview)
  - [Check the demo app code on GitHub]({{< github_repo_url >}}/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview)

2. Official RecyclerView references
  - [Creating Lists and Cards | Android Developers - Training](https://developer.android.com/training/material/lists-cards.html)
  - [RecyclerView | Android Developers - Reference](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html)

3. Check the Issues page of Advanced RecyclerView
  - [Issues - Advanced RecyclerView | GitHub]({{< github_repo_url >}}/issues)

4. Javadoc
  - [Javadoc - Advanced RecyclerView (v{{< library_version_code >}})](/javadoc/{{< library_version_code >}})

