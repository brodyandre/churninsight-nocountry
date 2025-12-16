package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictDTO {

    @JsonProperty("previsao")
    private String previsao;

    @JsonProperty("probabilidade")
    private double probabilidade;

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

    public void setProbabilidade(double probabilidade) {
        this.probabilidade = probabilidade;
    }
}
