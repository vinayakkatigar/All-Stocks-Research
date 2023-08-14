
package stock.research.yfinance.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "maxAge",
    "preMarketChangePercent",
    "preMarketChange",
    "preMarketTime",
    "preMarketPrice",
    "preMarketSource",
    "postMarketChangePercent",
    "postMarketChange",
    "postMarketTime",
    "postMarketPrice",
    "postMarketSource",
    "regularMarketChangePercent",
    "regularMarketChange",
    "regularMarketTime",
    "priceHint",
    "regularMarketPrice",
    "regularMarketDayHigh",
    "regularMarketDayLow",
    "regularMarketVolume",
    "averageDailyVolume10Day",
    "averageDailyVolume3Month",
    "regularMarketPreviousClose",
    "regularMarketSource",
    "regularMarketOpen",
    "strikePrice",
    "openInterest",
    "exchange",
    "exchangeName",
    "exchangeDataDelayedBy",
    "marketState",
    "quoteType",
    "symbol",
    "underlyingSymbol",
    "shortName",
    "longName",
    "currency",
    "quoteSourceName",
    "currencySymbol",
    "fromCurrency",
    "toCurrency",
    "lastMarket",
    "volume24Hr",
    "volumeAllCurrencies",
    "circulatingSupply",
    "marketCap"
})
@Generated("jsonschema2pojo")
public class Price {

    @JsonProperty("maxAge")
    private Integer maxAge;
    @JsonProperty("preMarketChangePercent")
    private PreMarketChangePercent preMarketChangePercent;
    @JsonProperty("preMarketChange")
    private PreMarketChange preMarketChange;
    @JsonProperty("preMarketTime")
    private Integer preMarketTime;
    @JsonProperty("preMarketPrice")
    private PreMarketPrice preMarketPrice;
    @JsonProperty("preMarketSource")
    private String preMarketSource;
    @JsonProperty("postMarketChangePercent")
    private PostMarketChangePercent postMarketChangePercent;
    @JsonProperty("postMarketChange")
    private PostMarketChange postMarketChange;
    @JsonProperty("postMarketTime")
    private Integer postMarketTime;
    @JsonProperty("postMarketPrice")
    private PostMarketPrice postMarketPrice;
    @JsonProperty("postMarketSource")
    private String postMarketSource;
    @JsonProperty("regularMarketChangePercent")
    private RegularMarketChangePercent regularMarketChangePercent;
    @JsonProperty("regularMarketChange")
    private RegularMarketChange regularMarketChange;
    @JsonProperty("regularMarketTime")
    private Integer regularMarketTime;
    @JsonProperty("priceHint")
    private PriceHint priceHint;
    @JsonProperty("regularMarketPrice")
    private RegularMarketPrice regularMarketPrice;
    @JsonProperty("regularMarketDayHigh")
    private RegularMarketDayHigh regularMarketDayHigh;
    @JsonProperty("regularMarketDayLow")
    private RegularMarketDayLow regularMarketDayLow;
    @JsonProperty("regularMarketVolume")
    private RegularMarketVolume regularMarketVolume;
    @JsonProperty("averageDailyVolume10Day")
    private AverageDailyVolume10Day averageDailyVolume10Day;
    @JsonProperty("averageDailyVolume3Month")
    private AverageDailyVolume3Month averageDailyVolume3Month;
    @JsonProperty("regularMarketPreviousClose")
    private RegularMarketPreviousClose regularMarketPreviousClose;
    @JsonProperty("regularMarketSource")
    private String regularMarketSource;
    @JsonProperty("regularMarketOpen")
    private RegularMarketOpen regularMarketOpen;
    @JsonProperty("strikePrice")
    private StrikePrice strikePrice;
    @JsonProperty("openInterest")
    private OpenInterest openInterest;
    @JsonProperty("exchange")
    private String exchange;
    @JsonProperty("exchangeName")
    private String exchangeName;
    @JsonProperty("exchangeDataDelayedBy")
    private Integer exchangeDataDelayedBy;
    @JsonProperty("marketState")
    private String marketState;
    @JsonProperty("quoteType")
    private String quoteType;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("underlyingSymbol")
    private Object underlyingSymbol;
    @JsonProperty("shortName")
    private String shortName;
    @JsonProperty("longName")
    private String longName;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("quoteSourceName")
    private String quoteSourceName;
    @JsonProperty("currencySymbol")
    private String currencySymbol;
    @JsonProperty("fromCurrency")
    private Object fromCurrency;
    @JsonProperty("toCurrency")
    private Object toCurrency;
    @JsonProperty("lastMarket")
    private Object lastMarket;
    @JsonProperty("volume24Hr")
    private Volume24Hr volume24Hr;
    @JsonProperty("volumeAllCurrencies")
    private VolumeAllCurrencies volumeAllCurrencies;
    @JsonProperty("circulatingSupply")
    private CirculatingSupply circulatingSupply;
    @JsonProperty("marketCap")
    private MarketCap marketCap;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("maxAge")
    public Integer getMaxAge() {
        return maxAge;
    }

