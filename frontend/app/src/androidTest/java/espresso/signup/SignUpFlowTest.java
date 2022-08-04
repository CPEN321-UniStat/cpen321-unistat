package espresso.signup;

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

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;

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
import com.example.unistat.R;
import com.example.unistat.classes.IpConstants;
import com.example.unistat.ui.login.MainActivity;
import com.example.unistat.ui.login.UserStatusActivity;

import espresso.SignUpGoogle;
import espresso.ToastMatcher;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpFlowTest {
    private UiDevice mUiDevice;
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
    }
}

