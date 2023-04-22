package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.domain.FtseStockInfo;
import stock.research.domain.SensexStockInfo;

public interface FtseStockInfoRepositary extends CrudRepository<FtseStockInfo, Long> {
}