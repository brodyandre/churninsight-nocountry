package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.exception.ValidationBusinessException;
import nocountry.churninsight.churn.validator.ChurnDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por realizar predições de churn.
 * Inclui validação de dados antes de processar a predição.
 */
@Service
public class PredictionService {
    
    @Autowired
    private ChurnDataValidator churnDataValidator;

    /**
     * Realiza a predição de churn para um cliente após validação completa.
     * 
     * @param data dados do cliente para predição
     * @return resultado da predição com probabilidade
     * @throws ValidationBusinessException se houver erros de validação
     */
    public PredictDTO predict(ChurnDataDTO data) {
        // Validar campos obrigatórios
        List<String> missingFields = churnDataValidator.checkRequiredFields(data);
        if (!missingFields.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("Campos obrigatórios faltando: " + String.join(", ", missingFields));
            throw new ValidationBusinessException(
                "Validação falhou: campos obrigatórios ausentes",
                errorMessages
            );
        }

        // Validar regras de negócio complexas
        List<String> businessErrors = churnDataValidator.validate(data);
        if (!businessErrors.isEmpty()) {
            throw new ValidationBusinessException(
                "Validação de negócio falhou",
                businessErrors
            );
        }

        // Se passou em todas as validações, processar a predição
        return processPrediction(data);
    }

    /**
     * Processa a predição após todas as validações.
     * 
     * @param data dados validados do cliente
     * @return resultado da predição
     */
    private PredictDTO processPrediction(ChurnDataDTO data) {
        // Valor de retorno provisório enquanto o modelo não é integrado.
        // A lógica abaixo é apenas um exemplo e será substituída.
        if (data.getValorMensal() > 100) {
            return new PredictDTO("Vai cancelar", 0.8);
        } else {
            return new PredictDTO("Vai continuar", 0.47);

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    public PredictDTO predict(ChurnDataDTO data) {
        // Registra o início do processo para sabermos o que chegou
        logger.info("Iniciando análise de churn. Contrato: {}, Valor: {}",
                data.getTipoContrato(), data.getValorMensal());

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

            // Relança o erro para que o Controller saiba que falhou e devolva um 500
            throw new RuntimeException("Erro interno no serviço de previsão", e);
        }
    }
}