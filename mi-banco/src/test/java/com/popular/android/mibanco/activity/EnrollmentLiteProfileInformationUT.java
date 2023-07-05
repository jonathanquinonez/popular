package com.popular.android.mibanco.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.Editable;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ReportFragment;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.MiBancoEnviromentConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.model.EnrollmentLiteRequest;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.Utils;
import com.qburst.android.widget.spinnerextended.SpinnerExtended;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EnrollmentLiteProfileInformation.class, ReportFragment.class,
        MiBancoConstants.class, R.class, CookieSyncManager.class, MiBancoEnviromentConstants.class,
        BuildConfig.class, App.class, Utils.class, ContextCompat.class})
public class EnrollmentLiteProfileInformationUT {

    @Mock
    private AppCompatDelegate appDelegate;

    @Mock
    private App app;

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private EnrollmentLiteRequest enrollmentLiteRequest;

    @Mock
    private Intent intent;

    @Mock
    private Button btnContinue;
    @Mock
    private EditText editTextLastName;
    @Mock
    private EditText editTextEmail;
    @Mock
    private EditText editTextPhoneNumber;
    @Mock
    private SpinnerExtended spinnerProvider;

    @InjectMocks
    private EnrollmentLiteProfileInformation enrollmentLiteProfileInformation;


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ReportFragment.class);
        PowerMockito.mockStatic(R.class);
        PowerMockito.mockStatic(CookieSyncManager.class);
        PowerMockito.mockStatic(MiBancoConstants.class);
        PowerMockito.mockStatic(BuildConfig.class);
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(ContextCompat.class);
        //Need this to mock static MiBancoEnvironmentConstants
        PowerMockito.when(App.getApplicationInstance()).thenReturn(app);
        PowerMockito.when(app.getBaseContext()).thenReturn(context);
        PowerMockito.when(Utils.getSecuredSharedPreferences(context)).thenReturn(sharedPreferences);
        PowerMockito.mockStatic(MiBancoEnviromentConstants.class);

        enrollmentLiteProfileInformation = PowerMockito.spy(new EnrollmentLiteProfileInformation());

        PowerMockito.doReturn(appDelegate).when(enrollmentLiteProfileInformation).getDelegate();

        PowerMockito.when(enrollmentLiteProfileInformation.getIntent()).thenReturn(intent);

        PowerMockito.when(enrollmentLiteProfileInformation.findViewById(R.id.editTextPhoneNumber)).thenReturn(editTextPhoneNumber);

        PowerMockito.when(enrollmentLiteProfileInformation.findViewById(R.id.editTextLastName)).thenReturn(editTextLastName);

        PowerMockito.when(enrollmentLiteProfileInformation.findViewById(R.id.editTextEmail)).thenReturn(editTextEmail);

        PowerMockito.when(enrollmentLiteProfileInformation.findViewById(R.id.spinnerProvider)).thenReturn(spinnerProvider);

        PowerMockito.when(enrollmentLiteProfileInformation.findViewById(R.id.btnContinue)).thenReturn(btnContinue);

        doNothing().when(enrollmentLiteProfileInformation).setContentView(R.layout.activity_enrollment_lite_profile_information);

    }

    @Ignore
    @Test
    public void whenOnCreate_WithoutProcess_ThenFinishActivity() throws Exception{

        enrollmentLiteProfileInformation.onCreate(null);

        verify(enrollmentLiteProfileInformation, times(1)).onCreate(null);

    }

}
