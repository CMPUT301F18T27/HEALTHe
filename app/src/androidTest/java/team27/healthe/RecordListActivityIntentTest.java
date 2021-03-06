package team27.healthe;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.RecordElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.ui.RecordActivity;
import team27.healthe.ui.RecordListActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

public class RecordListActivityIntentTest {

    private Patient p;
    private Problem pr;
    private Record r;

    @Rule
    public IntentsTestRule<RecordListActivity> intentsTestRule =
            new IntentsTestRule<>(RecordListActivity.class, false, false);

    @Before
    public void setup() {
        String user_id = "johnsmith";
        String email = "johnsmith@example.com";
        String number = "7801234567";
        p = new Patient(user_id, email, number);
        pr = new Problem("newproblem", new Date(), "description", p.getUserid());
        r = new Record("newrecord", new Date(), "description");

        RecordElasticSearchController res = new RecordElasticSearchController();
        ProblemElasticSearchController pres = new ProblemElasticSearchController();
        UserElasticSearchController pes = new UserElasticSearchController();

        if (pes.getUser(p.getUserid()) == null) {
            pes.addUser(p);
        }

        if (pres.getProblem(pr.getProblemID()) == null) {
            pres.addProblem(pr);
            p.addProblem(pr.getProblemID());
            pes.addUser(p);
        }
        if (res.getRecord(r.getRecordID()) == null) {
            res.addRecord(r);
            pr.addRecord(r.getRecordID());
            pres.addProblem(pr);
        }

        waitForES();
        Gson gson = new Gson();
        Intent i = new Intent();
        i.putExtra("team27.healthe.User", gson.toJson(p));
        i.putExtra("team27.healthe.PROBLEM", gson.toJson(pr));
        intentsTestRule.launchActivity(i);
    }

    @Test
    public void testRecordsList() {

        onData(anything()).inAdapterView(withId(R.id.record_list)).atPosition(0).perform(click());
        intended(hasComponent(RecordActivity.class.getName()));
    }

//    @Test
//    public void testAddRecordBodyLocationPhoto() {
//        // Attempting to add a record without a body location photo
//        onView(withId(R.id.add_record_fab)).perform(click());
//        intended(hasComponent(ViewBodyLocationsActivity.class.getName()));
//        onView(isRoot()).perform(ViewActions.pressBack());
//    }

    @After
    public void after() {
        RecordElasticSearchController res = new RecordElasticSearchController();
        ProblemElasticSearchController pres = new ProblemElasticSearchController();
        UserElasticSearchController pes = new UserElasticSearchController();

        if (pes.getUser(p.getUserid()) != null) {
            pes.removeUser(p.getUserid());
        }

        if (pres.getProblem(pr.getProblemID()) != null) {
            pres.removeProblem(pr.getProblemID());
        }
        if (res.getRecord(r.getRecordID()) != null) {
            res.removeRecord(r.getRecordID());
        }
        waitForES();
    }


    private void waitForES() {
        try {
            Thread.sleep(500); // make sure new ids are loaded properly
        }
        catch (InterruptedException e) {
        }
    }
}