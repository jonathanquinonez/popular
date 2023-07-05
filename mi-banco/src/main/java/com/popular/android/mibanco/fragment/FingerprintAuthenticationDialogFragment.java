package com.popular.android.mibanco.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.EasyCashStaging;
import com.popular.android.mibanco.activity.EnterUsername;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.util.FingerprintUiHelper;

/**
 *A dialog which uses fingerprint APIs to authenticate the user,and falls back to password
 *authentication if fingerprint is not available.
 */
@TargetApi(23)
public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements TextView.OnEditorActionListener, FingerprintUiHelper.Callback {

    private Button mCancelButton;
    private View mFingerprintContent;
    private boolean isAuthenticated = false;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private BaseActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppCompatAlertDialogStyle/*android.R.style.Theme_Material_Light_Dialog*/);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.sign_in));
        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setTextColor(ContextCompat.getColor(getContext(), R.color.athm_header));
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        mFingerprintContent = v.findViewById(R.id.fingerprint_container);

        if(App.getApplicationInstance().getFingerprintSectionId() == 0) { //ENTER USERNAME
            mFingerprintUiHelper = new FingerprintUiHelper(
                    mActivity.getSystemService(FingerprintManager.class),
                    (ImageView) v.findViewById(R.id.fingerprint_icon),
                    (TextView) v.findViewById(R.id.fingerprint_description),
                    (TextView) v.findViewById(R.id.fingerprint_status), 0, this);

        }else{ // EASYCASH STAGING

            TextView description = (TextView) v.findViewById(R.id.fingerprint_description);
            description.setText(getResources().getString(R.string.fingerprint_easycash_description));
            mFingerprintUiHelper = new FingerprintUiHelper(
                    mActivity.getSystemService(FingerprintManager.class),
                    (ImageView) v.findViewById(R.id.fingerprint_icon),
                    description,
                    (TextView) v.findViewById(R.id.fingerprint_status),3, this);
        }
        updateStage();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EnterUsername) {
            mActivity = (EnterUsername) activity;
        }else{
            mActivity = (EasyCashStaging)activity;
        }
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }




    /**
     * @return true if {@code password} is correct, false otherwise
     */
    private boolean checkPassword(String password) {
        // Assume the password is always correct.
        // In the real world situation, the password needs to be verified in the server side.
        return password.length() > 0;
    }


    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel);
                mFingerprintContent.setVisibility(View.VISIBLE);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_GO);
    }


    @Override
    public void onAuthenticated() {
        isAuthenticated = true;
        if(mActivity instanceof EnterUsername){
            ((EnterUsername)mActivity).onFingerprintAuthSuccess(true);
        }else{
            ((EasyCashStaging)mActivity).onFingerprintAuthSuccess(true);
        }

        if(mFingerprintUiHelper != null) {
            mFingerprintUiHelper.stopListening();
        }
        dismissAllowingStateLoss();
    }

    @Override
    public void onError() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
            if(!isAuthenticated){
            if(App.getApplicationInstance().getFingerprintSectionId() == 0){
                ((EnterUsername)mActivity).onFingerprintAuthCanceled();
            }else{
                ((EasyCashStaging)mActivity).onFingerprintAuthCanceled();
            }
        }
    }

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED
    }
}
