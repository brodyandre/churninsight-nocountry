package nocountry.churninsight.churn.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GeneroEnum {
    MASCULINO("Masculino"),
    FEMININO("Feminino");

    private final String valor;

    GeneroEnum(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }
}
