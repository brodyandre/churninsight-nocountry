package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChurnDataDTO {
    @JsonProperty("gender")
    private String gender;

    @JsonProperty("SeniorCitizen")
    private int seniorCitizen;

    @JsonProperty("Partner")
    private String partner;

    @JsonProperty("Dependents")
    private String dependents;

    @JsonProperty("tenure")
    private int tenure;

    @JsonProperty("PhoneService")
    private String phoneService;

    @JsonProperty("MultipleLines")
    private String multipleLines;

    @JsonProperty("InternetService")
    private String internetService;

    @JsonProperty("OnlineSecurity")
    private String onlineSecurity;

    @JsonProperty("OnlineBackup")
    private String onlineBackup;

    @JsonProperty("DeviceProtection")
    private String deviceProtection;

    @JsonProperty("TechSupport")
    private String techSupport;

    @JsonProperty("StreamingTV")
    private String streamingTV;

    @JsonProperty("StreamingMovies")
    private String streamingMovies;

    @JsonProperty("Contract")
    private String contract;

    @JsonProperty("PaperlessBilling")
    private String paperlessBilling;

    @JsonProperty("PaymentMethod")
    private String paymentMethod;

    @JsonProperty("MonthlyCharges")
    private double monthlyCharges;

    @JsonProperty("TotalCharges")
    private double totalCharges;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSeniorCitizen() {
        return seniorCitizen;
    }

    public void setSeniorCitizen(int seniorCitizen) {
        this.seniorCitizen = seniorCitizen;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getDependents() {
        return dependents;
    }

    public void setDependents(String dependents) {
        this.dependents = dependents;
    }

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }

    public String getPhoneService() {
        return phoneService;
    }

    public void setPhoneService(String phoneService) {
        this.phoneService = phoneService;
    }

    public String getMultipleLines() {
        return multipleLines;
    }

    public void setMultipleLines(String multipleLines) {
        this.multipleLines = multipleLines;
    }

    public String getInternetService() {
        return internetService;
    }

    public void setInternetService(String internetService) {
        this.internetService = internetService;
    }

    public String getOnlineSecurity() {
        return onlineSecurity;
    }

    public void setOnlineSecurity(String onlineSecurity) {
        this.onlineSecurity = onlineSecurity;
    }

    public String getOnlineBackup() {
        return onlineBackup;
    }

    public void setOnlineBackup(String onlineBackup) {
        this.onlineBackup = onlineBackup;
    }

    public String getDeviceProtection() {
        return deviceProtection;
    }

    public void setDeviceProtection(String deviceProtection) {
        this.deviceProtection = deviceProtection;
    }

    public String getTechSupport() {
        return techSupport;
    }

    public void setTechSupport(String techSupport) {
        this.techSupport = techSupport;
    }

    public String getStreamingTV() {
        return streamingTV;
    }

    public void setStreamingTV(String streamingTV) {
        this.streamingTV = streamingTV;
    }

    public String getStreamingMovies() {
        return streamingMovies;
    }

    public void setStreamingMovies(String streamingMovies) {
        this.streamingMovies = streamingMovies;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getPaperlessBilling() {
        return paperlessBilling;
    }

    public void setPaperlessBilling(String paperlessBilling) {
        this.paperlessBilling = paperlessBilling;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getMonthlyCharges() {
        return monthlyCharges;
    }

    public void setMonthlyCharges(double monthlyCharges) {
        this.monthlyCharges = monthlyCharges;
    }

    public double getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(double totalCharges) {
        this.totalCharges = totalCharges;
    }
}
