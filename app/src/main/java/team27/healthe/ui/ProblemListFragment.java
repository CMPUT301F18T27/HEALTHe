package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import team27.healthe.model.ProblemsAdapter;
import team27.healthe.model.User;

public class ProblemListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "param1";

    // TODO: Rename and change types of parameters
    public static ListView listView;
    private ProblemsAdapter adapter;
    public static ArrayList<Problem> problems;
    public static String FILENAME = "problems.sav";
    public static LocalFileController file_controller = new LocalFileController();
    private Patient current_user;

    public ProblemListFragment() {
        // Required empty public constructor
    }

    public static ProblemListFragment newInstance(User user) {
        Gson gson = new Gson();

        ProblemListFragment fragment = new ProblemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, gson.toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ElasticSearchController es_controller = new ElasticSearchController();

        if (getArguments() != null) {
            this.current_user = (Patient)es_controller.jsonToUser(getArguments().getString(ARG_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_problem_list2, container, false);

        listView = (ListView) view.findViewById(R.id.problem_list);

        problems = new ArrayList<>();
        adapter = new ProblemsAdapter(getContext(), problems);
        listView.setAdapter(adapter);

        getProblems();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProblem();
            }
        });

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);

                Gson gson = new Gson();
                Intent intent = new Intent(getContext(), ProblemInfoActivity.class);
                intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
                intent.putExtra(ProblemInfoActivity.PROBLEM_MESSAGE,gson.toJson(problem));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Problem problem = (Problem) listView.getItemAtPosition(position);
                editProblem(problem);
                return true;
            }
        });
        return view;
    }

    public void addProblem() {
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
        final EditText date_text = new EditText(getContext());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        date_text.setInputType(InputType.TYPE_CLASS_DATETIME);
        date_text.setText(formatter.format(new Date()));
        layout.addView(date_text); // Another add method

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
                Toast.makeText(getContext(), "Adding problem...", Toast.LENGTH_SHORT).show();

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
                Problem problem = new Problem(title, date, desc);

                problems.add(problem);
                adapter.refresh(problems);

                file_controller.saveProblemInFile(problem, getContext());

                new AddProblemES().execute(problem);

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();
    }

    public void editProblem(Problem prob) {
        final Problem problem = prob;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Edit Problem");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for problem
        final TextView problem_title = new TextView(getContext());
        problem_title.setText("Problem Title:");
        layout.addView(problem_title);

        // Add a TextView for problem title
        final EditText title_text = new EditText(getContext());
        title_text.setText(problem.getTitle());
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
        final EditText date_text = new EditText(getContext());
        date_text.setText(problem.getPdateAsString());
        date_text.setInputType(InputType.TYPE_CLASS_DATETIME);
        layout.addView(date_text); // Another add method

        //Add title for problem description
        final TextView description_title = new TextView(getContext());
        description_title.setText("Description:");
        layout.addView(description_title);

        // Add a TextView for description
        final EditText description_text = new EditText(getContext());
        description_text.setText(problem.getDescription());
        description_text.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        layout.addView(description_text); // Another add method

        dialog.setView(layout);

        problems.remove(prob);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Updating problem...", Toast.LENGTH_SHORT).show();

                problem.setTitle(title_text.getText().toString());
                problem.setPdateAsDateObj(date_text.getText().toString());
                problem.setDescription(description_text.getText().toString());

                problems.add(problem);
                adapter.refresh(problems);

                file_controller.saveProblemsInFile(problems, getContext());

                new UpdateProblem().execute(problem);

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();
    }

    private class AddProblemES extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (Problem problem : problems) {

                Problem new_problem = es_controller.addProblem(problem);

                if (new_problem == null) {
                    return problem;
                } else {
                    return new_problem;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if (!problem.getProblemID().equals("")) {
                current_user.addProblem(problem.getProblemID());
                new UpdateUser().execute(current_user);
            }
        }
    }

    private class UpdateProblem extends AsyncTask<Problem, Void, Problem> {

        @Override
        protected Problem doInBackground(Problem... problems) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for(Problem problem:problems) {
                if (!problem.getProblemID().equals("")) {
                    es_controller.addProblem(problem);
                    return problem;
                } else {
                    return  es_controller.addProblem(problem);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Problem problem) {
            super.onPostExecute(problem);
            if (!problem.getProblemID().equals("")) {
                if (!current_user.hasProblem(problem.getProblemID())) {
                    current_user.addProblem(problem.getProblemID());
                    new UpdateUser().execute(current_user);
                }
            }
        }
    }

    // Async class for getting problems from elastic search server
    private class getProblemAsync extends AsyncTask<String, Void, Problem> {

        @Override
        protected Problem doInBackground(String... problem_ids) {
            ElasticSearchController es_controller = new ElasticSearchController();

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


                //TODO: fix saving
                LocalFileController localFileController = new LocalFileController();
                localFileController.saveProblemsInFile(problems, getContext());
            }
        }
    }

    private class UpdateUser extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for (User user : users) {
                es_controller.addUser(user);
            }
            return null;
        }
    }

    private void getProblemsES() {
        for (String problem_id : current_user.getProblemList()) {
            new getProblemAsync().execute(problem_id);
        }
    }

    private void getProblems() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            getProblemsES();
        } else {
            for (Problem problem : file_controller.loadProblemsFromFile(getContext())) {
                problems.add(problem);
            }
            adapter.refresh(problems);
        }
    }


}
