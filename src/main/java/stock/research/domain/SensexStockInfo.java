package stock.research.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "SENSEX_STOCK_INFO")
public class SensexStockInfo {

    @Column(name = "SENSEX_STOCK_INFO_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SENSEX_STOCK_INFO_ID_SEQ")
    @SequenceGenerator(sequenceName = "SENSEX_STOCK_INFO_ID_SEQ", allocationSize = 1, name = "SENSEX_STOCK_INFO_ID_SEQ")
    Long id;

    @Column(name = "STOCK_NAME")
    private String stockName;

    @Column(name = "STOCK_URL")
    private String stockURL;

    @Column(name = "STOCK_RANK")
    private Integer stockRankIndex;

    @Column(name = "STOCK_MKT_CAP")
    private Double stockMktCap;

    @Column(name = "CURRENT_MARKET_PRICE")
    private BigDecimal currentMarketPrice =  BigDecimal.ZERO;

    @Column(name = "YEARLY_LOW")
    private BigDecimal _52WeekLowPrice =  BigDecimal.ZERO;

    @Column(name = "YEARLY_HIGH")
    private BigDecimal _52WeekHighPrice =  BigDecimal.ZERO;

    @Column(name = "YEARLY_HIGH_LOW_DIFF")
    private BigDecimal _52WeekHighLowPriceDiff =  BigDecimal.ZERO;

    @Column(name = "YEARLY_HIGH_DIFF")
    private BigDecimal _52WeekHighPriceDiff =  BigDecimal.ZERO;

    @Column(name = "YEARLY_LOW_DIFF")
    private BigDecimal _52WeekLowPriceDiff =  BigDecimal.ZERO;

    @Column(name = "FII_PCT")
    private Double fiiPct = 0d;

    @Column(name = "DAILY_PCT_CHANGE")
    private BigDecimal dailyPCTChange = BigDecimal.ZERO;

    @Column(name = "EPS")
    private Double eps;

    @Column(name = "P2EPS")
    private Double p2eps;

    @Column(name = "BOOK_VALUE")
    private Double bv;

    @Column(name = "PRICE_TO_BOOK_VALUE")
    private Double p2bv;

    @Column(name = "QUOTETS")
    private String quoteInstant;

    @Column(name = "STOCKTS")
    Timestamp stockTS;

    @Transient
    private Instant stockInstant;

    public SensexStockInfo(String stockName, String stockURL, Integer stockRankIndex, Double stockMktCap) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        this.stockRankIndex = stockRankIndex;
        this.stockMktCap = stockMktCap;
    }

    public SensexStockInfo() {
    }

    public SensexStockInfo(String stockName, String stockURL) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        stockMktCap = 0.0;
    }

    @Override
    public String toString() {
        return "SensexStockInfo{" +
                "id=" + id +
                ", stockName='" + stockName + '\'' +
                ", stockURL='" + stockURL + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", stockMktCap=" + stockMktCap +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", fiiPct=" + fiiPct +
                ", dailyPCTChange=" + dailyPCTChange +
                ", eps=" + eps +
                ", p2eps=" + p2eps +
                ", bv=" + bv +
                ", p2bv=" + p2bv +
                ", timestamp='" + quoteInstant + '\'' +
                ", stockTS=" + stockTS +
                '}';
    }

    public Timestamp getStockTS() {
        return stockTS;
    }

    public void setStockTS(Timestamp stockTS) {
        this.stockTS = stockTS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensexStockInfo that = (SensexStockInfo) o;
        return Objects.equals(getStockName(), that.getStockName()) ||
                Objects.equals(getStockURL(), that.getStockURL());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStockName(), getStockURL());
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

    public Double getFiiPct() {
        return fiiPct;
    }

    public void setFiiPct(Double fiiPct) {
        this.fiiPct = fiiPct;
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

    public Double getP2eps() {
        return p2eps;
    }

    public void setP2eps(Double p2eps) {
        this.p2eps = p2eps;
    }

    public Double getBv() {
        return bv;
    }

    public void setBv(Double bv) {
        this.bv = bv;
    }

    public Double getP2bv() {
        return p2bv;
    }

    public void setP2bv(Double p2bv) {
        this.p2bv = p2bv;
    }

    public String getQuoteInstant() {
        return quoteInstant;
    }

    public void setQuoteInstant(String quoteInstant) {
        this.quoteInstant = quoteInstant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDailyPCTChange() {
        return dailyPCTChange;
    }

    public void setDailyPCTChange(BigDecimal dailyPCTChange) {
        this.dailyPCTChange = dailyPCTChange;
    }

    public Instant getStockInstant() {
        return stockInstant;
    }

    public void setStockInstant(Instant stockInstant) {
        this.stockInstant = stockInstant;
    }
}
