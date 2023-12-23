package stock.research.gfinance.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.gfinance.domain.GoogleFinanceStockDetails;

public interface GoogleFinanceStockDetailsRepositary extends CrudRepository<GoogleFinanceStockDetails, Long> {
}