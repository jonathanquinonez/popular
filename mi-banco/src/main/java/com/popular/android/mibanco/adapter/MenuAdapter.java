package com.popular.android.mibanco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.object.SidebarItem;

import java.util.List;

/**
 * Adapter to manage sidebar menu
 */
public class MenuAdapter extends BaseAdapter {

    Context context;
    List<SidebarItem> data;
    private static LayoutInflater inflater = null;

    public MenuAdapter(Context context, List<SidebarItem> menuItems) {
        this.context = context;
        this.data = menuItems;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {        
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item_sidebar, null);
        
        if(data.get(position).getIcon() != 0){
	        ImageView imageView = (ImageView) vi.findViewById(R.id.sidebarItemImageview);
	        imageView.setImageResource(data.get(position).getIcon());
        }
        
        TextView text = (TextView) vi.findViewById(R.id.sidebarTitleTextView);
        text.setText(data.get(position).getTitle());
        return vi;
	}
}