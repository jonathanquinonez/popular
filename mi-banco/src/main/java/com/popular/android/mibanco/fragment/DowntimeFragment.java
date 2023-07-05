package com.popular.android.mibanco.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popular.android.mibanco.R;

public class DowntimeFragment extends Fragment {

    public static final String DOWNTIME_MESSAGE = "downtimeMessage";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.downtime, container, false);
        TextView message = view.findViewById(R.id.maintenance_message);

        if (getArguments() != null) {
            String downtimeMessage = getArguments().getString(DOWNTIME_MESSAGE);
            if (message != null && downtimeMessage != null) {
                message.setText(downtimeMessage);
            }
        }
        return view;
    }

    public static DowntimeFragment newInstance(String downtimeMessage) {
        DowntimeFragment fragment = new DowntimeFragment();
        Bundle args = new Bundle();
        args.putString(DOWNTIME_MESSAGE, downtimeMessage);
        fragment.setArguments(args);

        return fragment;
    }
}