package team27.healthe.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import team27.healthe.R;

public class ProfileActivity extends AppCompatActivity {
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        getUserFromIntent();

        if (savedInstanceState == null) {
            ProfileFragment profile = ProfileFragment.newInstance(user_id);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, profile).commit();
        }
    }

    private void getUserFromIntent() {
        Intent intent = getIntent();
        user_id = intent.getStringExtra(LoginActivity.USER_MESSAGE);

    }
}
