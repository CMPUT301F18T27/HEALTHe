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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class RecordListActivity extends AppCompatActivity {
    public static final String RECORD_MESSAGE = "team27.healthe.Record";

    public static ListView listView;
    private RecordListAdapter adapter;

    private Problem current_problem;
    public static LocalFileController file_controller = new LocalFileController();
    public static ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        listView = (ListView) findViewById(R.id.record_list);
        //records = new ArrayList<Record>();

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
                addRecord();
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
        loadFromFile();

        adapter = new RecordListAdapter(this, records);
        listView.setAdapter(adapter);
    }


    public void viewRecord(Record record) {
        Gson gson = new Gson();
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
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

                records.remove(record);
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


    public void addRecord() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add Record");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for record
        final TextView record_title = new TextView(this);
        record_title.setText("Record Title:");
        layout.addView(record_title);

        // Add a TextView for record title
        final EditText title_text = new EditText(this);
        title_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        title_text.setLayoutParams(params);
        layout.addView(title_text); // Notice this is an add method

        //Add title for date started
        final TextView date_title = new TextView(this);
        date_title.setText("Date Started:");
        layout.addView(date_title);

        // Add a TextView for date
        final EditText date_text = new EditText(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        date_text.setInputType(InputType.TYPE_CLASS_DATETIME);
        date_text.setText(formatter.format(new Date()));
        layout.addView(date_text); // Another add method

        //Add title for record description
        final TextView description_title = new TextView(this);
        description_title.setText("Description:");
        layout.addView(description_title);

        // Add a TextView for description
        final EditText description_text = new EditText(this);
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO: check and add corresponding record
                String title = title_text.getText().toString();
                Date date;
                String desc = description_text.getText().toString();

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    date = formatter.parse(date_text.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = new Date();
                }
                Record record = new Record(title, date, desc);
                records.add(record);

                file_controller.saveRecordInFile(record, getApplicationContext());
                new AddRecordES().execute(record);
                adapter.refresh(records);
            }
        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        dialog.show();
    }

    private class AddRecordES extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (Record record : records) {

                Record new_record = es_controller.addRecord(record);

                if (new_record == null) {
                    return record;
                } else {
                    return new_record;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
            if (!record.getRecordID().equals("")) {
                current_problem.addRecord(record.getRecordID());
                new UpdateProblem().execute(current_problem);
            }
        }
    }

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

    private class UpdateProblem extends AsyncTask<Problem, Void, Void> {

        @Override
        protected Void doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for (Problem problem : problems) {
                es_controller.addProblem(problem);
            }
            return null;
        }
    }

    private void loadFromFile() {
//        Collection<String> records = current_problem.getRecords();
        records = file_controller.loadRecordsFromFile(getApplicationContext());
    }

    private void getFromIntent() {
        Gson gson = new Gson();
        Intent intent = getIntent();
        String problem_json = intent.getStringExtra(ProblemInfoActivity.PROBLEM_MESSAGE);
        this.current_problem = gson.fromJson(problem_json, Problem.class);
    }

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
}
