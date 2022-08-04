package espresso;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.classes.IpConstants;
import com.example.unistat.ui.login.MainActivity;
import com.example.unistat.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpGoogle {
    private static HttpURLConnection connection;

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
            completeSignUpFlow(isMentee);
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

        completeSignUpFlow(isMentee);

    }

    private void completeSignUpFlow(boolean isMentee) throws InterruptedException {
        if (isMentee) {
            // Check that initial animation and questions are displayed
            onView(withId(R.id.questionAnimation)).check(matches(isDisplayed()));
            onView(withId(R.id.userStatusQuestion)).check(matches(isDisplayed()));

            // Click on buttons and check animations
            onView(withId(R.id.hsStudentButton)).check(matches(isDisplayed()));
            onView(withId(R.id.hsStudentButton)).perform(click());
            onView(withId(R.id.schoolAnimation)).check(matches(isDisplayed()));

            // Click confirm and check if activity is correct
            onView(withId(R.id.nextUserStatusButton)).check(matches(isDisplayed()));
            onView(withId(R.id.nextUserStatusButton)).perform(click());
        } else {
            Thread.sleep(1500);

            // Check that initial animation and questions are displayed
            onView(withId(R.id.user_status_activity)).check(matches(isDisplayed()));
            onView(withId(R.id.questionAnimation)).check(matches(isDisplayed()));
            onView(withId(R.id.userStatusQuestion)).check(matches(isDisplayed()));

            // Click on buttons and check animations
            onView(withId(R.id.hsStudentButton)).check(matches(isDisplayed()));
            onView(withId(R.id.hsStudentButton)).perform(click());
            onView(withId(R.id.schoolAnimation)).check(matches(isDisplayed()));
            onView(withId(R.id.univStudentButton)).check(matches(isDisplayed()));
            onView(withId(R.id.univStudentButton)).perform(click());
            onView(withId(R.id.graduationAnimation)).check(matches(isDisplayed()));

            // Click confirm and check if activity is correct
            onView(withId(R.id.nextUserStatusButton)).check(matches(isDisplayed()));
            onView(withId(R.id.nextUserStatusButton)).perform(click());
            onView(withId(R.id.create_user_profile_activity)).check(matches(isDisplayed()));

            onView(withId(R.id.nextUserProfileButton)).check(matches(isDisplayed()));
            onView(withId(R.id.nextUserProfileButton)).perform(click());
            onView(withText("All fields need to be filled before continuing...")).inRoot(new ToastMatcher())
                    .check(matches(isDisplayed()));

            onView(withId(R.id.univNameInputEditText)).perform(typeText("UBC"));
            onView(isRoot()).perform(pressBack());
            onView(withId(R.id.univMajorInputEditText)).perform(typeText("cpen"));
            onView(isRoot()).perform(pressBack());
            onView(withId(R.id.univGpaInputEditText)).perform(typeText(String.valueOf(4.0)));
            onView(isRoot()).perform(pressBack());
            onView(withId(R.id.univEntranceScoreInputEditText)).perform(typeText(String.valueOf(400)));
            onView(isRoot()).perform(pressBack());
            onView(withId(R.id.univUserBioEditText)).perform(typeText("story"));

            onView(isRoot()).perform(pressBack());
            onView(withId(R.id.nextUserProfileButton)).perform(click());
        }

        onView(withId(R.id.view_user_stats_activity)).check(matches(isDisplayed()));
    }

    public void tearDownAccount() {
        String URL = IpConstants.URL + "users";

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            java.net.URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = "{\"userEmail\": \"cpen321.unistat@gmail.com\"}";
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}

