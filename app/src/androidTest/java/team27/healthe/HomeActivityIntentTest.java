package team27.healthe;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.ui.HomeActivity;
import team27.healthe.ui.LoginActivity;
import team27.healthe.ui.QRCodeActivity;
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

public class HomeActivityIntentTest {

    private Patient p;

    @Rule
    public IntentsTestRule<HomeActivity> intentsTestRule =
            new IntentsTestRule<>(HomeActivity.class, false, false);

    @Before
    public void setup() {
        String user_id = "johnsmith";
        String email = "johnsmith@example.com";
        String number = "7801234567";
        Patient p = new Patient(user_id, email, number);

        UserElasticSearchController es = new UserElasticSearchController();
        if (es.getUser(p.getUserid()) == null) {
            es.addUser(p);
            waitForES();
        }

        Gson gson = new Gson();
        Intent i = new Intent();
        i.putExtra("team27.healthe.User", gson.toJson(p));
        intentsTestRule.launchActivity(i);
    }

    @Test
    public void TestQRCode() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(containsString("QR Code"))).perform(click());
        intended(hasComponent(QRCodeActivity.class.getName()));
    }

    @Test
    public void TestEditBodyLocations() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(containsString("Body Locations"))).perform(click());
        intended(hasComponent(ViewBodyLocationsActivity.class.getName()));
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