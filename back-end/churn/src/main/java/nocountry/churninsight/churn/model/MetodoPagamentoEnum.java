package nocountry.churninsight.churn.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetodoPagamentoEnum {
    PIX("Pix"),
    TED("Ted"),
    BOLETO("Boleto"),
    DEBITO_EM_CONTA("Débito em conta"),
    CARTAO_CREDITO("Cartão de crédito");

    private final String valor;

    MetodoPagamentoEnum(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }
}
