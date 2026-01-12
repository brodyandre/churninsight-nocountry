package nocountry.churninsight.churn.swagger;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SwaggerCustomCssInjector extends SwaggerIndexPageTransformer {
    public SwaggerCustomCssInjector(
            final SwaggerUiConfigProperties swaggerUiConfig,
            final SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            final SwaggerWelcomeCommon swaggerWelcomeCommon,
            final ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
    }

    @Override
    public @NonNull Resource transform(
            @NonNull HttpServletRequest request,
            @NonNull Resource resource,
            @NonNull ResourceTransformerChain transformer) throws IOException {
        if ("index.html".equals(resource.getFilename())) {
            try (InputStream in = resource.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String html = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                String transformedHtml = injectCss(html);
                return new TransformedResource(resource, transformedHtml.getBytes());
            }
        }
        return super.transform(request, resource, transformer);
    }

    private String injectCss(String html) {
        String cssPath = "/assets/custom.css";
        return html.replace("</head>", "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssPath + "\" /></head>");
    }
}
