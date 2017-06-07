package fi.moflac.railtrack;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fi.moflac.railtrack.R;







public class ZugActivity extends MapActivity {
    
	private ZugParser zParser;
	public ArrayList<ZugTrain> trainList;
	private ZugItemizedOverlay itemizedOverlay;
	
	private static List<Overlay> mapOverlays;
	private MapView mapView;
	public static ZugStationParser sParser;
	private Context mContext;
	private MyLocationOverlay myLocOverlay;
	private ArrayList<ZugStation> stationList;
	private ZugStationItemizedOverlay stationItemizedOverlay0;
	private ZugStationItemizedOverlay stationItemizedOverlay1;
	private ZugStationItemizedOverlay stationItemizedOverlay25;
	private ZugStationItemizedOverlay stationItemizedOverlay21;
	private ZugStationItemizedOverlay stationItemizedOverlay22;
	private ZugStationItemizedOverlay stationItemizedOverlay23;
	private ZugStationItemizedOverlay stationItemizedOverlay24;
	private ZugStationItemizedOverlay stationItemizedOverlay3;
	private int showSpinner;
	private String sGuid;
	int looper=30000;
	int mainLoop;
	int oldZoomLevel=11;
	 Dialog dialog;
	 ArrayAdapter<CharSequence> sadapter;
	 ListView searchLView;
	 ArrayAdapter<ZugTrain> trainAdapter;
	 ArrayAdapter<String> za = null;
	 ImageButton myLocButton;
	GeoPoint animPoint;
	View vi;
	ImageView marker;
	AnimationDrawable markerImage;
	public String[] zVal;
	GeoPoint fromListPoint;
	String trainLockId;
	boolean trainLock;
	ImageButton lockButton;
	ImageButton refreshButton;
	int demiloop;
	int prefC;
	GeoPoint startPoint;
	Handler zoomHandler;
	SharedPreferences prefs;
	PowerManager.WakeLock wl;
	private ZugSearchAdapter searchAdapter;
	private ZugSearchStationAdapter searchStAdapter;
	private String searchString;
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	Handler hnd = new Handler();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
        this.setVisible(false);
        mContext=this;
        trainLock=false;
        trainLockId="";
        startPoint=new GeoPoint(60171126, 24941790);
        zoomHandler=new Handler();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);       
        fromListPoint=new GeoPoint(getIntent().getIntExtra("gX",0),getIntent().getIntExtra("gY",0));
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Partial wake");

        
        mainLoop=0;
        showSpinner=0;
        //Creat parser to download and sort train info and init station parser
        zParser=new ZugParser(this);
        // Create parser to sort stations into a list	
        sParser=new ZugStationParser(mContext);
        //holds parsed stations
        stationList=sParser.parseStationNames(mContext);
        //holds parsed trains
        //trainList=zParser.parseTrains(this, sParser);
        searchString="";
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
        
        //spinner for showing selected categories
        Spinner mSpinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sortSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        mSpinner.setSelection(2);
        
        //location button long click shows dialog for finding nearest station or train
        myLocButton=(ImageButton)findViewById(R.id.myLocButton);
        myLocButton.setOnLongClickListener(myLocButtonLongListener);
        
        myLocButton=(ImageButton)findViewById(R.id.refreshButton);
        myLocButton.setOnLongClickListener(myRefreshButtonLongListener);
        
        lockButton=(ImageButton)findViewById(R.id.lockButton);
       
        sadapter = ArrayAdapter.createFromResource(this.getApplicationContext(), R.array.searchSpinner, android.R.layout.simple_spinner_item);
        
        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.veturi4);
        Drawable stationDr0 =this.getResources().getDrawable(R.drawable.asema2);
        
        
        //overlay for trains
        prefC=Integer.valueOf(prefs.getString("train_drawable", "2"));
        
        
        itemizedOverlay=new ZugItemizedOverlay(drawable, mapView.getContext(), this, 10, zParser, stationList);       
        //create different layers for stations in different zoom levels
        stationItemizedOverlay0=new ZugStationItemizedOverlay(stationDr0, mContext, this);
        stationItemizedOverlay1=new ZugStationItemizedOverlay(stationDr0, mContext, this);	
        stationItemizedOverlay21=new ZugStationItemizedOverlay(stationDr0, mContext, this);
        stationItemizedOverlay22=new ZugStationItemizedOverlay(stationDr0, mContext, this);
        stationItemizedOverlay23=new ZugStationItemizedOverlay(stationDr0, mContext, this);
        stationItemizedOverlay24=new ZugStationItemizedOverlay(stationDr0, mContext, this);
        stationItemizedOverlay25=new ZugStationItemizedOverlay(stationDr0, mContext, this);
        stationItemizedOverlay3=new ZugStationItemizedOverlay(stationDr0, mContext, this);
       
        createStations();
        
        //add own location
        myLocOverlay = new MyLocationOverlay(this, mapView);
        mapOverlays.add(myLocOverlay);
        
        mapView.getController().setZoom(11);
       
        myLocOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                if(myLocOverlay.getMyLocation()!=null)
                {
                	mapView.getController().animateTo(myLocOverlay.getMyLocation());
                }
                else
                {
                	mapView.getController().animateTo(startPoint);
                }
                	
            }
        });
        
        	
        final ImageButton bt=(ImageButton)findViewById(R.id.select);
        bt.setOnClickListener(mMessageClickedHandler);
       
        ZoomButtonsController zoomButton = mapView.getZoomButtonsController();
        zoomButton.setOnZoomListener(onZoomHandler);
        this.setVisible(true);
        
        demiloop=0;
        //loop for handling reloading
        hnd.post(new Runnable(){
        	
            @Override
            public void run() {
            	looper=Integer.valueOf(prefs.getString("refresh_interval", "120000"));
            	if(demiloop>=looper&&looper>0)
            	{   //main loop, set main loop timer zero
            		trainList=zParser.parseTrains((ZugActivity)mContext, sParser);
            		demiloop=0;
            	}
                		//zVal=zParser.getSearchList();
                    //first loop in 10 seconds
                	        	
            	if(mainLoop==0)
            	{
            		// first loop after 10 seconds always
            		hnd.postDelayed(this,5000);
            		mainLoop=1;
            		demiloop=looper;
            	}
            	else
            	{	// 10 second check loop
            		hnd.postDelayed(this,10000);
            		demiloop=demiloop+10000;
            	}
            }

            

        });
       


    }
    
    

	private OnClickListener mMessageClickedHandler = new OnClickListener() {
       //search button functionality 
    	
    	int selectedView=0;
    	
		@Override
		public void onClick(View v) {
			
			
			
			dialog = new Dialog(mContext){
				@Override
				public void onBackPressed()
				{
					
					
					if(searchAdapter!=null)
					{
						
						
					}
					if(searchStAdapter!=null)
					{
						
						
					}
				//	searchAdapter=new  ZugSearchAdapter(mContext.getApplicationContext(), R.layout.searchrow, trainList);
				//	searchStAdapter=new ZugSearchStationAdapter(mContext.getApplicationContext(), R.layout.searchstationrow, stationList);
					cancel();
				}
			};
	        dialog.setTitle(R.string.search_dialog_title);
	        dialog.getWindow().getAttributes().y=-100;
	       
	        
	       
			LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View vi = li.inflate(R.layout.search_dialog, null, false);
			
			//dialog.setContentView(vi);
			dialog.setContentView(vi);
			searchLView=(ListView)dialog.findViewById(R.id.searchListView);
			searchLView.setTextFilterEnabled(true);
			
	        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        Spinner searchSpinner = (Spinner) dialog.findViewById(R.id.searchSpinner);
	        searchSpinner.setAdapter(sadapter);
	       
	       	        
	        searchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	        	public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3)
	        	{
	        		
	        		EditText filterText = null;
	        		if(arg2==0&&searchLView!=null&&trainList!=null)
	        		{
	        			ArrayList<ZugTrain> filterTrainList=new ArrayList<ZugTrain>(trainList);
	        			searchAdapter=new ZugSearchAdapter(mContext.getApplicationContext(), R.layout.searchrow, filterTrainList);
	        		
	        			searchAdapter.notifyDataSetChanged();
	        		        			
	        			searchLView.setAdapter(searchAdapter);
	        			filterText = (EditText) dialog.findViewById(R.id.searchText);
	        			filterText.setText(searchString);
	        			filterText.addTextChangedListener(filterTextWatcher);
	        			
	        	        selectedView=1;
	    	        	

	        		}
	        		if(arg2==1&&searchLView!=null)
	        		{
	        			//Show stations
	        			ArrayList<ZugStation> filterStationList=new ArrayList<ZugStation>(stationList);
	        		
	        			searchStAdapter = new ZugSearchStationAdapter(mContext.getApplicationContext(), R.layout.searchstationrow, filterStationList);
	        				        		
	        			searchStAdapter.notifyDataSetChanged();
	        			searchLView.setAdapter(searchStAdapter);
	        			filterText = (EditText) dialog.findViewById(R.id.searchText);
	        			filterText.setText(searchString);
	        			filterText.addTextChangedListener(stationTextWatcher);
	        			

	    	            selectedView=2;       

	    	        	
	        		}
	        		
	        		
	        	}
	        	public void onNothingSelected(AdapterView<?> arg0) {
	        		
	        	}
	        	});
	        
	        	searchLView.setOnItemClickListener(new OnItemClickListener() 
	        	{
	        		//searchlist click listener 
	        		public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) 
	        		{
	        			Spinner mSp=(Spinner)findViewById(R.id.spinner1);
	        			if(selectedView==1)
	        			{
	        				//pan to train
	        				TextView guidView = (TextView) view.findViewById(R.id.searchView1);
	        				String zId=(String) guidView.getText();
	        					        				
	        				ZugTrain searchTrain=zParser.searchTrain(zId);
	        				
	        				GeoPoint gp=new GeoPoint(searchTrain.gX, searchTrain.gY);
	        				
	        				if(gp!=null&&searchTrain.gX>0)
	        				{
	        					mSp.setSelection(2, true);
	        					mapView.getController().setZoom(14);
	        					mapView.getController().animateTo(gp);
	        					onItemClickBlink(gp);
	        				
	        				}
	        				dialog.cancel();
	        			}
	        			if(selectedView==2)
	        			{
	        				//pan to station
	        				TextView abbrView = (TextView) view.findViewById(R.id.searchStationView2);
	        				String sId=(String) abbrView.getText();
	        				ZugStation searchStation=sParser.searchStation(sId);
	        				GeoPoint sp=new GeoPoint(searchStation.gX, searchStation.gY);
	        				if(sp!=null&&searchStation.gX>0)
	        				{
	        					mSp.setSelection(2, true);
	        					
	        					mapView.getController().animateTo(sp);
	        					mapView.getController().setZoom(14);
	        					onItemClickBlink(sp);
	        					addStationOverlays();
	        				}
	        				dialog.cancel();
	        				
	        			}
	        			
	        			
	        			
	        			
	        			
	        		}
	        	});
	        	searchLView.setOnItemLongClickListener(new OnItemLongClickListener()
	        	{

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int pos, long id) 
					{
						if(selectedView==1)
						{
							TextView guidView = (TextView) arg1.findViewById(R.id.searchView1);
	        				String zId=(String) guidView.getText();
							Intent i= new Intent("zug.activity.ACTIVITY_TRAINLIST_INTENT");
					  	
							i.putExtra("trainkey", zId);
							startActivityForResult(i,1);
							dialog.cancel();
							
						}
						if(selectedView==2)
						{
							TextView abbrView = (TextView) arg1.findViewById(R.id.searchStationView2);
	        				String sId=(String) abbrView.getText();
							Intent i= new Intent("zug.activity.ACTIVITY_STATIONLIST_INTENT");
					  	
							i.putExtra("citykey", sId);
							startActivityForResult(i,0);
							dialog.cancel();
							
						}
						
						return false;
					}
					
	        		
	        	});
	       
			dialog.show();
		}

		
    };
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        	
            
        	searchAdapter.getFilter().filter(s);
        	searchAdapter.notifyDataSetChanged();
        	
        	
        	
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	
        	   
        }

		

    };
    private TextWatcher stationTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        	
        	
        	searchStAdapter.getFilter().filter(s);
        	
        	searchStAdapter.notifyDataSetChanged();
        	
        	
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	
        	   
        }

		

    };
    
    
    private OnZoomListener onZoomHandler = new OnZoomListener() {

		@Override
		public void onVisibilityChanged(boolean arg0) {
			
			mapView.invalidate();
		}

		
		public void onZoom(boolean arg0) {
			
			if(arg0==true)
			{
				mapView.getController().zoomIn();
			}	
			else
			{
				mapView.getController().zoomOut();
			}
			addStationOverlays();
			mapView.invalidate();
			
		}
    	
    };
    
    

    public class MyOnItemSelectedListener implements OnItemSelectedListener {
    	//Handles the spinner input from the main selection
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
        	showSpinner=pos;
        	mapOverlays.clear();
        	if(pos==0)
        	{
        		mapOverlays.add(itemizedOverlay);
        		mapOverlays.add(myLocOverlay);
        	}
        	if(pos==1)
        	{
        		addStationOverlays();
        		mapOverlays.add(myLocOverlay);
        	}
        	if(pos==2)
        	{
        		addStationOverlays();
        		mapOverlays.add(itemizedOverlay);
        		mapOverlays.add(myLocOverlay);
        	}
        	mapView.invalidate();
        	
        	
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        	mapView.invalidate();
        }
        
        
    }
    public class SearchOnItemSelectedListener implements OnItemSelectedListener {
    	//Handles the spinner input from the main selection
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
        	showSpinner=pos;
        	mapOverlays.clear();
        	if(pos==0)
        	{
        		mapOverlays.add(itemizedOverlay);
        		
        	}
        	if(pos==1)
        	{
        		addStationOverlays();
        		
        	}
        	if(pos==2)
        	{
        		addStationOverlays();
        		mapOverlays.add(itemizedOverlay);
        		
        	}
        	mapOverlays.add(myLocOverlay);
        	mapView.invalidate();
        
        	
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        	mapView.invalidate();
        }
        
        
    }
    private OnLongClickListener myLocButtonLongListener = new OnLongClickListener() {
        //long pressing mylocation shows dialog for finding nearest train or station

		@Override
		public boolean onLongClick(View arg0) {
			
			
			
			if(myLocOverlay.getMyLocation()!=null)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage(R.string.mylocDialog)
				.setCancelable(true)
				.setPositiveButton(R.string.mylocDialog_train, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						float distance=0f;
						float olddistance=10000000000f;
						float myLat;
						float myLon;
						float compLat;
						float compLon;
						int closest=0;
						Location myLocation = new Location("loc");
						Location compareLocation= new Location("toc");
						if(itemizedOverlay.size()>0&&trainList.size()>0)
						{
							for(int i=0;i<trainList.size();i++)
							{
								myLat=myLocOverlay.getMyLocation().getLatitudeE6()/1000000f;
								myLon=myLocOverlay.getMyLocation().getLongitudeE6()/1000000f;
								myLocation.setLatitude(myLat);
								myLocation.setLongitude(myLon);
								compLat=trainList.get(i).gX/1000000f;
								compLon=trainList.get(i).gY/1000000f;
								compareLocation.setLatitude(compLat);
								compareLocation.setLongitude(compLon);
								distance=compareLocation.distanceTo(myLocation);
								if(distance<olddistance)
								{
									olddistance=distance;
									closest=i;
								}
			        		   
							}
							GeoPoint closestPoint=new GeoPoint(trainList.get(closest).gX, trainList.get(closest).gY);
							mapView.getController().animateTo(closestPoint);
							itemizedOverlay.onItemClickBlink(closest, mapView);
							mapView.invalidate();
						}
					}
			           
				})
				.setNegativeButton(R.string.mylocDialog_station, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						float distance=0f;
						float olddistance=10000000000f;
						float myLat;
						float myLon;
						float compLat;
						float compLon;
						int closest=0;
						Location myLocation = new Location("loc2");
						Location compareLocation= new Location("toc2");
						for(int i=0;i<stationList.size();i++)
						{
							myLat=myLocOverlay.getMyLocation().getLatitudeE6()/1000000f;
							myLon=myLocOverlay.getMyLocation().getLongitudeE6()/1000000f;
							myLocation.setLatitude(myLat);
							myLocation.setLongitude(myLon);
							compLat=stationList.get(i).gX/1000000f;
							compLon=stationList.get(i).gY/1000000f;
							compareLocation.setLatitude(compLat);
							compareLocation.setLongitude(compLon);
							distance=compareLocation.distanceTo(myLocation);
							if(distance<olddistance)
							{
								olddistance=distance;
								closest=i;
							}
			        		   
						}
						GeoPoint closestPoint=new GeoPoint(stationList.get(closest).gX, stationList.get(closest).gY);
						mapView.getController().animateTo(closestPoint);  
						onItemClickBlink(closestPoint);
						mapView.invalidate();
			        	   
			        	   
			        	   
			                
					}
				});
			
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
			return true;
		}
		};

    //results from intents of other activities
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
        super.onActivityResult(requestCode, resultCode, data); 
        	
        	if(resultCode==0) 
        	{ 
        		
        		//if (resultCode == Activity.RESULT_OK)
        		//{    
        			//Log.i("ZugActivity", "resultCode ok");
        			if (data != null)
        			{
        				Bundle b= data.getExtras();
        				sGuid= b.getString("guid");
        				ZugTrain sTrain=zParser.searchTrain(sGuid);
        				GeoPoint pTo=new GeoPoint(sTrain.gX, sTrain.gY);
        				if(sTrain.gX>0)
        				{
        					mapView.getController().animateTo(pTo);
        					Spinner mSp=(Spinner)findViewById(R.id.spinner1);
        					mSp.setSelection(2, true);
        					
        				}
        				if(sTrain==null||sTrain.gX==0)
        				{
        					Toast.makeText(getApplicationContext(), R.string.noSuchTrainOnMap, Toast.LENGTH_LONG).show();
        				}
        			} 
        			
                
        		//}
        		
        	}
        	
        	if(resultCode==1) 
        	{ 
        		                     
        			if (data != null)
        			{
        				Bundle b= data.getExtras();
        				String tstation= b.getString("station").toUpperCase();
        				ZugStation sStation=sParser.searchStation(tstation);
        				
        				GeoPoint pTo=new GeoPoint(sStation.gX, sStation.gY);
        				if(sStation.gX>0)
        				{
        					mapView.getController().animateTo(pTo);
        					mapView.getController().setZoom(14);
        					Spinner mSp=(Spinner)findViewById(R.id.spinner1);
        					mSp.setSelection(2, true);
        					addStationOverlays();
        				}
              	  } 
        		
        	}
        	if(resultCode==2) 
        	{ 
        		                      
        			if (data != null)
        			{
        				Bundle b= data.getExtras();
        				trainLockId= b.getString("guid");
        				trainLock=true;
        				lockTrain();
              	  	} 
        		
        	}
        	
    }
    

    
   private void createStations()
   {
	   int kcounter=0;
	   if (stationList!=null)
	   {
		   for (int i=0; i<stationList.size();i++)
		   {
			   String asemaId=stationList.get(i).stationAbbr;
			   String asemaName=stationList.get(i).stationName;
		
			   float flat=Float.valueOf(stationList.get(i).etrsX)*1000000;
			   float flon=Float.valueOf(stationList.get(i).etrsY)*1000000;
			   int lat = (int)flat;
			   int lon = (int)flon;
			   int asemaZlevel=Integer.valueOf(stationList.get(i).zLevel).intValue();
			   
			   GeoPoint asemaPoint=new GeoPoint(lat,lon);
		   
			   OverlayItem stOverlayItem=new OverlayItem(asemaPoint, asemaName, asemaId);
			   if(asemaZlevel==0)
				   stationItemizedOverlay0.addOverlay(stOverlayItem);
			   else
			   if(asemaZlevel==1)
				   stationItemizedOverlay1.addOverlay(stOverlayItem);
			   else
			   {
				   if(asemaZlevel==2)
				   {
					   if(kcounter<=40)
						   stationItemizedOverlay21.addOverlay(stOverlayItem);
					   if(kcounter>40&&kcounter<=80)
						   stationItemizedOverlay22.addOverlay(stOverlayItem);
					   if(kcounter>80&&kcounter<=120)
						   stationItemizedOverlay23.addOverlay(stOverlayItem);
					   if(kcounter>120&&kcounter<=160)
						   stationItemizedOverlay24.addOverlay(stOverlayItem);
					   if(kcounter>160)
						   stationItemizedOverlay25.addOverlay(stOverlayItem);
				   }
			   }
			   
			   if(asemaZlevel==3)
				   stationItemizedOverlay3.addOverlay(stOverlayItem);
			   
			   kcounter++;
		   }
		   
	   }
	   else
       {
   		
       	Toast.makeText(getApplicationContext(), stationList.size(), Toast.LENGTH_LONG).show();
       }
	   mapView.invalidate();
   }

   public void updateTrains()
   {
	   	trainList=zParser.getTrainList();
	   	
	   	mapOverlays.remove(itemizedOverlay);
		itemizedOverlay.clear();
		
    	if(trainList!=null)
        {
    		
    		
    		
    		for (int i = 0; i < trainList.size(); i++)
    		{
    			int valueX=trainList.get(i).gX;
    			int valueY=trainList.get(i).gY;
    			String kUid=trainList.get(i).guid;
    			
    			String lFrom=sParser.searchStation(trainList.get(i).from).stationName;
    			String lTo=sParser.searchStation(trainList.get(i).to).stationName;
    			GeoPoint trainPoint = new GeoPoint(valueX,valueY);
    			OverlayItem overlayitem = new OverlayItem(trainPoint, kUid,lFrom+" - "+lTo);
    			itemizedOverlay.addOverlay(overlayitem);
    			
    		}
    		

    		if(showSpinner==0||showSpinner==2)
    		{
    			mapOverlays.add(itemizedOverlay);
    			
    		}
    		
    		
    		
    		
    		
    		
        }
    	else
        {
    		
        	
        }
    	mapView.invalidate();
    }
   
   @Override
   public boolean dispatchTouchEvent(MotionEvent ev) 
   {
	   super.dispatchTouchEvent(ev);
	   int zoomLevel=mapView.getZoomLevel();
	   if(zoomLevel!=oldZoomLevel)
	   {
		  addStationOverlays();
		 
	   }
	   oldZoomLevel=zoomLevel;
	   
	return true;
	   
   }
   
  
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// when our activity resumes, we want to register for location updates
    	stationItemizedOverlay0.myPopulate();
        stationItemizedOverlay1.myPopulate();
        stationItemizedOverlay21.myPopulate();
        stationItemizedOverlay22.myPopulate();
        stationItemizedOverlay23.myPopulate();
        stationItemizedOverlay24.myPopulate();
        stationItemizedOverlay25.myPopulate();
        stationItemizedOverlay3.myPopulate();
        itemizedOverlay.myPopulate();
    	myLocOverlay.enableMyLocation();
    	mapView.invalidate();
    	if(wl.isHeld()==true)
    	{
    		wl.release();
    		
    	}
    	
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	// when our activity pauses, we want to remove listening for location updates
    	myLocOverlay.disableMyLocation();
    	if(prefs.getBoolean("wake_lock", false)==true&&wl.isHeld()!=true)
    	{
    		wl.acquire();
    		
    	}
    	if(prefs.getBoolean("wake_lock", false)==false&&wl.isHeld()==true)
    	{
    		wl.release();
    		
    	}
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
    }
   
    public void goToMyLocation(View view)
    {
    	//my location button 
    	if(myLocOverlay.getMyLocation()!=null)
    	mapView.getController().animateTo(myLocOverlay.getMyLocation());
    }
   
    public void refreshView(View view)
    {
    	trainList=zParser.parseTrains((ZugActivity)mContext, sParser);
    }
    public void invalidateMapView(View view)
    {
    	//refresh map
    	mapView.invalidate();
    	itemizedOverlay.myPopulate();
    	//Log.i("overlay size", "overlay"+ itemizedOverlay.size());
    	//Log.i("map overlay size", "mapoverlay"+ mapOverlays.size());
    	
    }
    
	public void addStationOverlays() {
		
		mapOverlays.clear();
		
		int zoomLevel=mapView.getZoomLevel();
		if(showSpinner==1||showSpinner==2)
		{
			if(zoomLevel<11)
			{
				mapOverlays.add(stationItemizedOverlay0);
			}
			
			if(zoomLevel>=11&&zoomLevel<13)
			{
				mapOverlays.add(stationItemizedOverlay0);
				mapOverlays.add(stationItemizedOverlay1);
			}
			if(zoomLevel>=13&&zoomLevel<14)
			{
				mapOverlays.add(stationItemizedOverlay0);
				mapOverlays.add(stationItemizedOverlay1);
				mapOverlays.add(stationItemizedOverlay21);
				mapOverlays.add(stationItemizedOverlay22);
				mapOverlays.add(stationItemizedOverlay23);
				mapOverlays.add(stationItemizedOverlay24);
				mapOverlays.add(stationItemizedOverlay25);
			}
			if(zoomLevel>=14)
			{
				mapOverlays.add(stationItemizedOverlay0);
				mapOverlays.add(stationItemizedOverlay1);
				mapOverlays.add(stationItemizedOverlay21);
				mapOverlays.add(stationItemizedOverlay22);
				mapOverlays.add(stationItemizedOverlay23);
				mapOverlays.add(stationItemizedOverlay24);
				mapOverlays.add(stationItemizedOverlay25);
				mapOverlays.add(stationItemizedOverlay3);
			}
			
		}
		if(showSpinner==0||showSpinner==2)
			mapOverlays.add(itemizedOverlay);
		mapOverlays.add(myLocOverlay);
    	mapView.invalidate();
	}

	@Override
	protected void onDestroy() {
		
        super.onDestroy();
        
    }
	public void onBackPressed() {
		// do something on back.
		if(wl.isHeld()==true)
    	{
    		wl.release();
    	}
    	       
       this.finish();
		
	}
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//creates menu when menu-button pressed
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_settings:
        	startActivity(new Intent(this, ZugPreferences.class));
            return true;
        case R.id.menu_help:
        	startActivity(new Intent(this, ZugHelpActivity.class));
        	return true;
        case R.id.menu_exit:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void onItemClickBlink(int position)
	{
		animPoint=new GeoPoint(stationList.get(position).gX, stationList.get(position).gY);
		vi = (LinearLayout) View.inflate(mContext, R.layout.animmarkerlayout, null);
		marker = (ImageView) vi.findViewById(R.id.marker);
		markerImage = (AnimationDrawable)marker.getDrawable();
	    mapView.addView(vi, 0, new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, animPoint, MapView.LayoutParams.CENTER));
	    markerImage.start();
	    

		
	}
	public void onItemClickBlink(GeoPoint spoint)
	{
		
		vi = (LinearLayout) View.inflate(mContext, R.layout.animmarkerlayout, null);
		marker = (ImageView) vi.findViewById(R.id.marker);
		markerImage = (AnimationDrawable)marker.getDrawable();
	    mapView.addView(vi, 0, new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, spoint, MapView.LayoutParams.CENTER));
	    markerImage.start();
	    

		
	}
	//following a train, locking the map
	public void lockTrain()
	{
		
    	ZugTrain lTrain=zParser.searchTrain(trainLockId);
		if(lTrain!=null&&trainLock==true&&lTrain.gX!=0f)
		{
			GeoPoint gp=new GeoPoint(lTrain.gX, lTrain.gY);
			mapView.getController().animateTo(gp);
			mapView.setClickable(false);
			lockButton.setVisibility(View.VISIBLE);
		}
		else
		{
			releaseLock(mapView);
			
		}
	}
	public void releaseLock(View view)
	{
		trainLock=false;
		lockButton.setVisibility(View.GONE);
		mapView.setClickable(true);
		
	}
	private OnLongClickListener myRefreshButtonLongListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View arg0) {
			String sis=getResources().getString(R.string.lastRefresh)+"\n"+zParser.lastBuildDate;
			Toast.makeText(getApplicationContext(), sis, Toast.LENGTH_LONG).show();
			return true;
			
		}
	};
	
   
   
}