    @JsonProperty("maxAge")
    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    @JsonProperty("preMarketChangePercent")
    public PreMarketChangePercent getPreMarketChangePercent() {
        return preMarketChangePercent;
    }

    @JsonProperty("preMarketChangePercent")
    public void setPreMarketChangePercent(PreMarketChangePercent preMarketChangePercent) {
        this.preMarketChangePercent = preMarketChangePercent;
    }

    @JsonProperty("preMarketChange")
    public PreMarketChange getPreMarketChange() {
        return preMarketChange;
    }

    @JsonProperty("preMarketChange")
    public void setPreMarketChange(PreMarketChange preMarketChange) {
        this.preMarketChange = preMarketChange;
    }

    @JsonProperty("preMarketTime")
    public Integer getPreMarketTime() {
        return preMarketTime;
    }

    @JsonProperty("preMarketTime")
    public void setPreMarketTime(Integer preMarketTime) {
        this.preMarketTime = preMarketTime;
    }

    @JsonProperty("preMarketPrice")
    public PreMarketPrice getPreMarketPrice() {
        return preMarketPrice;
    }

    @JsonProperty("preMarketPrice")
    public void setPreMarketPrice(PreMarketPrice preMarketPrice) {
        this.preMarketPrice = preMarketPrice;
    }

    @JsonProperty("preMarketSource")
    public String getPreMarketSource() {
        return preMarketSource;
    }

    @JsonProperty("preMarketSource")
    public void setPreMarketSource(String preMarketSource) {
        this.preMarketSource = preMarketSource;
    }

    @JsonProperty("postMarketChangePercent")
    public PostMarketChangePercent getPostMarketChangePercent() {
        return postMarketChangePercent;
    }

    @JsonProperty("postMarketChangePercent")
    public void setPostMarketChangePercent(PostMarketChangePercent postMarketChangePercent) {
        this.postMarketChangePercent = postMarketChangePercent;
    }

    @JsonProperty("postMarketChange")
    public PostMarketChange getPostMarketChange() {
        return postMarketChange;
    }

    @JsonProperty("postMarketChange")
    public void setPostMarketChange(PostMarketChange postMarketChange) {
        this.postMarketChange = postMarketChange;
    }

    @JsonProperty("postMarketTime")
    public Integer getPostMarketTime() {
        return postMarketTime;
    }

    @JsonProperty("postMarketTime")
    public void setPostMarketTime(Integer postMarketTime) {
        this.postMarketTime = postMarketTime;
    }

    @JsonProperty("postMarketPrice")
    public PostMarketPrice getPostMarketPrice() {
        return postMarketPrice;
    }

    @JsonProperty("postMarketPrice")
    public void setPostMarketPrice(PostMarketPrice postMarketPrice) {
        this.postMarketPrice = postMarketPrice;
    }

    @JsonProperty("postMarketSource")
    public String getPostMarketSource() {
        return postMarketSource;
    }

    @JsonProperty("postMarketSource")
    public void setPostMarketSource(String postMarketSource) {
        this.postMarketSource = postMarketSource;
    }

    @JsonProperty("regularMarketChangePercent")
    public RegularMarketChangePercent getRegularMarketChangePercent() {
        return regularMarketChangePercent;
    }

    @JsonProperty("regularMarketChangePercent")
    public void setRegularMarketChangePercent(RegularMarketChangePercent regularMarketChangePercent) {
        this.regularMarketChangePercent = regularMarketChangePercent;
    }

