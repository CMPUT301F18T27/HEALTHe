package team27.healthe.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.OfflineController;
import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.RecordListAdapter;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class RecordListActivity extends AppCompatActivity {

    public static ListView listView;
    private RecordListAdapter adapter;

    private Problem current_problem;
    private User current_user;
    public static LocalFileController file_controller = new LocalFileController();
    public static ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.record_list);
        getFromIntent();

        // clicking the add button takes you to an alert dialog to choose the
        // type of record to add
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_record_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecord();
            }
        });
        if (current_user instanceof CareProvider) {
            fab.hide();
        }

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
                if (current_user instanceof Patient) {
                    deleteRecord(record);
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        records = new ArrayList<>();
        adapter = new RecordListAdapter(this, records);
        listView.setAdapter(adapter);
        getRecords();
    }

    public void viewRecord(Record record) {
        Gson gson = new Gson();
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(record));
        startActivity(intent);
    }

    public void deleteRecord(final Record record) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this Record?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        records.remove(record);
                        adapter.refresh(records);

                        new DeleteRecord().execute(record);

                        current_problem.removeRecord(record.getRecordID());
                        file_controller.saveProblemInFile(current_problem, getApplicationContext());
                        new UpdateProblem().execute(current_problem);


                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        dialog.show();
    }


    public void addRecord() {
        /** Added to ensure that the user has at least 1 body location photo when creating
         * a new record
        **/
        try{
            Patient current_patient = (Patient)current_user;
            if((current_patient.getBodyLocationCount() == 0)){
                Toast.makeText(getApplicationContext(),
                        "We require a body location photo to add records.",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ViewBodyLocationsActivity.class);
                intent.putExtra("current_user", current_user.getUserid());
                intent.putExtra("auto_photo", true);
                startActivity(intent);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("ERROR---failure forcing user to take body location photo");
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add Record");
        dialog.setMessage("");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(this);
        problem_title.setText("Record Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
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
        final TextView date_text = new TextView(this);
        ViewGroup.LayoutParams date_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) date_params).setMargins(0,0,0,64);
        date_text.setTextSize(18);
        date_text.setText(formatter.format(new Date()));
        date_text.setLayoutParams(date_params);
        date_text.setTextColor(Color.BLACK);
        layout.addView(date_text); // Another add method
        date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(date_text);
            }
        });


        //Add title for problem description
        final TextView description_title = new TextView(this);
        description_title.setText("Description:");
        layout.addView(description_title);

        // Add a TextView for description
        final EditText description_text = new EditText(this);
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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

                if (validProblemInputs(title, date, desc)) {
                    Toast.makeText(getApplicationContext(), "Adding record...", Toast.LENGTH_SHORT).show();
                    Record record = new Record(title, date, desc);

                    new AddRecordES().execute(record);

                    records.add(record);
                    adapter.refresh(records);

                    current_problem.addRecord(record.getRecordID());
                    new UpdateProblem().execute(current_problem);

                    file_controller.saveRecordInFile(record, getApplicationContext());
                    file_controller.saveProblemInFile(current_problem, getApplicationContext());

                    dialog.dismiss(); //Dismiss once everything is OK.
                }
            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();
    }

    private class AddRecordES extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();

            for (Record record : records) {
                if (!es_controller.addRecord(record)) {
                    return record;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
            if (record != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addRecord(record, getApplicationContext());
            }
        }
    }

    private class DeleteRecord extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for(Record record:records) {
                if(!es_controller.removeRecord(record.getRecordID())) {
                    return record;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record) {
            super.onPostExecute(record);
            if (record != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.deleteRecord(record, getApplicationContext());
            }
        }
    }


    // Async class for getting records from elastic search server
    private class getRecordAsync extends AsyncTask<String, Void, Record> {

        @Override
        protected Record doInBackground(String... record_ids) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();

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

                LocalFileController localFileController = new LocalFileController();
                localFileController.saveRecordInFile(record, getApplicationContext());
            }
        }
    }

    private class UpdateProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();
            for (Problem problem : problems) {
                if (!es_controller.addProblem(problem)) {
                    return problem;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if (problem != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addProblem(problem, getApplicationContext());
            }
        }
    }

    private class PerformTasks extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            OfflineController controller = new OfflineController();
            for(Boolean bool:booleans) {
                if (bool) {
                    controller.performTasks(getApplicationContext());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getRecordsES();
        }
    }

    private void getFromIntent() {
        Gson gson = new Gson();
        UserElasticSearchController es_controller = new UserElasticSearchController();
        Intent intent = getIntent();
        String problem_json = intent.getStringExtra(ProblemInfoActivity.PROBLEM_MESSAGE);
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);
        this.current_problem = gson.fromJson(problem_json, Problem.class);
    }

    private void getRecordsES() {
        for (String record_id : current_problem.getRecords()) {
            new getRecordAsync().execute(record_id);
        }
    }

    private void getRecords() {
        if (isNetworkConnected()) {
            OfflineController controller = new OfflineController();
            if (controller.hasTasks(this)) {
                new PerformTasks().execute(true);
            }
            else {
                getRecordsES();
            }
        } else {
            getLocalRecords();
        }
    }

    private void getLocalRecords() {
        for (Record record : file_controller.loadRecordsFromFile(current_problem,getApplicationContext())) {
            records.add(record);
        }
        adapter.refresh(records);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    public boolean validProblemInputs(String title, Date date, String desc) {
        if (title.length() > 30) {
            Toast.makeText(this, "Problem title exceeds maximum of 30 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (desc.length() > 300) {
            Toast.makeText(this, "Problem description exceeds maximum of 300 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDateTimePicker(final TextView date_textview) {
        //Taken from: https://stackoverflow.com/questions/2055509/datetime-picker-in-android-application
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        date = Calendar.getInstance();
        final Context context = this;
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Date new_date = date.getTime();
                        date_textview.setText(formatter.format(new_date));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }
}
