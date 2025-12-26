package nocountry.churninsight.churn.validator;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChurnDataValidatorTest {

    // Instanciamos diretamente (Unitário Puro = Performance Máxima)
    private final ChurnDataValidator validator = new ChurnDataValidator();

    // --- HELPER: Cria um DTO base que passa em todas as regras ---
    private ChurnDataDTO criarDtoValido() {
        ChurnDataDTO dto = new ChurnDataDTO();
        
        // Configuração segura para evitar conflitos de regras
        dto.setServicoInternet("Fibra Ótica"); // Evita regra de 'Nenhum'
        dto.setServicoTelefone("Sim");         // Evita regra de 'Sem telefone'
        dto.setMultiplasLinhasTel("Sim");
        
        // Serviços adicionais (OK porque tem internet)
        dto.setSegurancaOnline("Não");
        dto.setBackupOnline("Não");
        dto.setProtecaoDispositivo("Não");
        dto.setSuporteTecnico("Não");
        dto.setTvStreaming("Não");
        dto.setFilmesStreaming("Não");

        // Valores matematicamente coerentes
        dto.setValorMensal(100.0);
        dto.setTempoContrato(10);
        dto.setValorTotal(1000.0); // 100 * 10 = 1000 (Coerente)

        return dto;
    }

    @Test
    @DisplayName("Deve passar sem erros quando o DTO é logicamente consistente")
    void deveValidarSemErros() {
        ChurnDataDTO dto = criarDtoValido();

        List<String> erros = validator.validate(dto);

        assertThat(erros).isEmpty();
    }

    // --- REGRA 1: Internet "Nenhum" vs Serviços Adicionais ---

    @Test
    @DisplayName("Deve falhar se Internet é 'Nenhum' mas possui serviços digitais ativos")
    void deveDetectarConflitoInternet() {
        ChurnDataDTO dto = criarDtoValido();
        dto.setServicoInternet("Nenhum");
        
        // Erro: Não pode ter TV se não tem internet
        dto.setTvStreaming("Sim"); 

        List<String> erros = validator.validate(dto);

        assertThat(erros).hasSize(1);
        assertThat(erros.get(0)).contains("Não é possível ter serviços de internet");
    }
    
    @Test
    @DisplayName("Deve falhar se Internet é 'Nenhum' e possui múltiplos serviços digitais")
    void deveDetectarMultiplosConflitosInternet() {
        ChurnDataDTO dto = criarDtoValido();
        dto.setServicoInternet("Nenhum");
        
        // Ativando vários serviços proibidos
        dto.setBackupOnline("Sim");
        dto.setSuporteTecnico("Sim");

        List<String> erros = validator.validate(dto);

        // A lógica do seu validador retorna apenas 1 mensagem genérica para esse bloco,
        // então verificamos se a lista contem 1 erro
        assertThat(erros).isNotEmpty();
        assertThat(erros.get(0)).contains("quando o serviço de Internet é 'Nenhum'");
    }

    // --- REGRA 2: Telefone vs Múltiplas Linhas ---

    @Test
    @DisplayName("Deve falhar se não tem Telefone mas diz ter Múltiplas Linhas")
    void deveDetectarConflitoTelefone() {
        ChurnDataDTO dto = criarDtoValido();
        dto.setServicoTelefone("Não");
        dto.setMultiplasLinhasTel("Sim"); // Impossível fisicamente

        List<String> erros = validator.validate(dto);

        assertThat(erros).hasSize(1);
        assertThat(erros.get(0)).contains("Não é possível ter múltiplas linhas sem serviço de telefone");
    }

    // --- REGRA 3: Coerência Financeira (Total vs Mensal * Tempo) ---

    @Test
    @DisplayName("Deve falhar se o Valor Total for suspeitamente baixo (Inconsistência Matemática)")
    void deveDetectarInconsistenciaFinanceira() {
        ChurnDataDTO dto = criarDtoValido();
        
        dto.setValorMensal(100.0);
        dto.setTempoContrato(10); 
        // Esperado: aprox 1000. 
        // Limite da regra (0.8): 800.
        
        dto.setValorTotal(500.0); // 500 < 800 -> ERRO

        List<String> erros = validator.validate(dto);

        assertThat(erros).hasSize(1);
        assertThat(erros.get(0)).contains("Valor total parece inconsistente");
    }

    @Test
    @DisplayName("Deve aceitar Valor Total um pouco abaixo do cálculo exato (Margem de erro)")
    void deveAceitarMargemDeErroFinanceiro() {
        ChurnDataDTO dto = criarDtoValido();
        
        dto.setValorMensal(100.0);
        dto.setTempoContrato(10);
        // Esperado: 1000.
        // Regra aceita até 20% de desconto/erro -> Mínimo 800.
        
        dto.setValorTotal(850.0); // 850 > 800 -> VÁLIDO

        List<String> erros = validator.validate(dto);

        assertThat(erros).isEmpty();
    }

    // --- REGRA EXTRA: Múltiplos Erros ---

    @Test
    @DisplayName("Deve acumular múltiplos erros se houver várias inconsistências")
    void deveRetornarListaDeErros() {
        ChurnDataDTO dto = criarDtoValido();
        
        // Erro 1: Telefone
        dto.setServicoTelefone("Não");
        dto.setMultiplasLinhasTel("Sim");

        // Erro 2: Financeiro
        dto.setValorMensal(100.0);
        dto.setTempoContrato(10);
        dto.setValorTotal(10.0);

        List<String> erros = validator.validate(dto);

        assertThat(erros).hasSize(2); // Deve ter pego os dois problemas
    }
}