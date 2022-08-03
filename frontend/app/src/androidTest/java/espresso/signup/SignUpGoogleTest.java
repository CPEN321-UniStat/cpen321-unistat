package espresso.signup;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.unistat.ui.login.MainActivity;
import com.example.unistat.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpGoogleTest {
    private UiDevice mUiDevice;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule1 =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void signUp() throws UiObjectNotFoundException, InterruptedException {
        onView((withId(R.id.sign_in_button))).check(matches(isDisplayed()));
        onView(withId(R.id.sign_in_button)).perform(click());
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject mText = mUiDevice.findObject(new UiSelector().text("UniStat"));
        mText.click();
        Thread.sleep(1500);
        onView((withId(R.id.view_user_stats_activity))).check(matches(isDisplayed()));
//        UiObject2 mText = mUiDevice.findObject(new BySelector().text("Kush Arora"));
//        mText.click();
    }

//    @Test
//    public void signUpUnivStudent() {
//
//        // Figure out how to perform automated sign-in with Google
////        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
////        onView(withId(R.id.sign_in_button)).perform(click());
//
//        // Check that initial animation and questions are displayed
//        onView(withId(R.id.questionAnimation)).check(matches(isDisplayed()));
//        onView(withId(R.id.userStatusQuestion)).check(matches(isDisplayed()));
//
//        // Click on buttons and check animations
//        onView(withId(R.id.hsStudentButton)).check(matches(isDisplayed()));
//        onView(withId(R.id.hsStudentButton)).perform(click());
//        onView(withId(R.id.schoolAnimation)).check(matches(isDisplayed()));
//        onView(withId(R.id.univStudentButton)).check(matches(isDisplayed()));
//        onView(withId(R.id.univStudentButton)).perform(click());
//        onView(withId(R.id.graduationAnimation)).check(matches(isDisplayed()));
//
//        // Click confirm and check if activity is correct
//        onView(withId(R.id.nextUserStatusButton)).check(matches(isDisplayed()));
//        onView(withId(R.id.nextUserStatusButton)).perform(click());
//        onView(withId(R.id.create_user_profile_activity)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.nextUserProfileButton)).check(matches(isDisplayed()));
//        onView(withId(R.id.nextUserProfileButton)).perform(click());
//        onView(withText("All fields need to be filled before continuing...")).inRoot(new ToastMatcher())
//                .check(matches(isDisplayed()));
//
//        onView(withId(R.id.univNameInputEditText)).perform(typeText("UBC"));
//        onView(withId(R.id.univMajorInputEditText)).perform(typeText("cpen"));
//        onView(withId(R.id.univGpaInputEditText)).perform(typeText(String.valueOf(4.0)));
//        onView(withId(R.id.univEntranceScoreInputEditText)).perform(typeText(String.valueOf(400)));
//        onView(isRoot()).perform(pressBack());
//        onView(withId(R.id.univUserBioEditText)).perform(typeText("story"));
//
//        onView(isRoot()).perform(pressBack());
//        onView(withId(R.id.nextUserProfileButton)).perform(click());
//        onView(withId(R.id.view_user_stats_activity)).check(matches(isDisplayed()));
//
//    }
//
//    @Test
//    public void signUpHighSchoolStudent() {
//
//        // Figure out how to perform automated sign-in with Google
////        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
////        onView(withId(R.id.sign_in_button)).perform(click());
//
//        // Check that initial animation and questions are displayed
//        onView(withId(R.id.questionAnimation)).check(matches(isDisplayed()));
//        onView(withId(R.id.userStatusQuestion)).check(matches(isDisplayed()));
//
//        // Click on buttons and check animations
//        onView(withId(R.id.hsStudentButton)).check(matches(isDisplayed()));
//        onView(withId(R.id.hsStudentButton)).perform(click());
//        onView(withId(R.id.schoolAnimation)).check(matches(isDisplayed()));
//
//        // Click confirm and check if activity is correct
//        onView(withId(R.id.nextUserStatusButton)).check(matches(isDisplayed()));
//        onView(withId(R.id.nextUserStatusButton)).perform(click());
//        onView(withId(R.id.view_user_stats_activity)).check(matches(isDisplayed()));
//
//    }
}

