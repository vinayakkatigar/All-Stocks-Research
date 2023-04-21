package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.entity.dto.SensexStockDetails;

public interface SensexStockDetailsRepositary extends CrudRepository<SensexStockDetails, Long> {
}