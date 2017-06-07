package fi.moflac.railtrack;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import fi.moflac.railtrack.R;


import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class StationListActivity extends ListActivity{
   
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<Order> m_orders = null;
    private OrderAdapter m_adapter;
    private Runnable viewOrders;
    private ZugStationParser sParser;
    ArrayAdapter<String> adapter = null;
    private Context mcontext;
    String selCity;
    Activity rootAc;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stationlist);
        m_orders = new ArrayList<Order>();
        this.m_adapter = new OrderAdapter(this, R.layout.row, m_orders);
        setListAdapter(this.m_adapter);
        mcontext=this;
        
        selCity=getIntent().getStringExtra("citykey");
        
        
        sParser=new ZugStationParser(this);
        sParser.parseStationNames(this);
        
        TextView ots= (TextView)findViewById(R.id.stationHeader);
        if (ots != null)
        	ots.setText(sParser.searchStation(selCity).stationName);
      
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                onListItemClick(v,pos,id);
            }
        });
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
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(StationListActivity.this,    
              "Odota...", "Haetaan tietoja...", true, true);
        if (ZugNetworking.isNetworkAvailable(this)==false)
        {
        	Toast.makeText(this, R.string.noDataConnectionFI, 5000).show();
        }
       
        	 
        
    }
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_orders != null && m_orders.size() > 0)
            {
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_orders.size();i++)
                m_adapter.add(m_orders.get(i));
            }
            
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
            if(m_orders.size()==0)
            Toast.makeText(mcontext, R.string.noDataOnStationFI, Toast.LENGTH_LONG).show();
        }
    };
    private void getOrders(){
          try{
        	  DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
				 // Use the factory to create a builder
			  DocumentBuilder builder = factory.newDocumentBuilder();
	          Document  document = builder.parse("http://188.117.35.14/TrainRSS/TrainService.svc/stationInfo?station="+selCity);
              m_orders = new ArrayList<Order>();
              int minutes;
              
              if(document!=null)
              {
            	  		String mroute;
              			NodeList guidElemntList =  document.getElementsByTagName("guid");
              			NodeList titleElemntList =  document.getElementsByTagName("title");
              			NodeList fromElemntList =  document.getElementsByTagName("fromStation");
              			NodeList toElemntList =  document.getElementsByTagName("toStation");
              			//NodeList dateElemntList =  document.getElementsByTagName("pubDate");
              			NodeList lateElemntList =  document.getElementsByTagName("lateness");
              			NodeList statusElemntList =  document.getElementsByTagName("status");
              			NodeList georssElemntList = document.getElementsByTagName("georss:point");
              			NodeList etaElemntList = document.getElementsByTagName("eta");
              			NodeList etdElemntList = document.getElementsByTagName("etd");
              			NodeList schedaElemntList = document.getElementsByTagName("scheduledTime");
              			NodeList scheddElemntList = document.getElementsByTagName("scheduledDepartTime");
              			
              			
              			
              			
              			for (int i = 0; i < guidElemntList.getLength(); i++)
            			{
              				Order o1 = new Order();
              				if(titleElemntList.item(i+1).getTextContent()!="")
	          				{
              					o1.title=titleElemntList.item(i+1).getTextContent();
	          				}
              				else
              				{
              					o1.title=" ";
              				}
              				if(fromElemntList.item(i).getTextContent()!="")
	          				{
              					o1.from=fromElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.from=" ";
              				}
              				if(toElemntList.item(i).getTextContent()!="")
	          				{
              					o1.to=toElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.to=" ";
              				}
              				if(statusElemntList.item(i).getTextContent()!="")
	          				{
              					o1.status=statusElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.status=" ";
              				}
              				if(lateElemntList.item(i).getTextContent()!="")
	          				{
              					minutes=Integer.valueOf(lateElemntList.item(i).getTextContent())/60;
              					o1.lateness=String.valueOf(minutes);
	          				}
              				else
              				{
              					o1.lateness=" ";
              				}
              				
              				String mfrom=sParser.searchStation(o1.from).stationName;
              				String mto=sParser.searchStation(o1.to).stationName;
              				if(mfrom!=null&&mto!=null)
              					mroute=mfrom+" - "+mto;
              				else
              					mroute="----";
              				
              				if(guidElemntList.item(i).getTextContent()!="")
	          				{
              					o1.guid=guidElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.guid=" ";
              				}
              				
              				o1.pubDate="x";
              				o1.route=mroute;
              				
              				if(etaElemntList.item(i).getTextContent()!="")
	          				{
              					o1.eta=etaElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.eta=" ";
              				}
              				if(etdElemntList.item(i).getTextContent()!="")
	          				{
              					o1.etd=etdElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.etd=" ";
              				}
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
              				if(georssElemntList.item(i).getTextContent()!="")
	          				{
              					o1.georss=georssElemntList.item(i).getTextContent();
	          				}
              				else
              				{
              					o1.georss=" ";
              				}
              					m_orders.add(o1);
              				
            			}
              			
              }
              
              
              
              //Log.i("ARRAY", ""+ m_orders.size());
            } catch (Exception e) {
              //Log.e("BACKGROUND_PROC", e.getMessage());
            }
            runOnUiThread(returnRes);
        }
    
    protected void onListItemClick(View v, int pos, long id) {
    	Bundle bundle = new Bundle();
    	String sGuid=m_orders.get(pos).guid;
    	String tmp=m_orders.get(pos).georss;
    	StringTokenizer st = new StringTokenizer(tmp, " ");
    	//TextView tv=(TextView) findViewById(R.id.textView1);
    	//tv.setText(tmp);
		float tX=Float.valueOf(st.nextToken())*1000000;
		float tY=Float.valueOf(st.nextToken())*1000000;
		
        bundle.putInt("gX", Math.round(tX));
        bundle.putInt("gY", Math.round(tY));
        bundle.putString("guid", sGuid);
    	Intent in = new Intent("zug.activity.ACTIVITY_STATIONLIST_RESULT_INTENT");
    	in.putExtras(bundle);
    	    	
    	setResult(0, in);
    	
    	finish();
    }
    private void onListItemLongClick(View v, int pos, long id) {
		
    	String trainId=m_orders.get(pos).guid;
		 
		  Intent i= new Intent("zug.activity.ACTIVITY_TRAINLIST_INTENT").
		  	setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		  i.putExtra("trainkey", trainId);
		  startActivity(i);
		  finish();
	}
    
    private class OrderAdapter extends ArrayAdapter<Order> {

        private ArrayList<Order> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Order> items) {
                super(context, textViewResourceId, items);
                this.items = items;
                
                	 
                   	 
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }
                Order o = items.get(position);
                
                    //ots.setText(sParser.searchStation(selCity).stationName);	
                
                if (o != null) {
                        TextView t1 = (TextView) v.findViewById(R.id.text1);
                        TextView t2 = (TextView) v.findViewById(R.id.text2);
                       // TextView t3 = (TextView) v.findViewById(R.id.text3);
                        TextView t4 = (TextView) v.findViewById(R.id.text4);
                       // TextView t5 = (TextView) v.findViewById(R.id.text5);
                        TextView t6 = (TextView) v.findViewById(R.id.text6);
                        TextView t7 = (TextView) v.findViewById(R.id.text7);
                        
                        
                        
                        	//ots.setText("rrr");
                        
                        if (t1 != null) {
                              t1.setText(o.title);                            }
                        if(t2!= null){
                              t2.setText(o.route);
                        }
                        if (t7 != null) {
                        	if(o.status.equalsIgnoreCase("1"))
                        	{
                        		t7.setBackgroundColor(Color.argb(255, 0, 200, 0));
                        		//t7.setText(getResources().getText(R.string.ontimeString));
                        	}
                        	if(o.status.equalsIgnoreCase("2"))
                        	{
                        		t7.setBackgroundColor(Color.argb(255, 200, 200, 0));
                        		
                        	}
                        	if(o.status.equalsIgnoreCase("3"))
                        	{
                        		t7.setBackgroundColor(Color.argb(255, 200, 0, 0));
                        		
                        	}
                        	if(o.status.equalsIgnoreCase("4"))
                        	{
                        		t7.setBackgroundColor(Color.argb(255, 200, 200, 0));
                        		
                        	}
                        	if(o.status.equalsIgnoreCase("5"))
                        	{
                        		t7.setBackgroundColor(Color.argb(100, 255, 255, 255));
                        		
                        	}
                        	if(o.lateness.equalsIgnoreCase("0"))
                        		t7.setText(getResources().getText(R.string.ontimeString));
                        	else
                        		t7.setText("+ "+o.lateness+" min.");
                            
                        }
                        if (t4 != null) {
                        	t4.setText(o.scheda);
                        }
                        if (t6 != null) {
                        	t6.setText(o.schedd);
                        }
                    
                        	
                        		
                }
               
               
                
            
                return v;
        }
    }
    @Override
	public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
    }
    public void stationClick(View view)
    {
    	Bundle bundle = new Bundle();
    	
    			
        bundle.putString("station", selCity);
    	Intent in = new Intent("zug.activity.ACTIVITY_STATIONLIST_RESULT_INTENT");
    	in.putExtras(bundle);
    	
    	    	
    	setResult(1, in);
    	
    	
    	

    	finish();
    }

}

