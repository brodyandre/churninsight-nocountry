package nocountry.churninsight.churn.model;

public enum MetodoPagamentoEnum {
    PIX("Pix"),
    TED("Ted"),
    BOLETO("boleto"),
    DEBITO_EM_CONTA("debito em conta"),
    CARTAO_CREDITO("cartão de credito");

    private final String valor;

    MetodoPagamentoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
