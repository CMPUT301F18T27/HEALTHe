package team27.healthe.ui;

// Activity for listing patients for care provider

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
import team27.healthe.controllers.PhotoElasticSearchController;
import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.CareProvider;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.controllers.PatientListAdapter;
import team27.healthe.model.Photo;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
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
    private ArrayList<String> patient_ids;
    private boolean on_qr_code = false;

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
        UserElasticSearchController es_controller = new UserElasticSearchController();

        if (getArguments() != null) {
            this.current_user = (CareProvider) es_controller.jsonToUser(getArguments().getString(ARG_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        this.patient_ids = new ArrayList<String>(current_user.getPatients());

        adapter = new PatientListAdapter(getActivity(), patient_ids);

        final ListView list_view = (ListView) view.findViewById(R.id.patientListView);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) { // Patient clicked, open problem activity for clicked patient
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
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) { // Patient long clicked, open patient profile info
                String user_id = (String) list_view.getItemAtPosition(position);

                if (isNetworkConnected()) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra(LoginActivity.USER_MESSAGE, user_id);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "You must be online to view profile info", Toast.LENGTH_SHORT).show();
                }
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
    public void onResume() {
        super.onResume();
        if (on_qr_code == false){
            getPatients();
        }
        else{
            on_qr_code = false;
        }
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
                    on_qr_code = true;
                    Toast.makeText(getContext(), "Adding patient...", Toast.LENGTH_SHORT).show();
                    new getUserAsync().execute(barcode.rawValue);
                }
            }
        }
    }

    // Function for adding patient to care provider
    private void addPatient() {
        if (!isNetworkConnected()) {
            Toast.makeText(getContext(), "You must be online to add a patient", Toast.LENGTH_SHORT).show();
            return;
        }

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
            UserElasticSearchController es_controller = new UserElasticSearchController();

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

    // Get everything for a patient
    private void getAllForPatient(Patient patient) {
        for (String problem_id : patient.getProblemList()) {
            new GetProblem().execute(problem_id);
        }
        for (BodyLocationPhoto blp : patient.getBodyLocations()) {
            new GetPhoto().execute(blp.getBodyLocationPhotoId());
        }
    }


    // Perform tasks to update user locally and in elastic search
    private void updateUser(User user) {
        if(user != null) {
            if (user instanceof Patient) {
                getAllForPatient((Patient) user);

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

    // Async class for updating user
    private class UpdateUser extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            UserElasticSearchController es_controller = new UserElasticSearchController();
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
            UserElasticSearchController es_controller = new UserElasticSearchController();

            for (ArrayList<String> user_ids: user_ids_list) {
                ArrayList<Patient> patients = new ArrayList();
                for(String user_id:user_ids) {
                    User user = es_controller.getUser(user_id);
                    if (user != null) {
                        patients.add((Patient) user);
                    }
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

    // Get patients locally or from elastic search depending on connectivity
    private void getPatients() {
        if (isNetworkConnected()) {
            new getPatientsAsync().execute(new ArrayList<String>(current_user.getPatients()));
        } else {
            LocalFileController fs_controller = new LocalFileController();
            updatePatients(fs_controller.loadPatientsFromFile(current_user, getContext()));
        }
    }

    // Update list view with patients and save patients locally
    private void updatePatients(ArrayList<Patient> patients) {
        this.patients = patients;

        patient_ids.clear();
        for (Patient patient: patients) {
            patient_ids.add(patient.getUserid());
        }
        adapter.notifyDataSetChanged();


        LocalFileController fs_controller = new LocalFileController();
        fs_controller.savePatientsInFile(this.patients, getContext());
    }

    // Check for network connectivity
    private boolean isNetworkConnected() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    // Async class for getting problems from elastic search server
    private class GetProblem extends AsyncTask<String, Void, Problem> {

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
                LocalFileController localFileController = new LocalFileController();
                localFileController.saveProblemInFile(problem, getContext());

                for (String record_id : problem.getRecords()) {
                    new GetRecord().execute(record_id);
                }
            }
        }
    }


    // Async class for getting records from elastic search server
    private class GetRecord extends AsyncTask<String, Void, Record> {

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
                LocalFileController localFileController = new LocalFileController();
                localFileController.saveRecordInFile(record, getContext());

                for (Photo photo : record.getPhotos()) {
                    new GetPhoto().execute(photo.getId());
                }
            }
        }
    }


    // Async class for retrieving photos from elastic search
    private class GetPhoto extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... photo_ids) {
            PhotoElasticSearchController photo_controller = new PhotoElasticSearchController();

            for (String photo_id : photo_ids) {
                photo_controller.getPhoto(photo_id, getContext());
            }
            return null;
        }
    }

}
