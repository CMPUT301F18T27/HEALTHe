package team27.healthe.ui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_USER = "param2";
    private static final String ARG_USER_TYPE = "param3";

    // TODO: Rename and change types of parameters
    private User user;
    private String userid;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String profile_user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, profile_user_id);
        fragment.setArguments(args);
        return fragment;
    }


    public static ProfileFragment newInstance(User user) {
        Gson gson = new Gson();

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, gson.toJson(user));
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ElasticSearchController es_controller = new ElasticSearchController();

        if (getArguments().containsKey(ARG_USER_ID)) {
            this.userid = getArguments().getString(ARG_USER_ID);
        }
        else if (getArguments().containsKey(ARG_USER)) {
            this.user = es_controller.jsonToUser(getArguments().getString(ARG_USER));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (this.user == null) {
            new getUserAsync().execute(this.userid);
        }
        else {
            updateText(view);
        }

        return view;
    }


    public void updateText(View view){
        TextView userid_textview = (TextView) view.findViewById(R.id.profileIdText);
        TextView email_textview = (TextView) view.findViewById(R.id.profileEmailText);
        TextView number_textview = (TextView) view.findViewById(R.id.profilePhoneText);
        TextView type_textview = (TextView) view.findViewById(R.id.profileAccountType);

        userid_textview.setText(this.user.getUserid());
        email_textview.setText(this.user.getEmail());
        number_textview.setText(this.user.getPhone_number());

        if (user instanceof Patient) {
            type_textview.setText("Patient");
        } else {
            type_textview.setText("Care Provider");
        }

        ImageView image_email = (ImageView) view.findViewById(R.id.imageView6);
        ImageView image_phone = (ImageView) view.findViewById(R.id.imageView5);

        image_email.setVisibility(View.VISIBLE);
        image_phone.setVisibility(View.VISIBLE);
    }


    public void updateUser(User user) {
        this.user = user;
        updateText(getView());
    }


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


}
