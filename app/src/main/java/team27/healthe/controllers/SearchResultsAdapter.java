package team27.healthe.controllers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import io.searchbox.core.SearchResult;
import team27.healthe.R;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;

/**
 * Gets a list of records and problems as a search result
 * @author Chase
 */
public class SearchResultsAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> results; //Array of patient ids

    //Constructor
    public SearchResultsAdapter(Activity context, ArrayList<String> search_results) {
        super(context, R.layout.search_list_item, search_results);
        this.context = context;
        this.results = search_results;
    }

    /**
     * Returns the view of the search results
     * Called for each row/item in the ListView
     * @param position (int)
     * @param view (View)
     * @param parent (ViewGroup)
     * @return
     */
    public View getView (int position, View view, ViewGroup parent) {
        String hit = getItem(position); // get hit item at current location

        // inflate and get the view layout from the patient_list_item.xml file
        LayoutInflater inflater = context.getLayoutInflater();
        View list_row = inflater.inflate(R.layout.search_list_item, parent, false);

        TextView title_text = (TextView) list_row.findViewById(R.id.textTitle);
        TextView type_text = (TextView) list_row.findViewById(R.id.textType);

        // Get the proper object type for the hit and set the two text items in the row
        Gson gson = new Gson();
        Map hit_map = gson.fromJson(hit, Map.class);

        if (hit_map.containsKey("problem_id")) {
            Problem problem = gson.fromJson(hit, Problem.class);
            title_text.setText(problem.getTitle());
            type_text.setText("Problem");
        } else {
            Record record = gson.fromJson(hit, Record.class);
            title_text.setText(record.getTitle());
            type_text.setText("Record");
        }

        return list_row; // return the view(row)
    }

    /**
     * Refreshes the view when a change is made
     * @param search_results (array list of search results)
     */
    public void refresh(ArrayList<String> search_results) {
        this.results = search_results;
        notifyDataSetChanged();
    }
}
