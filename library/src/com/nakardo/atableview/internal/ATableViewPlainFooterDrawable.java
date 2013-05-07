package com.nakardo.atableview.internal;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

import com.nakardo.atableview.utils.DrawableUtils;
import com.nakardo.atableview.view.ATableView;

public class ATableViewPlainFooterDrawable extends ShapeDrawable {
	private Paint mStrokePaint;
	
	private int mStrokeWidth;
	private float mStrokeOffset;
	
	public ATableViewPlainFooterDrawable(ATableView tableView, int rowHeight) {
		super(new RectShape());
		
		Resources res = tableView.getResources();
		
		mStrokeWidth = DrawableUtils.getStrokeWidth(res);
		mStrokeOffset = (float) Math.ceil(rowHeight * res.getDisplayMetrics().density) + mStrokeWidth;
		
		// stroke.
		mStrokePaint = new Paint();
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(mStrokeWidth);
		mStrokePaint.setColor(DrawableUtils.getSeparatorColor(tableView));
	}
	
	@Override
	protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
		float offset = mStrokeWidth / 2;
		while (offset < shape.getHeight()) {
			canvas.drawLine(0, offset, shape.getWidth(), offset, mStrokePaint);
			offset += mStrokeOffset;
		}
	}
}
