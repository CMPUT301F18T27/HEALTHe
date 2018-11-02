package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ProblemTest {
    @Test
    public void testNewProblemConstructor() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();
        records.add(new Record("Initial burn mark"));
        Problem p1 = new Problem(title, pdate, description, records);

        assertEquals(title, p1.getTitle());
        assertEquals(pdate, p1.getPdate());
        assertEquals(description, p1.getDescription());

        Problem p2 = new Problem(title, description, records);
        assertEquals(title, p2.getTitle());
        assertEquals(description, p2.getDescription());
    }

    @Test
    public void testTitle() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();
        records.add(new Record("Initial burn mark"));
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR TITLE
        assertEquals(p.getTitle(), title);
        String newTitle = "Leg Burn";
        p.setTitle(newTitle);
        assertEquals(newTitle, p.getTitle());
    }

    @Test
    public void testDate() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();
        records.add(new Record("Initial burn mark"));
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR DATE
        assertEquals(p.getPdate(), pdate);
        Date newDate = Calendar.getInstance().getTime();
        p.setPdate(newDate);
        assertEquals(newDate, p.getPdate());
    }

    @Test
    public void testDescription() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();
        records.add(new Record("Initial burn mark"));
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(p.getDescription(), description);
        String newDesc = "I have a burn on the back of my hand.";
        p.setDescription(newDesc);
        assertEquals(newDesc, p.getDescription());
    }

    @Test
    public void testRecords() {
        /* Problem must contain at least 1 record. */
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();
        records.add(new Record("Initial burn mark"));
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR RECORDS
        assertEquals(p.getRecords(), records);
        RecordList newRecords= new RecordList();
        newRecords.add(new Record("Healed burn mark"));
        p.setRecords(newRecords);
        assertEquals(newRecords, p.getRecords());
    }

    @Test
    public void testHasMinimumRecord() {
        /* Problem must contain at least 1 record. */
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();

        // Testing to make sure there is at least 1 record in a problem
        boolean thrown = false;
        try {
            Problem p = new Problem(title, pdate, description, records);
        } catch (IllegalStateException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testNumberOfRecords() {
        /* Problem must contain at least 1 record. */
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        RecordList records = new RecordList();
        records.add(new Record("Initial burn mark"));
        Problem p = new Problem(title, pdate, description, records);

        // TESTING GETTING NUMBER OF RECORDS
        assertEquals(p.getNumberOfRecords(), 1);
        records.add(new Record("Healed burn mark"));
        assertEquals(p.getNumberOfRecords(), 2);
    }


}
