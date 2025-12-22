package nocountry.churninsight.churn.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            PredictDTO resultado;

            // Futuramente, aqui entra o modelo
            if (data.getValorMensal() > 100) {
                resultado = new PredictDTO("Vai cancelar", 0.82);
            } else {
                resultado = new PredictDTO("Vai continuar", 0.15);
            }

            // Registra o sucesso da operação
            logger.info("Análise finalizada. Resultado: '{}', Probabilidade: {}",
                    resultado.getPrevisao(), resultado.getProbabilidade());

            return resultado;

        } catch (Exception e) {
            // Se der erro, o log mostra o motivo exato e os dados que causaram o problema
            logger.error("Falha crítica ao calcular churn para os dados: {}", data, e);

            // Relança o erro para que o Controller saiba que falhou e devolva um 502
            throw new IntegrationException("Erro interno no serviço de previsão: " + e.getMessage());
        }
    }
}