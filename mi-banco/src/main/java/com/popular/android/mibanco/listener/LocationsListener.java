package com.popular.android.mibanco.listener;

import com.popular.android.mibanco.object.BankLocation;
import com.popular.android.mibanco.object.BankLocationDetail;

import java.util.List;

/**
 * Interface for locations listener
 */
public interface LocationsListener extends TaskListener {
    void updateATMs(List<BankLocation> atms);

    void updateBranches(List<BankLocation> branches);

    void updateLocations(List<BankLocation> atms, List<BankLocation> branches);

    void updateBranchDetail(BankLocationDetail branchDetail);
}
