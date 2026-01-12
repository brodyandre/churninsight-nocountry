package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PredictDTO {

    @JsonProperty("previsao")
    @NotBlank(message = "Previsão é obrigatória")
    @Pattern(regexp = "Vai cancelar|Vai continuar", message = "Previsão deve ser 'Vai cancelar' ou 'Vai continuar'")
    private String previsao;

    @JsonProperty("probabilidade")
    @NotNull(message = "Probabilidade é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Probabilidade não pode ser negativa")
    @DecimalMax(value = "1.0", inclusive = true, message = "Probabilidade não pode ser maior que 1")
    private Double probabilidade;

    @JsonProperty("confianca")
    @NotNull(message = "Confiança é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Confiança não pode ser negativa")
    @DecimalMax(value = "1.0", inclusive = true, message = "Confiança não pode ser maior que 1")
    private Double confianca;

    public PredictDTO() {
    }

    public PredictDTO(String previsao, Double probabilidade) {
        this.previsao = previsao;
        this.probabilidade = probabilidade;
        this.confianca = (probabilidade != null) ? Math.abs(probabilidade - 0.5) * 2 : 0.0;
    }

    public PredictDTO(String previsao, Double probabilidade, Double confianca) {
        this.previsao = previsao;
        this.probabilidade = probabilidade;
        this.confianca = confianca;
    }

    public String getPrevisao() { return previsao; }

    public void setPrevisao(String previsao) {
        this.previsao = previsao;
    }

    public Double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
    }

    public Double getConfianca() {
        return confianca;
    }

    public void setConfianca(Double confianca) {
        this.confianca = confianca;
    }
}
