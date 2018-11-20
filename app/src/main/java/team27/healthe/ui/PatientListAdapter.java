package team27.healthe.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.Patient;

public class PatientListAdapter extends ArrayAdapter<Patient> {
    private final Activity context;
    private final ArrayList<Patient> patients;

    //Constructor
    public PatientListAdapter(Activity context, ArrayList<Patient> patients) {
        super(context, R.layout.patient_list_item, patients);
        this.context = context;
        this.patients = patients;
    }

    // Called for each row/item in the ListView
    public View getView (int position, View view, ViewGroup parent) {
        Patient patient = getItem(position); // get patient at current position

        // inflate and get the view layout from the patient_list_item.xml file
        LayoutInflater inflater = context.getLayoutInflater();
        View list_row = inflater.inflate(R.layout.patient_list_item, parent, false);

        TextView upperText = (TextView) list_row.findViewById(R.id.textUpper);
        TextView lowerText = (TextView) list_row.findViewById(R.id.textLower);

        // Set the two text items in the row to the emotion type and emotion date
        upperText.setText(patient.getUserid());
        lowerText.setText("Problem count: " + patient.getProblemCount());

        return list_row; // return the view(row)
    }

    // Recreates the ListView when changes have been made to the list of emotions
    public void refresh(ArrayList<Patient> patients) {
        this.clear();
        this.addAll(patients);
        notifyDataSetChanged();
    }
}
