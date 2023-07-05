package com.popular.android.mibanco.object;

public class LocationProperties {

    private String atmHours;
    private String city;
    private String facilityDescription;
    private String facilityHours;
    private String facilityName;
    private String facilityType;
    private String fax;
    private String hoursEn;
    private String hoursEs;
    private String layer;
    private String phone;
    private String state;
    private String street1;
    private String street2;
    private String zipCode;

    public LocationProperties(final LocationProperties source) {
        city = source.getCity();
        zipCode = source.getZipCode();
        atmHours = source.getAtmHours();
        street1 = source.getStreet1();
        street2 = source.getStreet2();
        hoursEn = source.getHoursEn();
        facilityHours = source.getFacilityHours();
        facilityName = source.getFacilityName();
        phone = source.getPhone();
        fax = source.getFax();
        facilityDescription = source.getFacilityDescription();
        layer = source.getLayer();
        facilityType = source.getFacilityType();
        hoursEs = source.getHoursEs();
        state = source.getState();
    }

    public LocationProperties(final String city, final String zipCode, final String atmHours, final String street1, final String street2, final String hoursEn, final String facilityHours,
            final String facilityName, final String phone, final String fax, final String facilityDescription, final String layer, final String facilityType, final String hoursEs, final String state) {
        this.city = city;
        this.zipCode = zipCode;
        this.atmHours = atmHours;
        this.street1 = street1;
        this.street2 = street2;
        this.hoursEn = hoursEn;
        this.facilityHours = facilityHours;
        this.facilityName = facilityName;
        this.phone = phone;
        this.fax = fax;
        this.facilityDescription = facilityDescription;
        this.layer = layer;
        this.facilityType = facilityType;
        this.hoursEs = hoursEs;
        this.state = state;
    }

    /**
     * @return the atmHours
     */
    public String getAtmHours() {
        return atmHours;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the facilityDescription
     */
    public String getFacilityDescription() {
        return facilityDescription;
    }

    /**
     * @return the facilityHours
     */
    public String getFacilityHours() {
        return facilityHours;
    }

    /**
     * @return the facilityName
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * @return the facilityType
     */
    public String getFacilityType() {
        return facilityType;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @return the hours_en
     */
    public String getHoursEn() {
        return hoursEn;
    }

    /**
     * @return the hours_es
     */
    public String getHoursEs() {
        return hoursEs;
    }

    /**
     * @return the layer
     */
    public String getLayer() {
        return layer;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @return the street1
     */
    public String getStreet1() {
        return street1;
    }

    /**
     * @return the street2
     */
    public String getStreet2() {
        return street2;
    }

    /**
     * @return the zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param atmHours the atmHours to set
     */
    public void setAtmHours(final String atmHours) {
        this.atmHours = atmHours;
    }

    /**
     * @param city the city to set
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * @param facilityDescription the facilityDescription to set
     */
    public void setFacilityDescription(final String facilityDescription) {
        this.facilityDescription = facilityDescription;
    }

    /**
     * @param facilityHours the facilityHours to set
     */
    public void setFacilityHours(final String facilityHours) {
        this.facilityHours = facilityHours;
    }

    /**
     * @param facilityName the facilityName to set
     */
    public void setFacilityName(final String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * @param facilityType the facilityType to set
     */
    public void setFacilityType(final String facilityType) {
        this.facilityType = facilityType;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(final String fax) {
        this.fax = fax;
    }

    /**
     * @param hoursEn the hours_en to set
     */
    public void setHoursEn(final String hoursEn) {
        this.hoursEn = hoursEn;
    }

    /**
     * @param hoursEs the hours_es to set
     */
    public void setHoursEs(final String hoursEs) {
        this.hoursEs = hoursEs;
    }

    /**
     * @param layer the layer to set
     */
    public void setLayer(final String layer) {
        this.layer = layer;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * @param state the state to set
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * @param street1 the street1 to set
     */
    public void setStreet1(final String street1) {
        this.street1 = street1;
    }

    /**
     * @param street2 the street2 to set
     */
    public void setStreet2(final String street2) {
        this.street2 = street2;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }
}
