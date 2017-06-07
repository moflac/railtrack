package fi.moflac.railtrack;

import java.util.ArrayList;


import fi.moflac.railtrack.R;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class ZugSearchStationAdapter extends ArrayAdapter<ZugStation> {

	
	private Context mcontext;
	
	
	private Filter filter;
	private ArrayList<ZugStation> filtered;
	private ArrayList<ZugStation> original;
	



    public ZugSearchStationAdapter(Context context, int textViewResourceId, ArrayList<ZugStation> items) {
            super(context, textViewResourceId, items);
           // this.filter=new ZugSearchStationFilter();
            this.original = new ArrayList<ZugStation>(items);
            this.filtered=new ArrayList<ZugStation>(items);
            mcontext=context;
            
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.searchstationrow, null);
            }
            ZugStation o = filtered.get(position);
            if (o != null) {
            		
                    TextView t1 = (TextView) v.findViewById(R.id.searchStationView1);
                    TextView t2 = (TextView) v.findViewById(R.id.searchStationView2);
                   
                   
                    if (t1 != null) {
                          t1.setText(" "+o.stationName); 
                    }
                    if (t2 != null) {
                        t2.setText(o.stationAbbr); 
                       
                    }
                    
                    
                    
                    
                    
            }
            return v;
    }
   

    @Override
    public Filter getFilter()
    {
        if(filter == null)
            filter = new ZugSearchStationFilter();
        return filter;
    }
    public void resetFilter()
    {
    	filtered=original;
    }

    private class ZugSearchStationFilter extends Filter {

    	
    	FilterResults results = new FilterResults();
    	
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			
			String prefix = constraint.toString().toLowerCase();

			
			//Log.i("filter",prefix.toString());
			if(prefix==null||prefix.length()==0)
			{
				
					ArrayList<ZugStation> mlist= new ArrayList<ZugStation>(original);

                    results.values = mlist;
                    results.count = mlist.size();
                    Log.i("filter",String.valueOf(results.count));
                    
			}
			else
            {
				 
				    final ArrayList<ZugStation> mlist = new ArrayList<ZugStation>(original);
                    final ArrayList<ZugStation> nlist = new ArrayList<ZugStation>();

	                int l=mlist.size();
	                for(int i = 0; i < l; i++)
	                {
	                    final ZugStation m = mlist.get(i);
	                    final String sName=m.stationName.toLowerCase();
	                    final String sAbbr = m.stationAbbr.toLowerCase();
	                    
	                    if(sName.contains(prefix)||sAbbr.contains(prefix))
	                        nlist.add(m);
	                }
	                results.count = nlist.size();
	                results.values = nlist;
	               
            }

			 return results;
			
			
			
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
				
				filtered = (ArrayList<ZugStation>) results.values;
				
				
				notifyDataSetChanged();
				
					clear();
				for(int i = 0; i <filtered.size(); i++)
				{
						ZugStation fi = filtered.get(i);
						add(fi);
				}

				
						
		}
    
    }

}

