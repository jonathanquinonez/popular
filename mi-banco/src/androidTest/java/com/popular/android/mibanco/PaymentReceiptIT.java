package com.popular.android.mibanco;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.foound.widget.AmazingListView;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.activity.PaymentReceipt;
import com.popular.android.mibanco.activity.Receipts;
import com.popular.android.mibanco.activity.SelectStatement;
import com.popular.android.mibanco.model.PaymentHistoryEntry;
import com.popular.android.mibanco.object.SidebarItem;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PaymentReceiptIT {

    @Rule
    public ActivityTestRule<IntroScreen> mActivityRule = new ActivityTestRule<>(IntroScreen.class);

    @Test
    public void saveReceiptsWithoutCrashing() throws InterruptedException
    {
        onView(withId(R.id.editUsername)).perform(typeText("lugarol"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.editPassword)).perform(typeText("Test1234"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmitPassword)).perform(click());
        onView(withId(R.id.btn_sidebar_open)).perform(click());

        onView(withId(R.id.menulistView)).perform(swipeUp());
        onData(instanceOf(SidebarItem.class)).atPosition(4).perform(click());
        onView(withId(R.id.receipts_list)).perform(click());
        onView(withId(R.id.menu_save_receipt)).perform(click());
        onView(withId(R.id.buttonPositive)).perform(click());
        onView(withId(R.id.button_close)).perform(click());
        onView(withId(R.id.receipts_list)).perform(click());
        onView(withId(R.id.menu_save_receipt)).perform(click());
        onView(withId(R.id.buttonPositive)).perform(click());
    }

}
