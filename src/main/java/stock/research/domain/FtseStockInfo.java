package stock.research.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class FtseStockInfo {

    private String stockName;
    private String stockCode;
    private String stockURL;
    private Integer stockRankIndex;
    private Double stockMktCap;
    private BigDecimal currentMarketPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighLowPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPriceDiff =  BigDecimal.ZERO;
    private Double eps;
    private Instant timestamp;

    public FtseStockInfo(String stockName, String stockURL,
                         String stockCode, Double stockMktCap, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        this.stockCode = stockCode;
        this.stockMktCap = stockMktCap;
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
        return  Objects.equals(getStockRankIndex(), that.getStockRankIndex()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash( getStockRankIndex());
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
}
