package com.popular.android.mibanco.model;

import java.util.ArrayList;

/**
 * Class that represents an Ebill response
 */
public class EBills extends BaseResponse {

    private EBillsContent content;
    private EBillsFlags flags;

    public ArrayList<EBillsItem> getEbppInbox() {
        return content.ebppInbox;
    }

    public boolean hasNoBillers() {
        return flags.noBillers;
    }

    public boolean hasNoInbox() {
        return flags.noInbox;
    }

    private class EBillsContent {

        private ArrayList<EBillsItem> ebppInbox;
    }

    private class EBillsFlags {

        private boolean noBillers;
        private boolean noInbox;
    }
}
