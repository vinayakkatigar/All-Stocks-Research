package stock.research.yfinance.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "YFINANCE_STOCK_INFO")
public class YFinanceStockInfo {

    @Column(name = "YFINANCE_STOCK_INFO_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "YFINANCE_STOCK_INFO_ID_SEQ")
    @SequenceGenerator(sequenceName = "YFINANCE_STOCK_INFO_ID_SEQ", allocationSize = 1, name = "YFINANCE_STOCK_INFO_ID_SEQ")
    Long id;

    public YFinanceStockInfo(String stockName, Double mktCapRealValue, String mktCapFriendyValue, BigDecimal currentMarketPrice, BigDecimal _52WeekLowPrice, BigDecimal _52WeekHighPrice, BigDecimal _52WeekHighLowPriceDiff, BigDecimal _52WeekHighPriceDiff, BigDecimal _52WeekLowPriceDiff, Double p2e, Instant timestamp, Timestamp stockTS) {
        this.stockName = stockName;
        this.mktCapRealValue = mktCapRealValue;
        this.mktCapFriendyValue = mktCapFriendyValue;
        this.currentMarketPrice = currentMarketPrice;
        this._52WeekLowPrice = _52WeekLowPrice;
        this._52WeekHighPrice = _52WeekHighPrice;
        this._52WeekHighLowPriceDiff = _52WeekHighLowPriceDiff;
        this._52WeekHighPriceDiff = _52WeekHighPriceDiff;
        this._52WeekLowPriceDiff = _52WeekLowPriceDiff;
        this.p2e = p2e;
        this.timestamp = timestamp;
        this.stockTS = stockTS;
    }

    @Column(name = "STOCK_NAME")
    private String stockName = "";

    @Column(name = "STOCK_RANK")
    private Integer stockRankIndex;

    @Column(name = "STOCK_MKT_CAP")
    private Double mktCapRealValue;

    @Transient
    private String mktCapFriendyValue;

    @Transient
    private String ccy;

    @Transient
    private Double dividendRate;

    @Transient
    private String stockCode;

    @Transient
    private Double changePct;

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

    @Column(name = "P2EPS")
    private Double p2e;

    @Column(name = "EPS")
    private Double eps;

    @Column(name = "QUOTETS")
    private Instant timestamp;

    @Column(name = "STOCKTS")
    Timestamp stockTS;

    public YFinanceStockInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Integer getStockRankIndex() {
        return stockRankIndex;
    }

    public void setStockRankIndex(Integer stockRankIndex) {
        this.stockRankIndex = stockRankIndex;
    }

    public Double getMktCapRealValue() {
        return mktCapRealValue;
    }

    public void setMktCapRealValue(Double mktCapRealValue) {
        this.mktCapRealValue = mktCapRealValue;
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

    public Double getP2e() {
        return p2e;
    }

    public void setP2e(Double p2e) {
        this.p2e = p2e;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getStockTS() {
        return stockTS;
    }

    public void setStockTS(Timestamp stockTS) {
        this.stockTS = stockTS;
    }

    public String getMktCapFriendyValue() {
        return mktCapFriendyValue;
    }

    public void setMktCapFriendyValue(String mktCapFriendyValue) {
        this.mktCapFriendyValue = mktCapFriendyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YFinanceStockInfo that = (YFinanceStockInfo) o;
        if (stockName == null || that.stockName == null) return false;
        return stockName.equals(that.stockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockName);
    }

    @Override
    public String toString() {
        return "GFinanceNYSEStockInfo{" +
                "id=" + id +
                ", stockName='" + stockName + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", mktCapRealValue=" + mktCapRealValue +
                ", mktCapFriendyValue='" + mktCapFriendyValue + '\'' +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", p2e=" + p2e +
                ", timestamp=" + timestamp +
                ", stockTS=" + stockTS +
                ", changePct=" + changePct +
                '}';
    }

    public Double getChangePct() {
        return changePct;
    }

    public void setChangePct(Double changePct) {
        this.changePct = changePct;
    }

    public Double getEps() {
        return eps;
    }

    public void setEps(Double eps) {
        this.eps = eps;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Double getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(Double dividendRate) {
        this.dividendRate = dividendRate;
    }
}
