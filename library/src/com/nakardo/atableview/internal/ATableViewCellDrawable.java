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
	private static final float CELL_STROKE_WIDTH_DP = 1f;
	private static final float CELL_GROUPED_STYLE_CORNER_RADIUS = 7;
	
	public enum ATableViewCellBackgroundStyle { Single, Top, Middle, Bottom };
	
	private ATableView mTableView;
	private ATableViewCellBackgroundStyle mCellBackgroundStyle;
	private int mRowHeight;
	private int mStrokeWidth;
	
	private Paint mSeparatorPaint;
	private Paint mTopEtchedPaint;
	private Paint mBottomEtchedPaint;
	private Paint mBackgroundPaint;
	private Paint mSelectedPaint;
	
	private int mStartColor;
	private int mEndColor;
	
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
	
	private static boolean isGroupedDoubleLineEtchedRow(ATableView tableView,
			ATableViewCellBackgroundStyle backgroundStyle) {
		
		return tableView.getStyle() == ATableViewStyle.Grouped &&
			   tableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched &&
			   (backgroundStyle == ATableViewCellBackgroundStyle.Bottom ||
			   backgroundStyle == ATableViewCellBackgroundStyle.Single);
	}
	
	public static int getStrokeWidth(Resources res) {
		return (int) Math.floor(CELL_STROKE_WIDTH_DP * res.getDisplayMetrics().density);
	}
	
	public static Rect getContentPadding(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle) {
		int strokeWidth = getStrokeWidth(tableView.getResources());
		int margins = 0, marginTop = 0, marginBottom = 0;
		
		// calculate margins to avoid content to overlap with cell stroke lines.
		if (tableView.getStyle() == ATableViewStyle.Grouped) {
			margins = marginTop = strokeWidth;
			
			// double lines for etched single / bottom rows, this is a pain in the ass.
			if (isGroupedDoubleLineEtchedRow(tableView, backgroundStyle)) {
				marginBottom = strokeWidth * 2;
			} else if (backgroundStyle == ATableViewCellBackgroundStyle.Single ||
					backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
				marginBottom = strokeWidth;
			}
		} else if (backgroundStyle == ATableViewCellBackgroundStyle.Middle ||
				backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
			marginTop = strokeWidth;
		}
		
		return new Rect(margins, marginTop, margins, marginBottom);
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
			int rowHeight, int backgroundColor) {
		
		super(getShape(tableView, backgroundStyle));
		Resources res = tableView.getResources();
		
		mTableView = tableView;
		mCellBackgroundStyle = backgroundStyle;
		
		// Closes #11, even we should be able to pull height from canvas it doesn't work well on ~2.2.
		mRowHeight = rowHeight;
		
		// separator.
		mSeparatorPaint = new Paint(getPaint());
		mSeparatorPaint.setColor(getSeparatorColor());
		
		// calculate stroke width.
		mStrokeWidth = getStrokeWidth(res);
		
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
			int rowHeight, int startColor, int endColor) {
		
		this(tableView, backgroundStyle, rowHeight, android.R.color.transparent);
		
		// selected.
		mSelectedPaint = new Paint(getPaint());
		mStartColor = startColor;
		mEndColor = endColor;	
	}
	
	private Matrix getSeparatorPaintMatrix(Rect bounds) {
		Matrix matrix = new Matrix();
		
		if (isGroupedDoubleLineEtchedRow(mTableView, mCellBackgroundStyle)) {
			RectF rect = new RectF(0, 0, bounds.right, bounds.bottom - mStrokeWidth);
			matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom), rect, Matrix.ScaleToFit.FILL);
		}
		
		return matrix;
	}
	
	private Matrix getTopEtchedPaintMatrix(Rect bounds) {
		Matrix matrix = new Matrix();
		
		RectF rect = new RectF(mStrokeWidth, mStrokeWidth, bounds.right - mStrokeWidth, bounds.bottom);
		if (isGroupedDoubleLineEtchedRow(mTableView, mCellBackgroundStyle)) {
			rect.bottom -= mStrokeWidth * 2;
		}
		matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom), rect, Matrix.ScaleToFit.FILL);
        
		return matrix;
	}
	
	private Matrix getBackgroundPaintMatrix(Rect bounds) {		
		Matrix matrix = new Matrix();
		
		Rect padding = getContentPadding(mTableView, mCellBackgroundStyle);
		int paddingTop = padding.top;
		if (mTableView.getStyle() == ATableViewStyle.Grouped &&
			mTableView.getSeparatorStyle() == ATableViewCellSeparatorStyle.SingleLineEtched) {
			paddingTop *= 2;
		}
		RectF rect = new RectF(padding.left, paddingTop, bounds.right - padding.right, bounds.bottom - padding.bottom);
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom), rect, Matrix.ScaleToFit.FILL);
        
		return matrix;
	}
	
	private Matrix getSelectedPaintMatrix(Rect bounds) {
		Matrix matrix = new Matrix();
		
		Rect padding = getContentPadding(mTableView, mCellBackgroundStyle);
		RectF rect = new RectF(padding.left, padding.top, bounds.right - padding.right, bounds.bottom - padding.bottom);
		if (isGroupedDoubleLineEtchedRow(mTableView, mCellBackgroundStyle)) {
			rect.bottom += mStrokeWidth;
		}
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
		if (mSelectedPaint != null) {
			
			// we'll set the selected color on onDraw event since we don't know drawable height up to here.
			Shader shader = new LinearGradient(0, 0, 0, mRowHeight, mStartColor, mEndColor, Shader.TileMode.MIRROR);
			mSelectedPaint.setShader(shader);
			
			shape.draw(canvas, mSelectedPaint);
		}
	}
}
