package team27.healthe.controllers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team27.healthe.R;

/**
 * adapter for filling in list view with patient information (and allow selection)
 * @author [fill in]
 */
public class PatientListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private ArrayList<String> patients; //Array of patient ids

    //Constructor
    public PatientListAdapter(Activity context, ArrayList<String> patients) {
        super(context, R.layout.patient_list_item, patients);
        this.context = context;
        this.patients = patients;
    }

    /**
     * Called for each row/item in the ListView
     * @param position (patient at current position)
     * @param view (list row)
     * @param parent (list's parent)
     * @return list_row (View)
     */
    public View getView (int position, View view, ViewGroup parent) {
        String patient_id = getItem(position); // get patient at current position

        // inflate and get the view layout from the patient_list_item.xml file
        LayoutInflater inflater = context.getLayoutInflater();
        View list_row = inflater.inflate(R.layout.patient_list_item, parent, false);

        TextView upperText = (TextView) list_row.findViewById(R.id.textUpper);

        // Set the two text items in the row to the emotion type and emotion date
        upperText.setText(patient_id);

        return list_row; // return the view(row)
    }

    /**
     * Recreates the ListView when changes have been made to the list of patients
     * @param patients (ArrayList<String> - patient ids)
     */
    public void refresh(ArrayList<String> patients) {
        this.patients = patients;
        notifyDataSetChanged();
    }
}
