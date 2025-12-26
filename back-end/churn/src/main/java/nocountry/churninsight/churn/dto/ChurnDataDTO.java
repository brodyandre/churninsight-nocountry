package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ChurnDataDTO {
    @JsonProperty("gender")
    @NotBlank(message = "Gênero é obrigatório")
    @Pattern(regexp = "Masculino|Feminino", message = "Gênero deve ser 'Masculino' ou 'Feminino'")
    private String genero;

    @JsonProperty("SeniorCitizen")
    @Min(value = 0, message = "Idoso deve ser 0 ou 1")
    @Max(value = 1, message = "Idoso deve ser 0 ou 1")
    private Integer idoso;

    @JsonProperty("Partner")
    @NotBlank(message = "Cônjuge é obrigatório")
    @Pattern(regexp = "Sim|Não", message = "Cônjuge deve ser 'Sim' ou 'Não'")
    private String conjuge;

    @JsonProperty("Dependents")
    @NotBlank(message = "Dependentes é obrigatório")
    @Pattern(regexp = "Sim|Não", message = "Dependentes deve ser 'Sim' ou 'Não'")
    private String dependentes;

    @JsonProperty("tenure")
    @Min(value = 0, message = "Tempo de contrato não pode ser negativo")
    @Max(value = 72, message = "Tempo de contrato não pode exceder 72 meses")
    private Integer tempoContrato;

    @JsonProperty("PhoneService")
    @NotBlank(message = "Serviço de Telefone é obrigatório")
    @Pattern(regexp = "Sim|Não", message = "Serviço de Telefone deve ser 'Sim' ou 'Não'")
    private String servicoTelefone;

    @JsonProperty("MultipleLines")
    @NotBlank(message = "Múltiplas Linhas é obrigatório")
    @Pattern(regexp = "Sim|Não|Sem serviço de telefone", message = "Múltiplas Linhas deve ser 'Sim', 'Não' ou 'Sem serviço de telefone'")
    private String multiplasLinhasTel;

    @JsonProperty("InternetService")
    @NotBlank(message = "Serviço de Internet é obrigatório")
    @Pattern(regexp = "DSL|Fibra Ótica|Nenhum", message = "Serviço de Internet deve ser 'DSL', 'Fibra Ótica' ou 'Nenhum'")
    private String servicoInternet;

    @JsonProperty("OnlineSecurity")
    @NotBlank(message = "Segurança Online é obrigatória")
    @Pattern(regexp = "Sim|Não|Sem serviço de internet", message = "Segurança Online deve ser 'Sim', 'Não' ou 'Sem serviço de internet'")
    private String segurancaOnline;

    @JsonProperty("OnlineBackup")
    @NotBlank(message = "Backup Online é obrigatório")
    @Pattern(regexp = "Sim|Não|Sem serviço de internet", message = "Backup Online deve ser 'Sim', 'Não' ou 'Sem serviço de internet'")
    private String backupOnline;

    @JsonProperty("DeviceProtection")
    @NotBlank(message = "Proteção de Dispositivo é obrigatória")
    @Pattern(regexp = "Sim|Não|Sem serviço de internet", message = "Proteção de Dispositivo deve ser 'Sim', 'No' ou 'Sem serviço de internet'")
    private String protecaoDispositivo;

    @JsonProperty("TechSupport")
    @NotBlank(message = "Suporte Técnico é obrigatório")
    @Pattern(regexp = "Sim|Não|Sem serviço de internet", message = "Suporte Técnico deve ser 'Sim', 'Não' ou 'Sem serviço de internete'")
    private String suporteTecnico;

    @JsonProperty("StreamingTV")
    @NotBlank(message = "TV Streaming é obrigatória")
    @Pattern(regexp = "Sim|Não|Sem serviço de internet", message = "TV Streaming deve ser 'Sim', 'Não' ou 'Sem serviço de internet'")
    private String tvStreaming;

    @JsonProperty("StreamingMovies")
    @NotBlank(message = "Filmes Streaming é obrigatório")
    @Pattern(regexp = "Sim|Não|Sem serviço de internet", message = "Filmes Streaming deve ser 'Sim', 'Não' ou 'Sem serviço de internet'")
    private String filmesStreaming;

    @JsonProperty("Contract")
    @NotBlank(message = "Tipo de Contrato é obrigatório")
    @Pattern(regexp = "Mensal|Anual|Bianual", message = "Tipo de Contrato deve ser 'Mensal', 'Anual' ou 'Bianual'")
    private String tipoContrato;

    @JsonProperty("PaperlessBilling")
    @NotBlank(message = "Fatura Online é obrigatória")
    @Pattern(regexp = "Sim|Não", message = "Fatura Online deve ser 'Sim' ou 'No'")
    private String faturaOnline;

    @JsonProperty("PaymentMethod")
    @NotBlank(message = "Método de Pagamento é obrigatório")
    @Pattern(regexp = "Pix|Ted|Boleto|Débito em conta|Cartão de crédito", message = "Método de Pagamento inválido")
    private String metodoPagamento;

    @JsonProperty("MonthlyCharges")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor mensal não pode ser negativo")
    @DecimalMax(value = "9999.99", message = "Valor mensal muito alto")
    private Double valorMensal;

    @JsonProperty("TotalCharges")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor total não pode ser negativo")
    @DecimalMax(value = "99999.99", message = "Valor total muito alto")
    private Double valorTotal;

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

    public Integer getTempoContrato() {
        return tempoContrato;
    }

    public void setTempoContrato(Integer tempoContrato) {
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

    public void setValorMensal(Double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
