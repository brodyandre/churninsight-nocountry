package nocountry.churninsight.churn.validator;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChurnDataValidator {
    // Valida as regras de negócio complexas para dados de churn.
    public List<String> validate(ChurnDataDTO data) {
        List<String> errors = new ArrayList<>();

        // Validação: Se não tem serviço de internet (Nenhum), não pode ter outros serviços relacionados
        if ("Nenhum".equals(data.getServicoInternet())) {
            if ("Sim".equals(data.getSegurancaOnline()) || 
                "Sim".equals(data.getBackupOnline()) || 
                "Sim".equals(data.getProtecaoDispositivo()) ||
                "Sim".equals(data.getSuporteTecnico()) ||
                "Sim".equals(data.getTvStreaming()) ||
                "Sim".equals(data.getFilmesStreaming())) {
                errors.add("Não é possível ter serviços de internet (Segurança, Backup, etc.) quando o serviço de Internet é 'Nenhum'.");
            }
        }

        // Validação: Se não tem serviço de telefone, não pode ter múltiplas linhas
        if ("Não".equals(data.getServicoTelefone()) && "Sim".equals(data.getMultiplasLinhasTel())) {
            errors.add("Não é possível ter múltiplas linhas sem serviço de telefone.");
        }

        // Validação: Valor total deve ser maior ou igual ao valor mensal (logicamente)
        if (data.getValorTotal() > 0 && data.getValorMensal() > 0) {
            // Verificar se o valor total está coerente (aproximadamente)
            // Permitindo margem de erro para cálculos
            if (data.getTempoContrato() > 0 && data.getValorTotal() < (data.getValorMensal() * data.getTempoContrato() * 0.8)) {
                errors.add("Valor total parece inconsistente com valor mensal e tempo de contrato");
            }
        }

        return errors;
    }
}
