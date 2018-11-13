package team27.healthe.model;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import io.searchbox.core.Index;

public class ElasticSearchController {
    private static JestDroidClient client;
    private static String node = "http://cmput301.softwareprocess.es:8080/cmput301f18t27";
    private static String test_node = "http://cmput301.softwareprocess.es:8080/cmput301f18t27test";

    public ElasticSearchController(){
        verifyClient();
    }


    public static class addUser extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            verifyClient();

            for (User user : users) {
                Gson gson = new Gson();
                String user_json = gson.toJson(user);
                Index index = new Index.Builder(user_json).index("users").type("tweet").build();

                try {
                    client.execute(index);
                }
                catch (Exception e) {
                    //Log.i("Error", "The application failed to build and send the tweets");
                }

            }
            return null;
        }
    }


    public static void verifyClient() {
    // Code from LonelyTwitter
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(test_node);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
