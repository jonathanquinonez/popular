package com.popular.android.mibanco.view.pickerview;

import org.apache.commons.lang3.StringUtils;

public class ArrayBankWheelItem {

    private String amount;
    private boolean balanceRed;
    private String code;
    private final String id;
    private String imgPath;
    private int imgResource;
    private String name;
    private int payeeId;
    private String title;
    private String lastPayment;
    private String lastPaymentDate;

    /**
     * Real Time Payee
     */
    private String rtNotification; // real time notification

    /**
     * If Payee Has Payment History
     */
    private String rtHasPaymentHistory; // payee has payment history

    /**
     * Constructor
     * @param mId id value
     */
    public ArrayBankWheelItem(final String mId) {
        this(mId, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                0, 0, false, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    /**
     * ArrayBankWheelItem Constructor
     * @param id id value
     * @param name name value
     * @param amount amount value
     * @param code code value
     * @param title title value
     * @param payeeId payeeid value
     * @param imgResource imgResource value
     * @param balanceRed balanceRed value
     * @param rtNotification rtNotification value
     * @param rtHasPaymentHistory rtHasPaymentHistory value
     */
    public ArrayBankWheelItem(final String id, final String name, final String amount,
                              final String code, final String title, final int payeeId,
                              final int imgResource, final boolean balanceRed,
                              final String rtNotification, final String rtHasPaymentHistory) {
        this(amount, balanceRed, code, id, StringUtils.EMPTY, imgResource, name, payeeId, title,
                StringUtils.EMPTY, StringUtils.EMPTY, rtNotification, rtHasPaymentHistory);
    }

    /**
     * ArrayBankWheelItem Constructor value
     * @param amount amount value
     * @param balanceRed balanceRed value
     * @param code code value
     * @param id id value
     * @param imgPath imgPath value
     * @param imgResource imgResource value
     * @param name name value
     * @param payeeId payeeId value
     * @param title title value
     * @param lastPayment lastPayment value
     * @param lastPaymentDate lastPaymentDate value
     * @param rtNotification rtNotification value
     * @param rtHasPaymentHistory rtHasPaymentHistory value
     */
    private ArrayBankWheelItem(String amount, boolean balanceRed, String code, String id,
                              String imgPath, int imgResource, String name, int payeeId,
                              String title, String lastPayment, String lastPaymentDate,
                              String rtNotification, String rtHasPaymentHistory) {
        this.amount = amount;
        this.balanceRed = balanceRed;
        this.code = code;
        this.id = id;
        this.imgPath = imgPath;
        this.imgResource = imgResource;
        this.name = name;
        this.payeeId = payeeId;
        this.title = title;
        this.lastPayment = lastPayment;
        this.lastPaymentDate = lastPaymentDate;
        this.rtNotification = rtNotification;
        this.rtHasPaymentHistory = rtHasPaymentHistory;
    }

    /**
     * getAmount
     * @return String
     */
    public String getAmount() {
        return amount;
    }

    /**
     * getCode
     * @return String
     */
    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getName() {
        return name;
    }

    public int getPayeeId() {
        return payeeId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isBalanceRed() {
        return balanceRed;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public void setBalanceRed(final boolean balanceRed) {
        this.balanceRed = balanceRed;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setImgPath(final String imgPath) {
        this.imgPath = imgPath;
    }

    public void setImgResource(final int img) {
        imgResource = img;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setPayeeId(final int payeeId) {
        this.payeeId = payeeId;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setRtNotificatiion(final String rtNotification) {
        this.rtNotification = rtNotification;
    }

    /**
     * rtNotification
     * @return getRtNotification
     */
    public String getRtNotification() {
        return rtNotification;
    }

    /**
     * getLastPayment
     * @return String
     */
    public String getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(String lastPayment) {
        this.lastPayment = lastPayment;
    }

    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    /**
     * setRtHasPaymentHistory
     * @param rtHasPaymentHistory
     */
    public void setRtHasPaymentHistory(final String rtHasPaymentHistory) {
        this.rtHasPaymentHistory = rtHasPaymentHistory;
    }

    /**
     * getRtHasPaymentHistory
     * @return String
     */
    public String getRtHasPaymentHistory() {
        return rtHasPaymentHistory;
    }
}
