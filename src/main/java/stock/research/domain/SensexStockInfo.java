package stock.research.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class SensexStockInfo {

    private String stockName;
    private String stockCode;
    private String isin;
    private String stockURL;
    private Integer stockRankIndex;
    private Double stockMktCap;
    private BigDecimal currentMarketPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPrice =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighLowPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekHighPriceDiff =  BigDecimal.ZERO;
    private BigDecimal _52WeekLowPriceDiff =  BigDecimal.ZERO;
    private Double fiiPct;
    private Double eps;
    private Double p2eps;
    private Double bv;
    private Double p2bv;
    private Instant timestamp;

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
        return "Sensex500StockInfo{" +
                "stockName='" + stockName + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", isin='" + isin + '\'' +
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
                ", eps=" + eps +
                ", p2eps=" + p2eps +
                ", bv=" + bv +
                ", p2bv=" + p2bv +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensexStockInfo that = (SensexStockInfo) o;
        return Objects.equals(getStockName(), that.getStockName()) &&
                Objects.equals(getStockCode(), that.getStockCode()) &&
                Objects.equals(getIsin(), that.getIsin()) &&
                Objects.equals(getStockURL(), that.getStockURL()) &&
                Objects.equals(getStockRankIndex(), that.getStockRankIndex()) &&
                Objects.equals(getStockMktCap(), that.getStockMktCap()) &&
                Objects.equals(getCurrentMarketPrice(), that.getCurrentMarketPrice()) &&
                Objects.equals(get_52WeekLowPrice(), that.get_52WeekLowPrice()) &&
                Objects.equals(get_52WeekHighPrice(), that.get_52WeekHighPrice()) &&
                Objects.equals(get_52WeekHighLowPriceDiff(), that.get_52WeekHighLowPriceDiff()) &&
                Objects.equals(get_52WeekHighPriceDiff(), that.get_52WeekHighPriceDiff()) &&
                Objects.equals(get_52WeekLowPriceDiff(), that.get_52WeekLowPriceDiff()) &&
                Objects.equals(getFiiPct(), that.getFiiPct()) &&
                Objects.equals(getEps(), that.getEps()) &&
                Objects.equals(getP2eps(), that.getP2eps()) &&
                Objects.equals(getBv(), that.getBv()) &&
                Objects.equals(getP2bv(), that.getP2bv()) &&
                Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStockName(), getStockCode(), getIsin(), getStockURL(), getStockRankIndex(), getStockMktCap(), getCurrentMarketPrice(), get_52WeekLowPrice(), get_52WeekHighPrice(), get_52WeekHighLowPriceDiff(), get_52WeekHighPriceDiff(), get_52WeekLowPriceDiff(), getFiiPct(), getEps(), getP2eps(), getBv(), getP2bv(), getTimestamp());
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

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
