package nocountry.churninsight.churn.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "previsoes")
public class Previsao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "previsao", nullable = false, length = 50)
    private String previsao;
    
    @Column(name = "probabilidade", nullable = false, precision = 5, scale = 4)
    private Double probabilidade;
    
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
    
    // Constructors
    public Previsao() {
    }
    
    public Previsao(Long clienteId, String previsao, Double probabilidade) {
        this.clienteId = clienteId;
        this.previsao = previsao;
        this.probabilidade = probabilidade;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getPrevisao() {
        return previsao;
    }
    
    public void setPrevisao(String previsao) {
        this.previsao = previsao;
    }
    
    public Double getProbabilidade() {
        return probabilidade;
    }
    
    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    @Override
    public String toString() {
        return "Previsao{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", previsao='" + previsao + '\'' +
                ", probabilidade=" + probabilidade +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
