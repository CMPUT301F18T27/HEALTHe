package team27.healthe.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;

import team27.healthe.R;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.User;

public class TempFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "param2";

    private User user;

    public TempFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TempFragment newInstance(User user) {
        Gson gson = new Gson();

        TempFragment fragment = new TempFragment();
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
        View view =  inflater.inflate(R.layout.fragment_temp, container, false);

        final Button button = (Button) view.findViewById(R.id.tempButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TempFragment.this.onClick(v);
            }
        });

        return view;
    }

    public void onClick(View view) {
        Gson gson = new Gson();
        Intent intent = new Intent(getContext(), ProblemActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(this.user));
        startActivity(intent);
    }
}
