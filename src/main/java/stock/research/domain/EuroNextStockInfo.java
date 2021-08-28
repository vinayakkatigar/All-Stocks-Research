package stock.research.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

public class EuroNextStockInfo {

    public EuroNextStockInfo(){
    }
    private String stockName;
    private String stockCode;
    private String stockURL;
    private String stockCurrency;
    private String currentMarketPriceStr;
    private Integer stockRankIndex;
    private String stockMktCap;
    private BigDecimal currentMarketPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighLowPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPriceDiff =  BigDecimal.ZERO;
    private Double eps;
    private Double mktCapRealValue;

    private Double p2e;
    private Instant timestamp;

    public EuroNextStockInfo(String stockName, String stockURL,
                             String stockCode, String stockMktCap, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        this.stockCode = stockCode;
        this.stockMktCap = stockMktCap;
        this.currentMarketPrice = currentMarketPrice;
    }

    public EuroNextStockInfo(String stockName, String stockCode, String stockURL, Integer stockRankIndex, String stockMktCap, BigDecimal currentMarketPrice, BigDecimal _52WeekLowPrice, BigDecimal _52WeekHighPrice, BigDecimal _52WeekHighLowPriceDiff, BigDecimal _52WeekHighPriceDiff, BigDecimal _52WeekLowPriceDiff, Double eps, Double p2e, String timestamp) {
        this.stockName = stockName;
        this.stockCode = stockCode;
        this.stockURL = stockURL;
        this.stockRankIndex = stockRankIndex;
        this.stockMktCap = stockMktCap;
        this.currentMarketPrice = currentMarketPrice;
        this._52WeekLowPrice = _52WeekLowPrice;
        this._52WeekHighPrice = _52WeekHighPrice;
        this._52WeekHighLowPriceDiff = _52WeekHighLowPriceDiff;
        this._52WeekHighPriceDiff = _52WeekHighPriceDiff;
        this._52WeekLowPriceDiff = _52WeekLowPriceDiff;
        this.eps = eps;
        this.p2e = p2e;
        this.timestamp = Instant.parse(timestamp);
    }

    public EuroNextStockInfo(String stockCode, String stockURL) {
        this.stockCode = stockCode;
        this.stockURL = stockURL;
    }

    @Override
    public String toString() {
        return "EuroNextStockInfo{" +
                "stockName='" + stockName + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockURL='" + stockURL + '\'' +
                ", stockCurrency='" + stockCurrency + '\'' +
                ", currentMarketPriceStr='" + currentMarketPriceStr + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", stockMktCap='" + stockMktCap + '\'' +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", eps=" + eps +
                ", mktCapRealValue=" + mktCapRealValue +
                ", p2e=" + p2e +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EuroNextStockInfo that = (EuroNextStockInfo) o;
           return Objects.equals(getStockCode(), that.getStockCode()) &&
                Objects.equals(getStockURL(), that.getStockURL()) &&
                Objects.equals(getStockName(), that.getStockName()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStockCode(), getStockURL(), getStockName());
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

    public String getStockMktCap() {
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

    public void setStockMktCap(String stockMktCap) {
        this.stockMktCap = stockMktCap;
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

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
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

    public Double getP2e() {
        return p2e;
    }

    public void setP2e(Double p2e) {
        this.p2e = p2e;
    }

    public Double getMktCapRealValue() {
        return mktCapRealValue;
    }

    public void setMktCapRealValue(Double mktCapRealValue) {
        this.mktCapRealValue = mktCapRealValue;
    }

    public String getCurrentMarketPriceStr() {
        return currentMarketPriceStr;
    }

    public void setCurrentMarketPriceStr(String currentMarketPriceStr) {
        this.currentMarketPriceStr = currentMarketPriceStr;
    }

    public String getStockCurrency() {
        return stockCurrency;
    }

    public void setStockCurrency(String stockCurrency) {
        this.stockCurrency = stockCurrency;
    }
}
