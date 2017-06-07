package fi.moflac.railtrack;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import fi.moflac.railtrack.R;

@SuppressWarnings("rawtypes")
public class ZugStationItemizedOverlay extends ItemizedOverlay {
	Context mContext;
	int zLevel;
	private ZugActivity mMain;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Bitmap bmp;
	private int mNameTextSize;
	private int mAbbrTextSize;
	private static final float X_TEXT = 2.0f;
	private static final float Y_TEXT = 7.0f;
	private static final float X_BMP = 24.0f;
	private static final float Y_BMP = 24.0f;
	private Paint paint;
	private Point ptScreenCoord;
	private final SharedPreferences prefs;
	float scale;
	int yv;
	int xv;
	int xb;
	int yb;
	
	public ZugStationItemizedOverlay(Drawable defaultMarker) {
		  super(boundCenter(defaultMarker));
		  
		  prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		  
		}
	public ZugStationItemizedOverlay(Drawable defaultMarker, Context context, ZugActivity main) {
		  super(boundCenter(defaultMarker));
		  
		  mContext = context;
		  prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		  mMain=main;
		  paint = new Paint();
		  bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.asema2);
		  mOverlays = new ArrayList<OverlayItem>();
		  final float scale = mContext.getResources().getDisplayMetrics().density;
		  mNameTextSize = Math.round(scale*8-0.5f); //15
		  mAbbrTextSize = Math.round(scale*15-0.5f);
		  xv=(int) (X_TEXT * scale + 0.5f);
		  yv=(int) (Y_TEXT * scale + 0.5f);
		  xb=(int) (X_BMP * scale + 0.5f);
		  yb=(int) (Y_BMP * scale + 0.5f);
		  ptScreenCoord = new Point() ;
		}
	@Override
	protected OverlayItem createItem(int i) {
		
		return mOverlays.get(i);
	}
	@Override
	public int size() {
		
		return mOverlays.size();
	}
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    setLastFocusedIndex(-1);
	    populate();
	}
	public void clear()
	{
		mOverlays.clear();
		setLastFocusedIndex(-1);
		populate();
	
	}
	public void draw(android.graphics.Canvas canvas,MapView mapView, boolean shadow) {
		 
		for (int index = 0; index < mOverlays.size(); index++)
        {
            OverlayItem item = mOverlays.get(index);

            // Converts lat/lng-Point to coordinates on the screen
            GeoPoint point = item.getPoint();
            
            mapView.getProjection().toPixels(point, ptScreenCoord);
        
            //Paint
            
            paint.setTextAlign(Paint.Align.CENTER);
            
            String pp=item.getTitle();
            
            paint.setARGB(255, 0, 0, 0);
            
            
           // paint.setShadowLayer(5, 0, 0,Color.argb(255, 255, 255, 255) );
            paint.setAntiAlias(true);
            
            
            //show text to the right of the icon 35 74
            canvas.drawBitmap(bmp,ptScreenCoord.x-xb, ptScreenCoord.y-yb, paint);
            //item.setMarker(mContext.getResources().getDrawable(R.drawable.veturi));
            if(prefs.getBoolean("station_name", true)==true)
            {
            	if(pp.length()>10)
            	{
            		pp=pp.substring(0, 8)+".";
            		paint.setTextSize(mNameTextSize);
            	
            	}
            	canvas.drawText(pp, ptScreenCoord.x-xv, ptScreenCoord.y+yv, paint);
            }
            else
            {
            	paint.setTextSize(mAbbrTextSize);
            	canvas.drawText(item.getSnippet(), ptScreenCoord.x-xv, ptScreenCoord.y+yv, paint);
            }
            
            
            
        }
	}
	@Override
	public boolean onTap(int index) {
		try
		{
			OverlayItem item = mOverlays.get(index);
			String city=item.getSnippet().toUpperCase();
			Intent i= new Intent("zug.activity.ACTIVITY_STATIONLIST_INTENT");
			i.putExtra("citykey", city);
			mMain.startActivityForResult(i, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	  return true;
	}
	public void myPopulate()
	{
		this.populate();
	}

}
