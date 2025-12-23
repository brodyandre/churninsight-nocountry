package nocountry.churninsight.churn.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.exception.IntegrationException;
import nocountry.churninsight.churn.exception.ValidationBusinessException;
import nocountry.churninsight.churn.model.Cliente;
import nocountry.churninsight.churn.repository.ClientRepository;
import nocountry.churninsight.churn.validator.ChurnDataValidator;

@Service
public class PredictionService {
    public static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    @Autowired
    private ChurnDataValidator churnDataValidator;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Realiza a previsão de churn para um único cliente.
     */
    public PredictDTO predict(ChurnDataDTO data) {
        logger.info("Iniciando análise de churn. Contrato: {}, Valor: {}",
                data.getTipoContrato(), data.getValorMensal());

        // Validação dos dados
        List<String> businessErrors = churnDataValidator.validate(data);
        if (!businessErrors.isEmpty()) {
            throw new ValidationBusinessException(
                "Erro de consistência nos dados de churn",
                businessErrors
            );
        }

        try {
            PredictDTO resultado;

            // Lógica MOCK (Provisória) - Substituir pelo modelo real futuramente
            if (data.getValorMensal() > 100) {
                resultado = new PredictDTO("Vai cancelar", 0.82);
            } else {
                resultado = new PredictDTO("Vai continuar", 0.15);
            }

            logger.info("Análise finalizada. Resultado: '{}', Probabilidade: {}",
                    resultado.getPrevisao(), resultado.getProbabilidade());

            return resultado;

        } catch (Exception e) {
            logger.error("Falha crítica ao calcular churn para os dados: {}", data, e);
            throw new IntegrationException("Erro interno no serviço de previsão: " + e.getMessage());
        }
    }

    /**
     * Calcula estatísticas básicas sobre os clientes cadastrados no banco.
     */
    public StatsDTO getStats() {
        logger.info("Calculando estatísticas gerais...");
        List<Cliente> clientes = clientRepository.findAll();

        long total = clientes.size();
        double somaValor = clientes.stream()
                .mapToDouble(Cliente::getValorMensal)
                .sum();
        
        double media = total > 0 ? somaValor / total : 0.0;

        logger.info("Estatísticas calculadas: Total={}, Média={}", total, media);
        return new StatsDTO(total, media);
    }

    /**
     * Processa um arquivo CSV contendo uma lista de clientes e retorna as previsões.
     */
    public List<PredictDTO> processCsv(MultipartFile file) {
        logger.info("Iniciando processamento de arquivo CSV: {}", file.getOriginalFilename());
        List<PredictDTO> resultados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Pula a primeira linha (cabeçalho)
                    continue;
                }

                // Divide a linha por vírgula
                String[] fields = line.split(",");
                
                // Validação simples de tamanho
                if (fields.length < 19) { 
                    logger.warn("Linha ignorada por formato inválido (colunas insuficientes): {}", line);
                    continue;
                }

                // Mapeia as colunas do CSV para o DTO
                // ATENÇÃO: A ordem aqui deve corresponder exatamente à ordem das colunas no seu CSV
                ChurnDataDTO dto = new ChurnDataDTO();
                try {
                    dto.setGenero(fields[0].trim());
                    dto.setIdoso(Integer.parseInt(fields[1].trim()));
                    dto.setConjuge(fields[2].trim());
                    dto.setDependentes(fields[3].trim());
                    dto.setTempoContrato(Integer.parseInt(fields[4].trim()));
                    dto.setServicoTelefone(fields[5].trim());
                    dto.setMultiplasLinhasTel(fields[6].trim());
                    dto.setServicoInternet(fields[7].trim());
                    dto.setSegurancaOnline(fields[8].trim());
                    dto.setBackupOnline(fields[9].trim());
                    dto.setProtecaoDispositivo(fields[10].trim());
                    dto.setSuporteTecnico(fields[11].trim());
                    dto.setTvStreaming(fields[12].trim());
                    dto.setFilmesStreaming(fields[13].trim());
                    dto.setTipoContrato(fields[14].trim());
                    dto.setFaturaOnline(fields[15].trim());
                    dto.setMetodoPagamento(fields[16].trim());
                    dto.setValorMensal(Double.parseDouble(fields[17].trim()));
                    
                    String totalChargesStr = fields[18].trim();
                    dto.setValorTotal(totalChargesStr.isEmpty() ? 0.0 : Double.parseDouble(totalChargesStr));

                    // Realiza a previsão para este cliente
                    resultados.add(predict(dto));

                } catch (NumberFormatException nfe) {
                    logger.warn("Erro ao converter número na linha: {}. Erro: {}", line, nfe.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao ler/processar arquivo CSV", e);
            throw new IntegrationException("Erro ao ler o arquivo CSV: " + e.getMessage());
        }

        logger.info("Processamento de CSV concluído. {} previsões geradas com sucesso.", resultados.size());
        return resultados;
    }
}
