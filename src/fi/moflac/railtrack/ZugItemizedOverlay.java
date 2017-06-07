package fi.moflac.railtrack;
import java.util.ArrayList;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.maps.GeoPoint;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import fi.moflac.railtrack.R;


@SuppressWarnings("rawtypes")
public class ZugItemizedOverlay extends ItemizedOverlay {
	Context mContext;
	private ArrayList<OverlayItem> mOverlays;
	private int mTextSize;
	private Bitmap bmp;
	private Bitmap bmpm;
	private Bitmap arrowbmp;
	private Bitmap arrowbmpm;
	private ZugActivity mMain;
	OverlayItem item;
	private static final float X_TEXT = 2.0f;
	private static final float Y_TEXT = 4.0f;
	private static final float X_BMP = 24.0f;
	private static final float Y_BMP = 24.0f;
	private ArrayList<ZugTrain> nearTrainList;
	private Paint paint;
	private ZugParser mzParser;
	private ArrayList<ZugStation> mStationList;
	private int nearTrainsSize;
	final float scale;
	int yv;
	int xv;
	int xb;
	int yb;
	private MapView mmapView;
	private Dialog mdialog;
	SharedPreferences prefs;
	private Point ptScreenCoord;
	LinearLayout v;
	ImageView marker;
	AnimationDrawable markerImage;
	GeoPoint animPoint;
	int bmpchoice;
	
	public ZugItemizedOverlay(Drawable defaultMarker) {
		
		  super(boundCenter(defaultMarker));
		 // populate();
		  scale = mContext.getResources().getDisplayMetrics().density;
		}
	
	public ZugItemizedOverlay(Drawable defaultMarker, Context context, ZugActivity main, int textSize, ZugParser zParser, ArrayList<ZugStation> stationList) {
		  super(boundCenter(defaultMarker));
		  //populate();
		  mMain=main;
		  mContext = context;
		  
		  nearTrainList=new ArrayList<ZugTrain>();
		  paint = new Paint();
		  bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.veturi4);
				  
