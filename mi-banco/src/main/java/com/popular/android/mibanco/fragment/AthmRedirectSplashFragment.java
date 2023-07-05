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

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.util.ATHMUtils;

public class AthmRedirectSplashFragment extends Fragment implements View.OnClickListener {

    private Button btnContinue;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.athm_redirect, container, false);

        mContext = getContext();
        btnContinue = view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);
        btnContinue.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinue) {
            ATHMUtils.redirectToStore(mContext);
        }
    }
}