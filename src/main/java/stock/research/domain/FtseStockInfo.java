package stock.research.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;


@Entity
@Table(name = "FTSE_STOCK_INFO")
public class FtseStockInfo {

    @Column(name = "FTSE_STOCK_INFO_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FTSE_STOCK_INFO_ID_SEQ")
    @SequenceGenerator(sequenceName = "FTSE_STOCK_INFO_ID_SEQ", allocationSize = 1, name = "FTSE_STOCK_INFO_ID_SEQ")
    Long id;

    @Column(name = "STOCK_NAME")
    private String stockName;

    @Column(name = "STOCK_CODE")
    private String stockCode;

    @Column(name = "STOCK_URL")
    private String stockURL;

    @Column(name = "STOCK_RANK")
    private Integer stockRankIndex;

    @Column(name = "STOCK_MKT_CAP")
    private Double stockMktCap;

    @Column(name = "CURRENT_MARKET_PRICE")
    private BigDecimal currentMarketPrice =  BigDecimal.ZERO;
    @Column(name = "YEAR_LOW")
    private BigDecimal _52WeekLowPrice =  BigDecimal.ZERO;

    @Column(name = "YEAR_HIGH")
    private BigDecimal _52WeekHighPrice =  BigDecimal.ZERO;

    @Column(name = "YEARLY_HIGH_LOW_DIFF")
    private BigDecimal _52WeekHighLowPriceDiff =  BigDecimal.ZERO;
    @Column(name = "YEARLY_HIGH_DIFF")
    private BigDecimal _52WeekHighPriceDiff =  BigDecimal.ZERO;

    @Column(name = "YEARLY_LOW_DIFF")
    private BigDecimal _52WeekLowPriceDiff =  BigDecimal.ZERO;

    @Column(name = "P2EPS")
    private BigDecimal p2e =  BigDecimal.ZERO;

    @Column(name = "EPS")
    private Double eps;

    @Column(name = "QUOTETS")
    private Instant timestamp;

    public FtseStockInfo() {
    }

    public FtseStockInfo(String stockName, String stockURL,
                         String stockCode, Double stockMktCap, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        this.stockCode = stockCode;
        this.stockMktCap = stockMktCap;
        this.currentMarketPrice = currentMarketPrice;
    }

    public FtseStockInfo(String stockName, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.currentMarketPrice = currentMarketPrice;
    }

    @Override
    public String toString() {
        return "Ftse250StockInfo{" +
                "stockName='" + stockName + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockURL='" + stockURL + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", stockMktCap=" + stockMktCap +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", eps=" + eps +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FtseStockInfo that = (FtseStockInfo) o;
        return  (Objects.equals(getStockCode(), that.getStockCode()) || (Objects.equals(getStockURL(), that.getStockURL())));
    }

    @Override
    public int hashCode() {
        return Objects.hash( getStockCode(), getStockURL());
    }

    public String getStockName() {
        return stockName;
    }

    public String getStockURL() {
        return stockURL;
    }

    public Integer getStockRankIndex() {
        return stockRankIndex;
    }

    public Double getStockMktCap() {
        return stockMktCap;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setStockURL(String stockURL) {
        this.stockURL = stockURL;
    }

    public void setStockRankIndex(Integer stockRankIndex) {
        this.stockRankIndex = stockRankIndex;
    }

    public void setStockMktCap(Double stockMktCap) {
        this.stockMktCap = stockMktCap;
    }

    public BigDecimal getCurrentMarketPrice() {
        return currentMarketPrice;
    }

    public void setCurrentMarketPrice(BigDecimal currentMarketPrice) {
        this.currentMarketPrice = currentMarketPrice;
    }

    public BigDecimal get_52WeekLowPrice() {
        return _52WeekLowPrice;
    }

    public void set_52WeekLowPrice(BigDecimal _52WeekLowPrice) {
        this._52WeekLowPrice = _52WeekLowPrice;
    }

    public BigDecimal get_52WeekHighPrice() {
        return _52WeekHighPrice;
    }

    public void set_52WeekHighPrice(BigDecimal _52WeekHighPrice) {
        this._52WeekHighPrice = _52WeekHighPrice;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigDecimal get_52WeekHighLowPriceDiff() {
        return _52WeekHighLowPriceDiff;
    }

    public void set_52WeekHighLowPriceDiff(BigDecimal _52WeekHighLowPriceDiff) {
        this._52WeekHighLowPriceDiff = _52WeekHighLowPriceDiff;
    }

    public BigDecimal get_52WeekHighPriceDiff() {
        return _52WeekHighPriceDiff;
    }

    public void set_52WeekHighPriceDiff(BigDecimal _52WeekHighPriceDiff) {
        this._52WeekHighPriceDiff = _52WeekHighPriceDiff;
    }

    public BigDecimal get_52WeekLowPriceDiff() {
        return _52WeekLowPriceDiff;
    }

    public void set_52WeekLowPriceDiff(BigDecimal _52WeekLowPriceDiff) {
        this._52WeekLowPriceDiff = _52WeekLowPriceDiff;
    }
    public Double getEps() {
        return eps;
    }

    public void setEps(Double eps) {
        this.eps = eps;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getP2e() {
        return p2e;
    }

    public void setP2e(BigDecimal p2e) {
        this.p2e = p2e;
    }

}
