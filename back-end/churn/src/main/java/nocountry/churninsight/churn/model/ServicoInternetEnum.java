package nocountry.churninsight.churn.model;

public enum ServicoInternetEnum {
    DSL("DSL"),
    FIBRA_OTICA("Fibra Otica"),
    NENHUM("nenhum");

    private final String valor;

    ServicoInternetEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
