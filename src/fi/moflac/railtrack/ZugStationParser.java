package fi.moflac.railtrack;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import fi.moflac.railtrack.R;

import android.content.Context;
import android.widget.Toast;

public class ZugStationParser {
	
	
	NodeList nodelist;
	Element element;
	Element elementNext;
	InputStream is;
	XmlPullParser xpp;
	private ArrayList<ZugStation> stationList;
	ZugStation station;
	
	private ArrayList<String> tmpList;
	
	public ZugStationParser(Context context)
	{
		
		is=null;
		stationList=new ArrayList<ZugStation>();
		
		tmpList=new ArrayList<String>();
	}
	
	public ArrayList<ZugStation> parseStationNames(Context context)
	{
		
		try {
			
			xpp=context.getResources().getXml(R.xml.vk2);
			station=new ZugStation();
			while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT)
			{	
								
				if (xpp.getEventType()==XmlPullParser.START_TAG) 
				{
					if (xpp.getName().equalsIgnoreCase("abbrev"))
					
						//stationArray.add(xpp.nextText());
						station.stationAbbr=xpp.nextText(); 
					
					if (xpp.getName().equalsIgnoreCase("name"))
					{
						station.stationName=xpp.nextText();
						tmpList.add(station.stationName);
					}
					if (xpp.getName().equalsIgnoreCase("lat"))
					{
						station.etrsX=xpp.nextText();
					    station.gX=Math.round(Float.valueOf(station.etrsX)*1000000);
					}
					if (xpp.getName().equalsIgnoreCase("lon"))
					{
						station.etrsY=xpp.nextText();
						station.gY=Math.round(Float.valueOf(station.etrsY)*1000000);
					}
					if (xpp.getName().equalsIgnoreCase("zlevel"))
					{
						station.zLevel=xpp.nextText();
						//
						stationList.add(station);
						station = new ZugStation();
					}
				}
				xpp.next();
				
			}	
				
				
				
			}
		
		catch (Throwable t) {
			Toast.makeText(context.getApplicationContext(), R.string.TrainListConnectionError, Toast.LENGTH_LONG).show();
		}
		return stationList;
		
	
	}
	public ZugStation searchStation(String abbr)
	{
		float tmpX;
		float tmpY;
		ZugStation ret=new ZugStation();
		int searchListLength = stationList.size();
		for (int i = 0; i < searchListLength; i++)
		{
			if (stationList.get(i).stationAbbr.equalsIgnoreCase(abbr)) 
			{
				ret.stationAbbr=abbr;
				ret.stationName=stationList.get(i).stationName;
				ret.etrsX=stationList.get(i).etrsX;
				ret.etrsY=stationList.get(i).etrsY;
				ret.zLevel=stationList.get(i).zLevel;
				tmpX=Float.valueOf(ret.etrsX)*1000000;
				tmpY=Float.valueOf(ret.etrsY)*1000000;
				ret.gX=Math.round(tmpX);
				ret.gY=Math.round(tmpY);
			}
		}
		
		return ret;
	}
	public ZugStation searchStationWithName(String name)
	{
		float tmpX;
		float tmpY;
		ZugStation ret=new ZugStation();
		int searchListLength = stationList.size();
		for (int i = 0; i < searchListLength; i++)
		{
			if (stationList.get(i).stationName.equalsIgnoreCase(name)) 
			{
				ret.stationAbbr=stationList.get(i).stationAbbr;
				ret.stationName=stationList.get(i).stationName;
				ret.etrsX=stationList.get(i).etrsX;
				ret.etrsY=stationList.get(i).etrsY;
				ret.zLevel=stationList.get(i).zLevel;
				tmpX=Float.valueOf(ret.etrsX)*1000000;
				tmpY=Float.valueOf(ret.etrsY)*1000000;
				ret.gX=Math.round(tmpX);
				ret.gY=Math.round(tmpY);
			}
		}
		return ret;
	}
	public String[] getSearchList()
	{   
		String[] searchStationList=(String[])tmpList.toArray(new String[tmpList.size()]);
		return searchStationList;
	}
	
}
