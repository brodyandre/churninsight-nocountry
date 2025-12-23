package nocountry.churninsight.churn.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.exception.IntegrationException;
import nocountry.churninsight.churn.exception.ValidationBusinessException;
import nocountry.churninsight.churn.validator.ChurnDataValidator;

@Service
public class PredictionService {
    public static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    @Autowired
    private ChurnDataValidator churnDataValidator;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ds.service.url:http://localhost:8001}")
    private String dsServiceUrl;

    public PredictDTO predict(ChurnDataDTO data) {
        // Registra o início do processo para sabermos o que chegou
        logger.info("Iniciando análise de churn. Contrato: {}, Valor: {}",
                data.getTipoContrato(), data.getValorMensal());

        List<String> businessErrors = churnDataValidator.validate(data);

        // Validar regras de negócio complexas
        if (!businessErrors.isEmpty()) {
            throw new ValidationBusinessException(
                "Erro de consistência nos dados de churn",
                businessErrors
            );
        }

        try {
            // Chamar o microsserviço Python de predição
            logger.info("Enviando dados para microsserviço Python: {}", dsServiceUrl);
            
            ResponseEntity<PredictDTO> response = restTemplate.postForEntity(
                dsServiceUrl + "/predict",
                data,
                PredictDTO.class
            );

            if (response.getBody() == null) {
                throw new IntegrationException("Resposta vazia do serviço de predição");
            }

            PredictDTO resultado = response.getBody();
            
            // Registra o sucesso da operação
            logger.info("Análise finalizada. Resultado: '{}', Probabilidade: {}",
                    resultado.getPrevisao(), resultado.getProbabilidade());

            return resultado;

        } catch (RestClientException e) {
            // Erro de comunicação com o microsserviço
            logger.error("Falha ao conectar com microsserviço Python em {}: {}",
                    dsServiceUrl, e.getMessage());
            
            throw new IntegrationException(
                "Erro ao conectar com serviço de predição: " + e.getMessage()
            );
        } catch (Exception e) {
            // Se der erro, o log mostra o motivo exato e os dados que causaram o problema
            logger.error("Falha crítica ao calcular churn para os dados: {}", data, e);

            // Relança o erro para que o Controller saiba que falhou e devolva um 502
            throw new IntegrationException("Erro interno no serviço de previsão: " + e.getMessage());
        }
    }
}