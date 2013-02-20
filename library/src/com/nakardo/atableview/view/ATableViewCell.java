package com.nakardo.atableview.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nakardo.atableview.R;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;

public class ATableViewCell extends FrameLayout {
	public enum ATableViewCellStyle { Default, Subtitle, Value1, Value2 };
	public enum ATableViewCellSelectionStyle { None, Blue, Gray };
	public enum ATableViewCellSeparatorStyle { None, SingleLine, SingleLineEtched };
	
	private String mReuseIdentifier;
	private TextView mTextLabel;
	private TextView mDetailTextLabel;
	private ImageView mImageView;
	private int mBackgroundColor = -1;
	private ATableViewCellAccessoryType mAccessoryType = ATableViewCellAccessoryType.None;;
	private ATableViewCellSelectionStyle mSelectionStyle = ATableViewCellSelectionStyle.Blue;
	
	protected int getLayout(ATableViewCellStyle style) {
		switch (style) {
			case Subtitle:
				return R.layout.atv_cell_subtitle;
			case Value1: 
				return R.layout.atv_cell_value1;
			case Value2:
				return R.layout.atv_cell_value2;
			default:
				return R.layout.atv_cell_default;
		}
	}
	
	public ATableViewCell(ATableViewCellStyle style, String reuseIdentifier, Context context) {
		super(context);
		View contentView = LayoutInflater.from(context).inflate(getLayout(style), null);
		addView(contentView);
		
		mReuseIdentifier = reuseIdentifier;
		mTextLabel = (TextView)findViewById(R.id.textLabel);
		mDetailTextLabel = (TextView)findViewById(R.id.detailTextLabel);
		mImageView = (ImageView)findViewById(R.id.imageView);
	}
	
	public ATableViewCell(Context context) {
		super(context);
	}
	
	public int getBackgroundColor() {
		return mBackgroundColor;
	}
	
	public void setBackgroundColor(int resId) {
		mBackgroundColor = resId;
	}
	
	public String getReuseIdentifier() {
		return mReuseIdentifier;
	}
	
	public TextView getTextLabel() {
		return mTextLabel;
	}

	public TextView getDetailTextLabel() {
		return mDetailTextLabel;
	}
	
	public ImageView getImageView() {
		return mImageView;
	}
	
	public ATableViewCellAccessoryType getAccessoryType() {
		return mAccessoryType;
	}
	
	public void setAccessoryType(ATableViewCellAccessoryType accessoryType) {
		mAccessoryType = accessoryType;
		ATableViewCellAccessoryView.setup(this, accessoryType);
	}
	
	public ATableViewCellSelectionStyle getSelectionStyle() {
		return mSelectionStyle;
	}
	
	public void setSelectionStyle(ATableViewCellSelectionStyle selectionStyle) {
		mSelectionStyle = selectionStyle;
	}
	
	public class LayoutParams {
		public static final int UNDEFINED = -3;
	}
}
