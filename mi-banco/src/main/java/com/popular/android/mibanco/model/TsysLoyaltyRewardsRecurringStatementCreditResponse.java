package com.popular.android.mibanco.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TsysLoyaltyRewardsRecurringStatementCredit Response
 * @author leandro.baleriani
 * @version 1.0
 * @since 1.0
 * @see com.popular.android.mibanco.activity.AutomaticRedemptionActivity
 *
 */
public class TsysLoyaltyRewardsRecurringStatementCreditResponse implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 834382103313510311L; // serialVersionUID

    /**
     * itemCode
     */
    private String itemCode; // String value

    /**
     * recurringCashBackType
     */
    private String recurringCashBackType; // String value

    /**
     * identifiers
     */
    private Map<String, Object> identifiers = Collections.emptyMap(); // Map value

    /**
     * rewardsBalance
     */
    private Map<String, Object> rewardsBalance = Collections.emptyMap(); // Map value

    /**
     *
     * @return rewardsBalance
     */
    public Map<String, Object> getRewardsBalance () {
        return Collections.unmodifiableMap(rewardsBalance);
    }

    /**
     *
     * @param rewardsBalance
     */
    public void setRewardsBalance (Map<String, Object> rewardsBalance) {

        rewardsBalance = new HashMap<>(rewardsBalance);
        this.rewardsBalance = Collections.unmodifiableMap(rewardsBalance);
    }

    /**
     *
     * @return identifiers
     */
    public Map<String, Object> getIdentifiers () {

        return Collections.unmodifiableMap(identifiers);
    }

    /**
     *
     * @param identifiers
     */
    public void setIdentifiers (Map<String, Object> identifiers) {

        identifiers = new HashMap<>(identifiers);
        this.identifiers = Collections.unmodifiableMap(identifiers);
    }

    /**
     *
     * @return itemCode
     */
    public String getItemCode () {
        return itemCode;
    }

    /**
     *
     * @param strItemCode
     */
    public void setItemCode (String strItemCode) {
        this.itemCode = strItemCode;
    }

    /**
     *
     * @return recurringCashBackType
     */
    public String getRecurringCashBackType () {
        return recurringCashBackType;
    }

    /**
     *
     * @param strRecurringCashBackType
     */
    public void setRecurringCashBackType (String strRecurringCashBackType) {

        this.recurringCashBackType = strRecurringCashBackType;
    }
}
