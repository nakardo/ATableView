package com.nakardo.atableview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nakardo.atableview.R;

public class ATableViewCell extends FrameLayout {
	public enum ATableViewCellStyle { Default, Subtitle, Value1, Value2 };
	public enum ATableViewCellSelectionStyle { None, Blue, Gray };
	
	private String mReuseIdentifier;
	private TextView mTextLabel;
	private TextView mDetailTextLabel;
	private ImageView mImageView;
	private int mBackgroundColor = getResources().getColor(R.color.atv_cell_background);
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
		this(context);
		LinearLayout contentView = (LinearLayout)LayoutInflater.from(context).inflate(getLayout(style), null);
		addView(contentView);
		
		mReuseIdentifier = reuseIdentifier;
		mTextLabel = (TextView)findViewById(R.id.textLabel);
		mDetailTextLabel = (TextView)findViewById(R.id.detailTextLabel);
		mImageView = (ImageView)findViewById(R.id.imageView);
	}
	
	public ATableViewCell(Context context) {
		super(context);
	}
	
	public ATableViewCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ATableViewCell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
	
	public ATableViewCellSelectionStyle getSelectionStyle() {
		return mSelectionStyle;
	}
	
	public void setSelectionStyle(ATableViewCellSelectionStyle selectionStyle) {
		mSelectionStyle = selectionStyle;
	}
}
