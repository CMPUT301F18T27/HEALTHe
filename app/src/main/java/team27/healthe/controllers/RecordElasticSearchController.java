package team27.healthe.controllers;

import android.util.Log;

import com.google.gson.Gson;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import team27.healthe.model.Record;

public class RecordElasticSearchController extends ElasticSearchController{

    /**
     * Add record to elastic search database using record id as the id in elastic search
     * @param r (Record Class)
     */
    public static Record addRecord(Record r) {
        verifyClient();

        Gson gson = new Gson();
        String record_json = gson.toJson(r);

        Index index;
        if (r.getRecordID().equals("")) {
            index = new Index.Builder(record_json).index(test_index).type(record_type).build();
        }
        else {
            index = new Index.Builder(record_json).index(test_index).type(record_type).id(r.getRecordID()).build();
        }

        try {
            DocumentResult result = client.execute(index);
            r.setRecordID(result.getId());
            return r;
        }
        catch (Exception e) {
            Log.i("Error", e.toString());
            return null;
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
    public static void removeRecord(String record_id){
        verifyClient();
        try{
            client.execute(new Delete.Builder(record_id)
                    .index("record")
                    .type("record")
                    .build());
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
