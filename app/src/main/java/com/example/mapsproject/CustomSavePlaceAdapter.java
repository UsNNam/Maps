package com.example.mapsproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomSavePlaceAdapter extends ArrayAdapter<String>
{
    Context context;
    String[] address;

    public CustomSavePlaceAdapter(Context context, int layoutToBeInflated, List<String> addresses)
    {
        super(context, layoutToBeInflated, addresses);
        this.context = context;
        this.address= new String[addresses.size()];
        addresses.toArray(this.address);
    }
    static class ViewHolder {

        TextView locationname;
        ImageButton more;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.favorite_place, null);

            holder = new ViewHolder();
            holder.locationname = (TextView) convertView.findViewById(R.id.locationame);
//            holder.more = (ImageButton) convertView.findViewById(R.id.more);
            convertView.setTag(holder);

            String cur = address[position];
//            Log.d(TAG, cur.getAddressLine(0) + " address");
            holder.locationname.setText(cur);
//            holder.more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PopupMenu popupMenu = new PopupMenu(context, holder.more);
//
//                    // Inflating popup menu from popup_menu.xml file
//                    popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            int itemID = item.getItemId();
//                            if(itemID == R.id.remove) {        // Xử lý khi mục 1 được chọn
//                                Toast.makeText(context, "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                                return true;
//                            }
//                            if (itemID == R.id.copy)
//                            {
//                                    Toast.makeText(context, "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                                    return true;
//                            }
//                            return false;
//                        }
//                    });
//                    popupMenu.show();
//                }
//            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
