package nocountry.churninsight.churn.validator;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador customizado para lógica de negócio complexa de ChurnDataDTO.
 * Este validador verifica regras que não podem ser expressas apenas com anotações.
 */
@Component
public class ChurnDataValidator {

    /**
     * Valida as regras de negócio complexas para dados de churn.
     * 
     * @param data os dados de churn a validar
     * @return lista de mensagens de erro (vazia se válido)
     */
    public List<String> validate(ChurnDataDTO data) {
        List<String> errors = new ArrayList<>();

        // Validação: Se não tem serviço de internet, não pode ter serviços de internet
        if ("No".equals(data.getServicoInternet())) {
            if ("Yes".equals(data.getSegurancaOnline()) || 
                "Yes".equals(data.getBackupOnline()) || 
                "Yes".equals(data.getProtecaoDispositivo()) ||
                "Yes".equals(data.getSuporteTecnico()) ||
                "Yes".equals(data.getTvStreaming()) ||
                "Yes".equals(data.getFilmesStreaming())) {
                errors.add("Não é possível ter serviços de internet quando o serviço de Internet está desativado");
            }
        }

        // Validação: Se não tem serviço de telefone, não pode ter múltiplas linhas
        if ("No".equals(data.getServicoTelefone()) && "Yes".equals(data.getMultiplasLinhasTel())) {
            errors.add("Não é possível ter múltiplas linhas sem serviço de telefone");
        }

        // Validação: Valor total deve ser maior ou igual ao valor mensal (logicamente)
        if (data.getValorTotal() > 0 && data.getValorMensal() > 0) {
            // Verificar se o valor total está coerente (aproximadamente)
            // Permitindo margem de erro para cálculos
            if (data.getTempoContrato() > 0 && data.getValorTotal() < (data.getValorMensal() * data.getTempoContrato() * 0.8)) {
                errors.add("Valor total parece inconsistente com valor mensal e tempo de contrato");
            }
        }

        // Validação: Clientes com contrato de 2 anos geralmente têm menor risco de churn
        // (apenas aviso, não erro)

        return errors;
    }

    /**
     * Verifica se todos os campos obrigatórios estão preenchidos.
     * 
     * @param data os dados de churn a validar
     * @return lista de campos obrigatórios faltando
     */
    public List<String> checkRequiredFields(ChurnDataDTO data) {
        List<String> missingFields = new ArrayList<>();

        if (data.getGenero() == null || data.getGenero().isBlank()) {
            missingFields.add("genero (gender)");
        }
        if (data.getConjuge() == null || data.getConjuge().isBlank()) {
            missingFields.add("conjuge (Partner)");
        }
        if (data.getDependentes() == null || data.getDependentes().isBlank()) {
            missingFields.add("dependentes (Dependents)");
        }
        if (data.getServicoTelefone() == null || data.getServicoTelefone().isBlank()) {
            missingFields.add("servicoTelefone (PhoneService)");
        }
        if (data.getMultiplasLinhasTel() == null || data.getMultiplasLinhasTel().isBlank()) {
            missingFields.add("multiplasLinhasTel (MultipleLines)");
        }
        if (data.getServicoInternet() == null || data.getServicoInternet().isBlank()) {
            missingFields.add("servicoInternet (InternetService)");
        }
        if (data.getSegurancaOnline() == null || data.getSegurancaOnline().isBlank()) {
            missingFields.add("segurancaOnline (OnlineSecurity)");
        }
        if (data.getBackupOnline() == null || data.getBackupOnline().isBlank()) {
            missingFields.add("backupOnline (OnlineBackup)");
        }
        if (data.getProtecaoDispositivo() == null || data.getProtecaoDispositivo().isBlank()) {
            missingFields.add("protecaoDispositivo (DeviceProtection)");
        }
        if (data.getSuporteTecnico() == null || data.getSuporteTecnico().isBlank()) {
            missingFields.add("suporteTecnico (TechSupport)");
        }
        if (data.getTvStreaming() == null || data.getTvStreaming().isBlank()) {
            missingFields.add("tvStreaming (StreamingTV)");
        }
        if (data.getFilmesStreaming() == null || data.getFilmesStreaming().isBlank()) {
            missingFields.add("filmesStreaming (StreamingMovies)");
        }
        if (data.getTipoContrato() == null || data.getTipoContrato().isBlank()) {
            missingFields.add("tipoContrato (Contract)");
        }
        if (data.getFaturaOnline() == null || data.getFaturaOnline().isBlank()) {
            missingFields.add("faturaOnline (PaperlessBilling)");
        }
        if (data.getMetodoPagamento() == null || data.getMetodoPagamento().isBlank()) {
            missingFields.add("metodoPagamento (PaymentMethod)");
        }

        return missingFields;
    }
}
