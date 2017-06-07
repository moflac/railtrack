package fi.moflac.railtrack;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import fi.moflac.railtrack.R;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.view.View;

public class ZugParser{ 
	
	Document document;
	private ArrayList<ZugTrain> junaList;
	private Context mcontext;
	private ZugStationParser msParser;
	private String[] kList;
	private DocumentBuilder builder;
	private NodeList guidElemntList ;
	private NodeList titleElemntList ;
	private NodeList fromElemntList ;
	private NodeList toElemntList ;
	private NodeList pubElemntList ;
	private NodeList georssElemntList ;
	private NodeList directionElemntList ;
	private NodeList lastBuildList;
	private NodeList categList;
	private NodeList statusList;
	DownloadFile dLoad;
	ZugActivity myMain;
	
	public String lastBuildDate;
	public ZugParser(ZugActivity main)
	{
		
		myMain=main;
		new ArrayList<String>();
		junaList=new ArrayList<ZugTrain>();
		DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
		 // Use the factory to create a builder
		PowerManager pm = (PowerManager)myMain.getSystemService(Context.POWER_SERVICE);
	    pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ZugLock");
	    lastBuildDate=myMain.getResources().getString(R.string.lastRefreshDNK);
	    
		try{
			builder = factory.newDocumentBuilder();
		}
		catch (Exception e)
		{
			
			
			e.printStackTrace();
			
		}
		
	}
	
	public ArrayList<ZugTrain> parseTrains(ZugActivity mContext, ZugStationParser sParser)
	{
		
		mcontext=mContext.getApplicationContext();
		msParser=sParser;
		
		
		
		if (ZugNetworking.isNetworkAvailable(mcontext)==true)
		{
			dLoad= new DownloadFile(mContext);
			dLoad.execute();
		}
		return junaList;
			 
			 
			 
			 
			 
			
	}
	public ZugTrain searchTrain(String abbr)
	{
		ZugTrain ret=new ZugTrain();
		int searchListLength = junaList.size();
		for (int i = 0; i < searchListLength; i++)
		{
			if (junaList.get(i).guid.equalsIgnoreCase(abbr)) 
			{
				ret.guid=abbr;
				ret.gX= junaList.get(i).gX;
				ret.gY= junaList.get(i).gY;
			}
		}
		return ret;
	}
	private class DownloadFile extends AsyncTask<Void, Integer, ArrayList<ZugTrain>>{
		private ZugActivity dlContext = null;
		ArrayList<ZugTrain> rTrain=new ArrayList<ZugTrain>();
		public DownloadFile(ZugActivity context)
		{	
			dlContext=context;
		}

