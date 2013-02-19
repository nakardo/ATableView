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

import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;

public class ATableViewCellDrawable extends ShapeDrawable {
	public static final float CELL_STROKE_WIDTH_DP = 1f;
	private static final float CELL_GROUPED_STYLE_CORNER_RADIUS = 7;
	
	public enum ATableViewCellBackgroundStyle { Single, Top, Middle, Bottom };
	
	private ATableViewStyle mTableViewStyle;
	private ATableViewCellBackgroundStyle mCellBackgroundStyle;
	private Paint mSeparatorPaint;
	private Paint mBackgroundPaint;
	private float mStrokeWidth;
	
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
	
	private static RectF getDestinationRectF(Rect bounds, ATableViewStyle tableViewStyle,
			ATableViewCellBackgroundStyle backgroundStyle, float strokeWidth) {
		
		RectF rect = new RectF(strokeWidth, strokeWidth, bounds.right - strokeWidth, bounds.bottom - strokeWidth);
		if (tableViewStyle == ATableViewStyle.Plain) {
			if (backgroundStyle == ATableViewCellBackgroundStyle.Bottom ||
				backgroundStyle == ATableViewCellBackgroundStyle.Single) {
				rect.bottom += strokeWidth; 
			}
			rect.left = rect.top = 0; rect.right += strokeWidth;
		} else {
			if (backgroundStyle == ATableViewCellBackgroundStyle.Top ||
				backgroundStyle == ATableViewCellBackgroundStyle.Middle) {
				rect.bottom += strokeWidth;
			}
		}
		
		return rect;
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int backgroundColor) {
		
		super(getShape(tableView, backgroundStyle));
		
		mTableViewStyle = tableView.getStyle();
		mCellBackgroundStyle = backgroundStyle;
		
		// separator.
		mSeparatorPaint = new Paint(getPaint());
		mSeparatorPaint.setColor(tableView.getSeparatorColor());
		
		// stroke.
		mStrokeWidth = CELL_STROKE_WIDTH_DP * tableView.getResources().getDisplayMetrics().density;
		int roundedStrokeWidth = (int) Math.ceil(mStrokeWidth);
		
		// add padding to avoid content to overlap with cell stroke lines.
		int marginBottom = 0;
		if (backgroundStyle == ATableViewCellBackgroundStyle.Single ||
			backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
			marginBottom = roundedStrokeWidth;
		}
		setPadding(roundedStrokeWidth, roundedStrokeWidth, roundedStrokeWidth, marginBottom);
		
		// background.
		mBackgroundPaint = new Paint(getPaint());
		mBackgroundPaint.setColor(backgroundColor);
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int rowHeight, int startColor, int endColor) {
		
		this(tableView, backgroundStyle, startColor);
		Shader shader = new LinearGradient(0, 0, 0, rowHeight, startColor, endColor, Shader.TileMode.MIRROR);
		mBackgroundPaint.setShader(shader);	
	}
	
	@Override
	protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
		shape.draw(canvas, mSeparatorPaint);
		
		Rect bounds = canvas.getClipBounds();
		
		Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom),
        		getDestinationRectF(bounds, mTableViewStyle, mCellBackgroundStyle, mStrokeWidth),
                Matrix.ScaleToFit.FILL);
        canvas.concat(matrix);
		
        shape.draw(canvas, mBackgroundPaint);
	}
}
