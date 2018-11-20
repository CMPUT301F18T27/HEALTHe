package team27.healthe;

import org.junit.Test;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;


import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;

@RunWith(AndroidJUnit4.class)
public class ElasticSearchTest {
    @Test
    public void addProblemESTest(){
        String title1 = "Hand Cold";
        Date pdate1 = Calendar.getInstance().getTime();
        String description1 = "I have a cold on the palm of my hand.";
        Collection<Integer> records1 = new ArrayList<Integer>();
        records1.add(new Record("Initial cold mark").getRecordID());
        Problem p1 = new Problem(title1, pdate1, description1, records1);

        ElasticSearchController.addProblem(p1, "12345678");
        Integer p1_id = p1.getProblemID();
        System.out.println("Int prob id: "+p1_id);
        Problem p2 = ElasticSearchController.getProblem(p1_id, "12345678");
        assertTrue(p2 != null);
        assertTrue(p1.getTitle().equals(p2.getTitle())); //not comprehensive -- need comparable to be implemented
    }
}
