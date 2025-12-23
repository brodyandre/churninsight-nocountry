package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsDTO {

    @JsonProperty("total_clientes")
    private long totalClientes;

    @JsonProperty("media_valor_mensal")
    private double mediaValorMensal;

    public StatsDTO(long totalClientes, double mediaValorMensal) {
        this.totalClientes = totalClientes;
        this.mediaValorMensal = mediaValorMensal;
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public double getMediaValorMensal() {
        return mediaValorMensal;
    }

    public void setMediaValorMensal(double mediaValorMensal) {
        this.mediaValorMensal = mediaValorMensal;
    }
}