		  bmpm=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.veturi4m);
		  arrowbmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow);
		  arrowbmpm=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrowm);
		  mOverlays = new ArrayList<OverlayItem>();
		  scale = mContext.getResources().getDisplayMetrics().density;
		  mTextSize = Math.round(scale*textSize-0.5f);
		  xv=(int) (X_TEXT * scale + 0.5f);
		  yv=(int) (Y_TEXT * scale + 0.5f);
		  xb=(int) (X_BMP * scale + 0.5f);
		  yb=(int) (Y_BMP * scale + 0.5f);
		  mzParser=zParser;
		  new Matrix();
		  prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		  ptScreenCoord = new Point();
		  mStationList=stationList;
		 
		  
		}
	
	@Override
	public void draw(android.graphics.Canvas canvas,MapView mapView, boolean shadow) {
		
		mmapView=mapView;
		float zDirection=0;
		
		ArrayList<ZugTrain> roTrain=mzParser.getTrainList();
			
		 
			if (shadow == false)
	        {
	            //cycle through all overlays
	            for (int index = 0; index < mOverlays.size(); index++)
	            {
	                OverlayItem item = mOverlays.get(index);

	                // Converts lat/lng-Point to coordinates on the screen
	                GeoPoint point = item.getPoint();
	                
	                mapView.getProjection().toPixels(point, ptScreenCoord);
	            
	                
	                
	                
	                //Paint
	               
	                paint.setTextAlign(Paint.Align.CENTER);
	                paint.setTextSize(mTextSize);
	                
	                paint.setARGB(255, 255, 255, 255); 
	                paint.setAntiAlias(true);
	                
	                
	                if(index<roTrain.size())
	                {
	                	
	                	zDirection=roTrain.get(index).dir+90;
	                }
	                
	                       

	                
	                canvas.save();
	               
	                
	                //show text to the right of the icon 35 74
	                
	                	//if(prefs.getBoolean("train_direction", true)==true)
	                	if(Integer.valueOf(prefs.getString("train_pic", "2"))==2)
	                	{
	                		if(zDirection>90&&zDirection<=270)
	                		{
	                	
	                			canvas.rotate(zDirection+180, ptScreenCoord.x, ptScreenCoord.y);
	                			canvas.drawBitmap(bmpm,ptScreenCoord.x-xb, ptScreenCoord.y-yb, paint);
	                		}
	                		else
	                		{	
	                			canvas.rotate(zDirection, ptScreenCoord.x, ptScreenCoord.y);
	                			canvas.drawBitmap(bmp,ptScreenCoord.x-xb, ptScreenCoord.y-yb, paint);
	                		}
	                	}
	                	if(Integer.valueOf(prefs.getString("train_pic", "2"))==1)
	                	{
	                		canvas.drawBitmap(bmp,ptScreenCoord.x-xb, ptScreenCoord.y-yb, paint);
	                	}
	                	if(Integer.valueOf(prefs.getString("train_pic", "2"))==3)
	                	{
	                		if(zDirection>90&&zDirection<=270)
	                		{
	                	
	                			canvas.rotate(zDirection+180, ptScreenCoord.x, ptScreenCoord.y);
	                			canvas.drawBitmap(arrowbmpm,ptScreenCoord.x-xb, ptScreenCoord.y-yb, paint);
	                		}
	                		else
	                		{	
	                			canvas.rotate(zDirection, ptScreenCoord.x, ptScreenCoord.y);
	                			canvas.drawBitmap(arrowbmp,ptScreenCoord.x-xb, ptScreenCoord.y-yb, paint);
	                		}
	                	}
	                	//item.setMarker(mContext.getResources().getDrawable(R.drawable.veturi));
	               	canvas.drawText(item.getTitle(), ptScreenCoord.x, ptScreenCoord.y+yv, paint);
	                canvas.restore();
	            }
	        }
			

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
	@Override
	protected boolean onTap(int index) {
		int zoomLevel=mmapView.getZoomLevel();
		ArrayAdapter<ZugTrain> nearAdapter;
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		GeoPoint stationPoint;
		ListView nearLView;
		int curZoom=0;
		Point origCoord=new Point();
		Point compCoord=new Point();
		int dist=0;
		try{
			
		
			nearTrainList.clear();
			mmapView.getProjection().toPixels(mOverlays.get(index).getPoint(), origCoord);
			if(zoomLevel<11)
				curZoom=0;
			if(zoomLevel>=11&&zoomLevel<13)
				curZoom=1;
			if(zoomLevel>=13&&zoomLevel<14)
				curZoom=2;
			if(zoomLevel>14)
				curZoom=3;
			for(int i=0;i<mOverlays.size();i++)
			{
				//add trains near clicked marker
				mmapView.getProjection().toPixels(mOverlays.get(i).getPoint(), compCoord);
				dist=(int)Math.sqrt((Math.pow((origCoord.x-compCoord.x), 2)+Math.pow((origCoord.y-compCoord.y), 2)));
				if (dist<50*scale)
				{
					ZugTrain addT=new ZugTrain();
					addT.guid=mOverlays.get(i).getTitle();
					addT.from=mOverlays.get(i).getSnippet();
					nearTrainList.add(addT);
				}
			}
			nearTrainsSize=nearTrainList.size();
			for(int i=0;i<mStationList.size();i++)
			{
				//add stations near clicked marker
				stationPoint=new GeoPoint(mStationList.get(i).gX, mStationList.get(i).gY);
				mmapView.getProjection().toPixels(stationPoint, compCoord);
				dist=(int)Math.sqrt((Math.pow((origCoord.x-compCoord.x), 2)+Math.pow((origCoord.y-compCoord.y), 2)));
				if (dist<50*scale)
				{
					if(Integer.valueOf(mStationList.get(i).zLevel)<=curZoom)
					{
						ZugTrain addT=new ZugTrain();
					  	addT.guid=mStationList.get(i).stationName;
					  	addT.from=mStationList.get(i).stationAbbr;
					  	nearTrainList.add(addT);
					}
				}
					
			  }
			  if(nearTrainList.size()>1)
			  {
				  mdialog = new Dialog(mContext);
				  mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	
				  LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				  View v = li.inflate(R.layout.near_dialog, null, false);
				 
				  mdialog.setContentView(v);
				  lp.copyFrom(mdialog.getWindow().getAttributes());
				  lp.width=Math.round(100*scale);
				  lp.x=Math.round(200*scale);
				  nearLView=(ListView)mdialog.findViewById(R.id.nearListView);
				  nearAdapter = new ZugNearAdapter(mContext.getApplicationContext(), R.layout.nearrow, nearTrainList);
				  nearLView.setAdapter(nearAdapter);
				  mdialog.getWindow().setAttributes(lp);
				 
				  
				  nearLView.setOnItemClickListener(new OnItemClickListener() 
		        	{
		        	  
					  @Override
		        	  public void onItemClick(AdapterView<?> parent, View vv, int position, long id) 
		        		{
						 
						  onListItemClick(vv,position,id);
		        		}
		        	});
				  mdialog.show();
			  }
			  if(nearTrainList.size()==1)
			  {
				  String trainId=mOverlays.get(index).getTitle();
				 
				  Intent i= new Intent("zug.activity.ACTIVITY_TRAINLIST_INTENT");
				  i.putExtra("trainkey", trainId);
				  mMain.startActivityForResult(i,1);
			  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 return true;
	  
	}
	public void onListItemClick(View view,  int position, long id)
	{
		TextView tv=(TextView)view.findViewById(R.id.neartextView1);
		TextView tv2=(TextView)view.findViewById(R.id.neartextView2);
		String zId =tv.getText().toString();
		String sId=tv2.getText().toString();
		if(position<nearTrainsSize)
		{
			//pan to train
		
			Intent i= new Intent("zug.activity.ACTIVITY_TRAINLIST_INTENT");
			
			i.putExtra("trainkey", zId);
			mMain.startActivityForResult(i,1);
			mdialog.cancel();
		}
		else
		{
			Intent i= new Intent("zug.activity.ACTIVITY_STATIONLIST_INTENT");
			//String train=item.getTitle();
			i.putExtra("citykey", sId);
			mMain.startActivityForResult(i,0);
			mdialog.cancel();
		}
	}
	public void onItemClickBlink(int position, MapView mView)
	{
		animPoint=mOverlays.get(position).getPoint();
		v = (LinearLayout) View.inflate(mContext, R.layout.animmarkerlayout, null);
		marker = (ImageView) v.findViewById(R.id.marker);
		markerImage = (AnimationDrawable)marker.getDrawable();
	    mView.addView(v, 0, new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, animPoint, MapView.LayoutParams.CENTER));
	    markerImage.start();
	    

		
	}
	public void myPopulate()
	{
		this.populate();
	}
}
