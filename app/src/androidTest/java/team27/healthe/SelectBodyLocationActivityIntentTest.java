package team27.healthe;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;
import team27.healthe.ui.HomeActivity;
import team27.healthe.ui.LoginActivity;
import team27.healthe.ui.SignupActivity;

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

public class SelectBodyLocationActivityIntentTest {
    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule =
            new IntentsTestRule<>(LoginActivity.class, false, false);

    @Before
    public void before() {
        intentsTestRule.launchActivity(new Intent());
        waitForES();
    }

    @Test
    public void TestPatientLogin() {
        Patient p = new Patient("johnsmith", "johnsmith@example.com", "7801234567");
        UserElasticSearchController es = new UserElasticSearchController();

        try{
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withText(containsString("Logout"))).perform(click());
            waitForES();
        } catch (Exception e){

        }

        if (es.getUser(p.getUserid()) != null) {
            es.removeUser(p.getUserid());
        }
        es.addUser(p);
        waitForES();

        onView(withId(R.id.loginIdText))
                .perform(typeText("johnsmith"));
        onView(withId(R.id.loginButton))
                .perform(click());
        intended(hasComponent(HomeActivity.class.getName()));

        es.removeUser(p.getUserid());
        waitForES();
    }

    @Test
    public void TestCareProviderLogin() {
        CareProvider cp = new CareProvider("marymiller", "marymiller@example.com", "7808302463");
        UserElasticSearchController es = new UserElasticSearchController();

        try{
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withText(containsString("Logout"))).perform(click());
            waitForES();
        } catch (Exception e){

        }

        if (es.getUser(cp.getUserid()) != null) {
            es.removeUser(cp.getUserid());
        }
        es.addUser(cp);
        waitForES();

        onView(withId(R.id.loginIdText))
                .perform(typeText("marymiller"));
        onView(withId(R.id.loginButton))
                .perform(click());
        intended(hasComponent(HomeActivity.class.getName()));

        es.removeUser(cp.getUserid());
        waitForES();
    }

    @Test
    public void TestInvalidLogin() {
        try{
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withText(containsString("Logout"))).perform(click());
            waitForES();
        } catch (Exception e){

        }

        onView(withId(R.id.loginIdText))
                .perform(typeText("johnsmith"), closeSoftKeyboard());
        onView(withId(R.id.loginButton))
                .perform(click());
        onView(withId(R.id.loginButton))
                .check(matches(withText(containsString("Login"))));
    }

    @Test
    public void TestSignupActivity() {
        onView(withId(R.id.textView7))
                .perform(click());
        intended(hasComponent(SignupActivity.class.getName()));
    }

    private void waitForES() {
        try {
            Thread.sleep(500); // make sure new ids are loaded properly
        }
        catch (InterruptedException e) {
        }
    }
}
