package team27.healthe.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class RecordActivity extends AppCompatActivity {
    public static final String RECORD_MESSAGE = "team27.healthe.RECORD";
    private static final Integer GEO_REQUEST_CODE = 4;
    private static final Integer COMMENT_REQUEST_CODE = 5;
    private static final Integer PHOTO_REQUEST_CODE = 6;
    private static final Integer BODYLOCATION_REQUEST_CODE = 7;
    private User current_user;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getItems(getIntent());
        setTextViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        MenuItem edit_item = menu.findItem(R.id.action_edit_record);
        edit_item.setVisible(!(current_user instanceof CareProvider));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_record) {
            editRecord();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GEO_REQUEST_CODE || requestCode == COMMENT_REQUEST_CODE || requestCode == PHOTO_REQUEST_CODE || requestCode == BODYLOCATION_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Gson gson = new Gson();
                String record_json = data.getStringExtra(RECORD_MESSAGE);
                this.record = gson.fromJson(record_json, Record.class);
                //TODO: Save record to es server
            }
        }
    }

    private void getItems(Intent intent) {
        ElasticSearchController es_controller = new ElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String record_json = intent.getStringExtra(RECORD_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.record = gson.fromJson(record_json, Record.class);
    }

    private void setTextViews() {
        TextView title = (TextView) findViewById(R.id.recordTitle);
        TextView date = (TextView) findViewById(R.id.recordDate);
        TextView description = (TextView) findViewById(R.id.recordDescription);

        title.setText(record.getTitle());
        date.setText(record.getRdate().toString());
        description.setText(record.getDescription());
    }

    private void editRecord() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Record");
        //dialog.setMessage("");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(this);
        problem_title.setText("Record Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
        final EditText title_text = new EditText(this);
        title_text.setText(record.getTitle());
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
        date_text.setTextSize(18);
        date_text.setText(formatter.format(record.getRdate()));
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
        description_text.setText(record.getDescription());
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Deleting problem...", Toast.LENGTH_SHORT).show();
                record.setTitle(title_text.getText().toString());
                record.setDescription(description_text.getText().toString());

                new UpdateRecord().execute(record);

                LocalFileController file_controller = new LocalFileController();
                file_controller.replaceRecordInFile(record, getApplicationContext());
                setTextViews();

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

    }

        private void showDateTimePicker(final TextView date_textview) {
            //Taken from: https://stackoverflow.com/questions/2055509/datetime-picker-in-android-application
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            final Calendar date;
            final Calendar currentDate = Calendar.getInstance();
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
                            record.setRdate(new_date);
                            date_textview.setText(formatter.format(new_date));
                        }
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

        }

    private class UpdateRecord extends AsyncTask<Record, Void, Void> {

        @Override
        protected Void doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for (Record record: records) {
                es_controller.addRecord(record);
            }
            return null;
        }
    }

    public void onClickComments(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, COMMENT_REQUEST_CODE);
    }

    public void onClickPhotos(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, SlideshowActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, PHOTO_REQUEST_CODE);

    }

    public void onClickBodyLocation(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, BODYLOCATION_REQUEST_CODE);

    }

    public void onClickGeoLocation(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, GeoLocationActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, GEO_REQUEST_CODE);
    }

}
