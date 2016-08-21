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

