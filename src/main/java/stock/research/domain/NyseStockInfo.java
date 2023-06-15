package stock.research.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "NYSE_STOCK_INFO")
public class NyseStockInfo {

    @Column(name = "NYSE_STOCK_INFO_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NYSE_STOCK_INFO_ID_SEQ")
    @SequenceGenerator(sequenceName = "NYSE_STOCK_INFO_ID_SEQ", allocationSize = 1, name = "NYSE_STOCK_INFO_ID_SEQ")
    Long id;

    @Column(name = "STOCK_NAME")
    private String stockName;

    @Transient
    private String sectorIndustry;

    @Transient
    private String stockCode;

    @Column(name = "STOCK_URL")
    private String stockURL;

    @Transient
    private String currentMarketPriceStr;

    @Column(name = "STOCK_RANK")
    private Integer stockRankIndex;

    @Column(name = "STOCK_MKT_CAP")
    private Double mktCapRealValue;

    @Transient
    private String stockMktCap;

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

    @Column(name = "EPS")
    private Double eps;

    @Column(name = "P2EPS")
    private Double p2e;

    @Column(name = "QUOTETS")
    private Instant timestamp;

    @Column(name = "STOCKTS")
    Timestamp stockTS;

    public NyseStockInfo(String stockName, String stockURL,
                         String stockCode, String stockMktCap, BigDecimal currentMarketPrice) {
        this.stockName = stockName;
        this.stockURL = stockURL;
        this.stockCode = stockCode;
        this.stockMktCap = stockMktCap;
        this.currentMarketPrice = currentMarketPrice;
    }

    public NyseStockInfo(String stockName, String stockCode, String stockURL, Integer stockRankIndex, String stockMktCap, BigDecimal currentMarketPrice, BigDecimal _52WeekLowPrice, BigDecimal _52WeekHighPrice, BigDecimal _52WeekHighLowPriceDiff, BigDecimal _52WeekHighPriceDiff, BigDecimal _52WeekLowPriceDiff, Double eps, Double p2e, String timestamp) {
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

    public NyseStockInfo(String stockCode, String stockURL) {
        this.stockCode = stockCode;
        this.stockURL = stockURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NyseStockInfo that = (NyseStockInfo) o;
        if (getStockCode() != null && that.getStockCode() != null){
            if (getStockCode().toLowerCase().equalsIgnoreCase(that.getStockCode().toLowerCase())){
                return true;
            }
        }if (getStockName() != null && that.getStockName() != null){
            if (getStockName().toLowerCase().equalsIgnoreCase(that.getStockName().toLowerCase())){
                return true;
            }
        }
        return  Objects.equals(getStockURL().toLowerCase(), that.getStockURL().toLowerCase()) ;
    }


    public String getSectorIndustry() {
        return sectorIndustry;
    }

    public void setSectorIndustry(String sectorIndustry) {
        this.sectorIndustry = sectorIndustry;
    }



    @Override
    public int hashCode() {
        return Objects.hash( getStockURL());
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

    public NyseStockInfo(){
    }

    public Timestamp getStockTS() {
        return stockTS;
    }

    public void setStockTS(Timestamp stockTS) {
        this.stockTS = stockTS;
    }

    @Override
    public String toString() {
        return "NyseStockInfo{" +
                "id=" + id +
                ", stockName='" + stockName + '\'' +
                ", sectorIndustry='" + sectorIndustry + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockURL='" + stockURL + '\'' +
                ", currentMarketPriceStr='" + currentMarketPriceStr + '\'' +
                ", stockRankIndex=" + stockRankIndex +
                ", mktCapRealValue=" + mktCapRealValue +
                ", stockMktCap='" + stockMktCap + '\'' +
                ", currentMarketPrice=" + currentMarketPrice +
                ", _52WeekLowPrice=" + _52WeekLowPrice +
                ", _52WeekHighPrice=" + _52WeekHighPrice +
                ", _52WeekHighLowPriceDiff=" + _52WeekHighLowPriceDiff +
                ", _52WeekHighPriceDiff=" + _52WeekHighPriceDiff +
                ", _52WeekLowPriceDiff=" + _52WeekLowPriceDiff +
                ", eps=" + eps +
                ", p2e=" + p2e +
                ", timestamp=" + timestamp +
                ", stockTS=" + stockTS +
                '}';
    }
}
