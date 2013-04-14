# ATableView (UITableView)

![ATableView](http://oi45.tinypic.com/vshn2x.jpg)

## Summary

ATableView intends to imitate same object model proposed on UIKit for building tables, so it's not only limited on theming Android ListView. If you've some background on iOS development you may jump over some of the sections below, you'll find a lot of similarities with the native framework.

If not, you should be good with the examples included here and the demo project.

## Updates

Documentation is a little outdated, please use it only for reference and stay close to the examples included on the demo project. Feel free to submit a bug if you find any issues using it.

## How to use it

### Creating tables
    
```java
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
        
    // ATableViewStyle.Plain & Grouped supported.
    ATableView tableView = new ATableView(ATableViewStyle.Grouped, this);
        
    // don't forget to set the datasource, otherwise you'll get an exception.
    // it must be an object extending ATableViewDataSource, or ATableViewDataSourceExt (more on this later).
    tableView.setDataSource(new SampleATableViewDataSource());
        
    // delegates are optional, it must extend ATableViewDelegate.
    tableView.setDelegate(new SampleATableViewDelegate());
        
    FrameLayout container = (FrameLayout)findViewById(android.R.id.content);
    container.addView(tableView);
}
```
    
### Implementing a data source

It's your responsability to implement the required methods when extending `ATableViewDataSource`. The following are supported:

```java
public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath); [Required]
public int numberOfRowsInSection(ATableView tableView, int section); [Required]
public int numberOfSectionsInTableView(ATableView tableView);
```

More on how this methods works can be found on the iOS [UITableViewDataSource Protocol Reference](http://developer.apple.com/library/ios/#documentation/uikit/reference/UITableViewDataSource_Protocol/Reference/Reference.html).

#### Example

```java
@Override
public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
    final String cellIdentifier = "CellIdentifier";
        
    // ATableViewCellStyle.Default, Subtitle, Value1 & Value2 supported.
    ATableViewCellStyle style = ATableViewCellStyle.Default;
        
    // reuse cells. if the table has different row types it will result on performance issues.
    // Use ATableViewDataSourceExt on this cases.
    // please notice we ask the datasource for a cell instead the table as we do on ios.
    ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
    if (cell == null) {
        cell = new ATableViewCell(style, cellIdentifier, MainActivity.this);
        // ATableViewCellSelectionStyle.Blue, Gray & None supported. It defaults to Blue.
        cell.setSelectionStyle(ATableViewCellSelectionStyle.Blue);
    }
    
    // set title.
    cell.getTextLabel().setText("Buenos Aires");
        
    // set detail text. careful, detail text is not present on every cell style.
    // null references are not as neat as in obj-c.
    TextView detailTextLabel = cell.getDetailTextLabel();
    if (detailTextLabel != null) {
        detailTextLabel.setText("Argentina");
    }
            
    return cell;
}
    
@Override
public int numberOfRowsInSection(ATableView tableView, int section) {
    // return number of rows for this section.
    if (section == 1) {
        return 4;
    }
        
    return 2;
}
    
@Override
public int numberOfSectionsInTableView(ATableView tableView) {
    // defaults to 1.
    return 2;
}
```

### Table styles (ATableViewStyle)

All [UITableViewStyle](http://developer.apple.com/library/ios/#documentation/UIKit/Reference/UITableView_Class/Reference/Reference.html#//apple_ref/c/tdef/UITableViewStyle) styles are supported. These are:

* ATableViewStyle.Plain
* ATableViewStyle.Grouped

### Creating cells

You can use cell styles included by default, or extend `ATableViewCell` to create your own cells.

### Cell styles (ATableViewCellStyle)

All [UITableViewCellStyles](http://developer.apple.com/library/ios/#documentation/uikit/reference/UITableViewCell_Class/Reference/Reference.html#//apple_ref/c/tdef/UITableViewCellStyle) styles are supported. These are:

* ATableViewCellStyle.Default
* ATableViewCellStyle.Subtitle
* ATableViewCellStyle.Value1
* ATableViewCellStyle.Value2

![ATableViewCellStyle](http://oi45.tinypic.com/auyv8.jpg)

### Cell selection styles (ATableViewCellSelectionStyle)

All [UITableViewCellSelectionStyle](http://developer.apple.com/library/ios/#documentation/uikit/reference/UITableViewCell_Class/Reference/Reference.html#//apple_ref/doc/c_ref/UITableViewCellSelectionStyle) styles are supported These are:

* ATableViewCellSelectionStyle.None
* ATableViewCellSelectionStyle.Blue (Default)
* ATableViewCellSelectionStyle.Gray

![ATableViewCellSelectionStyle](http://oi47.tinypic.com/2l8c2e8.jpg)

### Adding images to cells (imageView)

imageView is shown automatically when an image is defined for it, otherwise is hidden by default on each cell.

![imageView](http://i45.tinypic.com/34508zd.jpg)

#### Example

```java
Drawable drawable = getResources().getDrawable(R.drawable.some_image);
cell.getImageView().setImageDrawable(drawable);
```

### Creating custom cells

Creating custom cells is as simple as extending `ATableViewCellStyle` and indicate the layout you want your cell to use.

#### Example

```java
public class MyCustomCell extends ATableViewCell {
    private UILabel mCustomLabel;
    
    protected int getLayout(ATableViewCellStyle style) {
        // here it goes your custom cell layout.
        return R.layout.my_custom_cell;
    }
    
    public MyCustomCell(ATableViewCellStyle style, String reuseIdentifier, Context context) {
        super(style, reuseIdentifier, context);
        mCustomLabel = (UILabel)findViewById(R.id.custom_cell_label);
    }
    
    public UILabel getCustomLabel() {
        return mCustomLabel;
    }
}
```

### Implementing a delegate

Adding a delegate to the table is optional. [UITableViewDelegate](http://developer.apple.com/library/ios/#documentation/UIKit/Reference/UITableViewDelegate_Protocol/Reference/Reference.html) defines many methods to describe how the table should look and behave. Only a few of them are currently supported on `ATableViewDelegate`. These are:

```java
public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath);
public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath);
```
    
#### Example

```java
@Override
public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
	// do something when the row is selected. rows are identified by its indexPath.    
}
		
@Override
public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
    // return height size on dip. defaults to 44 if not implemented.
    return 54;
}
```
    
### Table data source additional methods (ATableViewDataSourceExt)

On the case you need to use different cell styles on the same table, you should extend `ATableViewDataSourceExt` to avoid performance issues when scrolling. This is necessary since Android ListView uses different pools for reusing cells, a pool for each cell type. `ATableViewDataSourceExt` is used to specify how many rows and pools should be created to run smoothly.

You'll have additionally to implement the following methods:

```java
public int numberOfRowStyles(); [Required]
public int styleForRowAtIndexPath(NSIndexPath indexPath); [Required]
```

#### Example

```java
@Override
public int numberOfRowStyles() {
    // number of different rows on the table.
    return 4;
}
    
@Override
public int styleForRowAtIndexPath(NSIndexPath indexPath) {
    // integer identifying the style for a cell at a given indexPath.
    return myOwnImplementationGetStyle(indexPath);
}
```

## Extra stuff

### UILabel

UILabel uses internally [Roboto](http://developer.android.com/design/style/typography.html) font, which make labels look-alike in iOS and it's rendered great by Android. Roboto font is available from ICS and above, so it should be included on your project `/assets` folder in order to make it work for any version.

Both **Roboto-Regular.ttf** and **Roboto-Bold.ttf** are included on the demo project. On the case you don't include these files, text will render using the default font available.

## Roadmap

* Support for pinned headers & footers in UITableViewPlainStyle.
* Support for creating the table from layout.
* ATableViewActivity and ATableViewFragment classes.
* More.

## License

Copyright 2012 Diego Acosta - Contact me at diegonake@gmail.com / @nakardo

Released under the [Apache 2.0.](http://www.apache.org/licenses/LICENSE-2.0.html) license.

Roboto font by Google Android - Licensed under the Apache License.

Awesome Android PSD images by [slaveoffear](http://slaveoffear.deviantart.com).
