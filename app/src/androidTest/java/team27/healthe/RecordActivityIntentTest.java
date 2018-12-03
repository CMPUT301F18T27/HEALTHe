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
import team27.healthe.model.GeoLocation;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;
import team27.healthe.ui.CommentActivity;
import team27.healthe.ui.GeoLocationActivity;
import team27.healthe.ui.LoginActivity;
import team27.healthe.ui.RecordActivity;
import team27.healthe.ui.RecordListActivity;
import team27.healthe.ui.SlideshowActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

public class RecordActivityIntentTest {

    private Patient p;
    private Problem pr;
    private Record r;

    @Rule
    public IntentsTestRule<RecordActivity> intentsTestRule =
            new IntentsTestRule<>(RecordActivity.class, false, false);

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
        i.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(p));
        i.putExtra(RecordActivity.RECORD_MESSAGE, gson.toJson(r));
        intentsTestRule.launchActivity(i);
    }

    @Test
    public void testViewComments() {
        onView(withId(R.id.button23)).perform(click());
        intended(hasComponent(CommentActivity.class.getName()));
    }

    @Test
    public void testViewPhotos() {
        onView(withId(R.id.button22)).perform(click());
        intended(hasComponent(SlideshowActivity.class.getName()));
    }

    @Test
    public void testViewBodyLocations() {
        onView(withId(R.id.button24)).perform(click());
        intended(hasComponent(CommentActivity.class.getName()));
    }

    @Test
    public void testViewGeoLocations() {
        onView(withId(R.id.button25)).perform(click());
        intended(hasComponent(GeoLocationActivity.class.getName()));
    }

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