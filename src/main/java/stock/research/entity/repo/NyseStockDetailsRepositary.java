package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.entity.dto.NyseStockDetails;

public interface NyseStockDetailsRepositary extends CrudRepository<NyseStockDetails, Long> {
}