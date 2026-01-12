package nocountry.churninsight.churn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsDTO {

    @JsonProperty("total_clientes")
    private Long totalClients;

    @JsonProperty("total_previsoes")
    private Long totalPredictions;

    @JsonProperty("taxa_churn")
    private Double churnRate;

    @JsonProperty("clientes_retidos")
    private Long retainedClients;

    @JsonProperty("clientes_churn")
    private Long churnedClients;

    // Construtor privado para o Builder
    private StatsDTO(Builder builder) {
        this.totalClients = builder.totalClients;
        this.totalPredictions = builder.totalPredictions;
        this.churnRate = builder.churnRate;
        this.retainedClients = builder.retainedClients;
        this.churnedClients = builder.churnedClients;
    }

    public StatsDTO() {
    }

    public StatsDTO(long total, double media) {
        this.totalClients = total;
        this.churnRate = media;
    }

    // Getters
    public Long getTotalClients() {
        return totalClients;
    }

    public Long getTotalPredictions() {
        return totalPredictions;
    }

    public Double getChurnRate() {
        return churnRate;
    }

    public Long getRetainedClients() {
        return retainedClients;
    }

    public Long getChurnedClients() {
        return churnedClients;
    }

    // Método estático para iniciar o Builder
    public static Builder builder() {
        return new Builder();
    }

    // Classe estática interna Builder
    public static class Builder {
        private Long totalClients;
        private Long totalPredictions;
        private Double churnRate;
        private Long retainedClients;
        private Long churnedClients;

        public Builder totalClients(Long totalClients) {
            this.totalClients = totalClients;
            return this;
        }

        public Builder totalPredictions(Long totalPredictions) {
            this.totalPredictions = totalPredictions;
            return this;
        }

        public Builder churnRate(Double churnRate) {
            this.churnRate = churnRate;
            return this;
        }

        public Builder retainedClients(Long retainedClients) {
            this.retainedClients = retainedClients;
            return this;
        }

        public Builder churnedClients(Long churnedClients) {
            this.churnedClients = churnedClients;
            return this;
        }

        public StatsDTO build() {
            return new StatsDTO(this);
        }
    }
}
