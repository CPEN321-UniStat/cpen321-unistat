package espresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.unistat.R;
import com.example.unistat.SignOutActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoButtonTest {
    @Rule
    public ActivityScenarioRule<SignOutActivity> activityScenarioRule =
            new ActivityScenarioRule<SignOutActivity>(SignOutActivity.class);

    @Test
    public void viewProfileButtonClicked() {
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed())); //check that button is visible (in the correct activity)
        onView(withId(R.id.view_profile_button)).perform(click()); // click the button
        onView(withId(R.id.userProfileActivity)).check(matches(isDisplayed())); // check that the activity has changed due to Intent from clicked button
    }
}
