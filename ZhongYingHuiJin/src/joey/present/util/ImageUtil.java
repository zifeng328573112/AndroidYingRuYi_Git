package joey.present.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ImageUtil {
	Context 		mContext;
	ImageView 		mImageView;
	int 			id;

	public ImageUtil(Context mContext, ImageView mImageView, int id) {
		this.mContext = mContext;
		this.mImageView = mImageView;
		this.id = id;
	}

	public int getScreenHeight() {
		Activity activity = (Activity) mContext;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public int getScreenWidth() {
		Activity activity = (Activity) mContext;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public void scaleBitmap() {
		float scale;
		if (getScreenHeight() == 720) {
			scale 					= 1.5f;
			Bitmap bitmap 			= BitmapFactory.decodeResource(((Activity) mContext).getResources(), id);
			int bmptwidth 			= bitmap.getWidth();
			int bmptHeight 			= bitmap.getHeight();
			float scaleWidth 		= (float) (bmptwidth * scale);
			float scaleHeight 		= (float) (bmptHeight * scale);
			Matrix matrix 			= new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizeBmp 		= Bitmap.createBitmap(bitmap, 0, 0, bmptwidth, bmptHeight, matrix, true);
			mImageView.setImageBitmap(resizeBmp);
		}
	}

	public void bigBitmap() {
		float scaleWidth 			= 1;
		float scaleHeight 			= 1;
		Activity activity 			= (Activity) mContext;
		Bitmap bitmap 				= BitmapFactory.decodeResource(activity.getResources(), id);
		int bmpWidth 				= bitmap.getWidth();
		int bmpHeight 				= bitmap.getHeight();
		double scale				= 1.1;
		scaleWidth 					= (float) (scaleWidth * scale);
		scaleHeight 				= (float) (scaleHeight * scale);
		Matrix matrix 				= new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp 			= Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
		mImageView.setImageBitmap(resizeBmp);
	}

	public void smallBitmap() {
		float scaleWidth 		= 1;
		float scaleHeight 		= 1;
		Activity activity 		= (Activity) mContext;
		Bitmap bitmap 			= BitmapFactory.decodeResource(activity.getResources(), id);
		int bmpWidth 			= bitmap.getWidth();
		int bmpHeight 			= bitmap.getHeight();
		double scale 			= 0.9;
		scaleWidth 				= (float) (scaleWidth * scale);
		scaleHeight 			= (float) (scaleHeight * scale);
		Matrix matrix 			= new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp 		= Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
		mImageView.setImageBitmap(resizeBmp);
	}
}
