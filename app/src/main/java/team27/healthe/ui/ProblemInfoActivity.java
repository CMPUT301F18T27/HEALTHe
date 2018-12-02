package team27.healthe.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import team27.healthe.R;
import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Problem;
import team27.healthe.model.User;

public class ProblemInfoActivity extends AppCompatActivity {
    public static final String PROBLEM_MESSAGE = "team27.healthe.PROBLEM";

    private User current_user;
    private Problem problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_info);

        getExtras(getIntent());
        setTextViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_problem_info, menu);
        MenuItem edit_item = menu.findItem(R.id.action_problem_edit);
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
        if (id == R.id.action_problem_edit) {
            editProblem();
        }

        return super.onOptionsItemSelected(item);
    }

    // Get user and problem from intent
    private void getExtras(Intent intent) {
        Gson gson = new Gson();
        UserElasticSearchController es_controller = new UserElasticSearchController();

        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        String problem_json = intent.getStringExtra(PROBLEM_MESSAGE);

        this.current_user = es_controller.jsonToUser(user_json);
        this.problem = gson.fromJson(problem_json, Problem.class);
    }

    private void setTextViews() {
        TextView title = (TextView) findViewById(R.id.recordTitle);
        TextView date = (TextView) findViewById(R.id.recordDate);
        TextView description = (TextView) findViewById(R.id.problemDescription);

        title.setText(problem.getTitle());
        date.setText(problem.getPdateAsString());
        description.setText(problem.getDescription());
    }

    public void onSelectRecords(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, RecordListActivity.class);
        intent.putExtra(PROBLEM_MESSAGE, gson.toJson(problem));
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        startActivity(intent);
    }

    private void editProblem() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Problem");
        //dialog.setMessage("");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(this);
        problem_title.setText("Problem Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
        final EditText title_text = new EditText(this);
        title_text.setText(problem.getTitle());
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
        date_text.setText(problem.getPdateAsString());
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
        description_text.setText(problem.getDescription());
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Deleting problem...", Toast.LENGTH_SHORT).show();
                problem.setTitle(title_text.getText().toString());
                problem.setDescription(description_text.getText().toString());


                new UpdateProblem().execute(problem);

                LocalFileController file_controller = new LocalFileController();
                file_controller.replaceProblemInFile(problem, getApplicationContext());
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
                        problem.setPdate(new_date);
                        date_textview.setText(problem.getPdateAsString());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

    }

    private class UpdateProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();
            for(Problem problem:problems) {
                es_controller.addProblem(problem);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
        }
    }

}
