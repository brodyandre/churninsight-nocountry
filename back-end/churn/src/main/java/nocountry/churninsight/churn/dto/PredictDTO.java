package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class PredictDTO {

    @JsonProperty("previsao")
    @NotBlank(message = "Previsão é obrigatória")
    @Pattern(regexp = "Vai cancelar|Vai continuar", message = "Previsão deve ser 'Vai cancelar' ou 'Vai continuar'")
    private String previsao;

    @JsonProperty("probabilidade")
    @DecimalMin(value = "0.0", inclusive = true, message = "Probabilidade não pode ser negativa")
    @DecimalMax(value = "1.0", inclusive = true, message = "Probabilidade não pode ser maior que 1")
    private Double probabilidade;

    public PredictDTO(String previsao, double probabilidade) {
        this.previsao = previsao;
        this.probabilidade = probabilidade;
    }

    public String getPrevisao() {
        return previsao;
    }

    public void setPrevisao(String previsao) {
        this.previsao = previsao;
    }

    public double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
    }
}
