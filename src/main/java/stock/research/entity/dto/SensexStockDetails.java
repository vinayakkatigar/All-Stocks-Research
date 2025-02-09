package stock.research.entity.dto;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "SENSEX_STOCK_DETAILS")
public class SensexStockDetails {

    @Column(name = "SENSEX_STOCK_DETAILS_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SENSEX_STOCK_DETAILS_ID_SEQ")
    @SequenceGenerator(sequenceName = "SENSEX_STOCK_DETAILS_ID_SEQ", allocationSize = 1, name = "SENSEX_STOCK_DETAILS_ID_SEQ")
    Long id;

    @Column(name = "STOCKTS")
    Timestamp stockTS;

    @Column(name = "SENSEX_STOCKS_PAYLOAD", columnDefinition = "CLOB NOT NULL")
    String sensexStocksPayload;

    @Override
    public String toString() {
        return "SensexStockDetails{" +
                "id=" + id +
                ", stockTS=" + stockTS +
                ", sensexStocksPayload='" + sensexStocksPayload + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getStockTS() {
        return stockTS;
    }

    public void setStockTS(Timestamp stockTS) {
        this.stockTS = stockTS;
    }

    public String getSensexStocksPayload() {
        return sensexStocksPayload;
    }

    public void setSensexStocksPayload(String sensexStocksPayload) {
        this.sensexStocksPayload = sensexStocksPayload;
    }
}
