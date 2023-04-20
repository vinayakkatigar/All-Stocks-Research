package stock.research.entity.repo;

import org.springframework.data.repository.CrudRepository;
import stock.research.entity.dto.NyseStockDetails;
import stock.research.entity.dto.SensexStockDetails;

public interface NyseStockRepositary extends CrudRepository<NyseStockDetails, Long> {
}