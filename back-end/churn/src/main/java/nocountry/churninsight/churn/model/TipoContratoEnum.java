package nocountry.churninsight.churn.model;

public enum TipoContratoEnum {
    MENSAL("mensal"),
    ANUAL("anual"),
    BI_ANUAL("bi anual");

    private final String valor;

    TipoContratoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
