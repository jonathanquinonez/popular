package com.popular.android.mibanco;

import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.popular.android.mibanco.model.MarketplaceCard;
import com.popular.android.mibanco.object.SettingsItem;
import com.popular.android.mibanco.object.SidebarItem;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShowOpenEAccount {

    @Rule
    public ActivityTestRule<IntroScreen> mActivityRule = new ActivityTestRule<>(IntroScreen.class);

    /**
     *  IMPORTANTE Leer completo
     *  Condiciones a tomar en cuenta para ejecutar estas pruebas correctamente:
     *  Haber instalado o ejecutado la app de MiBanco en el emulador antes de ejecutar la prueba
     *  darle los permisos de acceesos y denegarle la autenticacion con huella digital.
     *  Ejecutar el test correspondiente si el 2Step esta activo o inactivo
     *  NO haber guardado ningun nombre de usuario en la pantalla de login (opción de recordar username)
     *  Si se ejecuta por primera vez la aplicación en el emulador, favor descomentar la primera linea de cada test
     *
     * @throws InterruptedException
     */
    //@Ignore
    @Test
    public void ShowOptionOpenEAccountWhit2StepActive() throws InterruptedException
    {
        //onView(withId(R.id.button_log_in)).perform(click()); //Descomentar si inicia en la pantalla anterior a la de ingresar username

        onView(withId(R.id.editUsername)).perform(typeText("susieanne2"), closeSoftKeyboard()); //Cambiar aqui el perfil si se desea utilizar otro
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.editPassword)).perform(typeText("Test1234"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmitPassword)).perform(click());
        onView(withId(R.id.btn_sidebar_open)).perform(click());
        //Mostrar el estado del 2-Step
        onView(withId(R.id.menulistView)).perform(swipeUp());
        onData(instanceOf(SidebarItem.class)).atPosition(13).perform(click());
        onData(instanceOf(SettingsItem.class)).atPosition(7).perform(click());
        Thread.sleep(5000); //Espera por la vista ya que hace una consulta
        Espresso.pressBack();
        onView(withId(R.id.up)).perform(click());
        //
        onView(withId(R.id.btn_sidebar_open)).perform(click());
        onView(withId(R.id.menulistView)).perform(swipeDown());
        onData(instanceOf(SidebarItem.class)).atPosition(1).perform(click());
        onData(instanceOf(MarketplaceCard.class)).atPosition(1).check(matches(isDisplayed()));
    }
    @Ignore
    @Test
    public void ShowOptionOpenEAccountWhit2StepInActive() throws InterruptedException
    {
        //onView(withId(R.id.button_log_in)).perform(click()); //Descomentar si inicia en la pantalla anterior a la de ingresar username

        onView(withId(R.id.editUsername)).perform(typeText("susieanne2"), closeSoftKeyboard());  //Cambiar aqui el perfil si se desea utilizar otro
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.editPassword)).perform(typeText("Test1234"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmitPassword)).perform(click());
        onView(withId(R.id.btn_sidebar_open)).perform(click());
        //Mostrar el estado del 2-Step
        onView(withId(R.id.menulistView)).perform(swipeUp());
        onData(instanceOf(SidebarItem.class)).atPosition(13).perform(click());
        onData(instanceOf(SettingsItem.class)).atPosition(8).perform(click());
        Thread.sleep(5000); //Espera por la vista ya que hace una consulta
        Espresso.pressBack();
        onView(withId(R.id.up)).perform(click());
        //
        onView(withId(R.id.btn_sidebar_open)).perform(click());
        onView(withId(R.id.menulistView)).perform(swipeDown());
        onData(instanceOf(SidebarItem.class)).atPosition(1).perform(click());
        onData(instanceOf(MarketplaceCard.class)).atPosition(1).check(matches(isDisplayed()));
    }

}
