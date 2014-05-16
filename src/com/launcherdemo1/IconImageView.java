package com.launcherdemo1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 这是显示应用图标的imageview,对应用图标进行加边框和加阴影的处理
 * 
 *说明：
 *1. xml 设置的宽和高是包括了阴影的
 *
 * @author ZhiCheng
 * 
 */
public class IconImageView extends View {

	private final String TAG = IconImageView.class.getSimpleName();
	
	
	
	/**
	 * 绘制效果的参数
	 */
	//圆角的角度
	private float mRoundedRate = 6f;
	
	
	/**
	 * 阴影只有底部和左右两边有，以原图片做为比例进行相乘
	 */
	//阴影的宽度比例
	private float mShadowWith =  0.2f;
	
	//下方阴影的高度比例
	private float mShadowDropDownHeight = 0.4f;
	
	/************************************结束绘制参数调整****************************/
	
	//原始的没有处理过的图片
	private Bitmap mOriginalBitmap;
	
	//处理过的图片
	private Bitmap mBitmapWithBorderAndShadow;
	
	//用于绘制的图片
	private Bitmap mDrawBitmap;

	
	private Context mContext;
	
	public IconImageView(Context context) {
		super(context);
		mContext = context;
	}

	public IconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	public IconImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public void setImageBitmap(Bitmap bm){
		mOriginalBitmap = bm;
		update();
		Log.i("test", "调用setImageBitmap");
	}
	
	public void setImageDrawable(Drawable drawable){
		setImageBitmap(drawableToBitmap(drawable));
	}
	
	private void update(){
		mBitmapWithBorderAndShadow = null;
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mOriginalBitmap == null){
			return ;
		}
		if(mBitmapWithBorderAndShadow == null){
			createIconBitmap();
			mDrawBitmap = mBitmapWithBorderAndShadow;
		}
		if(mDrawBitmap != null){
			canvas.drawBitmap(mBitmapWithBorderAndShadow,0,0,null);
		}else{
			Log.e(TAG, "  mDrawBitmap is null");
		}
		
	}
	
	private void createIconBitmap(){
		mBitmapWithBorderAndShadow = addShadowToBitmap(mContext, addBorderToImage(mOriginalBitmap));
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if (specMode == MeasureSpec.EXACTLY) {

		} else {
			
		}
		
		result = specSize;
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if (specMode == MeasureSpec.EXACTLY) {

		} else {
			
		}
		
		result = specSize;
		return result;
	}
	
	/**
	 * 绘制阴影，实际上是绘制了一张阴影的图片
	 * @param context
	 * @param src
	 * @return
	 */
	private Bitmap addShadowToBitmap(Context context,Bitmap src){
		
		
		int shadowWidth = (int) (mShadowWith * src.getWidth());
		int dropshadowHeith = (int) (mShadowDropDownHeight * src.getHeight());
		
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth()+shadowWidth*2,
				src.getHeight() + dropshadowHeith, Bitmap.Config.ARGB_8888);
		
		Bitmap shadow = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow3);
		
		Canvas canvas = new Canvas(bmOut);
		
		Rect srcSrcRect = new Rect(0, 0, src.getWidth(), src.getHeight());
		Rect srcDstRect = new Rect(shadowWidth,0,shadowWidth+src.getWidth(),src.getHeight());
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		
		canvas.drawBitmap(src, srcSrcRect, srcDstRect, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		
		Rect shadowDstRect = new Rect(0,(int) (0.0*bmOut.getHeight()),bmOut.getWidth(),bmOut.getHeight());
		Rect shadowSrcRect = new Rect(0,0,shadow.getWidth(),shadow.getHeight());
		
		canvas.drawBitmap(shadow,shadowSrcRect, shadowDstRect, paint);
		return bmOut;
		
	}

	/**
	 * 计算除去阴影的实际图片的高
	 * @return
	 */
	private int getBorderHeight(){
		return (int) (getHeight()/(1+mShadowDropDownHeight));
	}
	
	/**
	 * 计算除去阴影的实际图片的宽
	 * @return
	 */
	private int getBorderWidth(){
		return (int)(getWidth()/(1+2*mShadowWith));
	}
	
	private Bitmap addBorderToImage(Bitmap src) {

		int width = getBorderWidth();
		int height = getBorderHeight();
		
		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(output);
		
		
		
		RectF outerRect = new RectF(0,0,width,height);
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		
		LinearGradient mLinearGradientClamp = new LinearGradient(0, 0, 0, height, new int[] {getCenterTopColor(src),getCenterBottomColor(src)}, null, TileMode.CLAMP);
		
		
		paint.setShader(mLinearGradientClamp);
		
		canvas.drawRoundRect(outerRect, width/mRoundedRate, height/mRoundedRate, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		int sw = src.getWidth();
		int sh = src.getHeight();
		
		Rect srcRect = new Rect(0,0,sw,sh);
		Rect dstRect = getDstRect(sw,sh,width,height);
		
		canvas.drawBitmap(src, srcRect, dstRect, paint);
		
		
		return output;
	}
	
	
	private Rect getDstRect( int sw,int sh,int w,int h){
			return new Rect(0,0,w,h);
	}
	
	/**
	 * 得到原始图片中，底部中间的第一个完全不透明的颜色
	 * @return
	 */
	private int getCenterBottomColor(Bitmap src){
		int w = src.getWidth();
		int h = src.getHeight();
		
		int x = (int) (w*0.5);
		
		int c = Color.WHITE;
		for(int y =0;y<h;++y){
			int pixel = src.getPixel(x, y);
			if(Color.alpha(pixel)==0xFF){
				c = pixel;
			}
		}
		
		return softColor(c);
	}
	
	/**
	 * 得到原始图片中，顶部中间的第一个完全不透明的颜色
	 * @return
	 */
	private int getCenterTopColor(Bitmap src){
		int w = src.getWidth();
		int h = src.getHeight();
		
		int x = (int) (w*0.5);
		
		int c = Color.WHITE;
		for(int y =h-1;y>=0;--y){
			int pixel = src.getPixel(x, y);
			if(Color.alpha(pixel)==0xFF){
				c = pixel;
			}
		}
		return softColor(c);
	}
	
	/**
	 * 将颜色变浅，变淡
	 * @param color
	 * @return
	 */
	private int softColor(int color){
		int r = (int) (Color.red(color)*1.3);
		int b = (int) (Color.blue(color)*1.3);
		int g = (int) (Color.green(color)*1.3);
		int af = 0xFF;
		
		if(b >= 0xFF){
			b = 0xFF;
		}
		if(r>=0xFF){
			r = 0xFF;
		}
		if(g>=0xFF){
			g = 0xFF;
		}
		
		return af<<24|r<<16|g<<8|b;
	}

	
}
