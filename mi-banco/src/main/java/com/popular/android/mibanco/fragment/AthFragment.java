package com.popular.android.mibanco.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.ATHMUtils;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.Utils;

public class AthFragment extends Fragment implements View.OnClickListener{

    private Button btnContinue;
    private Context mContext;
    private String athmSsoToken;
    private boolean athmSsoBounded;
    private boolean btnWasClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.athm_welcome, container, false);

        BPAnalytics.logEvent(BPAnalytics.EVENT_ATHM_SSO_WELCOME_SPLASH);

        Bundle arguments = getArguments();
        if (arguments != null) {
            athmSsoToken = arguments.getString(MiBancoConstants.ATHM_SSO_TOKEN_KEY);
            athmSsoBounded = arguments.getBoolean(MiBancoConstants.ATHM_SSO_BOUND_KEY, false);
        }

        btnContinue = view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        btnContinue.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            btnWasClicked = true;
            Utils.saveAthmWelcomeSplash(mContext, true);
            ATHMUtils.verifyAthmAppVersion(mContext, athmSsoToken, athmSsoBounded, btnWasClicked);
            if(getFragmentManager() != null && btnWasClicked) {
                getFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }
}