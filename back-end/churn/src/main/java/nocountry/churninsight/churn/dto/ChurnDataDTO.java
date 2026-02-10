package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChurnDataDTO {
    @JsonProperty("gender")
    private String genero;

    @JsonProperty("SeniorCitizen")
    private int idoso;

    @JsonProperty("Partner")
    private String conjuge;

    @JsonProperty("Dependents")
    private String dependentes;

    @JsonProperty("tenure")
    private int tempoContrato;

    @JsonProperty("PhoneService")
    private String servicoTelefone;

    @JsonProperty("MultipleLines")
    private String multiplasLinhasTel;

    @JsonProperty("InternetService")
    private String servicoInternet;

    @JsonProperty("OnlineSecurity")
    private String segurancaOnline;

    @JsonProperty("OnlineBackup")
    private String backupOnline;

    @JsonProperty("DeviceProtection")
    private String protecaoDispositivo;

    @JsonProperty("TechSupport")
    private String suporteTecnico;

    @JsonProperty("StreamingTV")
    private String tvStreaming;

    @JsonProperty("StreamingMovies")
    private String filmesStreaming;

    @JsonProperty("Contract")
    private String tipoContrato;

    @JsonProperty("PaperlessBilling")
    private String faturaOnline;

    @JsonProperty("PaymentMethod")
    private String metodoPagamento;

    @JsonProperty("MonthlyCharges")
    private double valorMensal;

    @JsonProperty("TotalCharges")
    private double valorTotal;

    // Getters and setters

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getIdoso() {
        return idoso;
    }

    public void setIdoso(int idoso) {
        this.idoso = idoso;
    }

    public String getConjuge() {
        return conjuge;
    }

    public void setConjuge(String conjuge) {
        this.conjuge = conjuge;
    }

    public String getDependentes() {
        return dependentes;
    }

    public void setDependentes(String dependentes) {
        this.dependentes = dependentes;
    }

    public int getTempoContrato() {
        return tempoContrato;
    }

    public void setTempoContrato(int tempoContrato) {
        this.tempoContrato = tempoContrato;
    }

    public String getServicoTelefone() {
        return servicoTelefone;
    }

    public void setServicoTelefone(String servicoTelefone) {
        this.servicoTelefone = servicoTelefone;
    }

    public String getMultiplasLinhasTel() {
        return multiplasLinhasTel;
    }

    public void setMultiplasLinhasTel(String multiplasLinhasTel) {
        this.multiplasLinhasTel = multiplasLinhasTel;
    }

    public String getServicoInternet() {
        return servicoInternet;
    }

    public void setServicoInternet(String servicoInternet) {
        this.servicoInternet = servicoInternet;
    }

    public String getSegurancaOnline() {
        return segurancaOnline;
    }

    public void setSegurancaOnline(String segurancaOnline) {
        this.segurancaOnline = segurancaOnline;
    }

    public String getBackupOnline() {
        return backupOnline;
    }

    public void setBackupOnline(String backupOnline) {
        this.backupOnline = backupOnline;
    }

    public String getProtecaoDispositivo() {
        return protecaoDispositivo;
    }

    public void setProtecaoDispositivo(String protecaoDispositivo) {
        this.protecaoDispositivo = protecaoDispositivo;
    }

    public String getSuporteTecnico() {
        return suporteTecnico;
    }

    public void setSuporteTecnico(String suporteTecnico) {
        this.suporteTecnico = suporteTecnico;
    }

    public String getTvStreaming() {
        return tvStreaming;
    }

    public void setTvStreaming(String tvStreaming) {
        this.tvStreaming = tvStreaming;
    }

    public String getFilmesStreaming() {
        return filmesStreaming;
    }

    public void setFilmesStreaming(String filmesStreaming) {
        this.filmesStreaming = filmesStreaming;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getFaturaOnline() {
        return faturaOnline;
    }

    public void setFaturaOnline(String faturaOnline) {
        this.faturaOnline = faturaOnline;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
