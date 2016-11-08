Documentation source of Advanced RecyclerView library
===

## What's this branch?

This branch contains GitHub Pages source of Advanced RecyclerView.

## Setup

1. Install [Hugo](https://gohugo.io/).
2. Clone this repository.

  ```bash
  git clone -b gh-pages-source --recurse-submodules git@github.com:h6ah4i/android-advancedrecyclerview.git android-advancedrecyclerview-gh-page
  ```

## How to develop the page contents?

1. Run Hugo server

  ```bash
  mkdocs serve
  ```

2. Open `http://127.0.0.1:8000/` in your browser

3. Modify files in the `/content` directory.


## How to deploy?

```bash
mkdocs gh-deploy
```
