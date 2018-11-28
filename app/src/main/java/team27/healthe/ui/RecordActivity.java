package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import team27.healthe.R;
import team27.healthe.model.Record;

public class RecordActivity extends AppCompatActivity {
    public static ListView listView;
    private RecordListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        listView = (ListView) findViewById(R.id.record_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record) listView.getItemAtPosition(position);
                viewRecord(record);
            }
        });
    }

    // TODO: decide how to get here..

    public void viewRecord(Record record) {

    }

    public void selectRecordType() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Record Type");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView record_type_text = new TextView(this);
        record_type_text.setText("Record Type:");
        layout.addView(record_type_text);

        // TODO: test to see how spinner works
        // Add a spinner for record type title
        final Spinner record_type_spinner = new Spinner(this);
        // Creates an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Record_Types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        record_type_spinner.setAdapter(adapter);

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0, 0, 0, 64);
        record_type_spinner.setLayoutParams(params);

        record_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String record_type = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // auto generated method stub
            }
        });

        layout.addView(record_type_spinner); // Notice this is an add method

        dialog.setView(layout);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO: check and add corresponding record
            }
        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        dialog.show();
    }

    public void addCommentRecord(){

    }
}
