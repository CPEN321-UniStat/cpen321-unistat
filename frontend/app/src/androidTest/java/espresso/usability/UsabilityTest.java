package espresso.usability;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import com.example.unistat.R;
import com.example.unistat.ui.login.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import espresso.SignUpGoogle;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UsabilityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    int listCount;
    UiDevice mDevice;
    private View decorView;
    private final SignUpGoogle signUpGoogle = new SignUpGoogle();

    @Before
    public void setUp() throws Exception{
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        activityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            AutoCompleteTextView recyclerView = (AutoCompleteTextView) view;
            ListAdapter adapter = recyclerView.getAdapter();
            listCount = adapter.getCount();
        }
    }

    @Test
    public void testClicks() throws UiObjectNotFoundException, InterruptedException {

        signUpGoogle.signUp(false);

        Thread.sleep(8000);
        onView(withId(R.id.filterAutoComplete)).check(new RecyclerViewItemCountAssertion(4));
        System.out.println("List count: " + listCount);

        int clicks = 0;
        for (int i=0; i < listCount; i++) {
            clicks = 0;
            clickSuggestionAtPosition(i);
            clicks++;
            assertTrue(clicks <= 3);

            Thread.sleep(500);
            clickSortByGpa();
            clicks++;
            assertTrue(clicks <= 3);
            Thread.sleep(500);
            //Unclick
            clickSortByGpa();

            Thread.sleep(500);
            clickSortByEntranceScore();
            clicks++;
            assertTrue(clicks <= 3);
            Thread.sleep(500);
            //Unclick
            clickSortByEntranceScore();
        }

        // Sign out
        onView(withId(R.id.sign_out_activity)).perform(click());
        onView(withId(R.id.settingsAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.dark_mode_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_button)).perform(click());

        signUpGoogle.tearDownAccount();
    }

    private void clickSuggestionAtPosition(int pos) throws InterruptedException {
        onView(withId(R.id.filterAutoComplete)).perform(replaceText(""));
        Thread.sleep(500);
        onView(withId(R.id.filterAutoComplete)).perform(click());
        Thread.sleep(1000);
        onData(instanceOf(String.class))
                .inRoot(RootMatchers.withDecorView(not(decorView)))
                .atPosition(pos)
                .perform(click());
    }

    private void clickSortByGpa() {
        onView(withId(R.id.sortByGpaChip)).perform(click());
    }

    private void clickSortByEntranceScore() {
        onView(withId(R.id.sortByEntranceScoreChip)).perform(click());
    }
}
