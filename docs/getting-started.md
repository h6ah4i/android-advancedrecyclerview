# Installation

Add the following lines of code into your `build.gradle`.


```gradle
dependencies {
    compile ('com.h6ah4i.android.widget.advrecyclerview:advrecyclerview:[VERSION_CODES_GOES_HERE]@aar'){
        transitive=true
    }
}
```

**NOTE:**
This library is served on [jCenter](https://bintray.com/h6ah4i/maven/android-advancedrecyclerview/). If the above gradle setting not getting work, try adding the following lines.

```gradle
repositories {
    jcenter()
}
```

# What's next?

First, I recommend you to learn about plain [`RecyclerView`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html). Basic knowledges about `RecyclerView` is required before using this library. The following tutorial is good for starting point;

- [Creating Lists and Cards | Android Developers - Training](https://developer.android.com/training/material/lists-cards.html)


If you've already know well about plain `RecyclerView`, proceed to each sections written about what you want to use:

- [Wrapper Adapter](/wrapper-adapter/)
    - [`ComposedAdapter`](/wrapper-adapter/composed-adapter)
    - [Headers & Footers](/wrapper-adapter/headers-footers)
    - [Insertion & Filtering](/wrapper-adapter/insertion-filtering)
- [Drag & Drop](/draggable)
- [Swipeable](/swipeable)
- [Expandable](/expandable)

# Other references

- **Demo app code**
    - [Install the demo app from Google Play](https://play.google.com/store/apps/details?id=com.h6ah4i.android.example.advrecyclerview)
    - [Check the demo app code on GitHub](https://github.com/h6ah4i/android-advancedrecyclerview/tree/master/example/src/main/java/com/h6ah4i/android/example/advrecyclerview)

- **Official RecyclerView reference**
    - [Creating Lists and Cards --- Android Developers - Training](https://developer.android.com/training/material/lists-cards.html)
    - [RecyclerView --- Android Developers - Reference](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html)
  
- **Check the Issues page of Advanced RecyclerView**
    - [Issues - Advanced RecyclerView --- GitHub](https://github.com/h6ah4i/android-advancedrecyclerview/issues)

- **Javadoc**
    - [Javadoc - Advanced RecyclerView](/javadoc/latest)

