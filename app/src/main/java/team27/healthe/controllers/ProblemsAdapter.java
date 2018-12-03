package team27.healthe.controllers;

import team27.healthe.R;
import team27.healthe.model.Problem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * adapter for filling in list view with problem information (and allow selection)
 * @author Cody/Chase
 */
public class ProblemsAdapter extends ArrayAdapter<Problem> {
    private Context context;
    private ArrayList<Problem> recordedProblems;

    /**
     * Gets the context and list of problems to do with the context
     * @param context (Context class)
     * @param recordedProblems (Array list of problems)
     */
    public ProblemsAdapter (Context context, ArrayList<Problem> recordedProblems) {
        super(context, 0, recordedProblems);
        this.context = context;
        this.recordedProblems = recordedProblems;
    }

    /**
     * Gets the view of the problems
     * @param position (int)
     * @param convertView (View)
     * @param parent (ViewGroup)
     * @return listItem
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.activity_problem_listview, parent, false);
        }
        Problem current_problem = recordedProblems.get(position);

        TextView problem = (TextView) listItem.findViewById(R.id.Problem);
        problem.setText(current_problem.getTitle());

        TextView date = (TextView) listItem.findViewById(R.id.Date);
        date.setText(current_problem.getPdateAsString());

        TextView recordNum = (TextView) listItem.findViewById(R.id.RecordNumber);
        recordNum.setText("Records: " + Integer.toString(current_problem.getNumberOfRecords()));

        return listItem;
    }

    /**
     * Refreshes the view when an item is changed
     * @param problems (ArrayList of problems)
     */
    public void refresh(ArrayList<Problem> problems) {
        this.recordedProblems = problems;
        notifyDataSetChanged();
    }
}