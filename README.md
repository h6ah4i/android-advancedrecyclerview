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
  hugo server
  ```

2. Open `http://localhost:1313/android-advancedrecyclerview/` in your browser

3. Modify files in the `/content` directory.


## How to deploy?

```bash
./deploy.sh "commit message"
```
