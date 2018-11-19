package team27.healthe;
import org.junit.Before;
import org.junit.Test;

import team27.healthe.model.*;

public class RecordListTest {
    RecordList test_list;
    String tag1 = "tag1";
    Record r1, r2;
    @Before
    public void testSetup(){
        test_list = new RecordList();
        r1 = new Record();
        r1.setTitle("tag1 tag2");

        r2 = new Record();
        r2.setTitle("tag2 tag 3");

        test_list.add(r1);
        test_list.add(r2);
    }

    @Test
    public void searchRecordsTest(){
        RecordList results = test_list.searchRecords(tag1);
        assert(results.getObjects().contains(r1));
    }

}