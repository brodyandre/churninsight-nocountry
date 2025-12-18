package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Cliente, Long> {
}
