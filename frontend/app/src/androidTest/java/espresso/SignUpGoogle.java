package espresso;

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

public class SignUpGoogle {

    public void signUp(boolean isMentee) throws UiObjectNotFoundException, InterruptedException {
        UiDevice mUiDevice;
        String popUpAccountName = "UniStat";
        String userName = "cpen321.unistat";
        String password = "unistat@123";

        onView((withId(R.id.sign_in_button))).check(matches(isDisplayed()));
        onView(withId(R.id.sign_in_button)).perform(click());
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Check all possible sign-in/sign-up cases
        UiObject use_another_account = mUiDevice.findObject(new UiSelector().text("Use another account"));

        try {
            // Check if account name is there
            UiObject accountName = mUiDevice.findObject(new UiSelector().text(popUpAccountName));
            accountName.click();
            Thread.sleep(3000);
            return;
        } catch (UiObjectNotFoundException e) {
            // Check if use another account is there
            try {
                use_another_account.click();
            } catch (UiObjectNotFoundException err) {
                // No other option left go with initial sign up
                assert true; // noop
            }
        }

        Thread.sleep(10000);

        try {
            UiObject email_or_phone = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
            email_or_phone.click();
            email_or_phone.setText(userName);
            mUiDevice.pressEnter();
        } catch (UiObjectNotFoundException e) {
            // If not found in 10 seconds, wait more
            Thread.sleep(25000);
            UiObject email_or_phone = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
            email_or_phone.click();
            email_or_phone.setText(userName);
            mUiDevice.pressEnter();
        }

        Thread.sleep(1500);

        UiObject enter_password = mUiDevice.findObject(new UiSelector().className("android.widget.EditText"));
        enter_password.click();
        enter_password.setText(password);
        mUiDevice.pressEnter();

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

        completeSignUpFlow();

        return;
    }

    private void completeSignUpFlow() {

    }
}

