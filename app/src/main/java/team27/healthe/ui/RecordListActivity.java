package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class RecordListActivity extends AppCompatActivity {
    public static final String RECORD_INFO = "team27.healthe.Record";

    public static ListView listView;
    private RecordListAdapter adapter;

    private Problem current_problem = new Problem();
    private User current_user;
    public static LocalFileController file_controller = new LocalFileController();
    public static ArrayList<Record> records;
    //public static String FILENAME = "records.sav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        listView = (ListView) findViewById(R.id.record_list);
        records = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFromIntent();
        getRecords();

        // clicking the add button takes you to an alert dialog to choose the
        // type of record to add
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_record_fab);
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

        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record) listView.getItemAtPosition(position);
                deleteRecord(record);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //loadFromFile();

        adapter = new RecordListAdapter(this, records);
        listView.setAdapter(adapter);
    }

    // TODO: decide how to get here..

    public void viewRecord(Record record) {
        Gson gson = new Gson();
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        // TODO: decide what to pass through here, record or use elastic search?
        intent.putExtra(RECORD_INFO, gson.toJson(record));
        startActivity(intent);
    }

    public void deleteRecord(Record rec) {
        final Record record = rec;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
        dialog.setTitle("Delete Record");
        dialog.setMessage("Are you sure you want to delete this record?");

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Deleting record...", Toast.LENGTH_SHORT).show();

                // TODO: implement elastic search & file search?
                new DeleteRecord().execute(record);

                file_controller.removeRecordFromFile();
                adapter.refresh(records);

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

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

//    private class AddProblemES extends AsyncTask<Problem, Void, Problem> {
//
//        @Override
//        protected Problem doInBackground(Problem... problems) {
//            ElasticSearchController es_controller = new ElasticSearchController();
//
//            for (Problem problem : problems) {
//
//                if (problem.getProblemID() == null) { // If account already exists
//                    es_controller.addProblem(problem, current_user.getUserid());
//                    return problem;
//                } else {
//                    return null;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Problem problem) {
//            super.onPostExecute(problem);
//        }
//    }

    private class DeleteRecord extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for(Record record:records) {
                es_controller.removeRecord(record.getRecordID());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
        }
    }


    // Async class for getting records from elastic search server
    private class getRecordAsync extends AsyncTask<String, Void, Record> {

        @Override
        protected Record doInBackground(String... record_ids) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (String record_id : record_ids) {
                return es_controller.getRecord(record_id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
            if (record != null) {
                records.add(record);
                adapter.refresh(records);


                //TODO: fix saving
                LocalFileController localFileController = new LocalFileController();
                localFileController.saveRecordsInFile(records, getApplicationContext());
            }
        }
    }

//    private void loadFromFile() {
//        Collection<String> records = current_problem.getRecords();
//
//        for (String record_id : records) {
//            Record record = file_controller.loadRecordFromFile(record_id, getApplicationContext());
//        }
//    }

    private void getFromIntent() {
        Intent intent = getIntent();

        Gson gson = new Gson();
        ElasticSearchController es_controller = new ElasticSearchController();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String problem_json = intent.getStringExtra(ProblemInfoActivity.PROBLEM_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.current_problem = gson.fromJson(problem_json, Problem.class);
    }

//    private void getRecords() {
//        if (current_problem.getNumberOfRecords() != 0) {
//            for (String record_id : current_problem.getRecords()) {
//                new getRecordAsync().execute(record_id);
//            }
//        } else {
//            records = new ArrayList<Record>();
//        }
//    }

    private void getRecordsES() {
        for (String record_id : current_problem.getRecords()) {
            new getRecordAsync().execute(record_id);
        }
    }

    private void getRecords() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            getRecordsES();
        } else {
            records = file_controller.loadRecordsFromFile(getApplicationContext());
            adapter.refresh(records);
        }
    }

    private void refreshList() {
        adapter.refresh(records);
    }
}
