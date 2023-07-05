package com.popular.android.mibanco;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AccountsClickOnMLASeeTransactions {

    /**
     *  Pre-Condiciones antes de ejecutar este test:
     *  Se debe haber instalado y ejecutado la app de MiBanco en el emulador antes de ejecutar la prueba
     *  La primera pantalla del app debe ser donde se ingresa el username
     *  El idioma de la app estar en Ingles.
     *
     * @throws InterruptedException
     */


    @Rule
    public ActivityTestRule<IntroScreen> mActivityTestRule = new ActivityTestRule<>(IntroScreen.class);


    @Test
    public void accountsClickOnMLASeeTransactions() {

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editUsername),
                        childAtPosition(
                                allOf(withId(R.id.linearEnterUsername),
                                        childAtPosition(
                                                withId(R.id.rootView),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("sandycruz72"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnLogin), withText("Log in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearEnterUsername),
                                        4),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rootView),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Test1234"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btnSubmitPassword), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rootView),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton4.perform(click());

        onView(withText(R.string.account_list_mla_type)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));

        ViewInteraction linearLayout = onView(
                (withId(R.id.mortgage_list)))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()));
        linearLayout.perform(click());


        ViewInteraction textView = onView(
                allOf(withId(R.id.mortgage_disclaimer), withText("The amount and transactions can include a late fee."),
                        withParent(withParent(withId(R.id.transaction_list))),
                        isDisplayed()));
        textView.check(matches(withText("The amount and transactions can include a late fee.")));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_logout), withContentDescription("Log out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
