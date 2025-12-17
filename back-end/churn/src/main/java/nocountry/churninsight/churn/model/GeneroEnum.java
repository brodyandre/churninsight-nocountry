package nocountry.churninsight.churn.model;

public enum GeneroEnum {
    MASCULINO("masculino"),
    FEMININO("feminino");

    private final String valor;

    GeneroEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
