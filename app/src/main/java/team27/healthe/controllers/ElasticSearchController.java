package team27.healthe.controllers;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

public abstract class ElasticSearchController {
    protected static JestDroidClient client;
    protected static String node = "http://cmput301.softwareprocess.es:8080";
    protected static String index = "cmput301f18t27";
    protected static String test_index = "cmput301f18t27test";
    protected static String user_type = "user";
    protected static String problem_type = "problem";
    protected static String record_type = "record";
    protected static String photo_type = "photo";
    protected static String body_location_type = "body_location";

    public ElasticSearchController() {
        verifyClient();
    }

        /**
         * Create connection to elastic search server
         */
        public static void verifyClient() {
            // Code from LonelyTwitter
            if (client == null) {
                DroidClientConfig.Builder builder = new DroidClientConfig.Builder(node);
                DroidClientConfig config = builder.build();

                JestClientFactory factory = new JestClientFactory();
                factory.setDroidClientConfig(config);
                client = (JestDroidClient) factory.getObject();
            }
        }
}
