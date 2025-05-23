package stock.research.gfinance.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "GFINANCE_NYSE_STOCK_INFO")
public class GFinanceStockInfo {

    @Column(name = "GFINANCE_NYSE_STOCK_INFO_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GFINANCE_NYSE_STOCK_INFO_ID_SEQ")
    @SequenceGenerator(sequenceName = "GFINANCE_NYSE_STOCK_INFO_ID_SEQ", allocationSize = 1, name = "GFINANCE_NYSE_STOCK_INFO_ID_SEQ")
    Long id;

    public GFinanceStockInfo(String stockName, Double mktCapRealValue, String mktCapFriendyValue, BigDecimal currentMarketPrice, BigDecimal _52WeekLowPrice, BigDecimal _52WeekHighPrice, BigDecimal _52WeekHighLowPriceDiff, BigDecimal _52WeekHighPriceDiff, BigDecimal _52WeekLowPriceDiff, Double p2e, String quoteInstant, Timestamp stockTS) {
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
        this.quoteInstant = quoteInstant;
        this.stockTS = stockTS;
    }

    public GFinanceStockInfo(String stockName, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.currentMarketPrice = currentMarketPrice;
    }
    @Column(name = "STOCK_NAME")
    private String stockName;

    @Column(name = "STOCK_RANK")
    private Integer stockRankIndex;

    @Column(name = "STOCK_MKT_CAP")
    private Double mktCapRealValue;

    @Transient
    private String mktCapFriendyValue;

    @Transient
    private String ccy;

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

    @Column(name = "QUOTETS")
    private String quoteInstant;

    @Column(name = "STOCKTS")
    private Timestamp stockTS;

    @Column(name = "DAILY_PCT_CHANGE")
    private BigDecimal dailyPctChange =  BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Column(name = "DAILY_HIGH_PRICE")
    private BigDecimal dailyHighPrice =  BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Column(name = "DAILY_LOW_PRICE")
    private BigDecimal dailyLowPrice =  BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Column(name = "COUNTRY")
    private String country;

    @Transient
    private Instant stockInstant;


    @Transient
    private BigDecimal dailyPctPnLChange =  BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public GFinanceStockInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName == null ? null : Normalizer.normalize(stockName, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
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
        return currentMarketPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public void setCurrentMarketPrice(BigDecimal currentMarketPrice) {
        this.currentMarketPrice = currentMarketPrice;
    }

    public BigDecimal get_52WeekLowPrice() {
        return _52WeekLowPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public void set_52WeekLowPrice(BigDecimal _52WeekLowPrice) {
        this._52WeekLowPrice = _52WeekLowPrice;
    }

    public BigDecimal get_52WeekHighPrice() {
        return _52WeekHighPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public void set_52WeekHighPrice(BigDecimal _52WeekHighPrice) {
        this._52WeekHighPrice = _52WeekHighPrice;
    }

    public BigDecimal get_52WeekHighLowPriceDiff() {
        return _52WeekHighLowPriceDiff.setScale(2, RoundingMode.HALF_UP);
    }

    public void set_52WeekHighLowPriceDiff(BigDecimal _52WeekHighLowPriceDiff) {
        this._52WeekHighLowPriceDiff = _52WeekHighLowPriceDiff;
    }

    public BigDecimal get_52WeekHighPriceDiff() {
        return _52WeekHighPriceDiff.setScale(2, RoundingMode.HALF_UP);
    }

    public void set_52WeekHighPriceDiff(BigDecimal _52WeekHighPriceDiff) {
        this._52WeekHighPriceDiff = _52WeekHighPriceDiff;
    }

    public BigDecimal get_52WeekLowPriceDiff() {
        return _52WeekLowPriceDiff.setScale(2, RoundingMode.HALF_UP);
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

    public String getQuoteInstant() {
        return quoteInstant;
    }

    public void setQuoteInstant(String quoteInstant) {
        this.quoteInstant = quoteInstant;
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
        GFinanceStockInfo that = (GFinanceStockInfo) o;
        return stockName.equals(that.stockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockName);
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @Override
    public String toString() {
        return "GFinanceStockInfo{" +
                "id=" + id +
                ", stockName='" + stockName + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", mktCapRealValue=" + mktCapRealValue +
                ", mktCapFriendyValue='" + mktCapFriendyValue + '\'' +
                ", ccy='" + ccy + '\'' +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", p2e=" + p2e +
                ", quoteInstant='" + quoteInstant + '\'' +
                ", stockTS=" + stockTS +
                ", dailyPctChange=" + dailyPctChange +
                ", dailyHighPrice=" + dailyHighPrice +
                ", dailyLowPrice=" + dailyLowPrice +
                ", country='" + country + '\'' +
                ", stockInstant=" + stockInstant +
                ", dailyPctPnLChange=" + dailyPctPnLChange +
                '}';
    }

    public BigDecimal getDailyPctChange() {
        return dailyPctChange.setScale(2, RoundingMode.HALF_UP);
    }

    public void setDailyPctChange(BigDecimal dailyPctChange) {
        this.dailyPctChange = dailyPctChange;
    }

    public BigDecimal getDailyHighPrice() {
        return dailyHighPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public void setDailyHighPrice(BigDecimal dailyHighPrice) {
        this.dailyHighPrice = dailyHighPrice;
    }

    public BigDecimal getDailyLowPrice() {
        return dailyLowPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public void setDailyLowPrice(BigDecimal dailyLowPrice) {
        this.dailyLowPrice = dailyLowPrice;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Instant getStockInstant() {
        return stockInstant;
    }

    public void setStockInstant(Instant stockInstant) {
        this.stockInstant = stockInstant;
    }

    public BigDecimal getDailyPctPnLChange() {
        return dailyPctPnLChange.setScale(2, RoundingMode.HALF_UP);
    }

    public void setDailyPctPnLChange(BigDecimal dailyPctPnLChange) {
        this.dailyPctPnLChange = dailyPctPnLChange;
    }
}
