package stock.research.gfinance.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "GOOGLE_FINANCE_STOCK_DETAILS")
public class GoogleFinanceStockDetails {

    @Column(name = "GOOGLE_FINANCE_STOCK_DETAILS_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GOOGLE_FINANCE_STOCK_DETAILS_ID_SEQ")
    @SequenceGenerator(sequenceName = "GOOGLE_FINANCE_STOCK_DETAILS_ID_SEQ", allocationSize = 1, name = "GOOGLE_FINANCE_STOCK_DETAILS_ID_SEQ")
    Long id;

    @Column(name = "STOCKTS")
    Timestamp stockTS;

    @Column(name = "GOOGLE_FINANCE_STOCKS_PAYLOAD", columnDefinition = "CLOB NOT NULL")
    String googleFinanceStocksPayload;

    @Column(name = "QUOTETS")
    String quoteTS;

    @Column(name = "COUNTRY")
    String country;

    public GoogleFinanceStockDetails(Timestamp stockTS, String googleFinanceStocksPayload, String quoteTS, String country) {
        this.stockTS = stockTS;
        this.googleFinanceStocksPayload = googleFinanceStocksPayload;
        this.quoteTS = quoteTS;
        this.country = country;
    }

    @Override
    public String toString() {
        return "GoogleFinanceStockDetails{" +
                "id=" + id +
                ", stockTS=" + stockTS +
                ", googleFinanceStocksPayload='" + googleFinanceStocksPayload + '\'' +
                ", quoteTS='" + quoteTS + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public String getQuoteTS() {
        return quoteTS;
    }

    public void setQuoteTS(String quoteTS) {
        this.quoteTS = quoteTS;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getGoogleFinanceStocksPayload() {
        return googleFinanceStocksPayload;
    }

    public void setGoogleFinanceStocksPayload(String googleFinanceStocksPayload) {
        this.googleFinanceStocksPayload = googleFinanceStocksPayload;
    }
}
