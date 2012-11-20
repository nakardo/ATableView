package com.nakardo.atableview.protocol;

import com.nakardo.atableview.foundation.NSIndexPath;

public abstract class ATableViewDataSourceExt extends ATableViewDataSource {
	public abstract int numberOfRowStyles();
	public abstract int styleForRowAtIndexPath(NSIndexPath indexPath);
}
