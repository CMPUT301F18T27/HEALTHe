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
    public void testNewProblemContructor() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Problem p1 = new Problem(title, pdate, description);

        assertEquals(title, p1.getTitle());
        assertEquals(pdate, p1.getPdate());
        assertEquals(description, p1.getDescription());

        Problem p2 = new Problem(title, description);
        assertEquals(title, p2.getTitle());
        assertEquals(description, p2.getDescription());
    }

    @Test
    public void testTitle() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Problem p = new Problem(title, pdate, description);

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
        Problem p = new Problem(title, pdate, description);

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
        Problem p = new Problem(title, pdate, description);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(p.getDescription(), description);
        String newDesc = "I have a burn on the back of my hand.";
        p.setDescription(newDesc);
        assertEquals(newDesc, p.getDescription());
    }


}
