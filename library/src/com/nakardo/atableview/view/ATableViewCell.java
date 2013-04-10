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
	
	// UIView
	private int mBackgroundColor = -1;
	
	// internal
	private View mContainerView;
	
	private String mReuseIdentifier;
	private TextView mTextLabel;
	private TextView mDetailTextLabel;
	private ImageView mImageView;
	private View mContentView;
	private View mBackgroundView;
	private ATableViewCellAccessoryType mAccessoryType = ATableViewCellAccessoryType.None;
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
		LayoutInflater.from(context).inflate(getLayout(style), this, true);
		
		mReuseIdentifier = reuseIdentifier;
		mTextLabel = (TextView)findViewById(R.id.textLabel);
		mDetailTextLabel = (TextView)findViewById(R.id.detailTextLabel);
		mImageView = (ImageView)findViewById(R.id.imageView);
		mContentView = findViewById(R.id.contentView);
		mBackgroundView = findViewById(R.id.backgroundView);
		mContainerView = findViewById(R.id.containerView);
	}
	
	public ATableViewCell(Context context) {
		super(context);
	}
	
	public View getInternalContainerView() {
		return mContainerView;
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
	
	public View getContentView() {
		return mContentView;
	}
	
	public View getBackgroundView() {
		return mBackgroundView;
	}
	public void setBackgroundView(View backgroundView) {
		mBackgroundView = backgroundView;
	}
	
	public ATableViewCellAccessoryType getAccessoryType() {
		return mAccessoryType;
	}
	
	public void setAccessoryType(ATableViewCellAccessoryType accessoryType) {
		mAccessoryType = accessoryType;
		
		// TODO build accessory view, it should support building accessories from any view.
		ATableViewCellAccessoryView.Builder builder = new ATableViewCellAccessoryView.Builder(this);
		builder.setAccessoryType(accessoryType);
		builder.create();
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