    @JsonProperty("regularMarketChange")
    public RegularMarketChange getRegularMarketChange() {
        return regularMarketChange;
    }

    @JsonProperty("regularMarketChange")
    public void setRegularMarketChange(RegularMarketChange regularMarketChange) {
        this.regularMarketChange = regularMarketChange;
    }

    @JsonProperty("regularMarketTime")
    public Integer getRegularMarketTime() {
        return regularMarketTime;
    }

    @JsonProperty("regularMarketTime")
    public void setRegularMarketTime(Integer regularMarketTime) {
        this.regularMarketTime = regularMarketTime;
    }

    @JsonProperty("priceHint")
    public PriceHint getPriceHint() {
        return priceHint;
    }

    @JsonProperty("priceHint")
    public void setPriceHint(PriceHint priceHint) {
        this.priceHint = priceHint;
    }

    @JsonProperty("regularMarketPrice")
    public RegularMarketPrice getRegularMarketPrice() {
        return regularMarketPrice;
    }

    @JsonProperty("regularMarketPrice")
    public void setRegularMarketPrice(RegularMarketPrice regularMarketPrice) {
        this.regularMarketPrice = regularMarketPrice;
    }

    @JsonProperty("regularMarketDayHigh")
    public RegularMarketDayHigh getRegularMarketDayHigh() {
        return regularMarketDayHigh;
    }

    @JsonProperty("regularMarketDayHigh")
    public void setRegularMarketDayHigh(RegularMarketDayHigh regularMarketDayHigh) {
        this.regularMarketDayHigh = regularMarketDayHigh;
    }

    @JsonProperty("regularMarketDayLow")
    public RegularMarketDayLow getRegularMarketDayLow() {
        return regularMarketDayLow;
    }

    @JsonProperty("regularMarketDayLow")
    public void setRegularMarketDayLow(RegularMarketDayLow regularMarketDayLow) {
        this.regularMarketDayLow = regularMarketDayLow;
    }

    @JsonProperty("regularMarketVolume")
    public RegularMarketVolume getRegularMarketVolume() {
        return regularMarketVolume;
    }

    @JsonProperty("regularMarketVolume")
    public void setRegularMarketVolume(RegularMarketVolume regularMarketVolume) {
        this.regularMarketVolume = regularMarketVolume;
    }

    @JsonProperty("averageDailyVolume10Day")
    public AverageDailyVolume10Day getAverageDailyVolume10Day() {
        return averageDailyVolume10Day;
    }

    @JsonProperty("averageDailyVolume10Day")
    public void setAverageDailyVolume10Day(AverageDailyVolume10Day averageDailyVolume10Day) {
        this.averageDailyVolume10Day = averageDailyVolume10Day;
    }

    @JsonProperty("averageDailyVolume3Month")
    public AverageDailyVolume3Month getAverageDailyVolume3Month() {
        return averageDailyVolume3Month;
    }

    @JsonProperty("averageDailyVolume3Month")
    public void setAverageDailyVolume3Month(AverageDailyVolume3Month averageDailyVolume3Month) {
        this.averageDailyVolume3Month = averageDailyVolume3Month;
    }

    @JsonProperty("regularMarketPreviousClose")
    public RegularMarketPreviousClose getRegularMarketPreviousClose() {
        return regularMarketPreviousClose;
    }

    @JsonProperty("regularMarketPreviousClose")
    public void setRegularMarketPreviousClose(RegularMarketPreviousClose regularMarketPreviousClose) {
        this.regularMarketPreviousClose = regularMarketPreviousClose;
    }

    @JsonProperty("regularMarketSource")
    public String getRegularMarketSource() {
        return regularMarketSource;
    }

    @JsonProperty("regularMarketSource")
    public void setRegularMarketSource(String regularMarketSource) {
        this.regularMarketSource = regularMarketSource;
    }

    @JsonProperty("regularMarketOpen")
    public RegularMarketOpen getRegularMarketOpen() {
        return regularMarketOpen;
    }

    @JsonProperty("regularMarketOpen")
    public void setRegularMarketOpen(RegularMarketOpen regularMarketOpen) {
        this.regularMarketOpen = regularMarketOpen;
    }

