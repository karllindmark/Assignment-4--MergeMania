
package com.ninetwozero.assignment4.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.assignment4.R;
import com.ninetwozero.assignment4.misc.Constants;

public class HighscoreListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private Cursor cursor;
    private LayoutInflater layoutInflater;

    // Constructs
    public HighscoreListAdapter(Context c, Cursor cr, LayoutInflater l) {

        context = c;
        cursor = cr;
        layoutInflater = l;

    }

    public int getCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Let's do this
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_highscore, null);

        }

        // Let's set it straigh
        cursor.moveToPosition(position);

        // Set the text
        ((TextView) convertView.findViewById(R.id.text_position)).setText("#" + (position + 1));
        ((TextView) convertView.findViewById(R.id.text_name)).setText(cursor.getString(cursor
                .getColumnIndex(Constants.SQLITE_FIELD_NAME)));
        ((TextView) convertView.findViewById(R.id.text_time)).setText(

                context.getString(R.string.info_top_time).replace(

                        "{time}", cursor.getString(cursor
                                .getColumnIndex(Constants.SQLITE_FIELD_TIME)
                                )
                        )
                );

        // Return
        return convertView;

    }

    public void setCursor(Cursor c) {

        cursor = c;
        notifyDataSetChanged();
    }

}
