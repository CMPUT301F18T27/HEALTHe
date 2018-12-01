package team27.healthe.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import java.util.ArrayList;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.PatientListAdapter;
import team27.healthe.model.User;

public class PatientListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "param2";
    private static final String FILENAME = "patients.sav";
    private static final Integer BARCODE_READER_ACTIVITY_REQUEST = 27;

    // TODO: Rename and change types of parameters
    private CareProvider current_user;
    private PatientListAdapter adapter;
    private ArrayList<Patient> patients;

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
            this.current_user = (CareProvider) es_controller.jsonToUser(getArguments().getString(ARG_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        adapter = new PatientListAdapter(getActivity(), new ArrayList<String>(current_user.getPatients()));

        getPatients();

        final ListView list_view = (ListView) view.findViewById(R.id.patientListView);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                User patient = patients.get(position);

                Gson gson = new Gson();
                Intent intent = new Intent(getContext(), ProblemActivity.class);
                intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(patient));
                intent.putExtra(ProblemActivity.VIEWING_USER_MESSAGE, gson.toJson(current_user));
                startActivity(intent);
            }
        });
        list_view.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String user_id = (String) list_view.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra(LoginActivity.USER_MESSAGE, user_id);
                startActivity(intent);
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

        FloatingActionButton qrfab = (FloatingActionButton) view.findViewById(R.id.qrFab);
        qrfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // https://github.com/avaneeshkumarmaurya/Barcode-Reader
                Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(getContext(), true, false);
                startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(getContext(), "Error scanning QR code", Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
                Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);

                if (current_user.hasPatient(barcode.rawValue)) {
                    Toast.makeText(getContext(), "Patient already assigned to you", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Adding patient...", Toast.LENGTH_SHORT).show();
                    new getUserAsync().execute(barcode.rawValue);
                }
            }
        }
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
        layout.addView(patient_text);

        dialog.setView(layout);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (current_user.hasPatient(patient_text.getText().toString())) {
                    Toast.makeText(getContext(), "Patient already assigned to you", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Adding patient...", Toast.LENGTH_SHORT).show();
                    new getUserAsync().execute(patient_text.getText().toString());
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
                current_user.addPatient(user.getUserid());
                adapter.refresh(new ArrayList<String>(current_user.getPatients()));
                this.patients.add((Patient) user);

                new UpdateUser().execute(current_user);

                LocalFileController fs_controller = new LocalFileController();
                fs_controller.saveUserInFile(current_user, getContext());

                fs_controller = new LocalFileController();
                fs_controller.savePatientsInFile(this.patients, getContext());
            }
            else {
                Toast.makeText(getContext(), "User must be a patient", Toast.LENGTH_SHORT).show();
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


    // Async class for getting patients from elastic search server
    private class getPatientsAsync extends AsyncTask<ArrayList<String>, Void, ArrayList<Patient>> {

        @Override
        protected ArrayList<Patient> doInBackground(ArrayList<String>... user_ids_list) {
            ElasticSearchController es_controller = new ElasticSearchController();

            for (ArrayList<String> user_ids: user_ids_list) {
                ArrayList<Patient> patients = new ArrayList();
                for(String user_id:user_ids) {
                    User user = es_controller.getUser(user_id);
                    patients.add((Patient) user);
                }
                return patients;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Patient> patients) {
            super.onPostExecute(patients);
            updatePatients(patients);
        }
    }

    private void getPatients() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            new getPatientsAsync().execute(new ArrayList<String>(current_user.getPatients()));
        } else {
            LocalFileController fs_controller = new LocalFileController();
            this.patients = fs_controller.loadPatientsFromFile(getContext());
        }
    }

    private void updatePatients(ArrayList<Patient> patients) {
        this.patients = patients;

        LocalFileController fs_controller = new LocalFileController();
        fs_controller.savePatientsInFile(this.patients, getContext());
    }

}
