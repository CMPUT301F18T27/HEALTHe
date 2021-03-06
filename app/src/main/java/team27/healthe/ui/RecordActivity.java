package team27.healthe.ui;

// Activity for displaying record info

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.controllers.OfflineController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class RecordActivity extends AppCompatActivity implements SensorEventListener {
    public static final String RECORD_MESSAGE = "team27.healthe.RECORD";
    private static final Integer GEO_REQUEST_CODE = 4;
    private static final Integer COMMENT_REQUEST_CODE = 5;
    private static final Integer PHOTO_REQUEST_CODE = 6;
    private static final Integer BODYLOCATION_REQUEST_CODE = 7;
    private User current_user;
    private Record record;
    private SensorManager sensor_manager;
    private Sensor heart_sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getItems(getIntent());
        setTextViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (current_user instanceof Patient) {
            if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, 121);
            } else {
                setButtons();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getRecord();
    }

    @Override
    public void onPause() {
        try {
            sensor_manager.unregisterListener(this);
        } catch (Exception e) {
            // There was no listener to unregister
        }
        super.onPause();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // Record has been passed back from comments, geo location, photo or body location activity
        if (requestCode == GEO_REQUEST_CODE || requestCode == COMMENT_REQUEST_CODE || requestCode == PHOTO_REQUEST_CODE || requestCode == BODYLOCATION_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Gson gson = new Gson();
                String record_json = data.getStringExtra(RECORD_MESSAGE);
                this.record = gson.fromJson(record_json, Record.class);
            }
        }
    }

    // If permission granted for body sensors update buttons
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 121) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setButtons();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Set get heart rate sensor button as visible if phone has a heart rate sensor
    private void setButtons() {
        Button heart_button = findViewById(R.id.buttonHeartRate);
        this.sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        heart_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (heart_sensor != null) {
            heart_button.setVisibility(heart_button.VISIBLE);
        }
    }

    // Get items passed in intent
    private void getItems(Intent intent) {
        UserElasticSearchController es_controller = new UserElasticSearchController();
        Gson gson = new Gson();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String record_json = intent.getStringExtra(RECORD_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.record = gson.fromJson(record_json, Record.class);
    }

    // Set text views with record info
    private void setTextViews() {
        TextView title = (TextView) findViewById(R.id.recordTitle);
        TextView date = (TextView) findViewById(R.id.recordDate);
        TextView description = (TextView) findViewById(R.id.recordDescription);
        TextView heart_rate_title = (TextView) findViewById(R.id.textHeartTitle);
        TextView heart_rate = (TextView) findViewById(R.id.textHeartRate);

        title.setText(record.getTitle());
        date.setText(record.getRdate().toString());
        description.setText(record.getDescription());
        if (record.getHeartRate() != null) {
            heart_rate.setText(record.getHeartRate());
            heart_rate.setVisibility(heart_rate.VISIBLE);
            heart_rate_title.setVisibility(heart_rate_title.VISIBLE);
        }
    }

    // Show dialog for editing record info
    private void editRecord() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Record");
        dialog.setMessage("");


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
        ViewGroup.LayoutParams date_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) date_params).setMargins(0,0,0,64);
        date_text.setTextSize(18);
        date_text.setText(formatter.format(record.getRdate()));
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
                file_controller.saveRecordInFile(record, getApplicationContext());
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

    // Show date time picker for selecting dat
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

    // Get record from local files
    private void getLocalRecord() {
        LocalFileController file_controller = new LocalFileController();
        Record temp_record = file_controller.loadRecordFromFile(record.getRecordID(), getApplicationContext());
        if (temp_record != null) {
            this.record = temp_record;
            setTextViews();
        }
    }

    // Decide how to get record
    private void getRecord() {
        if (isNetworkConnected()) {
            OfflineController controller = new OfflineController();
            if (controller.hasTasks(this)) {
                new PerformTasks().execute(true);
            }
            else {
                new getRecordAsync().execute(record.getRecordID());
            }
        } else {
            getLocalRecord();
        }
    }

    // If network connected
    private boolean isNetworkConnected() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    // On heart rate sensor value read
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView heart_rate_text = findViewById(R.id.textHeartRate);

        if (sensorEvent.values[0] != 0) { // If heart rate registered
            Toast.makeText(getApplicationContext(), "Heart rate registered", Toast.LENGTH_SHORT).show();
            heart_rate_text.setText(Math.round(sensorEvent.values[0]) + " bpm");
            sensor_manager.unregisterListener(this, heart_sensor);
            record.setHeartRate(Math.round(sensorEvent.values[0]) + " bpm");
            new UpdateRecord().execute(record);
            setTextViews();
        }

    }

    // If accuracy changed on heart rate sensor vibrate phone
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(100);
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
        protected void onPostExecute(Record es_record) {
            super.onPostExecute(es_record);
            if (es_record != null) {
                record = es_record;
                setTextViews();

                LocalFileController localFileController = new LocalFileController();
                localFileController.saveRecordInFile(record, getApplicationContext());
            }
        }
    }

    // Async class for updating record in elastic search server
    private class UpdateRecord extends AsyncTask<Record, Void, Record> {

        @Override
        protected Record doInBackground(Record... records) {
            RecordElasticSearchController es_controller = new RecordElasticSearchController();
            for (Record record: records) {
                if(!es_controller.addRecord(record)) {
                    return record;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Record record){
            super.onPostExecute(record);
            if (record != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addRecord(record, getApplicationContext());
            }
        }
    }

    // Async class for performing offline controller tasks
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
    }

    // onCLick for comment button
    public void onClickComments(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, COMMENT_REQUEST_CODE);
    }

    // onClick for photo button
    public void onClickPhotos(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, SlideshowActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, PHOTO_REQUEST_CODE);

    }

    // onClick for body location button
    public void onClickBodyLocation(View view){
        Gson gson = new Gson();
        Intent intent = new Intent(this, SelectBodyLocationActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, BODYLOCATION_REQUEST_CODE);

    }

    // onClick for geo location button
    public void onClickGeoLocation(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, GeoLocationActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(RECORD_MESSAGE, gson.toJson(record));
        startActivityForResult(intent, GEO_REQUEST_CODE);
    }

    // onClick for heart rate button
    public void onClickHeartButton(View view) {
        if (heart_sensor != null) {
            Toast.makeText(getApplicationContext(), "Place and hold your finger on the heart rate sensor, it may take a while to get a reading.", Toast.LENGTH_LONG).show();
            sensor_manager.registerListener(this, heart_sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

}
