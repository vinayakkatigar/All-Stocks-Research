package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.domain.SensexStockInfo;

public interface SensexStockInfoRepositary extends CrudRepository<SensexStockInfo, Long> {
}