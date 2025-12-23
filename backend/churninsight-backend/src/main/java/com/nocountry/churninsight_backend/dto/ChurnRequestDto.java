package com.nocountry.churninsight_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChurnRequestDto {

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("SeniorCitizen")
    private Integer seniorCitizen;

    @JsonProperty("Partner")
    private String partner;

    @JsonProperty("Dependents")
    private String dependents;

    @JsonProperty("tenure")
    private Integer tenure;

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
    private Double monthlyCharges;

    @JsonProperty("TotalCharges")
    private Double totalCharges;

    public ChurnRequestDto() {}

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getSeniorCitizen() { return seniorCitizen; }
    public void setSeniorCitizen(Integer seniorCitizen) { this.seniorCitizen = seniorCitizen; }

    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }

    public String getDependents() { return dependents; }
    public void setDependents(String dependents) { this.dependents = dependents; }

    public Integer getTenure() { return tenure; }
    public void setTenure(Integer tenure) { this.tenure = tenure; }

    public String getPhoneService() { return phoneService; }
    public void setPhoneService(String phoneService) { this.phoneService = phoneService; }

    public String getMultipleLines() { return multipleLines; }
    public void setMultipleLines(String multipleLines) { this.multipleLines = multipleLines; }

    public String getInternetService() { return internetService; }
    public void setInternetService(String internetService) { this.internetService = internetService; }

    public String getOnlineSecurity() { return onlineSecurity; }
    public void setOnlineSecurity(String onlineSecurity) { this.onlineSecurity = onlineSecurity; }

    public String getOnlineBackup() { return onlineBackup; }
    public void setOnlineBackup(String onlineBackup) { this.onlineBackup = onlineBackup; }

    public String getDeviceProtection() { return deviceProtection; }
    public void setDeviceProtection(String deviceProtection) { this.deviceProtection = deviceProtection; }

    public String getTechSupport() { return techSupport; }
    public void setTechSupport(String techSupport) { this.techSupport = techSupport; }

    public String getStreamingTV() { return streamingTV; }
    public void setStreamingTV(String streamingTV) { this.streamingTV = streamingTV; }

    public String getStreamingMovies() { return streamingMovies; }
    public void setStreamingMovies(String streamingMovies) { this.streamingMovies = streamingMovies; }

    public String getContract() { return contract; }
    public void setContract(String contract) { this.contract = contract; }

    public String getPaperlessBilling() { return paperlessBilling; }
    public void setPaperlessBilling(String paperlessBilling) { this.paperlessBilling = paperlessBilling; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getMonthlyCharges() { return monthlyCharges; }
    public void setMonthlyCharges(Double monthlyCharges) { this.monthlyCharges = monthlyCharges; }

    public Double getTotalCharges() { return totalCharges; }
    public void setTotalCharges(Double totalCharges) { this.totalCharges = totalCharges; }
}
