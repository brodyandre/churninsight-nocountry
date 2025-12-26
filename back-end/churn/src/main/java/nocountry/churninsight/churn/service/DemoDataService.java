package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.DemoExampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemoDataService {
    public static final Logger logger = LoggerFactory.getLogger(DemoDataService.class);

    private final RestTemplate restTemplate;

    @Value("${ds.service.url}")
    private String dsServiceUrl;

    public DemoDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Recupera exemplos de demonstração para o preenchimento automático do formulário.
     * Tenta buscar dados dinâmicos do FastAPI e, em caso de erro, recorre aos presets locais.
     */
    public List<DemoExampleDTO> getDemoExamples() {
        logger.info("Buscando presets dinâmicos em: {}", dsServiceUrl + "/demo-examples");

        try {
            ResponseEntity<DemoExampleDTO[]> response = restTemplate.getForEntity(
                    dsServiceUrl + "/demo-examples",
                    DemoExampleDTO[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                logger.info("Presets carregados do FastAPI com sucesso.");

                return List.of(response.getBody());
            }

        } catch (Exception e) {
            logger.warn("FastAPI offline ou erro no endpoint. Usando presets locais. Detalhe: {}", e.getMessage());
        }

        return getPresetsLocaisJava();
    }

    /**
     * Gera uma lista de presets estáticos (fallback) codificados no Java.
     */
    public List<DemoExampleDTO> getPresetsLocaisJava() {
        List<DemoExampleDTO> backup = new ArrayList<>();

        // --- PRESET 1: ALTO RISCO (CHURN PROVÁVEL) ---
        DemoExampleDTO highRisk = new DemoExampleDTO();
        highRisk.setId("high-risk-local");
        highRisk.setLabel("Novo Cliente (Risco Alto - Local)");
        highRisk.setRisk("high");
        highRisk.setDescription("Contrato mensal, Fibra Ótica e sem serviços de segurança.");

        ChurnDataDTO p1 = new ChurnDataDTO();
        p1.setGenero("Feminino");
        p1.setIdoso(0);
        p1.setConjuge("Não");
        p1.setDependentes("Não");
        p1.setTempoContrato(2);
        p1.setServicoTelefone("Sim");
        p1.setMultiplasLinhasTel("Não");
        p1.setServicoInternet("Fibra Ótica");
        p1.setSegurancaOnline("Não");
        p1.setBackupOnline("Não");
        p1.setProtecaoDispositivo("Não");
        p1.setSuporteTecnico("Não");
        p1.setTvStreaming("Sim");
        p1.setFilmesStreaming("Sim");
        p1.setTipoContrato("Mensal");
        p1.setFaturaOnline("Sim");
        p1.setMetodoPagamento("Boleto");
        p1.setValorMensal(85.00);
        p1.setValorTotal(170.00);

        highRisk.setPayload(p1);
        backup.add(highRisk);

        // --- PRESET 2: BAIXO RISCO (FIDELIZADO) ---
        DemoExampleDTO lowRisk = new DemoExampleDTO();
        lowRisk.setId("low-risk-local");
        lowRisk.setLabel("Cliente Fiel (Risco Baixo - Local)");
        lowRisk.setRisk("low");
        lowRisk.setDescription("Contrato Bianual com todos os serviços de suporte.");

        ChurnDataDTO p2 = new ChurnDataDTO();
        p2.setGenero("Masculino");
        p2.setIdoso(0);
        p2.setConjuge("Sim");
        p2.setDependentes("Sim");
        p2.setTempoContrato(65);
        p2.setServicoTelefone("Sim");
        p2.setMultiplasLinhasTel("Sim");
        p2.setServicoInternet("DSL");
        p2.setSegurancaOnline("Sim");
        p2.setBackupOnline("Sim");
        p2.setProtecaoDispositivo("Sim");
        p2.setSuporteTecnico("Sim");
        p2.setTvStreaming("Não");
        p2.setFilmesStreaming("Não");
        p2.setTipoContrato("Bianual");
        p2.setFaturaOnline("Não");
        p2.setMetodoPagamento("Cartão de crédito");
        p2.setValorMensal(60.00);
        p2.setValorTotal(3900.00);

        lowRisk.setPayload(p2);
        backup.add(lowRisk);

        // --- PRESET 3: ERRO DE VALIDAÇÃO ---
        DemoExampleDTO invalidDemo = new DemoExampleDTO();
        invalidDemo.setId("invalid-local");
        invalidDemo.setLabel("Caso de Erro de Validação (Local)");
        invalidDemo.setRisk("invalid");
        invalidDemo.setDescription("Simula erro de validação: campos vazios e valores negativos.");

        ChurnDataDTO p3 = new ChurnDataDTO();
        p3.setGenero("");
        p3.setIdoso(99); // Valor fora do esperado (0 ou 1)
        p3.setTempoContrato(-5); // Tempo negativo para testar validação de intervalo
        p3.setTipoContrato("");
        p3.setValorMensal(-100.00); // Valor negativo
        p3.setValorTotal(0.0);

        invalidDemo.setPayload(p3);
        backup.add(invalidDemo);

        // --- PRESET 4: ERRO 422 (SCHEMA FASTAPI) ---
        DemoExampleDTO logicError = new DemoExampleDTO();
        logicError.setId("negocio-422-local");
        logicError.setLabel("Erro 422 (Regra de Negócio - Local)");
        logicError.setRisk("invalid");
        logicError.setDescription("Internet='Nenhum' mas Segurança='Sim'. Gera 422 via Validator Java.");

        ChurnDataDTO p = new ChurnDataDTO();
        p.setGenero("Feminino"); // OK para o DTO
        p.setIdoso(0);
        p.setConjuge("Não");
        p.setDependentes("Não");
        p.setTempoContrato(12);
        p.setServicoTelefone("Sim");
        p.setMultiplasLinhasTel("Não");

        // VIOLAÇÃO DA REGRA DE NEGÓCIO:
        p.setServicoInternet("Nenhum"); // DTO aceita (está no Pattern)
        p.setSegurancaOnline("Sim");    // DTO aceita (está no Pattern), mas Validator REJEITA

        p.setBackupOnline("Sem serviço de internet");
        p.setProtecaoDispositivo("Sem serviço de internet");
        p.setSuporteTecnico("Sem serviço de internet");
        p.setTvStreaming("Sem serviço de internet");
        p.setFilmesStreaming("Sem serviço de internet");
        p.setTipoContrato("Anual");
        p.setFaturaOnline("Sim");
        p.setMetodoPagamento("Boleto");
        p.setValorMensal(50.0);
        p.setValorTotal(600.0);

        logicError.setPayload(p);
        backup.add(logicError);

        return backup;
    }

}
