package com.nakardo.atableview.foundation;

public class NSIndexPath {
	private int mSection;
	private int mRow;
	
	private NSIndexPath(int row, int section) {
		mRow = row;
		mSection = section;
	}
	
	public static NSIndexPath indexPathForRowInSection(int row, int section) {
		return new NSIndexPath(row, section);
	}
	
	public int getSection() {
		return mSection;
	}
	
	public int getRow() {
		return mRow;
	}
	
	@Override
	public String toString() {
		return "[" + mRow + ", " + mSection + "]";
	}
}
