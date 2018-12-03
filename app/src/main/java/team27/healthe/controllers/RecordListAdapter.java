package team27.healthe.controllers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.Record;

/**
 * Adapter for filling in the list view with records
 * @author Cody/Chase
 */
public class RecordListAdapter extends ArrayAdapter<Record> {
    private final Activity context;
    private ArrayList<Record> records;

    //Constructor
    public RecordListAdapter(Activity context, ArrayList<Record> records) {
        super(context, R.layout.record_list_item, records);
        this.context = context;
        this.records = records;
    }

    /**
     * Gets the view of the record list
     * Called for each row/item in the list view
     * @param position (int)
     * @param view (View)
     * @param parent (ViewGroup)
     * @return list_row
     */
    public View getView (int position, View view, ViewGroup parent) {
        Record record = getItem(position); // get record at current position

        // inflate and get the view layout from the record_list_item.xml file
        LayoutInflater inflater = context.getLayoutInflater();
        View list_row = inflater.inflate(R.layout.record_list_item, parent, false);

        TextView upperText = (TextView) list_row.findViewById(R.id.textUpper);
        TextView lowerText = (TextView) list_row.findViewById(R.id.textLower);

        // Set the two text items in the row to the record type and record date
        upperText.setText(record.getTitle());
        lowerText.setText(record.getRdate().toString());

        return list_row; // return the view(row)
    }

    /**
     * Refreshed the view after changes are made
     * @param records (Array list of records)
     */
    public void refresh(ArrayList<Record> records) {
        this.records = records;
        notifyDataSetChanged();
    }
}
