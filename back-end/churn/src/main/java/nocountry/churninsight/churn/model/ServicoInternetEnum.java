package nocountry.churninsight.churn.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServicoInternetEnum {
    DSL("DSL"),
    FIBRA_OTICA("Fibra Ótica"),
    NENHUM("Nenhum");

    private final String valor;

    ServicoInternetEnum(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }
}
