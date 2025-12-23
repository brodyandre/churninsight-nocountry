package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.model.Previsao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChurnRepository extends JpaRepository<Previsao, Long> {
    
    /**
     * Count predictions where the forecast is "Vai cancelar" (churn)
     * @return count of churned clients
     */
    @Query("SELECT COUNT(p) FROM Previsao p WHERE p.previsao = 'Vai cancelar'")
    Long countByPrevisaoChurnTrue();
}
