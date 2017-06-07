package fi.moflac.railtrack;

import java.util.ArrayList;
import fi.moflac.railtrack.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class ZugSearchAdapter extends ArrayAdapter<ZugTrain> {

	
	private Context mcontext;
	
	
	private Filter filter;
	private ArrayList<ZugTrain> filtered;
	private ArrayList<ZugTrain> original;
	


    public ZugSearchAdapter(Context context, int textViewResourceId, ArrayList<ZugTrain> items) {
            super(context, textViewResourceId, items);
            //this.filter= new ZugSearchFilter();
            this.original = new ArrayList<ZugTrain>(items);
            this.filtered=new ArrayList<ZugTrain>(items);
            mcontext=context;
           
           
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.searchrow, null);
               
            }
            ZugTrain o = filtered.get(position);
           if (o != null) {
            		
                    TextView t1 = (TextView) v.findViewById(R.id.searchView1);
                    TextView t2 = (TextView) v.findViewById(R.id.searchView2);
                    TextView t3 = (TextView) v.findViewById(R.id.searchView3);
                    TextView t4 = (TextView) v.findViewById(R.id.searchView4);
                   
                    if (t1 != null) {
                          t1.setText(o.guid); 
                    }
                    if (t2 != null) {
                        t2.setText(o.fromName+" - "+o.toName); 
                       
                    }
                    if (t3 != null) {
                    	t3.setText(o.categ);
                       
                    }
                    if(t4!=null){
                    	t4.setTextColor(Color.BLACK);
                    	if(o.status==1)
                    	{
                    		//t4.setBackgroundColor(R.color.trainStatusColor_1);
                    		t4.setBackgroundColor(mcontext.getResources().getColor(R.color.trainStatusColor_1));
                    		t4.setText(R.string.trainStatusString_1);
                    	}
                    	if(o.status==2)
                    	{
                    		t4.setBackgroundColor(mcontext.getResources().getColor(R.color.trainStatusColor_2));
                    		t4.setText(R.string.trainStatusString_2);
                    	}
                    	if(o.status==3)
                    	{
                    		t4.setBackgroundColor(mcontext.getResources().getColor(R.color.trainStatusColor_3));
                    		t4.setText(R.string.trainStatusString_3);
                    	}
                    	if(o.status==4)
                    	{
                    		t4.setBackgroundColor(mcontext.getResources().getColor(R.color.trainStatusColor_4));
                    		t4.setText(R.string.trainStatusString_3);
                    	}
                    	if(o.status==5)
                    	{
                    		t4.setBackgroundColor(mcontext.getResources().getColor(R.color.trainStatusColor_5));
                    		t4.setText(R.string.trainStatusString_5);
                    	}
                    }
                    
                    
                    
            }
            return v;
    }
   

    @Override
    public Filter getFilter()
    {
        if(filter == null)
            filter = new ZugSearchFilter();
        return filter;
    }
    public void resetFilter()
    {
    	filtered=original;
    	
    }

    private class ZugSearchFilter extends Filter {

    	
    	FilterResults results = new FilterResults();
    	
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			
			String prefix = constraint.toString().toLowerCase();

			if(prefix==null||prefix.length()==0)
			{
				
					ArrayList<ZugTrain> mlist= new ArrayList<ZugTrain>(original);

                    results.values = mlist;
                    results.count = mlist.size();
                    
                    
			}
			else
            {
				 
				    final ArrayList<ZugTrain> mlist = new ArrayList<ZugTrain>(original);
                    final ArrayList<ZugTrain> nlist = new ArrayList<ZugTrain>();

	                int l=mlist.size();
	                for(int i = 0; i < l; i++)
	                {
	                    final ZugTrain m = mlist.get(i);
	                    final String sRoute=(m.fromName+" - "+m.toName).toLowerCase();
	                    final String sGuid = m.guid.toLowerCase();
	                    final String sCateg = m.categ.toLowerCase();
	                    if(sRoute.contains(prefix)||sGuid.contains(prefix)||sCateg.contains(prefix))
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
				    
					filtered = (ArrayList<ZugTrain>) results.values;
					
					
					notifyDataSetChanged();
						clear();
					for(int i = 0; i < filtered.size(); i++)
					{
						    ZugTrain fi= filtered.get(i);
							add(fi);
					}
					
				
						
		}
    
    }

}

