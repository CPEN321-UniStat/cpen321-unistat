package espresso.requestmeeting;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.unistat.R;
import com.example.unistat.ui.stats.ViewStatsActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import espresso.ToastMatcher;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RequestMeetingActivityTest {

    String myDisplayName = "Manek Gujral";
    String otherMentorDisplayName = "UniStat";

    UiDevice mDevice;
    private View decorView;
    @Before
    public void setUp() throws Exception{
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        activityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<ViewStatsActivity>() {
            @Override
            public void perform(ViewStatsActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    @Rule
    public ActivityScenarioRule<ViewStatsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ViewStatsActivity.class);

    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();

                if (error == null) {
                    return false;
                }

                String hint = error.toString();

                return expectedErrorText.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private void testMeetingTitleAndStart(boolean startTimeInPast) throws  UiObjectNotFoundException {
        /* Click on own university admission statistic */
        onView(allOf(withText(myDisplayName), withId(R.id.mentorName))).check(matches(isDisplayed()));
        onView(allOf(withText(myDisplayName), withId(R.id.mentorName))).perform(click());
        /* New page shows up, and request meeting button is disabled */
        onView(withId(R.id.view_mentor_profile_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.requestMeetingButton)).check(matches(not(isDisplayed())));

        /* Click back button */
        Espresso.pressBack();
        /* Check main stats page is reloaded */
        onView(withId(R.id.view_stats_activity)).check(matches(isDisplayed()));

        /* Click on a university admission statistic other than own */
        onView(allOf(withText(otherMentorDisplayName), withId(R.id.mentorName))).check(matches(isDisplayed()));
        onView(allOf(withText(otherMentorDisplayName), withId(R.id.mentorName))).perform(click());
        /* New page shows up, and request meeting button is shown */
        onView(withId(R.id.view_mentor_profile_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.requestMeetingButton)).check(matches(isDisplayed()));

        /* Click on Request Meeting button */
        onView(withId(R.id.requestMeetingButton)).perform(click());
        /* New page titled "Book a Meeting" and input field labelled "Meeting Title"*/
        onView(withText("Book a Meeting")).check(matches(isDisplayed()));
        onView(withHint("Meeting Title")).check(matches(isDisplayed()));

        /* Populate the "Meeting Title" input field with "#123abc/" */
        onView(withId(R.id.meeting_title_input_edit_text)).perform(typeText("#123abc/"));
        /* The “Meeting Title” input text field is filled out with “#123abc/” */
        onView(withId(R.id.meeting_title_input_edit_text)).check(matches(withText("#123abc/")));

        /* User clicks on 'From' date */
        onView(withId(R.id.start_date_text)).check(matches(isDisplayed()));
        onView(withId(R.id.start_date_text)).perform(click());
        /* Calendar date picker is shown */
        UiObject calendar = mDevice.findObject(new UiSelector().text("SELECT START DATE"));
        assertTrue(calendar.exists());


        /* Click on a date (yesterday) that is before the current day */
        Calendar today = Calendar.getInstance();
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        if (yesterday.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
            UiObject prevMonthButton = mDevice.findObject(new UiSelector().description("Change to previous month"));
            prevMonthButton.click();
        }
        UiObject yesterdayButton = mDevice.findObject(new UiSelector().text(String.valueOf(yesterday.get(Calendar.DAY_OF_MONTH))));
        yesterdayButton.click();
        /* The clicked date is not selected */
        UiObject selectedDateText = mDevice.findObject(new UiSelector().description("Current selection: Selected date"));
        assertEquals("Selected date", selectedDateText.getText());


        /* Click on a date (today) that is current date or in the future */
        UiObject todayButton = mDevice.findObject(new UiSelector().text(String.valueOf(today.get(Calendar.DAY_OF_MONTH))));
        todayButton.click();
        /* Clicked date is selected */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy");
        String todayDateText = simpleDateFormat.format(today.getTime());
        selectedDateText = mDevice.findObject(new UiSelector().description("Current selection: " + todayDateText));
        assertEquals(todayDateText, selectedDateText.getText());

        /* Click on OK */
        UiObject okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.click();
        /* The Calendar date picker is closed */
        assertFalse(calendar.exists());


        /* Click on "From" time */
        onView(withId(R.id.start_time_text)).check(matches(isDisplayed()));
        onView(withId(R.id.start_time_text)).perform(click());
        /* Time picker is shown */
        UiObject time = mDevice.findObject(new UiSelector().text("SELECT START TIME"));
        assertTrue(time.exists());


        /* Input time of last 5th minute */
        UiObject2 colonText = mDevice.findObject(By.text(":"));
        UiObject2 colonParent = colonText.getParent();
        UiObject2 hourChip = colonParent.getChildren().get(0);
        UiObject2 minuteChip = colonParent.getChildren().get(2);
        UiObject amButton = mDevice.findObject(new UiSelector().text("AM"));
        UiObject pmButton = mDevice.findObject(new UiSelector().text("PM"));
        Calendar selectedTime = (Calendar) today.clone();
        if (startTimeInPast) {
            if (selectedTime.get(Calendar.MINUTE) % 5 == 0)
                selectedTime.add(Calendar.MINUTE, -5);
            else
                selectedTime.add(Calendar.MINUTE, -5 - selectedTime.get(Calendar.MINUTE) % 5);
        }
        else {
            if (selectedTime.get(Calendar.MINUTE) % 5 == 0)
                selectedTime.add(Calendar.MINUTE, 10);
            else {
                selectedTime.add(Calendar.MINUTE, 10 - selectedTime.get(Calendar.MINUTE) % 5);
            }
        }
        int selectedHour = selectedTime.get(Calendar.HOUR);
        boolean am = selectedTime.get(Calendar.AM_PM) == Calendar.AM;
        int selectedMin = selectedTime.get(Calendar.MINUTE);
        hourChip.click();
        UiObject hourText = mDevice.findObject(new UiSelector().text(String.valueOf(selectedHour)).className("android.widget.TextView"));
        hourText.click();
        minuteChip.click();
        DecimalFormat formatter = new DecimalFormat("00");
        UiObject minText = mDevice.findObject(new UiSelector().text(formatter.format(selectedMin)).className("android.widget.TextView"));
        minText.click();
        if (am)
            amButton.click();
        else
            pmButton.click();
        okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.click();

    }



    @Test
    public void requestMeetingInPast() throws UiObjectNotFoundException {

        testMeetingTitleAndStart(true);

        /* Enter a small number (1) into "Payment Offer" text input field */
        onView(withId(R.id.payment_offer_input_edit_text)).perform(replaceText("1"));
        /* The “Payment Offer” field is filled out successfully */
        onView(withId(R.id.payment_offer_input_edit_text)).check(matches(withText("1")));

        /* Click the request meeting button */
        onView(withId(R.id.book_meeting_button)).perform(click());
        /* An error message says that the start/end time cannot be in the past */
        onView(withText("Start time or end time cannot be in the past")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void requestMeetingPayments() throws UiObjectNotFoundException {

        testMeetingTitleAndStart(true);

        onView(withId(R.id.book_meeting_button)).perform(click());
        /* The “Payment Offer” text field is outlined in red with a warning widget appearing in the text box */
        onView(withId(R.id.payment_offer_input)).check(matches(hasTextInputLayoutErrorText("Enter a valid number")));

        /* Enter string into "Payment Offer" text input field */
        onView(withId(R.id.payment_offer_input_edit_text)).perform(typeText("String"));
        /* Check that text field remains empty */
        onView(withId(R.id.payment_offer_input_edit_text)).check(matches(withText("")));

        /* Enter a large number (1000) into "Payment Offer" text input field and click Request Meeting */
        onView(withId(R.id.payment_offer_input_edit_text)).perform(replaceText("1000"));
        onView(withId(R.id.book_meeting_button)).perform(click());
        /* Error message showing "Not enough balance for payment" is shown */
        onView(withText("Not enough balance for payment " + Double.parseDouble("1000"))).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        /* Enter a small number (1) into "Payment Offer" text input field */
        onView(withId(R.id.payment_offer_input_edit_text)).perform(replaceText("1"));
        /* The “Payment Offer” field is filled out successfully */
        onView(withId(R.id.payment_offer_input_edit_text)).check(matches(withText("1")));

    }

    @Test
    public void requestMeetingStartTimeAfterEndTime() throws UiObjectNotFoundException {

        testMeetingTitleAndStart(false);

        /* Enter a small number (1) into "Payment Offer" text input field */
        onView(withId(R.id.payment_offer_input_edit_text)).perform(replaceText("1"));
        /* The “Payment Offer” field is filled out successfully */
        onView(withId(R.id.payment_offer_input_edit_text)).check(matches(withText("1")));

        /* Click on 'To' date */
        onView(withId(R.id.end_date_text)).check(matches(isDisplayed()));
        onView(withId(R.id.end_date_text)).perform(click());
        /* Calendar date picker is shown */
        UiObject calendar = mDevice.findObject(new UiSelector().text("SELECT END DATE"));
        assertTrue(calendar.exists());

        /* Click on a date (yesterday) that is before the start date */
        Calendar today = Calendar.getInstance();
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        if (yesterday.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
            UiObject prevMonthButton = mDevice.findObject(new UiSelector().description("Change to previous month"));
            prevMonthButton.click();
        }
        UiObject yesterdayButton = mDevice.findObject(new UiSelector().text(String.valueOf(yesterday.get(Calendar.DAY_OF_MONTH))));
        yesterdayButton.click();
        /* The clicked date is not selected */
        UiObject selectedDateText = mDevice.findObject(new UiSelector().description("Current selection: Selected date"));
        assertEquals("Selected date", selectedDateText.getText());


        /* Click on a date (today) that is the start date or in the future */
        UiObject todayButton = mDevice.findObject(new UiSelector().text(String.valueOf(today.get(Calendar.DAY_OF_MONTH))));
        todayButton.click();
        /* Clicked date is selected */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy");
        String todayDateText = simpleDateFormat.format(today.getTime());
        selectedDateText = mDevice.findObject(new UiSelector().description("Current selection: " + todayDateText));
        assertEquals(todayDateText, selectedDateText.getText());

        /* Click on OK */
        UiObject okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.click();
        /* The Calendar date picker is closed */
        assertFalse(calendar.exists());


        /* Click on "To" time */
        onView(withId(R.id.end_time_text)).check(matches(isDisplayed()));
        onView(withId(R.id.end_time_text)).perform(click());
        /* Time picker is shown */
        UiObject time = mDevice.findObject(new UiSelector().text("SELECT END TIME"));
        assertTrue(time.exists());


        /* Input time of last 5th minute */
        UiObject2 colonText = mDevice.findObject(By.text(":"));
        UiObject2 colonParent = colonText.getParent();
        UiObject2 hourChip = colonParent.getChildren().get(0);
        UiObject2 minuteChip = colonParent.getChildren().get(2);
        UiObject amButton = mDevice.findObject(new UiSelector().text("AM"));
        UiObject pmButton = mDevice.findObject(new UiSelector().text("PM"));
        Calendar selectedTime = (Calendar) today.clone();
        if (selectedTime.get(Calendar.MINUTE) % 5 == 0)
            selectedTime.add(Calendar.MINUTE, -5);
        else
            selectedTime.add(Calendar.MINUTE, -5 - selectedTime.get(Calendar.MINUTE) % 5);
        int selectedHour = selectedTime.get(Calendar.HOUR);
        boolean am = selectedTime.get(Calendar.AM_PM) == Calendar.AM;
        int selectedMin = selectedTime.get(Calendar.MINUTE);
        hourChip.click();
        UiObject hourText = mDevice.findObject(new UiSelector().text(String.valueOf(selectedHour)).className("android.widget.TextView"));
        hourText.click();
        minuteChip.click();
        DecimalFormat formatter = new DecimalFormat("00");
        UiObject minText = mDevice.findObject(new UiSelector().text(formatter.format(selectedMin)).className("android.widget.TextView"));
        minText.click();
        if (am)
            amButton.click();
        else
            pmButton.click();
        okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.click();

        onView(withId(R.id.book_meeting_button)).perform(click());
        onView(withText("Start time cannot be after end time")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));


    }


    @Test
    public void requestMeetingSuccessful() throws UiObjectNotFoundException {

        testMeetingTitleAndStart(false);

        /* Enter a small number (1) into "Payment Offer" text input field */
        onView(withId(R.id.payment_offer_input_edit_text)).perform(replaceText("1"));
        /* The “Payment Offer” field is filled out successfully */
        onView(withId(R.id.payment_offer_input_edit_text)).check(matches(withText("1")));

        /* Click on 'To' date */
        onView(withId(R.id.end_date_text)).check(matches(isDisplayed()));
        onView(withId(R.id.end_date_text)).perform(click());
        /* Calendar date picker is shown */
        UiObject calendar = mDevice.findObject(new UiSelector().text("SELECT END DATE"));
        assertTrue(calendar.exists());

        /* Click on a date (yesterday) that is before the start date */
        Calendar today = Calendar.getInstance();
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        if (yesterday.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
            UiObject prevMonthButton = mDevice.findObject(new UiSelector().description("Change to previous month"));
            prevMonthButton.click();
        }
        UiObject yesterdayButton = mDevice.findObject(new UiSelector().text(String.valueOf(yesterday.get(Calendar.DAY_OF_MONTH))));
        yesterdayButton.click();
        /* The clicked date is not selected */
        UiObject selectedDateText = mDevice.findObject(new UiSelector().description("Current selection: Selected date"));
        assertEquals("Selected date", selectedDateText.getText());


        /* Click on a date (today) that is the start date or in the future */
        UiObject todayButton = mDevice.findObject(new UiSelector().text(String.valueOf(today.get(Calendar.DAY_OF_MONTH))));
        todayButton.click();
        /* Clicked date is selected */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy");
        String todayDateText = simpleDateFormat.format(today.getTime());
        selectedDateText = mDevice.findObject(new UiSelector().description("Current selection: " + todayDateText));
        assertEquals(todayDateText, selectedDateText.getText());

        /* Click on OK */
        UiObject okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.click();
        /* The Calendar date picker is closed */
        assertFalse(calendar.exists());


        /* Click on "To" time */
        onView(withId(R.id.end_time_text)).check(matches(isDisplayed()));
        onView(withId(R.id.end_time_text)).perform(click());
        /* Time picker is shown */
        UiObject time = mDevice.findObject(new UiSelector().text("SELECT END TIME"));
        assertTrue(time.exists());


        /* Input time of last 5th minute */
        UiObject2 colonText = mDevice.findObject(By.text(":"));
        UiObject2 colonParent = colonText.getParent();
        UiObject2 hourChip = colonParent.getChildren().get(0);
        UiObject2 minuteChip = colonParent.getChildren().get(2);
        UiObject amButton = mDevice.findObject(new UiSelector().text("AM"));
        UiObject pmButton = mDevice.findObject(new UiSelector().text("PM"));
        Calendar selectedTime = (Calendar) today.clone();
        if (selectedTime.get(Calendar.MINUTE) % 5 == 0)
            selectedTime.add(Calendar.MINUTE, 25);
        else {
            selectedTime.add(Calendar.MINUTE, 25 - selectedTime.get(Calendar.MINUTE) % 5);
        }
        int selectedHour = selectedTime.get(Calendar.HOUR);
        boolean am = selectedTime.get(Calendar.AM_PM) == Calendar.AM;
        int selectedMin = selectedTime.get(Calendar.MINUTE);
        hourChip.click();
        UiObject hourText = mDevice.findObject(new UiSelector().text(String.valueOf(selectedHour)).className("android.widget.TextView"));
        hourText.click();
        minuteChip.click();
        DecimalFormat formatter = new DecimalFormat("00");
        UiObject minText = mDevice.findObject(new UiSelector().text(formatter.format(selectedMin)).className("android.widget.TextView"));
        minText.click();
        if (am)
            amButton.click();
        else
            pmButton.click();
        okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.click();

        onView(withId(R.id.book_meeting_button)).perform(click());
        onView(withText("Your meeting request was sent")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        onView(withId(R.id.calendar_activity_screen)).check(matches(isDisplayed()));



    }

}

