package nocountry.churninsight.churn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import nocountry.churninsight.churn.dto.DemoExampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DemoDataService {
    public static final Logger logger = LoggerFactory.getLogger(DemoDataService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private List<DemoExampleDTO> cachedPresets;

    @Value("${ds.service.url}")
    private String dsServiceUrl;

    public DemoDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        logger.info("Carregando presets locais para cache de segurança...");
        this.cachedPresets = loadPresetsFromFiles();
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

        return cachedPresets;
    }

    /**
     * Gera uma lista de presets estáticos (fallback) codificados no Java.
     */
    public List<DemoExampleDTO> loadPresetsFromFiles() {
        List<DemoExampleDTO> presets = new ArrayList<>();

        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:presets/*.json");

            for (Resource resource : resources) {
                DemoExampleDTO dto = objectMapper.readValue(
                    resource.getInputStream(),
                    DemoExampleDTO.class);

                presets.add(dto);
            }
        } catch (Exception e) {
            logger.error("Erro crítico ao ler arquivos de preset", e);
        }

        presets.sort(Comparator.comparingInt(dto -> dto.order));
        return presets;
    }

}
