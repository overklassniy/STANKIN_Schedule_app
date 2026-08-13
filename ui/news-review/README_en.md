# Module ui/news-review (`:ui:news-review`)

UI module for news review. Contains news feed components: posts, tabs, loading and error indicators. Used as a tab on the home screen.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/news/review/ui/components/
  NewsPost.kt                  - News post Compose component
  NewsPostColumn.kt            - News posts column
  NewsReviewToolBar.kt         - News review toolbar
  NewsError.kt                 - Loading error display
  NewsLoading.kt               - Loading display
  AppTabIndicator.kt           - Tab indicator
```

## Key components

- `NewsPost` - news post card Compose component
- `NewsPostColumn` - paginated posts column
- `NewsReviewToolBar` - toolbar with news source tabs
- `NewsError` - news loading error display
- `NewsLoading` - news loading display
- `AppTabIndicator` - active tab indicator

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:news-core`

## Technologies

- Jetpack Compose, Material 3