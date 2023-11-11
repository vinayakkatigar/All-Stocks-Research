package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.domain.FtseStockInfo;

public interface FtseStockInfoRepositary extends CrudRepository<FtseStockInfo, Long> {
}