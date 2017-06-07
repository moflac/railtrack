package fi.moflac.railtrack;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import fi.moflac.railtrack.R;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrainListActivity extends ListActivity{
   
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<ZugStation> m_orders = null;
    private OrderAdapter m_adapter;
    private Runnable viewOrders;
    private ZugStationParser sParser;
    ArrayAdapter<String> adapter = null;
    private String selTrain;
    String mroute;
	String mname;
	int mspeed;
	int mlateness;
	int mstatus;
	Activity rootAc;
	Button trainButton;
	Context mContext;
	private String mfrom;
	private String mto;
	private String sOnTime;
    private String sLate;
    private String sVelocity;
	
	//Activity for displaying list of train for clicked station
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainlist);
        m_orders = new ArrayList<ZugStation>();
        this.m_adapter = new OrderAdapter(this, R.layout.trainrow, m_orders);
        setListAdapter(this.m_adapter);
        mContext=this;
        
        selTrain=getIntent().getStringExtra("trainkey");

        trainButton=(Button) findViewById(R.id.trainListButton1);
        trainButton.setOnLongClickListener(myTrainButtonLongListener);
        sParser=new ZugStationParser(this);
        sParser.parseStationNames(this);
        
        sOnTime=getResources().getString(R.string.trainList_ontime);
        sLate=getResources().getString(R.string.trainList_late);
        sVelocity=getResources().getString(R.string.trainList_speed);
       
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(v,pos,id);
            }
        });
        
        //long click straight to time-table
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
        	@Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemLongClick(v,pos,id);
				return true;
            }

			
        });
       
        viewOrders = new Runnable(){
        	@Override
        	public void run() {
        		getOrders();
        	}
        };
        
        Thread thread =  new Thread(null, viewOrders, "trainbg");
        
        thread.start();
        m_ProgressDialog = ProgressDialog.show(TrainListActivity.this,    
              "Odota...", "Haetaan tietoja ...", true, true, 
              new DialogInterface.OnCancelListener(){
            @Override
            public void onCancel(DialogInterface dialog) {
                
                finish();
            }
        });
        if (ZugNetworking.isNetworkAvailable(this)==false)
        {
        	Toast.makeText(this, R.string.noDataConnectionFI, 5000).show();
        }
        
    }
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_orders != null && m_orders.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_orders.size();i++)
                	m_adapter.add(m_orders.get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };
    private void getOrders(){
    	
    	if (ZugNetworking.isNetworkAvailable(this)==true)
    	{
    	try{
        	  
        	  DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
				 // Use the factory to create a builder
			  DocumentBuilder builder = factory.newDocumentBuilder();
	          Document  document = builder.parse("http://188.117.35.14/TrainRSS/TrainService.svc/trainInfo?train="+selTrain);
	          m_orders = new ArrayList<ZugStation>();            
              if(document!=null)
              {
            	  
            	  NodeList guidElemntList =  document.getElementsByTagName("guid"); //station
            	  NodeList titleElemntList =  document.getElementsByTagName("title");  //train+station
            	  NodeList fromElemntList =  document.getElementsByTagName("startStation"); //train
            	  NodeList toElemntList =  document.getElementsByTagName("endStation");  //train
            	  //NodeList dateElemntList =  document.getElementsByTagName("pubDate");  //train + station
            	  NodeList lateElemntList =  document.getElementsByTagName("lateness"); //train
            	  NodeList statusElemntList =  document.getElementsByTagName("status"); //train
            	  //NodeList georssElemntList = document.getElementsByTagName("georss:point"); //train + station
            	  NodeList completedElemntList = document.getElementsByTagName("completed"); //station
            	  //NodeList etdElemntList = document.getElementsByTagName("etd"); //station
            	  NodeList schedaElemntList = document.getElementsByTagName("scheduledTime"); //station
            	  NodeList scheddElemntList = document.getElementsByTagName("scheduledDepartTime"); //station
            	  NodeList speedElemntList = document.getElementsByTagName("speed");
              			
              			
              		if(guidElemntList!=null&&guidElemntList.getLength()>0)
              		{
	          			for (int i = 0; i < guidElemntList.getLength(); i++)
	        			{
	          				ZugStation o1 = new ZugStation();
	          			//	o1.stationName=titleElemntList.item(i).getTextContent();
	          			//	o1.from=fromElemntList.item(i).getTextContent();
	          			//	o1.to=toElemntList.item(i).getTextContent();
	          			//	o1.status=statusElemntList.item(i).getTextContent();
	          			//	minutes=Integer.valueOf(lateElemntList.item(i).getTextContent())/60;
	          			//	o1.lateness=String.valueOf(minutes);
	          			//	mroute=sParser.searchStation(o1.from).stationName+"->"+sParser.searchStation(o1.to).stationName;
	          				String mCity="";
	          				if(guidElemntList.item(i).getTextContent()!="")
	          				{
	          					o1.stationAbbr=guidElemntList.item(i).getTextContent();
	          					mCity=sParser.searchStation(o1.stationAbbr).stationName;
	          				}
	          				else
	          				{
	          					o1.stationAbbr="x";
	          					mCity="x";
	          				}
	          				
	          				o1.stationName=mCity;
	          			//	o1.pubDate="x";
	          			//	o1.route=mroute;
	          			//	o1.eta=etaElemntList.item(i).getTextContent();
	          			//	o1.etd=etdElemntList.item(i).getTextContent();
	          				if(schedaElemntList.item(i).getTextContent()!="")
	          				{
	          					o1.scheda=schedaElemntList.item(i).getTextContent();
	          				}
	          				else
	          				{
	          					o1.scheda=" ";
	          				}
	          				if(scheddElemntList.item(i).getTextContent()!="")
	          				{
	          					o1.schedd=scheddElemntList.item(i).getTextContent();
	          				}
	          				else
	          				{
	          					o1.schedd=" ";
	          				}
	          				if(completedElemntList.item(i).getTextContent()!="")
	          				{
	          					o1.completed=completedElemntList.item(i).getTextContent();
	          				}
	          				m_orders.add(o1);
	        			}
	          			
            		}
              		else
          			{
          				ZugStation o1 = new ZugStation();
          				o1.scheda=getResources().getString(R.string.TrainListNoStationInfo);
          				m_orders.add(o1);
          				
          			}
              		if(titleElemntList.item(0).getTextContent()!="")
			  		{
          				mname=titleElemntList.item(0).getTextContent();
			  		}
          			else
			  		{
			  			mname=" ";
			  		}
          			if(fromElemntList.item(0).getTextContent()!="")
          			{
          				mfrom=sParser.searchStation(fromElemntList.item(0).getTextContent()).stationName;
          			}
          			else
          			{
          				mfrom = " ";
          			}
          			if(toElemntList.item(0).getTextContent()!="")
          			{
          				mto=sParser.searchStation(toElemntList.item(0).getTextContent()).stationName;
          			}
          			else
          			{
          				mto=" ";
          			}
          			mroute=mfrom+" - "+mto;
          			if(speedElemntList.item(0).getTextContent()!="")
          			{
          				mspeed=Integer.valueOf(speedElemntList.item(0).getTextContent());
          			}
          			else
          			{
          				mspeed=0;
          			}
          			if(lateElemntList.item(0).getTextContent()!="")
          			{
          				mlateness=Integer.valueOf(lateElemntList.item(0).getTextContent())/60;
          			}
          			else
          			{
          				mlateness=0;
          			}
          			if(statusElemntList.item(0).getTextContent()!="")
          			{
          				mstatus=Integer.valueOf(statusElemntList.item(0).getTextContent());
          			}	
          			else
          			{
          				mstatus=1;
          			}
              }
             // Log.i("ARRAY", ""+ m_orders.size());
            } catch (Exception e) {
             // Log.e("BACKGROUND_PROC", e.getMessage());
            }
    	}
    	else
    	{
    		
    	}
            runOnUiThread(returnRes);
        }
    
    protected void onListItemClick(View v, int pos, long id) {
    	Bundle bundle = new Bundle();
    	String sAbbr=m_orders.get(pos).stationAbbr;
    			
        bundle.putString("station", sAbbr);
    	Intent in = new Intent("zug.activity.ACTIVITY_STATIONLIST_RESULT_INTENT");
    	in.putExtras(bundle);
    	
    	    	
    	setResult(1, in);
    	
    	
    	

    	finish();
    }
    private void onListItemLongClick(View v, int pos, long id) {
		
    	String stationId=m_orders.get(pos).stationAbbr;
		 
		  Intent i= new Intent("zug.activity.ACTIVITY_STATIONLIST_INTENT").
		  	setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		  i.putExtra("citykey", stationId);
		  startActivity(i);
		  finish();
	}
    private class OrderAdapter extends ArrayAdapter<ZugStation> {

        private ArrayList<ZugStation> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<ZugStation> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.trainrow, null);
                }
                ZugStation o = items.get(position);
                if (o != null) {
                		
                        TextView t1 = (TextView) v.findViewById(R.id.trainView1);
                        TextView t2 = (TextView) v.findViewById(R.id.trainView2);
                        TextView t3 = (TextView) v.findViewById(R.id.trainView3);
                        TextView ots1 = (TextView)findViewById(R.id.tlView1);
                        TextView ots2 = (TextView)findViewById(R.id.tlView2);
                        TextView ots3 = (TextView)findViewById(R.id.tlView6);
                        TextView ots4 = (TextView)findViewById(R.id.tlView7);
                      //  TextView ots5 = (TextView)findViewById(R.id.tlView8);
                       
                        if (t1 != null) {
                              t1.setText(o.stationName);                            }
                        if(t2!= null){
                              t2.setText(o.scheda);
                        }
                        if(t3!=null){
                        	t3.setText(o.schedd);
                        }
                        if(ots1!=null){
                        	ots1.setText(mname);
                        	
                        }
                        if(ots2!=null){
                        	ots2.setText(mroute);
                        }
                        if(o.completed.equals("1"))
                        {
                        	v.setBackgroundColor(Color.argb(50, 255, 255, 255));
                        	t1.setTextColor(Color.argb(150, 255, 255, 255));
                        	t2.setTextColor(Color.argb(150, 255, 255, 255));
                        	t3.setTextColor(Color.argb(150, 255, 255, 255));
                        }
                        else
                        {
                        	v.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        	t1.setTextColor(Color.argb(205, 255, 255, 255));
                        	t2.setTextColor(Color.argb(205, 255, 255, 255));
                        	t3.setTextColor(Color.argb(205, 255, 255, 255));
                        }
                        
                        if(ots3!=null){
                        	ots3.setText(sVelocity+" "+mspeed+" km/h");
                        }
                        if(ots4!=null){
                        	ots4.setText(sLate+" "+mlateness+" min.");
                        }
                        
                        
                        
                        
                        
                        if (ots4 != null) {
                        	if(mstatus==1)
                        	{
                        		ots4.setBackgroundColor(Color.argb(255, 0, 200, 0));
                        	}
                        	if(mstatus==2)
                        	{
                        		ots4.setBackgroundColor(Color.argb(255, 200, 200, 0));
                        		
                        	}
                        	if(mstatus==3)
                        	{
                        		ots4.setBackgroundColor(Color.argb(255, 200, 0, 0));
                        		
                        	}
                        	if(mstatus==4)
                        	{
                        		ots4.setBackgroundColor(Color.argb(255, 200, 0, 0));
                        		
                        	}
                        	if(mstatus==5)
                        	{
                        		ots4.setBackgroundColor(Color.argb(100, 255, 255, 255));
                        		
                        	}
                        	if(mlateness==0)
                        		ots4.setText(sOnTime);
                        	
                            
                        }
                }
                return v;
        }
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig)
    {
    	
    	
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.trainlist);
        
    }
    public void trainClick(View view)
    {
    	Bundle bundle = new Bundle();
    	bundle.putString("guid", selTrain);
    	Intent in = new Intent("zug.activity.ACTIVITY_STATIONLIST_RESULT_INTENT");
    	in.putExtras(bundle);
    	    	
    	setResult(0, in);
    	
    	finish();
    }
    private OnLongClickListener myTrainButtonLongListener = new OnLongClickListener()
    {

		@Override
		public boolean onLongClick(View arg0) 
		{
			Bundle bundle = new Bundle();
	    	bundle.putString("guid", selTrain);
	    	Intent in = new Intent("zug.activity.ACTIVITY_TRAINLIST_LOCK_INTENT");
	    	in.putExtras(bundle);
	    	    	
	    	setResult(2, in);
	    	
	    	finish();
			return false;
		}
    	
    };
    
}
