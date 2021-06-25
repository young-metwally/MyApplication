package com.example.myapplication;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * This test case checks if user provides only number, then it should show a failure message
     */
    @Test
    public void textOnlyNumeric() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editTextPassword));
        appCompatEditText.perform(replaceText("12345"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(withId(R.id.btnLogin));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.textView2));
        textView.check(matches(withText("You shall not pass!")));
    }

    /**
     * This test case checks if user doesn't enter an upper case letter, then it should show a failure message
     */
    @Test
    public void testFindMissingUpperCase() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editTextPassword));
        appCompatEditText.perform(replaceText("password123#$*"));

        ViewInteraction materialButton = onView(withId(R.id.btnLogin));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.textView2));
        textView.check(matches(withText("You shall not pass!")));
    }

    /**
     * This test case checks if user doesn't enter an lower case letter, then it should show a failure message
     */
    @Test
    public void textFindMissingLowerCase() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editTextPassword));
        appCompatEditText.perform(replaceText("AS123#$*"));

        ViewInteraction materialButton = onView(withId(R.id.btnLogin));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.textView2));
        textView.check(matches(withText("You shall not pass!")));
    }

    /**
     * This test case checks if user doesn't enter a number, then it should show a failure message
     */
    @Test
    public void textFindMissingNumber() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editTextPassword));
        appCompatEditText.perform(replaceText("passworD#$*"));

        ViewInteraction materialButton = onView(withId(R.id.btnLogin));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.textView2));
        textView.check(matches(withText("You shall not pass!")));
    }

    /**
     * This test case checks if user doesn't enter a symbol, then it should show a failure message
     */
    @Test
    public void textFindMissingSymbol() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editTextPassword));
        appCompatEditText.perform(replaceText("passworD123"));

        ViewInteraction materialButton = onView(withId(R.id.btnLogin));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.textView2));
        textView.check(matches(withText("You shall not pass!")));
    }

    /**
     * This test case checks if user enters a valid password, then it should show a success message
     */
    @Test
    public void textValidPassword() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editTextPassword));
        appCompatEditText.perform(replaceText("Password123#$*"));

        ViewInteraction materialButton = onView(withId(R.id.btnLogin));
        materialButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.textView2));
        textView.check(matches(withText("Your password meets the requirements")));
    }

}
