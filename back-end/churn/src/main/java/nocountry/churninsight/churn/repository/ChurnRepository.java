package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.entity.Churn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChurnRepository extends JpaRepository<Churn, Long> {
}
