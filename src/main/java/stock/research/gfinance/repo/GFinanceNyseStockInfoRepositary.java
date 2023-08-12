package stock.research.gfinance.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.gfinance.domain.GFinanceStockInfo;

public interface GFinanceNyseStockInfoRepositary extends CrudRepository<GFinanceStockInfo, Long> {
}