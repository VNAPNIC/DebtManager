package vnapnic.project.debtmanager.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class RoundedImageView extends ImageView {

	public Paint mBackgroundPaint;

	public static final int bgPaint = Color.parseColor("#34495e");

	private static final double SCALE_FACTOR = 0.92;

	public RoundedImageView(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
		mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBackgroundPaint.setColor(bgPaint);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (getDrawable() == null)
		{
			return;
		}

		if (getWidth() == 0 || getHeight() == 0)
		{
			return;
		}
		Bitmap b =  ((BitmapDrawable)getDrawable()).getBitmap() ;
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);

		double w = getWidth() * SCALE_FACTOR;

		double left = getWidth() - w;
		double top = getHeight() - (getHeight() * SCALE_FACTOR);

		Bitmap roundBitmap =  getCroppedBitmap(bitmap, (int)w);
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, mBackgroundPaint);
		canvas.drawBitmap(roundBitmap, (int) left / 2, (int) top / 2, null);
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius)
	{
	    Bitmap sbmp;
	    if(bmp.getWidth() != radius || bmp.getHeight() != radius)
	        sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
	    else
	        sbmp = bmp;
	    Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

	    paint.setAntiAlias(true);
	    paint.setFilterBitmap(true);
	    paint.setDither(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(Color.parseColor("#BAB399"));
	    canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
	            sbmp.getWidth() / 2+0.1f, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(sbmp, rect, rect, paint);
	    return output;
	}

	public void setOuterColor(int color)
	{
		mBackgroundPaint.setColor(color);
		invalidate();
		requestLayout();
	}
}
