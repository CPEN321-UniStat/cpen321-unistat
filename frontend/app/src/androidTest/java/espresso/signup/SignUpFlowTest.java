package espresso.signup;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.unistat.R;
import com.example.unistat.ui.login.MainActivity;
import espresso.SignUpGoogle;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpFlowTest {
    private final SignUpGoogle signUpGoogle = new SignUpGoogle();

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule1 =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void signUpUnivStudent() throws InterruptedException, UiObjectNotFoundException {
        signUpGoogle.signUp(false);

        // Sign Out
        onView(withId(R.id.sign_out_activity)).perform(click());
        onView(withId(R.id.settingsAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.dark_mode_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).perform(click());

        signUpGoogle.tearDownAccount();

    }

    @Test
    public void signUpHighSchoolStudent() throws InterruptedException, UiObjectNotFoundException {
        signUpGoogle.signUp(true);

        // Sign Out
        onView(withId(R.id.sign_out_activity)).perform(click());
        onView(withId(R.id.settingsAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.dark_mode_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).perform(click());

        signUpGoogle.tearDownAccount();
    }
}

