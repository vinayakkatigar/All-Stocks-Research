package stock.research.gfinance.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.gfinance.domain.GoogleFinanceStockDetails;

import java.util.List;

public interface GoogleFinanceStockDetailsRepositary extends CrudRepository<GoogleFinanceStockDetails, Long> {

    List<GoogleFinanceStockDetails> findByCountryIgnoreCase(String country);
}