package fi.moflac.railtrack;

import java.util.ArrayList;

import fi.moflac.railtrack.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ZugNearAdapter extends ArrayAdapter<ZugTrain> {

	private ArrayList<ZugTrain> items;
	private Context mcontext;

    public ZugNearAdapter(Context context, int textViewResourceId, ArrayList<ZugTrain> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            mcontext=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.nearrow, null);
            }
            ZugTrain o = items.get(position);
           if (o != null) {
            		
                    TextView t1 = (TextView) v.findViewById(R.id.neartextView1);
                    TextView t2 = (TextView) v.findViewById(R.id.neartextView2);
                    
                   
                    if (t1 != null) {
                          t1.setText(o.guid); 
                    }
                    if (t2 != null) {
                        t2.setText(o.from);  
                  }
                    
                    
            }
            return v;
    }
}

