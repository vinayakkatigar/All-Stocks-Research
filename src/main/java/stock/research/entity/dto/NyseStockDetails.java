package stock.research.entity.dto;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "NYSE_STOCK_DETAILS")
public class NyseStockDetails {

    @Column(name = "NYSE_STOCK_DETAILS_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NYSE_STOCK_DETAILS_ID_SEQ")
    @SequenceGenerator(sequenceName = "NYSE_STOCK_DETAILS_ID_SEQ", allocationSize = 1, name = "NYSE_STOCK_DETAILS_ID_SEQ")
    Long id;

    @Column(name = "STOCKTS")
    Timestamp stockTS;

    @Column(name = "NYSE_STOCKS_PAYLOAD", columnDefinition = "CLOB NOT NULL")
    String nyseStocksPayload;

    @Override
    public String toString() {
        return "NyseStockDetails{" +
                "id=" + id +
                ", stockTS=" + stockTS +
                ", nyseStocksPayload='" + nyseStocksPayload + '\'' +
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

    public String getNyseStocksPayload() {
        return nyseStocksPayload;
    }

    public void setNyseStocksPayload(String nyseStocksPayload) {
        this.nyseStocksPayload = nyseStocksPayload;
    }
}
