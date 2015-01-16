Advanced RecyclerView
===============

This RecyclerView extension library provides Google's Inbox app like swiping and Play Music app like drag and drop sorting features. Works on API level 9 or later.

---

### Download the example app

<a href="https://play.google.com/store/apps/details?id=com.h6ah4i.android.example.advrecyclerview">
<img alt="Get it on Google Play"
src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>


### Demonstration video on YouTube

<a href="http://www.youtube.com/watch?feature=player_embedded&v=R9uc9-1ETaU" target="_blank">
<img src="http://img.youtube.com/vi/R9uc9-1ETaU/0.jpg" alt="Advanced" width="480" height="360" border="10" />
</a>

NOTE: This library does not contain [Snackbar](http://www.google.com/design/spec/components/snackbars-toasts.html). The above example app uses third-party library. (thx. [Snackbar developed by William Mora ](https://github.com/nispok/snackbar)).

---

Target platforms
---

- API level 9 or later  (However, some animations are not supported on Gingerbread.)


Latest version
---

- Version 0.5.0  (January 25, 2015)

Usage
---

Please check the [`RecyclerListViewFragment`](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo/fragment/RecyclerListViewFragment.java) and the
[`MyItemAdapter`](example/src/main/java/com/h6ah4i/android/example/advrecyclerview/demo/MyItemAdapter.java)  classes in the example code.


Primary classes/interfaces
---

### Drag & Drop related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| `RecyclerViewDragDropManager`         | Provides Drag & Drop sort operation                      |
| `DraggableItemAdapter<T>`             | Implement this interface on your RecyclerView.Adapter    |
| `DraggableItemViewHolder`             | Implement this interface on your RecyclerView.ViewHolder |


### Swiping related classes/interfaces

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| `RecyclerViewSwipeManager`            | Provides Swipe operation                             　  |
| `SwipeableItemAdapter<T>`             | Implement this interface on your RecyclerView.Adapter    |
| `SwipeableItemViewHolder`             | Implement this interface on your RecyclerView.ViewHolder |


### RecyclerView decorations

| Class/Interface name                  | Description                                              |
|---------------------------------------|----------------------------------------------------------|
| `ItemShadowDecorator`                 | Drop shadow decoration for pre-Lollipop devices          |
| `SimpleListDividerDecorator`          | Simple list divider decoration                           |


### Misc.

| Class name                                 | Description                                              |
|--------------------------------------------|----------------------------------------------------------|
| `RecyclerViewTouchActionGuardManager`      | Suppress scrolling while item animations are running     |
| `AbstractDraggableItemViewHolder`          | ViewHolder class which implements boilerplate code of `DraggableItemViewHolder` interface      |
| `AbstractSwipeableItemViewHolder`          | ViewHolder class which implements boilerplate code of `SwipeableItemViewHolder` interface      |
| `AbstractDraggableSwipeableItemViewHolder` | ViewHolder class which implements boilerplate code of `DraggableItemViewHolder` and `SwipeableItemViewHolder` interfaces      |


License
---

This library is licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE) for full of the license text.

    Copyright (C) 2015 Haruki Hasegawa

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
