package team27.healthe.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.searchbox.core.SearchResult;
import team27.healthe.R;
import team27.healthe.controllers.ElasticSearchSearchController;
import team27.healthe.controllers.OfflineController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.controllers.LocalFileController;
import team27.healthe.model.Patient;
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
    public void onResume() {
        super.onResume();
        loadLocalUser();
        checkTasks();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem search_item = (MenuItem) menu.findItem(R.id.app_bar_search);
        SearchView search_view = (SearchView) search_item.getActionView();
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String search_string) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Search")
                        .setMessage("What would you like to search for?")
                        .setPositiveButton("General", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new SearchGeneral().execute(search_string);
                            }
                        })
                        .setNegativeButton("Body Location", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNeutralButton("Geo Location", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] string_inputs = search_string.split(" ");
                                Map<String, Double> inputs = new HashMap();
                                try {
                                    Double lat = Double.parseDouble(string_inputs[0]);
                                    Double lon = Double.parseDouble(string_inputs[1]);
                                    inputs.put("lat", lat);
                                    inputs.put("lon", lon);
                                    inputs.put("distance", 100.0);

                                    if (string_inputs.length >= 3) {
                                        Double distance = Double.parseDouble(string_inputs[2]);
                                        inputs.put("distance", distance);
                                    }

                                    new SearchGeoLocation().execute(inputs);

                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Please input a latitude, longitude and optional search radius(km) separated by spaces", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem map_view = menu.findItem(R.id.action_map_view);
        map_view.setVisible(current_user instanceof Patient);
        MenuItem body_location = menu.findItem(R.id.action_body_locations);
        body_location.setVisible(current_user instanceof Patient);
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
                problem_list_fragment = ProblemListFragment.newInstance(current_user, current_user);
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
        UserElasticSearchController es_controller = new UserElasticSearchController();
        String user_json = intent.getStringExtra(LoginActivity.USER_MESSAGE);
        this.current_user = es_controller.jsonToUser(user_json);

    }

    private void updateTabName(TabLayout tab_layout){
        if (this.current_user instanceof Patient) { tab_layout.getTabAt(1).setText("Problems");}
        else {tab_layout.getTabAt(1).setText("Patients");}
    }

    private void logout() {
        LocalFileController file_controller = new LocalFileController();
        file_controller.deleteAllFiles(this);
        finish();
    }

    private void editProfile() {
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
        phone_text.setText(current_user.getPhoneNumber());
        phone_text.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.addView(phone_text); // Another add method

        dialog.setView(layout); // Again this is a set method, not add

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Updating profile...", Toast.LENGTH_SHORT).show();

                current_user.setEmail(email_text.getText().toString());
                current_user.setPhoneNumber(phone_text.getText().toString());

                List<Fragment> allFragments = getSupportFragmentManager().getFragments();
                Fragment fragment  = (ProfileFragment)allFragments.get(0);
                ((ProfileFragment) fragment).updateUser(current_user);

                new UpdateUser().execute(current_user);

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
                offline_controller.addUser(user, getApplicationContext());
            }
        }
    }

    private class SearchGeneral extends AsyncTask<String, Void, SearchResult> {

        @Override
        protected SearchResult doInBackground(String... terms) {
            ElasticSearchSearchController es_controller = new ElasticSearchSearchController();
            for(String term:terms) {
                return es_controller.searchGeneral(term);
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchResult search) {
            super.onPostExecute(search);
            if (search != null) {
                if (search.isSucceeded()) {
                    ArrayList<String> hits = new ArrayList<>();
                    List<String> temp_hits = search.getSourceAsStringList();
                    hits.addAll(temp_hits);
                    startSearchActivity(hits);
                }
            }
        }
    }

    private class SearchGeoLocation extends AsyncTask<Map<String, Double>, Void, SearchResult> {

        @Override
        protected SearchResult doInBackground(Map<String, Double>... input_maps) {
            ElasticSearchSearchController es_controller = new ElasticSearchSearchController();
            for(Map<String, Double> inputs : input_maps) {
                return es_controller.searchGeoLocation(inputs.get("lat"), inputs.get("lon"), inputs.get("distance"));
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchResult search) {
            super.onPostExecute(search);
            if (search != null) {
                if (search.isSucceeded()) {
                    ArrayList<String> hits = new ArrayList<>();
                    List<String> temp_hits = search.getSourceAsStringList();
                    hits.addAll(temp_hits);
                    startSearchActivity(hits);
                }
            }
        }
    }

    private class PerformTasks extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            OfflineController controller = new OfflineController();
            for(Boolean bool:booleans) {
                if (bool) {
                    controller.performTasks(getApplicationContext());
                }
            }
            return null;
        }
    }

    private void startSearchActivity(ArrayList<String> hits) {
        Gson gson = new Gson();
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(current_user));
        intent.putExtra(SearchResultsActivity.SEARCH_MESSAGE, hits);
        startActivity(intent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager conn_mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network_info = conn_mgr.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            return true;
        }
        return false;
    }

    private void loadLocalUser() {
        LocalFileController controller = new LocalFileController();
        this.current_user = controller.loadUserFromFile(this);
    }

    private void checkTasks() {
        if (isNetworkConnected()) {
            OfflineController controller = new OfflineController();
            if (controller.hasTasks(this)) {
                new PerformTasks().execute(true);
            }
        }
    }

}
