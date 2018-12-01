package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import team27.healthe.R;
import team27.healthe.model.CareProvider;
import team27.healthe.model.ElasticSearchController;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Patient;
import team27.healthe.model.Photo;
import team27.healthe.model.Record;
import team27.healthe.model.User;

public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private User current_user;
    private boolean doubleBackToExitPressedOnce = false;
    private ProblemListFragment problem_list_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        getUserFromIntent();
        updateTabName(tabLayout);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem map_view = menu.findItem(R.id.action_map_view);
        map_view.setVisible(current_user instanceof Patient);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
        }

        else if (id == R.id.action_edit) {
            editProfile();
        }

        else if (id == R.id.action_QRcode) {
            Intent intent = new Intent(getApplicationContext(), QRCodeActivity.class);
            intent.putExtra(QRCodeActivity.USERID_MESSAGE, current_user.getUserid());
            startActivity(intent);
        }

        else if (id == R.id.action_map_view) {
            problem_list_fragment.getAllGeoLocations();
        }

        else if (id == R.id.action_test) {
            ArrayList<String> comment_list = new ArrayList<String>();
            comment_list.add("Test 123");
            comment_list.add("Suck this!");
            Record record = new Record(comment_list);
            Photo photo2 = new Photo("AWdj5Gbf8OXLMedoUnvl");
            Photo photo = new Photo("AWdjxIfb8OXLMedoUnvX");
            record.addPhoto(photo);
            record.addPhoto(photo2);

            Gson gson = new Gson();
            Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
            intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
            intent.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(record));
            startActivity(intent);
        }

        else if (id == R.id.action_body_locations) {
            editBodyLocations();
        }


        return super.onOptionsItemSelected(item);
    }

    // Double press back button to logout
    // Code from: https://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            LocalFileController file_controller = new LocalFileController();
            file_controller.clearUserFile(this);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to logout", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return ProfileFragment.newInstance(current_user);
            }
            else if (position == 1 && current_user instanceof CareProvider) {
                return  PatientListFragment.newInstance(current_user);
            }
                problem_list_fragment = ProblemListFragment.newInstance(current_user);
                return problem_list_fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    private void getUserFromIntent() {
        Intent intent = getIntent();
        ElasticSearchController es_controller = new ElasticSearchController();
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);

    }

    private void updateTabName(TabLayout tab_layout){
        if (this.current_user instanceof Patient) { tab_layout.getTabAt(1).setText("Problems");}
        else {tab_layout.getTabAt(1).setText("Patients");}
    }

    private void logout() {
        LocalFileController file_controller = new LocalFileController();
        file_controller.clearUserFile(this);
        finish();
    }

    private void editProfile() {
        // TODO: Do not allow editing of profile while offline

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Profile");
        dialog.setMessage("");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Add title for email input
        final TextView email_title = new TextView(this);
        email_title.setText("Email");
        layout.addView(email_title);

        // Add a TextView for email
        final EditText email_text = new EditText(this);
        email_text.setText(current_user.getEmail());
        email_text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((LinearLayout.LayoutParams) params).setMargins(0,0,0,64);
        email_text.setLayoutParams(params);
        layout.addView(email_text); // Notice this is an add method

        //Add title for phone number input
        final TextView phone_title = new TextView(this);
        phone_title.setText("Phone number");
        layout.addView(phone_title);

        // Add another TextView for phone number
        final EditText phone_text = new EditText(this);
        phone_text.setText(current_user.getPhone_number());
        phone_text.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.addView(phone_text); // Another add method

        dialog.setView(layout); // Again this is a set method, not add

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Updating profile...", Toast.LENGTH_SHORT).show();

                current_user.setEmail(email_text.getText().toString());
                current_user.setPhone_number(phone_text.getText().toString());

                updateElasticSearch();

                LocalFileController file_controller = new LocalFileController();
                file_controller.saveUserInFile(current_user, getApplicationContext());

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.show();

    }

    private void editBodyLocations(){
        Intent intent = new Intent(this, ViewBodyLocationsActivity.class);
        intent.putExtra("current_user", current_user.getUserid());
        startActivity(intent);
    }
    private void updateElasticSearch(){
        new UpdateUser().execute(current_user);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            List<Fragment> allFragments = getSupportFragmentManager().getFragments();
            Fragment fragment  = (ProfileFragment)allFragments.get(0);
            ((ProfileFragment) fragment).updateUser(current_user);
        }
    }

}
