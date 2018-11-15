package team27.healthe.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
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
    private static final String ARG_CURRENT_USER = "param1";
    private static final String ARG_PROFILE_USER_ID = "param2";
    private static final String ARG_USER_TYPE = "param3";

    // TODO: Rename and change types of parameters
    private User current_user;
    private String userid;

    //private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(User current_user, String profile_user_id, String user_type) {
        Gson gson = new Gson();

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_USER, gson.toJson(current_user));
        args.putString(ARG_PROFILE_USER_ID, profile_user_id);
        args.putString(ARG_USER_TYPE, user_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        if (getArguments() != null) {
            String user_json = getArguments().getString(ARG_CURRENT_USER);
            String user_type = getArguments().getString(ARG_USER_TYPE);
            this.userid = getArguments().getString(ARG_PROFILE_USER_ID);

            if (user_type.equals("patient")) {
                this.current_user = gson.fromJson(user_json, Patient.class);
            } else {
                this.current_user = gson.fromJson(user_json, CareProvider.class);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setTextViews(view);
        return view;
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

*/
    private void setTextViews(View view) {
        TextView userid_textview = (TextView) view.findViewById(R.id.profileIdText);
        TextView email_textview = (TextView) view.findViewById(R.id.profileEmailText);
        TextView number_textview = (TextView) view.findViewById(R.id.profilePhoneText);

        userid_textview.setText(this.current_user.getUserid());
        email_textview.setText(this.current_user.getEmail());
        number_textview.setText(this.current_user.getPhone_number());
    }

    public void updateText(User user){
        this.current_user = user;
        setTextViews(getView());
    }


}
