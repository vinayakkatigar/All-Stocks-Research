package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.domain.NyseStockInfo;

public interface NyseStockInfoRepositary extends CrudRepository<NyseStockInfo, Long> {
}