package com.nakardo.atableview.internal;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

import com.nakardo.atableview.R;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

public class ATableViewCellDrawable extends ShapeDrawable {
	public static final float CELL_STROKE_WIDTH_DP = 1f;
	private static final float CELL_GROUPED_STYLE_CORNER_RADIUS = 7;
	
	public enum ATableViewCellBackgroundStyle { Single, Top, Middle, Bottom };
	
	private ATableView mTableView;
	private ATableViewCellBackgroundStyle mCellBackgroundStyle;
	private float mStrokeWidth;
	
	private Paint mSeparatorPaint;
	private Paint mEtchedPaint;
	private Paint mBackgroundPaint;
	private Paint mSelectedPaint;
	
	private static RoundRectShape getShape(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle) {
		ATableViewStyle tableStyle = tableView.getStyle();
		
		float[] radius = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		if (tableStyle == ATableViewStyle.Grouped) {
			Resources res = tableView.getResources();
			
			float radii = (float) Math.round(CELL_GROUPED_STYLE_CORNER_RADIUS * res.getDisplayMetrics().density);
			if (backgroundStyle == ATableViewCellBackgroundStyle.Single) {
				radius = new float[] { radii, radii, radii, radii, radii, radii, radii, radii };
			} else if (backgroundStyle == ATableViewCellBackgroundStyle.Top) {
				radius = new float[] { radii, radii, radii, radii, 0, 0, 0, 0 };
			} else if (backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
				radius = new float[] { 0, 0, 0, 0, radii, radii, radii, radii };
			}
		}
		
		return new RoundRectShape(radius, null, null);
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int backgroundColor) {
		
		super(getShape(tableView, backgroundStyle));
		Resources res = tableView.getResources();
		
		mTableView = tableView;
		mCellBackgroundStyle = backgroundStyle;
		
		// separator.
		mSeparatorPaint = new Paint(getPaint());
		mSeparatorPaint.setColor(tableView.getSeparatorColor());
//		mSeparatorPaint.setColor(0xFF990000);
		
		// calculate stroke width, use rounded with for padding only.
		mStrokeWidth = CELL_STROKE_WIDTH_DP * res.getDisplayMetrics().density;
		int roundedStrokeWidth = (int) Math.ceil(mStrokeWidth);
		
		// add padding to avoid content to overlap with cell stroke lines.
		int marginBottom = 0;
		if (backgroundStyle == ATableViewCellBackgroundStyle.Single ||
			backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
			marginBottom = roundedStrokeWidth;
		}
		setPadding(roundedStrokeWidth, roundedStrokeWidth, roundedStrokeWidth, marginBottom);
		
		// etched line, only for grouped tables, with SingleLineEtched style.
		if (mTableView.getStyle() == ATableViewStyle.Grouped &&
			mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched) {
		
			int etchedLineColor = res.getColor(R.color.atv_cell_grouped_etched_line);
			if (backgroundStyle == ATableViewCellBackgroundStyle.Top) {
				etchedLineColor = res.getColor(R.color.atv_cell_grouped_top_cell_etched_line);
			}
			mEtchedPaint = new Paint();
			mEtchedPaint.setColor(etchedLineColor);
//			mEtchedPaint.setColor(0xDDEEAA00);
		}
		
		// background.
		mBackgroundPaint = new Paint(getPaint());
		mBackgroundPaint.setColor(backgroundColor);
//		mBackgroundPaint.setColor(0xDEDEDE00);
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int backgroundColor, int startColor, int endColor, int rowHeight) {
		
		this(tableView, backgroundStyle, backgroundColor);
		
		// selected.
		mSelectedPaint = new Paint(getPaint());
		Shader shader = new LinearGradient(0, 0, 0, rowHeight, startColor, endColor, Shader.TileMode.MIRROR);
		mSelectedPaint.setShader(shader);	
	}
	
	private Matrix getSeparatorPaintMatrix(Rect bounds) {
		Matrix matrix = new Matrix();
		if (mTableView.getStyle() == ATableViewStyle.Grouped && mCellBackgroundStyle == ATableViewCellBackgroundStyle.Bottom &&
			mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched) {
			matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom),
	        		new RectF(0, 0, bounds.right, bounds.bottom - mStrokeWidth),
	                Matrix.ScaleToFit.FILL);
		}
		
		return matrix;
	}
	
	private Matrix getEtchedPaintMatrix(Rect bounds) {
		Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom),
        		new RectF(mStrokeWidth, mStrokeWidth, bounds.right - mStrokeWidth, bounds.bottom),
                Matrix.ScaleToFit.FILL);
        
        return matrix;
	}
	
	private Matrix getBackgroundPaintMatrix(Rect bounds) {
		ATableViewStyle tableViewStyle = mTableView.getStyle();
		
		RectF rect = new RectF(mStrokeWidth, mStrokeWidth, bounds.right - mStrokeWidth, bounds.bottom - mStrokeWidth);
		if (tableViewStyle == ATableViewStyle.Plain) {
			if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Bottom ||
				mCellBackgroundStyle == ATableViewCellBackgroundStyle.Single) {
				rect.bottom += mStrokeWidth; 
			}
			rect.left = rect.top = 0; rect.right += mStrokeWidth;
		} else {
			rect.top += mStrokeWidth;
			if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Bottom &&
				mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched) {
				rect.bottom -= mStrokeWidth;
			} else if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Top ||
				mCellBackgroundStyle == ATableViewCellBackgroundStyle.Middle) {
				rect.bottom += mStrokeWidth;
			}
		}
		
		Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom), rect, Matrix.ScaleToFit.FILL);
		return matrix;
	}
	
	@Override
	protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
		canvas.save();
		Rect bounds = canvas.getClipBounds();
		
		// separator.
		canvas.concat(getSeparatorPaintMatrix(bounds));
		shape.draw(canvas, mSeparatorPaint);
		
        // etched.
		canvas.concat(getEtchedPaintMatrix(bounds));
		if (mEtchedPaint != null) shape.draw(canvas, mEtchedPaint);
		
		canvas.restore();
		
		// background.
		canvas.concat(getBackgroundPaintMatrix(bounds));
		shape.draw(canvas, mBackgroundPaint);
		
		/*
		if (mEtchedPaint != null) shape.draw(canvas, mEtchedPaint);
		
		Rect bounds = canvas.getClipBounds();
		
		Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom),
        		getDestinationRectF(bounds, mTableViewStyle, mCellBackgroundStyle, mStrokeWidth),
                Matrix.ScaleToFit.FILL);
        canvas.concat(matrix);
		
        shape.draw(canvas, mBackgroundPaint);
        if (mSelectedPaint != null) shape.draw(canvas, mSelectedPaint);*/
	}
}
