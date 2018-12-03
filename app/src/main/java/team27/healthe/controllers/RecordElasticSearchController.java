package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.Record;

/**
 * Class for add/edit/delete operations for Record objects from the Elasticsearch server
 * @author [fill in]
 */
public class RecordElasticSearchController extends ElasticSearchController{

    /**
     * Add record to elastic search database using record id as the id in elastic search
     * @param r (Record Class)
     */
    public static boolean addRecord(Record r) {
        verifyClient();

        Gson gson = new Gson();
        String record_json = gson.toJson(r);

        Index index = new Index.Builder(record_json).index(test_index).type(record_type).id(r.getRecordID()).build();

        try {
            DocumentResult result = client.execute(index);
            return result.isSucceeded();
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
            return false;
        }
    }

    /**
     * Get the record from a given record id
     * @param record_id (Integer)
     * @return Record (Class)
     */
    public static Record getRecord(String record_id) {
        verifyClient();
        Get get = new Get.Builder(test_index, record_id).type(record_type).build();

        try {
            JestResult result = client.execute(get);

            Gson gson = new Gson();
            Record record = gson.fromJson(result.getSourceAsString(), Record.class);
            return record;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a specified record for the problem (corresponding to user_id)
     * @param record_id (String)
     */
    public static boolean removeRecord(String record_id){
        verifyClient();
        try{
            DocumentResult result = client.execute(new Delete.Builder(record_id)
                    .index(test_index)
                    .type(record_type)
                    .build());
            return result.isSucceeded();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
