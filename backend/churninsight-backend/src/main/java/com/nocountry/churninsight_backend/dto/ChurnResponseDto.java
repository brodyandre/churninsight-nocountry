package com.nocountry.churninsight_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChurnResponseDto {

    @JsonProperty("previsao")
    private String previsao;

    @JsonProperty("probabilidade")
    private Double probabilidade;

    public ChurnResponseDto() {}

    public String getPrevisao() { return previsao; }
    public void setPrevisao(String previsao) { this.previsao = previsao; }

    public Double getProbabilidade() { return probabilidade; }
    public void setProbabilidade(Double probabilidade) { this.probabilidade = probabilidade; }
}
