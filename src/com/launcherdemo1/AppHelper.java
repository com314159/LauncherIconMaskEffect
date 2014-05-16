package com.launcherdemo1;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppHelper {
	private Context mContext;

	private List<ResolveInfo> mAppResolveInfoList = new ArrayList<ResolveInfo>();

	public AppHelper(Context context) {
		mContext = context;
	}

	public void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mAppResolveInfoList = new ArrayList<ResolveInfo>();
		List<ResolveInfo> list = mContext.getPackageManager()
				.queryIntentActivities(mainIntent, 0);
		for (int i = 0; i < list.size(); ++i) {
			if (!mAppResolveInfoList.contains(list.get(i))) {
				mAppResolveInfoList.add(list.get(i));
			}
		}
		// mAppResolveInfoList =
		// mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		mAppAdapter.notifyDataSetChanged();
	}

	public BaseAdapter getAppAdapter() {
		return mAppAdapter;
	}

	public OnItemClickListener getAppOnItemClickListener() {
		return mAppOnItemClickListener;
	}

	public void openAppAt(int position) {
		ResolveInfo appInfo = mAppResolveInfoList.get(position);
		ComponentName component = new ComponentName(
				appInfo.activityInfo.packageName, appInfo.activityInfo.name);
		Intent intent = new Intent();
		intent.setComponent(component);
		mContext.startActivity(intent);
	}

	private BaseAdapter mAppAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return mAppResolveInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppResolveInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View appItemLayout;
			if (convertView == null) {
				appItemLayout = ((LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.gridview_item, null);
			} else {
				appItemLayout = convertView;
			}

			ResolveInfo appInfo = mAppResolveInfoList.get(position);
			BitmapDrawable appIcon = (BitmapDrawable) appInfo.activityInfo
					.loadIcon(mContext.getPackageManager());

			String appName = appInfo.loadLabel(mContext.getPackageManager())
					.toString();

			ImageView appIconImageView = (ImageView) appItemLayout
					.findViewById(R.id.appicon_imageview);
			TextView appNameTextView = (TextView) appItemLayout
					.findViewById(R.id.appname_textview);

			// if(appName.equals("浏览器")){
			Log.i("test", appName + appIcon.getBitmap().getWidth());
			Log.i("test", appName + appIcon.getBitmap().getHeight());
			appIconImageView.setImageBitmap(addShadowToBitmap(mContext,addBorderToImage(appIcon
					.getBitmap())));
			appNameTextView.setText(appName);
			return appItemLayout;
		}
	};

	private OnItemClickListener mAppOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			openAppAt(position);
		}
	};
	
	public Bitmap addShadowToImage(Bitmap src) {
		// create new bitmap, which will be painted and becomes result image
		
		int radis = 4;
		
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth()+radis*2,
				src.getHeight()+radis*2, Bitmap.Config.ARGB_8888);
//		// setup canvas for painting
		Canvas canvas = new Canvas(bmOut);
//		// setup default color
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		// create a blur paint for capturing alpha
		Paint ptBlur = new Paint();
		
		ptBlur.setMaskFilter(new BlurMaskFilter(radis, Blur.NORMAL));
		// capture alpha into a bitmap
		Bitmap bmAlpha = src.extractAlpha(ptBlur, new int[]{0,0});
		// create a color paint
		Paint ptAlphaColor = new Paint();
		ptAlphaColor.setColor(0x994E4E4E);
		// paint color for captured alpha region (bitmap)
		canvas.drawBitmap(bmAlpha, 0, 0, ptAlphaColor);
		// free memory
		bmAlpha.recycle();

		// paint the image source
		canvas.drawBitmap(src, radis, 0, null);

		// return out final image
		return bmOut;
	}
	
	
	public Bitmap addShadowToBitmap(Context context,Bitmap src){
		
		float shadowWidthRadio = 0.2f;
		float shadowDropHeithRadio = 0.4f;
		
		int shadowWidth = (int) (shadowWidthRadio * src.getWidth());
		int dropshadowHeith = (int) (shadowDropHeithRadio * src.getHeight());
		
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

	
	
	public Bitmap addBorderToImage(Bitmap src) {

		int size = 250;
		Bitmap output = Bitmap.createBitmap(size, size,
				Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(output);
		
		
		
		RectF outerRect = new RectF(0,0,size,size);
		
		float outerRadius = 6f;
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		
		LinearGradient mLinearGradientClamp = new LinearGradient(0, 0, 0, size, new int[] {getCenterTopColor2(src),getCenterBottomColor(src)}, null, TileMode.CLAMP);
		
		
		paint.setShader(mLinearGradientClamp);
		
		canvas.drawRoundRect(outerRect, size/outerRadius, size/outerRadius, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		int sw = src.getWidth();
		int sh = src.getHeight();
		
		Rect srcRect = new Rect(0,0,sw,sh);
		Rect dstRect = getDstRect(sw,sh,size);
		
		canvas.drawBitmap(src, srcRect, dstRect, paint);
		
		
		return output;
	}
	
	
	private Rect getDstRect( int sw,int sh,int size){
			return new Rect(0,0,size,size);
	}
	
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
	
	private int getCenterTopColor2(Bitmap src){
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
