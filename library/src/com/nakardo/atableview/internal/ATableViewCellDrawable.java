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

public class ATableViewCellDrawable extends ShapeDrawable {
	public static final float CELL_STROKE_WIDTH_DP = 1f;
	private static final float CELL_GROUPED_STYLE_CORNER_RADIUS = 7;
	
	public enum ATableViewCellBackgroundStyle { Single, Top, Middle, Bottom };
	
	private ATableView mTableView;
	private ATableViewCellBackgroundStyle mCellBackgroundStyle;
	private float mStrokeWidth;
	
	private Paint mSeparatorPaint;
	private Paint mTopEtchedPaint;
	private Paint mBottomEtchedPaint;
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
	
	private int getSeparatorColor() {
		Resources res = mTableView.getResources();
		
		// pull color, -1 implies no custom color has being defined so we go with defaults.
		int color = mTableView.getSeparatorColor();
		if (color == -1) {
			color = res.getColor(R.color.atv_plain_separator);
			if (mTableView.getStyle() == ATableViewStyle.Grouped) {
				color = res.getColor(R.color.atv_grouped_separator);
			}
		}
		return color;
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int backgroundColor) {
		
		super(getShape(tableView, backgroundStyle));
		Resources res = tableView.getResources();
		
		mTableView = tableView;
		mCellBackgroundStyle = backgroundStyle;
		
		// separator.
		mSeparatorPaint = new Paint(getPaint());
		mSeparatorPaint.setColor(getSeparatorColor());
		
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
		
		// etched lines, only for grouped tables, with SingleLineEtched style.
		if (mTableView.getStyle() == ATableViewStyle.Grouped &&
			mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched) {
			int etchedLineColor = res.getColor(R.color.atv_cell_grouped_etched_line);
			
			mBottomEtchedPaint = new Paint(getPaint());
			mBottomEtchedPaint.setColor(etchedLineColor);
			
			if (backgroundStyle == ATableViewCellBackgroundStyle.Top ||
				backgroundStyle == ATableViewCellBackgroundStyle.Single) {
				etchedLineColor = res.getColor(R.color.atv_cell_grouped_top_cell_etched_line);
			}
			mTopEtchedPaint = new Paint(getPaint());
			mTopEtchedPaint.setColor(etchedLineColor);
		}
		
		// background.
		mBackgroundPaint = new Paint(getPaint());
		mBackgroundPaint.setColor(backgroundColor);
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int backgroundColor, int startColor, int endColor, int rowHeight) {
		
		this(tableView, backgroundStyle, backgroundColor);
		
		// selected.
		mSelectedPaint = new Paint(getPaint());
		Shader shader = new LinearGradient(0, 0, 0, rowHeight, startColor, endColor, Shader.TileMode.MIRROR);
		mSelectedPaint.setShader(shader);	
	}
	
	private boolean isGroupedDoubleLineEtchedRow() {
		return mTableView.getStyle() == ATableViewStyle.Grouped &&
			   mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched &&
			   mCellBackgroundStyle == ATableViewCellBackgroundStyle.Bottom ||
			   mCellBackgroundStyle == ATableViewCellBackgroundStyle.Single;
	}
	
	private Matrix getSeparatorPaintMatrix(Rect bounds) {
		Matrix matrix = new Matrix();
		if (isGroupedDoubleLineEtchedRow()) {
			matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom),
					new RectF(0, 0, bounds.right, bounds.bottom - mStrokeWidth),
					Matrix.ScaleToFit.FILL);
		}
		
		return matrix;
	}
	
	private Matrix getTopEtchedPaintMatrix(Rect bounds) {
		RectF rect = new RectF(mStrokeWidth, mStrokeWidth, bounds.right - mStrokeWidth, bounds.bottom);
		if (isGroupedDoubleLineEtchedRow()) {
			rect.bottom -= mStrokeWidth * 2;
		}
		
		Matrix matrix = new Matrix();
		matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom), rect, Matrix.ScaleToFit.FILL);
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
			if (mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched) {
				if (isGroupedDoubleLineEtchedRow()) {
					rect.bottom -= mStrokeWidth;
				} else if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Top ||
						mCellBackgroundStyle == ATableViewCellBackgroundStyle.Middle) {
					rect.bottom += mStrokeWidth;
				}
				rect.top += mStrokeWidth;
			} else {
				if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Top ||
					mCellBackgroundStyle == ATableViewCellBackgroundStyle.Middle) {
					rect.bottom += mStrokeWidth;
				} else if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Single){
					rect.bottom -= mStrokeWidth;
				}
			}
		}
		
		Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom), rect, Matrix.ScaleToFit.FILL);
		return matrix;
	}
	
	private Matrix getSelectedPaintMatrix(Rect bounds) {
		ATableViewStyle tableViewStyle = mTableView.getStyle();
		
		RectF rect = new RectF(mStrokeWidth, mStrokeWidth, bounds.right - mStrokeWidth, bounds.bottom - mStrokeWidth);
		if (tableViewStyle == ATableViewStyle.Plain) {
			if (mCellBackgroundStyle == ATableViewCellBackgroundStyle.Bottom ||
				mCellBackgroundStyle == ATableViewCellBackgroundStyle.Single) {
				rect.bottom += mStrokeWidth;
			}
			rect.left = rect.top = 0;
			rect.right += mStrokeWidth;
		} else {
			if (mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLine &&
				mCellBackgroundStyle == ATableViewCellBackgroundStyle.Single) {
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
		
		// bottom etched line.
		if (mBottomEtchedPaint != null) shape.draw(canvas, mBottomEtchedPaint);
		canvas.restore(); canvas.save();	
		
		// separator.
		canvas.concat(getSeparatorPaintMatrix(bounds));
		shape.draw(canvas, mSeparatorPaint);
		canvas.restore(); canvas.save();
		
        // top etched line.
		canvas.concat(getTopEtchedPaintMatrix(bounds));
		if (mTopEtchedPaint != null) shape.draw(canvas, mTopEtchedPaint);
		canvas.restore(); canvas.save();
		
		// background.
		canvas.concat(getBackgroundPaintMatrix(bounds));
		shape.draw(canvas, mBackgroundPaint);
		canvas.restore(); canvas.save();
		
		// selected.
		canvas.concat(getSelectedPaintMatrix(bounds));
		if (mSelectedPaint != null) shape.draw(canvas, mSelectedPaint);
	}
}
