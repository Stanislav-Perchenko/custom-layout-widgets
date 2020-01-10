These are various demonstrations of custom views implementation. These views provides their own
measuring, layout and, sometimes, drawing features.

Colored Views
-------------

This is an example of the custom view directly inherited from the View. It does its layout and draws its content
directly on Canvas using Path tool.


Usage demonstration activity - **ColoredViewsActivity.java**, module - **app**

View implementation - **PrepassListItemColorView.java**, module - **widgets**

### Custom properties for the View ###
- **colorWidth** - width of the colored border around the View.
- **colorCorners** - a radius of the outer corners of the colored border around the View.

### Usage in layout ###

![Layout demo](/docs/colored_view_item.png "Layout demo")

### UI sample ###

![Filled RecyclerView example](/docs/colored_views.png "Filled RecyclerView example")
