package stock.research.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class StockInfo {
    public StockInfo() {
    }

    public StockInfo(String stockCode, String stockURL) {
        this.stockCode = stockCode;
        this.stockURL = stockURL;
    }

    private String stockName;
    private String stockCode;
    private String stockURL;
    private String currency;

    private String stockMktCapStr;
    private Integer stockRankIndex;
    private Double stockMktCapRealValue;
    private BigDecimal currentMarketPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighLowPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPriceDiff =  BigDecimal.ZERO;
    private Double eps;
    private Double p2e;
    private Instant timestamp;

    public StockInfo(String stockName, String stockURL,
                     String stockCode, Double stockMktCapRealValue, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        this.stockCode = stockCode;
        this.stockMktCapRealValue = stockMktCapRealValue;
        this.currentMarketPrice = currentMarketPrice;
    }

    @Override
    public String toString() {
        return "StockInfo{" +
                "stockName='" + stockName + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockURL='" + stockURL + '\'' +
                ", stockMktCapStr='" + stockMktCapStr + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", stockMktCapRealValue=" + stockMktCapRealValue +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", eps=" + eps +
                ", pe=" + p2e +
                ", currency=" + currency +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockInfo that = (StockInfo) o;
        return  Objects.equals(getStockCode(), that.getStockCode()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash( getStockCode());
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

    public Double getStockMktCapRealValue() {
        return stockMktCapRealValue;
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

    public void setStockMktCapRealValue(Double stockMktCapRealValue) {
        this.stockMktCapRealValue = stockMktCapRealValue;
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

    public String getStockMktCapStr() {
        return stockMktCapStr;
    }

    public void setStockMktCapStr(String stockMktCapStr) {
        this.stockMktCapStr = stockMktCapStr;
    }

    public Double getP2e() {
        return p2e;
    }

    public void setP2e(Double p2e) {
        this.p2e = p2e;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
