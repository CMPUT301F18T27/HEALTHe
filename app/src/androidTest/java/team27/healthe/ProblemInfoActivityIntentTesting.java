package team27.healthe;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import team27.healthe.controllers.ProblemElasticSearchController;
import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.ui.HomeActivity;
import team27.healthe.ui.LoginActivity;
import team27.healthe.ui.ProblemInfoActivity;
import team27.healthe.ui.ProblemListFragment;
import team27.healthe.ui.QRCodeActivity;
import team27.healthe.ui.RecordListActivity;
import team27.healthe.ui.SearchResultsActivity;
import team27.healthe.ui.SignupActivity;
import team27.healthe.ui.ViewBodyLocationsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

public class ProblemInfoActivityIntentTesting {

    private Patient p;
    private Problem pr;

    @Rule
    public IntentsTestRule<ProblemInfoActivity> intentsTestRule =
            new IntentsTestRule<>(ProblemInfoActivity.class, false, false);

    @Before
    public void setup() {
        String user_id = "johnsmith";
        String email = "johnsmith@example.com";
        String number = "7801234567";
        p = new Patient(user_id, email, number);

        pr = new Problem("newproblem", new Date(), "description");

        UserElasticSearchController pes = new UserElasticSearchController();
        if (pes.getUser(p.getUserid()) == null) {
            pes.addUser(p);
        }

        ProblemElasticSearchController pres = new ProblemElasticSearchController();
        if (pres.getProblem(pr.getProblemID()) == null) {
            pres.addProblem(pr);
            p.addProblem(pr.getProblemID());
            pes.addUser(p);
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
        onView(withId(R.id.button7))
                .perform(click());
        intended(hasComponent(RecordListActivity.class.getName()));
    }

    @After
    public void after() {
        UserElasticSearchController pes = new UserElasticSearchController();
        if (pes.getUser(p.getUserid()) != null) {
            pes.removeUser(p.getUserid());
        }

        ProblemElasticSearchController pres = new ProblemElasticSearchController();
        if (pres.getProblem(pr.getProblemID()) != null) {
            pres.removeProblem(pr.getProblemID());
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