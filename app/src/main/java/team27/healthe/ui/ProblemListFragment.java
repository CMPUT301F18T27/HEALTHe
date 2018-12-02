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
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
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
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.controllers.ProblemsAdapter;
import team27.healthe.model.User;

public class ProblemListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "param1";
    private static final String ARG_VIEWING = "param2";

    public static ListView listView;
    private ProblemsAdapter adapter;
    public static ArrayList<Problem> problems;
    public static LocalFileController file_controller = new LocalFileController();
    private Patient current_user;
    private User viewing_user;

    public ProblemListFragment() {
        // Required empty public constructor
    }

    public static ProblemListFragment newInstance(User current_user, User viewing_user) {
        Gson gson = new Gson();

        ProblemListFragment fragment = new ProblemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, gson.toJson(current_user));
        args.putString(ARG_VIEWING, gson.toJson(viewing_user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserElasticSearchController es_controller = new UserElasticSearchController();

        if (getArguments() != null) {
            this.current_user = (Patient)es_controller.jsonToUser(getArguments().getString(ARG_USER));
            this.viewing_user = es_controller.jsonToUser(getArguments().getString(ARG_VIEWING));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_problem_list2, container, false);

        listView = (ListView) view.findViewById(R.id.problem_list);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProblem();
            }
        });
        if (viewing_user instanceof CareProvider) {
            fab.hide();
        }

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);

                Gson gson = new Gson();
                Intent intent = new Intent(getContext(), ProblemInfoActivity.class);
                intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(viewing_user));
                intent.putExtra(ProblemInfoActivity.PROBLEM_MESSAGE,gson.toJson(problem));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);
                deleteProblem(problem);
                return true;
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        problems = new ArrayList<>();
        adapter = new ProblemsAdapter(getContext(), problems);
        listView.setAdapter(adapter);
        getProblems();
        if (problems.size() == 0) { // This check is because elastic search is down so often
            loadLocalProblems();
        }
    }

    public void addProblem() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Add Problem");
        dialog.setMessage("");


        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(getContext());
        problem_title.setText("Problem Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
        final EditText title_text = new EditText(getContext());
        title_text.setInputType(InputType.TYPE_CLASS_TEXT);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        title_text.setLayoutParams(params);
        layout.addView(title_text); // Notice this is an add method

        //Add title for date started
        final TextView date_title = new TextView(getContext());
        date_title.setText("Date Started:");
        layout.addView(date_title);

        // Add a TextView for date
        final TextView date_text = new TextView(getContext());
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
        final TextView description_title = new TextView(getContext());
        description_title.setText("Description:");
        layout.addView(description_title);

        // Add a TextView for description
        final EditText description_text = new EditText(getContext());
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Deleting problem...", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Adding problem...", Toast.LENGTH_SHORT).show();
                    Problem problem = new Problem(title, date, desc);

                    new AddProblemES().execute(problem);

                    current_user.addProblem(problem.getProblemID());
                    new UpdateUser().execute(current_user);

                    problems.add(problem);
                    adapter.refresh(problems);

                    file_controller.saveProblemInFile(problem, getContext());
                    file_controller.saveUserInFile(current_user, getContext());

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

    private void showDateTimePicker(final TextView date_textview) {
        //Taken from: https://stackoverflow.com/questions/2055509/datetime-picker-in-android-application
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        date = Calendar.getInstance();
        final Context context = getContext();
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

    public void deleteProblem(final Problem prob) {
        //final Problem problem = prob;
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Delete Problem")
                .setMessage("Are you sure you want to delete this problem?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        problems.remove(prob);
                        adapter.refresh(problems);

                        new DeleteProblem().execute(prob);

                        current_user.removeProblem(prob.getProblemID());
                        file_controller.saveUserInFile(current_user, getContext());
                        new UpdateUser().execute(current_user);

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        dialog.show();
    }

    private class AddProblemES extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();

            for (Problem problem : problems) {
                if (!es_controller.addProblem(problem)) {
                    return problem;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if(problem != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addProblem(problem, getContext());
            }
        }
    }


    private class DeleteProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();
            for(Problem problem:problems) {
                if (!es_controller.removeProblem(problem.getProblemID())) {
                    return problem;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if(problem != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.deleteProblem(problem, getContext());
            }
        }
    }

    // Async class for getting problems from elastic search server
    private class getProblemAsync extends AsyncTask<String, Void, Problem> {

        @Override
        protected Problem doInBackground(String... problem_ids) {
            ProblemElasticSearchController es_controller = new ProblemElasticSearchController();

            for (String problem_id: problem_ids) {
                return es_controller.getProblem(problem_id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if (problem != null) {
                problems.add(problem);
                adapter.refresh(problems);

                LocalFileController localFileController = new LocalFileController();
                localFileController.saveProblemInFile(problem, getContext());
            }
        }
    }

    private class UpdateUser extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... users) {
            UserElasticSearchController es_controller = new UserElasticSearchController();
            for (User user : users) {
                if(!es_controller.addUser(user)) {
                    return user;
                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user != null) {
                OfflineController offline_controller = new OfflineController();
                offline_controller.addUser(user, getContext());
            }
        }
    }

    private class PerformTasks extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            OfflineController controller = new OfflineController();
            for(Boolean bool:booleans) {
                if (bool) {
                    controller.performTasks(getContext());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getProblemsES();
        }
    }

    private void getProblemsES() {
        for (String problem_id : current_user.getProblemList()) {
            new getProblemAsync().execute(problem_id);
        }
    }

    private void getProblems() {
        if (isNetworkConnected()) {
            OfflineController controller = new OfflineController();
            if (controller.hasTasks(getContext())) {
                new PerformTasks().execute(true);
            }
            else {
                getProblemsES();
            }
        } else {
            loadLocalProblems();
        }
    }

    private void loadLocalProblems() {
        for (Problem problem : file_controller.loadProblemsFromFile(current_user, getContext())) {
            problems.add(problem);
        }
        adapter.refresh(problems);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    public void getAllGeoLocations() {
        Gson gson = new Gson();
        Intent intent = new Intent(getContext(), AllGeoLocationsActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(AllGeoLocationsActivity.PROBLEMS_MESSAGE, gson.toJson(problems));
        startActivity(intent);
    }

    public boolean validProblemInputs(String title, Date date, String desc) {
        if (title.length() > 30) {
            Toast.makeText(getContext(), "Problem title exceeds maximum of 30 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (desc.length() > 300) {
            Toast.makeText(getContext(), "Problem description exceeds maximum of 300 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
