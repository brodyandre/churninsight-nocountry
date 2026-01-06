package nocountry.churninsight.churn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import nocountry.churninsight.churn.dto.DemoExampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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
     */
    public List<DemoExampleDTO> getDemoExamples() {
        logger.info("Servindo {} presets carregados localmente.", cachedPresets.size());

        return cachedPresets;
    }

    /**
     * Gera uma lista de presets estáticos codificados no Java.
     */
    public List<DemoExampleDTO> loadPresetsFromFiles() {
        List<DemoExampleDTO> presets = new ArrayList<>();

        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:presets/*.json");

            if (resources.length == 0) {
                logger.warn("Nenhum arquivo encontrado em classpath:presets/*.json");
            }

            for (Resource resource : resources) {
                try {
                    DemoExampleDTO dto = objectMapper.readValue(
                        resource.getInputStream(),
                        DemoExampleDTO.class);
    
                    presets.add(dto);    
                } catch (Exception e) {
                    logger.error("Erro ao processar o arquivo: {}", resource.getFilename(), e);
                }

            }
        } catch (Exception e) {
            logger.error("Erro crítico ao ler arquivos de preset", e);
        }

        presets.sort(Comparator.comparingInt(dto -> dto.order));
        return presets;
    }

}
