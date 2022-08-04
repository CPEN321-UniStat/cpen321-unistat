package espresso.signup;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

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

        // Check all possible sign-in/sign-up cases
        UiObject accountName = mUiDevice.findObject(new UiSelector().text("Kush Arora"));
        UiObject use_another_account = mUiDevice.findObject(new UiSelector().text("Use another account"));

        try {
            accountName.click();
            Thread.sleep(1500);
            onView(withId(R.id.view_user_stats_activity)).check(matches(isDisplayed()));
            return;
        } catch (UiObjectNotFoundException e) {
            use_another_account.click();
        }

        Thread.sleep(10000);

        UiObject email_or_phone = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
        email_or_phone.click();
        email_or_phone.setText("kushar339");
        mUiDevice.pressBack();

        UiObject next1 = mUiDevice.findObject(new UiSelector().className("android.widget.Button").textContains("NEXT"));
        next1.click();

        Thread.sleep(10000);

        try {
            UiObject enter_your_password = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
            email_or_phone.click();
            enter_your_password.setText("kusharora339");
            mUiDevice.pressBack();
        } catch (UiObjectNotFoundException e) {
            // If not found in 10 seconds, wait more
            Thread.sleep(25000);
            UiObject enter_your_password = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
            email_or_phone.click();
            enter_your_password.setText("kusharora339");
            mUiDevice.pressBack();
        }

        UiObject next2 = mUiDevice.findObject(new UiSelector().className("android.widget.Button").textContains("NEXT"));
        next2.click();

        Thread.sleep(2000);

        try {
            UiObject next3 = mUiDevice.findObject(new UiSelector().className("android.widget.Button").textContains("Don't turn on"));
            next3.click();
        } catch (UiObjectNotFoundException e) {
            Thread.sleep(2000);
            UiObject i_agree = mUiDevice.findObject(new UiSelector().className("android.widget.Button").textContains("I agree"));
            i_agree.click();
        }

        Thread.sleep(1000);

        UiObject more = mUiDevice.findObject(new UiSelector().className("android.widget.Button").textContains("MORE"));
        more.click();

        UiObject accept = mUiDevice.findObject(new UiSelector().className("android.widget.Button").textContains("ACCEPT"));
        accept.click();

        Thread.sleep(15000);
    }
}

