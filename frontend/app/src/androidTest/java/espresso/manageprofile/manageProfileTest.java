package espresso.manageprofile;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.unistat.R;
import com.example.unistat.ui.stats.ViewStatsActivity;

import espresso.ToastMatcher;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class manageProfileTest {
    @Rule
    public ActivityScenarioRule<ViewStatsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ViewStatsActivity.class);

    @Test
    public void manageMentorProfile() throws InterruptedException {

        onView(withId(R.id.sign_out_activity)).perform(click());

        onView(withId(R.id.settingsAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.dark_mode_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));

        onView(withId(R.id.view_profile_button)).perform(click());

        onView(withId(R.id.coins)).check(matches(isDisplayed()));
        onView(withId(R.id.coinAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.userProfileImage)).check(matches(isDisplayed()));
        onView(withId(R.id.userNameText)).check(matches(isDisplayed()));
        onView(withId(R.id.userEmailText)).check(matches(isDisplayed()));

        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).perform(click());

        // Check for empty field error
        onView(withId(R.id.editUserUnivEditText)).perform(replaceText(""));

        onView(withId(R.id.confirmChangesButton)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmChangesButton)).perform(click());

        onView(withText("All fields need to be filled before continuing...")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        Thread.sleep(1500);

        // Check for invalid field error
        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).perform(click());

        onView(withId(R.id.editUserUnivEditText)).perform(replaceText("1234ubc"));

        onView(withId(R.id.confirmChangesButton)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmChangesButton)).perform(click());

        onView(withText("Please make sure your university name & major are valid.")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        Thread.sleep(1500);


        // Every field is correct
        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).perform(click());

        onView(withId(R.id.editUserUnivEditText)).perform(replaceText("UBC"));

        onView(withId(R.id.confirmChangesButton)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmChangesButton)).perform(click());

        onView(withText("Your Profile has been updated")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        Thread.sleep(1500);

        // Check for invalid GPA
        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).perform(click());

        onView(withId(R.id.editUserGpaEditText)).perform(replaceText(String.valueOf(5)));

        onView(withId(R.id.confirmChangesButton)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmChangesButton)).perform(click());

        onView(withText("Please make sure your GPA is valid.")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        Thread.sleep(1500);

        // Sign out for the next test
        onView(isRoot()).perform(pressBack());
        onView(withId(R.id.sign_out_button)).perform(click());

    }

    // Need to first sign-in with a mentee account to successfully run the following test case

    @Test
    public void manageMenteeProfile() {

        onView(withId(R.id.sign_out_activity)).perform(click());

        onView(withId(R.id.settingsAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.dark_mode_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));

        onView(withId(R.id.view_profile_button)).perform(click());

        onView(withId(R.id.coins)).check(matches(isDisplayed()));
        onView(withId(R.id.coinAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.userProfileImage)).check(matches(isDisplayed()));
        onView(withId(R.id.userNameText)).check(matches(isDisplayed()));
        onView(withId(R.id.userEmailText)).check(matches(isDisplayed()));

    }
}
