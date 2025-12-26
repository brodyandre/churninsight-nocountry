package nocountry.churninsight.churn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemHealthService {
    public static final Logger logger = LoggerFactory.getLogger(SystemHealthService.class);

    private final RestTemplate restTemplate;
    private final Environment env;

    @Value("${ds.service.url}")
    private String dsServiceUrl;

    @Autowired
    public SystemHealthService(RestTemplate restTemplate, Environment env) {
        this.restTemplate = restTemplate;
        this.env = env;
    }

    /**
     * Obtém os dados de saúde do backend.
     * Realiza uma chamada ao endpoint de health do Java e calcula a latência.
     */
    public Map<String, Object> getHealth() {
        long startTime = System.currentTimeMillis();

        Map<String, Object> info = new HashMap<>();

        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            String port = env.getProperty("server.port", "8080");

            info.put("status", "UP");
            info.put("java_service_url", "http://" + host + ":" + port);

            long internalLatency = System.currentTimeMillis() - startTime;
            info.put("java_internal_latency", internalLatency);

        } catch (Exception e) {
            info.put("status", "DOWN");
            info.put("error", e.getMessage());
        }

        return info;
    }

    /**
     * Obtém os dados de saúde do serviço de Data Science (FastAPI).
     * Realiza uma chamada ao endpoint de health do Python e calcula a latência.
     * Em caso de falha, retorna um estado de fallback (simulado).
     */
    public Map<String, Object> getDsHealth() {
        long startTime= System.currentTimeMillis();

        try {
            Map<String, Object> resultado = restTemplate.getForObject(dsServiceUrl + "/health", Map.class);
            long latency = System.currentTimeMillis() - startTime;

            if (resultado == null) { resultado = new HashMap<>(); }

            resultado.put("ds_service_url", dsServiceUrl);
            resultado.put("internal_latency", latency);

            return resultado;

        } catch (Exception e) {
            logger.warn("FastAPI indisponível, usando fallback. Motivo: {}", e.getMessage());

            Map<String, Object> fallback = new HashMap<>();
            fallback.put("status", "offline (simulado)");
            fallback.put("ds_service_url", dsServiceUrl);
            fallback.put("internal_latency", 0L);
            fallback.put("modelo_path", "nenhum");
            fallback.put("threshold", 0.5);

            return fallback;
        }
    }
}
