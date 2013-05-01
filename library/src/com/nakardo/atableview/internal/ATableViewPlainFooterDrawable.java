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
	private float mStrokeOffset;
	
	public ATableViewPlainFooterDrawable(ATableView tableView, int rowHeight) {
		super(new RectShape());
		
		Resources res = tableView.getResources();
		
		mStrokePaint = new Paint(getPaint());
		mStrokePaint.setStrokeWidth(DrawableUtils.getStrokeWidth(res));
		mStrokePaint.setColor(DrawableUtils.getSeparatorColor(tableView));
		
		mStrokeOffset = rowHeight * res.getDisplayMetrics().density;
	}
	
	@Override
	protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
		float offset = .5f;
		while (offset < shape.getHeight()) {
			canvas.drawLine(0, offset, shape.getWidth(), offset, mStrokePaint);
			offset += mStrokeOffset;
		}
	}
}
