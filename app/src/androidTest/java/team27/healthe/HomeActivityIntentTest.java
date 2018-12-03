package team27.healthe;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.model.User;
import team27.healthe.ui.HomeActivity;
import team27.healthe.ui.LoginActivity;
import team27.healthe.ui.QRCodeActivity;
import team27.healthe.ui.ViewBodyLocationsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

public class HomeActivityIntentTest {

    private Patient p;

    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule =
            new IntentsTestRule<>(LoginActivity.class, false, false);

    @Before
    public void setup() {
        intentsTestRule.launchActivity(new Intent());
        waitForES();
    }

    @Test
    public void TestQRCode() {
        Patient p = new Patient("johnsmith", "johnsmith@example.com", "7801234567");
        UserElasticSearchController es = new UserElasticSearchController();

        if (es.getUser(p.getUserid()) != null) {
            es.removeUser(p.getUserid());
        }
        es.addUser(p);
        waitForES();

        try{
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withText(containsString("Logout"))).perform(click());
            waitForES();
            onView(withId(R.id.loginIdText))
                    .perform(typeText("johnsmith"));
            onView(withId(R.id.loginButton))
                    .perform(click());
            intended(hasComponent(HomeActivity.class.getName()));
            waitForES();
        } catch (Exception e){

        }

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(containsString("QR Code"))).perform(click());
        intended(hasComponent(QRCodeActivity.class.getName()));

        es.removeUser(p.getUserid());
        waitForES();
    }

    @Test
    public void TestEditBodyLocations() {
        Patient p = new Patient("johnsmith", "johnsmith@example.com", "7801234567");
        UserElasticSearchController es = new UserElasticSearchController();

        if (es.getUser(p.getUserid()) != null) {
            es.removeUser(p.getUserid());
        }
        es.addUser(p);
        waitForES();

        try{
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withText(containsString("Logout"))).perform(click());
            waitForES();
            onView(withId(R.id.loginIdText))
                    .perform(typeText("johnsmith"));
            onView(withId(R.id.loginButton))
                    .perform(click());
            intended(hasComponent(HomeActivity.class.getName()));
            waitForES();
        } catch (Exception e){

        }

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(containsString("BodyLocations"))).perform(click());
        intended(hasComponent(ViewBodyLocationsActivity.class.getName()));

        es.removeUser(p.getUserid());
        waitForES();
    }

    @Test
    public void TestSearch() {
//        onView(withId(R.id.app_bar_search)).perform(click());
//        intended(hasComponent(SearchResultsActivity.class.getName()));
    }


    private void waitForES() {
        try {
            Thread.sleep(500); // make sure new ids are loaded properly
        }
        catch (InterruptedException e) {
        }
    }
}