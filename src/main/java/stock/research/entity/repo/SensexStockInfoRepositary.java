package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.domain.SensexStockInfo;
import stock.research.entity.dto.SensexStockDetails;

public interface SensexStockInfoRepositary extends CrudRepository<SensexStockInfo, Long> {
}