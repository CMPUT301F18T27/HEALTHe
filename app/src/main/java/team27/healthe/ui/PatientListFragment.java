package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.User;

public class PatientListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "param2";

    // TODO: Rename and change types of parameters
    private User user;
    private PatientListAdapter adapter;

    public PatientListFragment() {
        // Required empty public constructor
    }

    public static PatientListFragment newInstance(User user) {
        Gson gson = new Gson();

        PatientListFragment fragment = new PatientListFragment();
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
            this.user = es_controller.jsonToUser(getArguments().getString(ARG_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        this.adapter = new PatientListAdapter(getActivity(), ((CareProvider) user).getPatientsArray());
        final ListView list_view = (ListView) view.findViewById(R.id.patientListView);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                User user = (User) list_view.getItemAtPosition(position);

                Gson gson = new Gson();
                Intent intent = new Intent(getContext(), ProblemActivity.class);
                intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(user));
                startActivity(intent);
            }
        });
        list_view.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) list_view.getItemAtPosition(position);
                //
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.patientListFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPatient();
            }
        });

        return view;
    }

    private void addPatient() {
        // TODO: Do not allow editing of profile while offline

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Add Patient");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        /*
        //Add title for email input
        final TextView patient_id = new TextView(getActivity());
        patient_id.setHint("Patient ID");
        layout.addView(patient_id);
        */

        // Add a TextView for email
        final EditText patient_text = new EditText(getActivity());
        patient_text.setHint("Patient ID");
        patient_text.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(patient_text); // Notice this is an add method

        dialog.setView(layout); // Again this is a set method, not add

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Adding patient...", Toast.LENGTH_SHORT).show();

                new getUserAsync().execute(patient_text.getText().toString());

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

    }

    // Async class for getting user from elastic search server
    private class getUserAsync extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... user_ids) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (String user_id: user_ids) {
                User user = es_controller.getUser(user_id);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            updateUser(user);

        }
    }

    private void updateUser(User user) {
        if(user != null) {
            if (user instanceof Patient) {
                ((CareProvider) this.user).addPatient((Patient) user);

                adapter.refresh(((CareProvider) this.user).getPatientsArray());

                LocalFileController fs_controller = new LocalFileController("user.sav");
                fs_controller.saveUserInFile(this.user, getContext());

                new UpdateUser().execute(this.user);
            }
            else {
                Toast.makeText(getContext(), "Error: Given user a Care Provider", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), "A user does not exist for given ID", Toast.LENGTH_SHORT).show();
        }
    }

    private class UpdateUser extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            ElasticSearchController es_controller = new ElasticSearchController();
            for(User user:users) {
                es_controller.addUser(user);
            }
            return null;
        }

    }

}
