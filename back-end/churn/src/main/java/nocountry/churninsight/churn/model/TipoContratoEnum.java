package nocountry.churninsight.churn.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoContratoEnum {
    MENSAL("Mensal"),
    ANUAL("Anual"),
    BIANUAL("Bianual");

    private final String valor;

    TipoContratoEnum(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }
}
