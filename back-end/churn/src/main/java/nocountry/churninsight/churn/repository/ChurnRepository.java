package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.model.Previsao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChurnRepository extends JpaRepository<Previsao, Long> {
}
