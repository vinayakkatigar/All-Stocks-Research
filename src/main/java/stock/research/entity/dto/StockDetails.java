package stock.research.entity.dto;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "STOCK_DETAILS")
public class StockDetails {

    // "customer_seq" is Oracle sequence name.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
    @SequenceGenerator(sequenceName = "customer_seq", allocationSize = 1, name = "CUST_SEQ")
    Long id;

    String name;

    String email;

    @Column(name = "UPDATED_TS")
    Timestamp updatedTS;

    //getters and setters, contructors

}
