package com.popular.android.mibanco.object;

public class BankLocation {

    private int id = -1;
    private String prId;
    private int prefix;
    private String name;
    private String city;
    private String street1;
    private String street2;
    private String state;
    private String zipCode;
    private double latitude;
    private double longitude;
    private String location;
    private boolean euro;
    private boolean singleCheckDeposit;
    private boolean cashDeposit;
    private boolean voiceGuidance;
    private boolean popularOne;
    private boolean popularMortgage;
    private boolean popularSecurities;
    private boolean commercialLine;
    private boolean nightDeposit;
    private boolean autoBanco;

    private Float distance;
    private String direction;

    private boolean selected;
    private String type;

    public BankLocation(){}
    public BankLocation(final int id, final String type, final Double lat, final Double lon) {
        this.id = id;
        latitude = lat;
        longitude = lon;
        this.type = type;
    }

    public Float getDistance() {
        return distance;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setDistance(final Float dist) {
        distance = dist;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    public int getPrefix() {
        return prefix;
    }

    public void setPrefix(int prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEuro() {
        return euro;
    }

    public void setEuro(boolean euro) {
        this.euro = euro;
    }

    public boolean isSingleCheckDeposit() {
        return singleCheckDeposit;
    }

    public void setSingleCheckDeposit(boolean singleCheckDeposit) {
        this.singleCheckDeposit = singleCheckDeposit;
    }

    public boolean isCashDeposit() {
        return cashDeposit;
    }

    public void setCashDeposit(boolean cashDeposit) {
        this.cashDeposit = cashDeposit;
    }

    public boolean isVoiceGuidance() {
        return voiceGuidance;
    }

    public void setVoiceGuidance(boolean voiceGuidance) {
        this.voiceGuidance = voiceGuidance;
    }

    public boolean isPopularOne() {
        return popularOne;
    }

    public void setPopularOne(boolean popularOne) {
        this.popularOne = popularOne;
    }

    public boolean isPopularMortgage() {
        return popularMortgage;
    }

    public void setPopularMortgage(boolean popularMortgage) {
        this.popularMortgage = popularMortgage;
    }

    public boolean isPopularSecurities() {
        return popularSecurities;
    }

    public void setPopularSecurities(boolean popularSecurities) {
        this.popularSecurities = popularSecurities;
    }

    public boolean isCommercialLine() {
        return commercialLine;
    }

    public void setCommercialLine(boolean commercialLine) {
        this.commercialLine = commercialLine;
    }

    public boolean isNightDeposit() {
        return nightDeposit;
    }

    public void setNightDeposit(boolean nightDeposit) {
        this.nightDeposit = nightDeposit;
    }

    public boolean isAutoBanco() {
        return autoBanco;
    }

    public void setAutoBanco(boolean autoBanco) {
        this.autoBanco = autoBanco;
    }
}
