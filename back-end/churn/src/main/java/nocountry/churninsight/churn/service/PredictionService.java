package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

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