    @JsonProperty("strikePrice")
    public StrikePrice getStrikePrice() {
        return strikePrice;
    }

    @JsonProperty("strikePrice")
    public void setStrikePrice(StrikePrice strikePrice) {
        this.strikePrice = strikePrice;
    }

    @JsonProperty("openInterest")
    public OpenInterest getOpenInterest() {
        return openInterest;
    }

    @JsonProperty("openInterest")
    public void setOpenInterest(OpenInterest openInterest) {
        this.openInterest = openInterest;
    }

    @JsonProperty("exchange")
    public String getExchange() {
        return exchange;
    }

    @JsonProperty("exchange")
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @JsonProperty("exchangeName")
    public String getExchangeName() {
        return exchangeName;
    }

    @JsonProperty("exchangeName")
    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    @JsonProperty("exchangeDataDelayedBy")
    public Integer getExchangeDataDelayedBy() {
        return exchangeDataDelayedBy;
    }

    @JsonProperty("exchangeDataDelayedBy")
    public void setExchangeDataDelayedBy(Integer exchangeDataDelayedBy) {
        this.exchangeDataDelayedBy = exchangeDataDelayedBy;
    }

    @JsonProperty("marketState")
    public String getMarketState() {
        return marketState;
    }

    @JsonProperty("marketState")
    public void setMarketState(String marketState) {
        this.marketState = marketState;
    }

    @JsonProperty("quoteType")
    public String getQuoteType() {
        return quoteType;
    }

    @JsonProperty("quoteType")
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("underlyingSymbol")
    public Object getUnderlyingSymbol() {
        return underlyingSymbol;
    }

    @JsonProperty("underlyingSymbol")
    public void setUnderlyingSymbol(Object underlyingSymbol) {
        this.underlyingSymbol = underlyingSymbol;
    }

    @JsonProperty("shortName")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("longName")
    public String getLongName() {
        return longName;
    }

    @JsonProperty("longName")
    public void setLongName(String longName) {
        this.longName = longName;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("quoteSourceName")
    public String getQuoteSourceName() {
        return quoteSourceName;
    }

    @JsonProperty("quoteSourceName")
    public void setQuoteSourceName(String quoteSourceName) {
        this.quoteSourceName = quoteSourceName;
    }

    @JsonProperty("currencySymbol")
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    @JsonProperty("currencySymbol")
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    @JsonProperty("fromCurrency")
    public Object getFromCurrency() {
        return fromCurrency;
    }

    @JsonProperty("fromCurrency")
    public void setFromCurrency(Object fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    @JsonProperty("toCurrency")
    public Object getToCurrency() {
        return toCurrency;
    }

    @JsonProperty("toCurrency")
    public void setToCurrency(Object toCurrency) {
        this.toCurrency = toCurrency;
    }

    @JsonProperty("lastMarket")
    public Object getLastMarket() {
        return lastMarket;
    }

    @JsonProperty("lastMarket")
    public void setLastMarket(Object lastMarket) {
        this.lastMarket = lastMarket;
    }

    @JsonProperty("volume24Hr")
    public Volume24Hr getVolume24Hr() {
        return volume24Hr;
    }

    @JsonProperty("volume24Hr")
    public void setVolume24Hr(Volume24Hr volume24Hr) {
        this.volume24Hr = volume24Hr;
    }

    @JsonProperty("volumeAllCurrencies")
    public VolumeAllCurrencies getVolumeAllCurrencies() {
        return volumeAllCurrencies;
    }

    @JsonProperty("volumeAllCurrencies")
    public void setVolumeAllCurrencies(VolumeAllCurrencies volumeAllCurrencies) {
        this.volumeAllCurrencies = volumeAllCurrencies;
    }

    @JsonProperty("circulatingSupply")
    public CirculatingSupply getCirculatingSupply() {
        return circulatingSupply;
    }

    @JsonProperty("circulatingSupply")
    public void setCirculatingSupply(CirculatingSupply circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    @JsonProperty("marketCap")
    public MarketCap getMarketCap() {
        return marketCap;
    }

    @JsonProperty("marketCap")
    public void setMarketCap(MarketCap marketCap) {
        this.marketCap = marketCap;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
