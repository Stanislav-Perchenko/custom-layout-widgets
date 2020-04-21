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


Corner Rounding Decorations
---------------------------

A custom View which can be used as an overlay on top of any other View (ImageView the most)
to round its corners with required radius. Rounding is performed by shadowing the corners with required color,
equals to the surrounding background.

Usage demonstration activity - **RoundedCornersDecorationActivity.java**, module - **app**

View implementation - **RoundedCornersDecorationView.java**, module - **widgets**

### Custom properties for the View ###
- **fillColor** - the color which is used to shadow the corners
- **cornerRadius** - defines radius of shadows on all corners. This value can be overridden by separate corner properties
- **cornerLeftTop** - Top-Left corner radius
- **cornerTopRight** - Top-Right corner radius
- **cornerRightBottom** - Right-Bottom corner radius
- **cornerBottomLeft** - Bottom-Left corner radius

### UI sample ###

![Corner Rounding Decorations example](/docs/corner-decor.gif  "Corner Rounding Decorations example")


