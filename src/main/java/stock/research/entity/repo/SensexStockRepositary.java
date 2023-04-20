package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.entity.dto.SensexStockDetails;

public interface SensexStockRepositary extends CrudRepository<SensexStockDetails, Long> {
}