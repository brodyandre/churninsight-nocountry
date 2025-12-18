package nocountry.churninsight.churn.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "genero", columnDefinition = "genero_enum", nullable = false)
    private GeneroEnum genero;
    
    @Column(name = "idoso", nullable = false)
    private Integer idoso;
    
    @Column(name = "conjuge", nullable = false, length = 3)
    private String conjuge;
    
    @Column(name = "dependentes", nullable = false, length = 3)
    private String dependentes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contrato", columnDefinition = "tipo_contrato_enum", nullable = false)
    private TipoContratoEnum tipoContrato;
    
    @Column(name = "tempo_contrato", nullable = false)
    private Integer tempoContrato;
    
    @Column(name = "servico_telefone", nullable = false, length = 3)
    private String servicoTelefone;
    
    @Column(name = "multiplas_linhas_tel", nullable = false, length = 3)
    private String multiplasLinhasTel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "servico_internet", columnDefinition = "servico_internet_enum", nullable = false)
    private ServicoInternetEnum servicoInternet;
    
    @Column(name = "seguranca_online", nullable = false, length = 3)
    private String segurancaOnline;
    
    @Column(name = "backup_online", nullable = false, length = 3)
    private String backupOnline;
    
    @Column(name = "protecao_dispositivo", nullable = false, length = 3)
    private String protecaoDispositivo;
    
    @Column(name = "suporte_tecnico", nullable = false, length = 3)
    private String suporteTecnico;
    
    @Column(name = "tv_streaming", nullable = false, length = 3)
    private String tvStreaming;
    
    @Column(name = "filmes_streaming", nullable = false, length = 3)
    private String filmesStreaming;
    
    @Column(name = "fatura_online", nullable = false, length = 3)
    private String faturaOnline;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", columnDefinition = "metodo_pagamento_enum", nullable = false)
    private MetodoPagamentoEnum metodoPagamento;
    
    @Column(name = "valor_mensal", nullable = false)
    private Float valorMensal;
    
    @Column(name = "valor_total", nullable = false)
    private Float valorTotal;
    
    @Column(name = "data_criacao", nullable = false)
    private OffsetDateTime dataCriacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = OffsetDateTime.now();
    }
    
    // Constructors
    public Cliente() {
    }
    
    public Cliente(GeneroEnum genero, Integer idoso, String conjuge, String dependentes,
                   TipoContratoEnum tipoContrato, Integer tempoContrato, String servicoTelefone,
                   String multiplasLinhasTel, ServicoInternetEnum servicoInternet, String segurancaOnline,
                   String backupOnline, String protecaoDispositivo, String suporteTecnico, String tvStreaming,
                   String filmesStreaming, String faturaOnline, MetodoPagamentoEnum metodoPagamento,
                   Float valorMensal, Float valorTotal) {
        this.genero = genero;
        this.idoso = idoso;
        this.conjuge = conjuge;
        this.dependentes = dependentes;
        this.tipoContrato = tipoContrato;
        this.tempoContrato = tempoContrato;
        this.servicoTelefone = servicoTelefone;
        this.multiplasLinhasTel = multiplasLinhasTel;
        this.servicoInternet = servicoInternet;
        this.segurancaOnline = segurancaOnline;
        this.backupOnline = backupOnline;
        this.protecaoDispositivo = protecaoDispositivo;
        this.suporteTecnico = suporteTecnico;
        this.tvStreaming = tvStreaming;
        this.filmesStreaming = filmesStreaming;
        this.faturaOnline = faturaOnline;
        this.metodoPagamento = metodoPagamento;
        this.valorMensal = valorMensal;
        this.valorTotal = valorTotal;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public GeneroEnum getGenero() {
        return genero;
    }
    
    public void setGenero(GeneroEnum genero) {
        this.genero = genero;
    }
    
    public Integer getIdoso() {
        return idoso;
    }
    
    public void setIdoso(Integer idoso) {
        this.idoso = idoso;
    }
    
    public String getConjuge() {
        return conjuge;
    }
    
    public void setConjuge(String conjuge) {
        this.conjuge = conjuge;
    }
    
    public String getDependentes() {
        return dependentes;
    }
    
    public void setDependentes(String dependentes) {
        this.dependentes = dependentes;
    }
    
    public TipoContratoEnum getTipoContrato() {
        return tipoContrato;
    }
    
    public void setTipoContrato(TipoContratoEnum tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
    
    public Integer getTempoContrato() {
        return tempoContrato;
    }
    
    public void setTempoContrato(Integer tempoContrato) {
        this.tempoContrato = tempoContrato;
    }
    
    public String getServicoTelefone() {
        return servicoTelefone;
    }
    
    public void setServicoTelefone(String servicoTelefone) {
        this.servicoTelefone = servicoTelefone;
    }
    
    public String getMultiplasLinhasTel() {
        return multiplasLinhasTel;
    }
    
    public void setMultiplasLinhasTel(String multiplasLinhasTel) {
        this.multiplasLinhasTel = multiplasLinhasTel;
    }
    
    public ServicoInternetEnum getServicoInternet() {
        return servicoInternet;
    }
    
    public void setServicoInternet(ServicoInternetEnum servicoInternet) {
        this.servicoInternet = servicoInternet;
    }
    
    public String getSegurancaOnline() {
        return segurancaOnline;
    }
    
    public void setSegurancaOnline(String segurancaOnline) {
        this.segurancaOnline = segurancaOnline;
    }
    
    public String getBackupOnline() {
        return backupOnline;
    }
    
    public void setBackupOnline(String backupOnline) {
        this.backupOnline = backupOnline;
    }
    
    public String getProtecaoDispositivo() {
        return protecaoDispositivo;
    }
    
    public void setProtecaoDispositivo(String protecaoDispositivo) {
        this.protecaoDispositivo = protecaoDispositivo;
    }
    
    public String getSuporteTecnico() {
        return suporteTecnico;
    }
    
    public void setSuporteTecnico(String suporteTecnico) {
        this.suporteTecnico = suporteTecnico;
    }
    
    public String getTvStreaming() {
        return tvStreaming;
    }
    
    public void setTvStreaming(String tvStreaming) {
        this.tvStreaming = tvStreaming;
    }
    
    public String getFilmesStreaming() {
        return filmesStreaming;
    }
    
    public void setFilmesStreaming(String filmesStreaming) {
        this.filmesStreaming = filmesStreaming;
    }
    
    public String getFaturaOnline() {
        return faturaOnline;
    }
    
    public void setFaturaOnline(String faturaOnline) {
        this.faturaOnline = faturaOnline;
    }
    
    public MetodoPagamentoEnum getMetodoPagamento() {
        return metodoPagamento;
    }
    
    public void setMetodoPagamento(MetodoPagamentoEnum metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
    
    public Float getValorMensal() {
        return valorMensal;
    }
    
    public void setValorMensal(Float valorMensal) {
        this.valorMensal = valorMensal;
    }
    
    public Float getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(Float valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(OffsetDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", genero=" + genero +
                ", idoso=" + idoso +
                ", conjuge='" + conjuge + '\'' +
                ", dependentes='" + dependentes + '\'' +
                ", tipoContrato=" + tipoContrato +
                ", tempoContrato=" + tempoContrato +
                ", servicoTelefone='" + servicoTelefone + '\'' +
                ", multiplasLinhasTel='" + multiplasLinhasTel + '\'' +
                ", servicoInternet=" + servicoInternet +
                ", segurancaOnline='" + segurancaOnline + '\'' +
                ", backupOnline='" + backupOnline + '\'' +
                ", protecaoDispositivo='" + protecaoDispositivo + '\'' +
                ", suporteTecnico='" + suporteTecnico + '\'' +
                ", tvStreaming='" + tvStreaming + '\'' +
                ", filmesStreaming='" + filmesStreaming + '\'' +
                ", faturaOnline='" + faturaOnline + '\'' +
                ", metodoPagamento=" + metodoPagamento +
                ", valorMensal=" + valorMensal +
                ", valorTotal=" + valorTotal +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
