package nocountry.churninsight.churn;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "ChurnInsight API",
		version = "1.0.0",
		description = "API para análise e predição de rotatividade de clientes.",
		contact = @Contact(name = "ChurnGuard Analytics", email = "")
))
public class ChurnApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChurnApplication.class,
				args);
	}

}
