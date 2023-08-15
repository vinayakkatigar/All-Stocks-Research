package stock.research.yfinance.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.yfinance.domain.YFinanceStockInfo;

public interface YFinanceStockInfoRepositary extends CrudRepository<YFinanceStockInfo, Long> {
}