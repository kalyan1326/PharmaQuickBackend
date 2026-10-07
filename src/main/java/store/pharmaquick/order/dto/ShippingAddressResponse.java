package store.pharmaquick.order.dto;

public class ShippingAddressResponse {

    private Long addressId;

    private String fullName;

    private String mobile;

    private String addressLine;

    private String city;

    private String state;

    private String pincode;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ShippingAddressResponse() {
    }

    public ShippingAddressResponse(
            Long addressId,
            String fullName,
            String mobile,
            String addressLine,
            String city,
            String state,
            String pincode) {

        this.addressId = addressId;
        this.fullName = fullName;
        this.mobile = mobile;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}