		@Override
	    protected ArrayList<ZugTrain> doInBackground(Void... none) {
	        
			
	        try {
	            int k=0;
	            
	            document = builder.parse("http://188.117.35.14/TrainRSS/TrainService.svc/AllTrains");
	            
					 lastBuildList  = document.getElementsByTagName("lastBuildDate");	  
					 guidElemntList =  document.getElementsByTagName("guid");
					 titleElemntList =  document.getElementsByTagName("title");
					 document.getElementsByTagName("description");
					 fromElemntList = document.getElementsByTagName("from");
					 toElemntList = document.getElementsByTagName("to");
					 pubElemntList = document.getElementsByTagName("pubDate");
					 georssElemntList = document.getElementsByTagName("georss:point");
					 document.getElementsByTagName("status");
					 directionElemntList =  document.getElementsByTagName("dir");
					 categList=document.getElementsByTagName("cat");
					 statusList=document.getElementsByTagName("status");
					 
					 kList=new String[guidElemntList.getLength()];
					 if(lastBuildList.item(0).getTextContent()!="")
						 lastBuildDate=lastBuildList.item(0).getTextContent();
					 for (int i = 0; i < guidElemntList.getLength(); i++)
					 {
				  		  ZugTrain jjuna=new ZugTrain();
				  		  
				  		  if(guidElemntList.item(i).getTextContent().length()>0&&
				  				guidElemntList.item(i).getTextContent()!=null)
				  		  {
				  			jjuna.guid=guidElemntList.item(i).getTextContent();
				  		  }
				  		  else
				  		  {
				  			  jjuna.guid=" ";
				  		  }
				  		  
				  		  if(titleElemntList.item(i+1).getTextContent()!="")
				  		  {
				  			jjuna.title=titleElemntList.item(i+1).getTextContent();
				  		  }
				  		  else
				  		  {
				  			  jjuna.title=" ";
				  		  }
				  		 
				  		  if(pubElemntList.item(i).getTextContent()!="")
				  		  {
				  			  jjuna.pubDate=pubElemntList.item(i).getTextContent();
				  		  }
				  		  else
				  		  {
				  			  jjuna.pubDate=" ";
				  		  }
				  		  
				  		  if(fromElemntList.item(i).getTextContent()!="")
				  		  {
				  			  jjuna.from=fromElemntList.item(i).getTextContent();
				  			  jjuna.fromName=msParser.searchStation(jjuna.from).stationName;
				  		  }
				  		  else
				  		  {
				  			  jjuna.fromName=" ";
				  		  }
				  		  
				  		  if(toElemntList.item(i).getTextContent()!="")
				  		  {
				  			  jjuna.to=toElemntList.item(i).getTextContent();
				  			  jjuna.toName=msParser.searchStation(jjuna.to).stationName;
				  		  }
				  		  else
				  		  {
				  			  jjuna.toName=" ";
				  		  }
				  		  
				  		  if(categList.item(i).getTextContent()!="")
				  		  {
				  			  String sCat=categList.item(i).getTextContent();
				  			  if(sCat.equalsIgnoreCase("H"))
				  				  jjuna.categ=mcontext.getResources().getString(R.string.train_H);
				  			  if(sCat.equalsIgnoreCase("IC"))
				  				  jjuna.categ=mcontext.getResources().getString(R.string.train_ic);
				  			  if(sCat.equalsIgnoreCase("IC2"))
				  				  jjuna.categ=mcontext.getResources().getString(R.string.train_ic2);
				  			  if(sCat.equalsIgnoreCase("P"))
				  				  jjuna.categ=mcontext.getResources().getString(R.string.train_P);
				  			  if(sCat.equalsIgnoreCase("AE"))
				  				  jjuna.categ=mcontext.getResources().getString(R.string.train_AE);
				  			  if(sCat.equalsIgnoreCase("S"))
				  				  jjuna.categ=mcontext.getResources().getString(R.string.train_S);
				  			  
				  		  }
				  		  else
				  		  {
				  			  jjuna.categ=" ";
				  		  }
				  		  if(statusList.item(i).getTextContent()!="")
				  		  {
				  			  jjuna.status=Integer.valueOf(statusList.item(i).getTextContent());
				  			  
				  		  }
				  		  else
				  		  {
				  			  jjuna.status=1;
				  		  }
				  		  if(georssElemntList.item(i).getTextContent()!="")
				  		  {
				  			  String georsSt=georssElemntList.item(i).getTextContent();
				  			  StringTokenizer st = new StringTokenizer(georsSt, " ");
				  			  float tX=Float.valueOf(st.nextToken())*1000000;
				  			  float tY=Float.valueOf(st.nextToken())*1000000;
				  			  jjuna.gX=Math.round(tX);
				  			  jjuna.gY=Math.round(tY);
				  		  }
				  		  else
				  		  {
				  			  jjuna.gX=0;
				  			  jjuna.gY=0;
				  		  }
				  		  if(directionElemntList.item(i).getTextContent()!="")
				  			  jjuna.dir=Float.valueOf(directionElemntList.item(i).getTextContent());
				  		  else
				  			  jjuna.dir=270;
				  		  
				  		  if (jjuna.gX>0f)
				  		  {
				  			  rTrain.add(jjuna);
				  			  String fromName=msParser.searchStation(jjuna.from).stationName;
				  			  String toName=msParser.searchStation(jjuna.to).stationName;
				  			//  tmpList.add(jjuna.guid+" "+fromName+" - "+toName);
				  			  kList[k]=jjuna.guid+" "+fromName+" - "+toName;
				  			  k++;
				  		  }
				  		 
				  		  
				  		  
				  		  
				  		  
					  }
					 
	            
	            return rTrain;
			}
				
				catch (Exception e)
				{
					
					
					e.printStackTrace();
					rTrain=null;
					return rTrain;
				}
				
	                
	            
	       
	        
	    }
		protected void onPreExecute()
	    {
	    	dlContext.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
	    	//wl.acquire();
	    	//Log.i("Wakelock", "Acquired");
	    }
	    protected void onPostExecute(ArrayList<ZugTrain> result)
	    {
	    	if(rTrain!=null)
	    	{
	    		kList=new String[rTrain.size()];
				 for(int j=0;j<rTrain.size();j++)
				 {
					 kList[j]=rTrain.get(j).guid+" "+rTrain.get(j).fromName+" - "+rTrain.get(j).toName;
				 }
	    		dlContext.findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
	    		junaList=rTrain;
	    		//this updates the mapview through main activity
	    		myMain.updateTrains();
	    		myMain.zVal=kList;
	    		myMain.lockTrain();
	    	}
	    	//wl.release();
	    	//Log.i("Wakelock", "Released");
	    }

	   
	

	}
	 public String[] getSearchList()
		{   
			
			return kList;
		}
	 public ArrayList<ZugTrain> getTrainList()
	 {
		 return junaList;
	 }
}