package team27.healthe.model;

import team27.healthe.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProblemsAdapter extends ArrayAdapter<Problem> {
    private static final String FILENAME = "problems.sav";

    private Context context;
    private ArrayList<Problem> recordedProblems;

    public ProblemsAdapter (Context context, ArrayList<Problem> recordedProblems) {
        super(context, 0, recordedProblems);
        this.context = context;
        this.recordedProblems = recordedProblems;
    }

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

        //TextView recordNum = (TextView) listItem.findViewById(R.id.RecordNumber);
        //recordNum.setText(current_problem.getNumberOfRecords());

        return listItem;
    }

    public void refresh(ArrayList<Problem> probems) {
        this.clear();
        this.addAll(probems);
        notifyDataSetChanged();
    }
}