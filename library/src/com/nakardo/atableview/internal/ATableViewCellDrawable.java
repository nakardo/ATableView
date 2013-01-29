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
//	private static final float CELL_GROUPED_STYLE_BACKGROUND_RADIUS = 16;
	private static final float CELL_GROUPED_STYLE_BACKGROUND_RADIUS = 0;
	
	private ATableViewStyle mTableViewStyle;
	private ATableViewCellBackgroundStyle mCellBackgroundStyle;
	private Paint mFillPaint;
	private Paint mStrokePaint;
	private int mStrokeWidth;
	
	public enum ATableViewCellBackgroundStyle { Single, Top, Middle, Bottom };

	private static RoundRectShape getShape(ATableViewStyle tableStyle,
			ATableViewCellBackgroundStyle backgroundStyle) {
		
		float[] radius = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		if (tableStyle == ATableViewStyle.Grouped) {
			float radii = CELL_GROUPED_STYLE_BACKGROUND_RADIUS;
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
		
		float padding = strokeWidth / 2;
		
		RectF rect = new RectF(padding, padding, bounds.right - padding, bounds.bottom + padding);
		if (tableViewStyle == ATableViewStyle.Grouped) {
			if (backgroundStyle == ATableViewCellBackgroundStyle.Single ||
				backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
				rect = new RectF(padding, padding, bounds.right - padding, bounds.bottom - padding);
			}
		}
		
		return rect;
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int backgroundColor) {
		
		super(getShape(tableView.getStyle(), backgroundStyle));
		
		mTableViewStyle = tableView.getStyle();
		mCellBackgroundStyle = backgroundStyle;
		
		mFillPaint = new Paint(this.getPaint());
		mFillPaint.setColor(backgroundColor);

		Resources res = tableView.getResources();
		mStrokeWidth = (int)(CELL_STROKE_WIDTH_DP * res.getDisplayMetrics().density);
		
		// add padding to avoid content to overlap with cell stroke lines.
		int marginBottom = 0;
		if (backgroundStyle == ATableViewCellBackgroundStyle.Single ||
			backgroundStyle == ATableViewCellBackgroundStyle.Bottom) {
			marginBottom = mStrokeWidth;
		}
		setPadding(mStrokeWidth, mStrokeWidth, mStrokeWidth, marginBottom);
		
		mStrokePaint = new Paint(mFillPaint);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(mStrokeWidth);
		mStrokePaint.setColor(tableView.getSeparatorColor());
	}
	
	public ATableViewCellDrawable(ATableView tableView, ATableViewCellBackgroundStyle backgroundStyle,
			int rowHeight, int startColor, int endColor) {
		
		this(tableView, backgroundStyle, startColor);
		Shader shader = new LinearGradient(0, 0, 0, rowHeight, startColor, endColor, Shader.TileMode.MIRROR);
		mFillPaint.setShader(shader);
	}
 
    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
    	shape.resize(canvas.getClipBounds().right, canvas.getClipBounds().bottom);
        shape.draw(canvas, mFillPaint);
        
        Rect bounds = canvas.getClipBounds();
        
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bounds.right, bounds.bottom),
                getDestinationRectF(bounds, mTableViewStyle, mCellBackgroundStyle, mStrokeWidth),
                Matrix.ScaleToFit.FILL);
        canvas.concat(matrix);
        
        shape.draw(canvas, mStrokePaint);
    }
}