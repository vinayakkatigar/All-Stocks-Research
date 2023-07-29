package stock.research.gfinance.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.domain.NyseStockInfo;
import stock.research.gfinance.domain.GFinanceNYSEStockInfo;

public interface GFinanceNyseStockInfoRepositary extends CrudRepository<GFinanceNYSEStockInfo, Long> {
}