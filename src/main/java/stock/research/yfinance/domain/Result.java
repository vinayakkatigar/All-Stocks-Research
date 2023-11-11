
package stock.research.yfinance.domain;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "language",
    "region",
    "quoteType",
    "typeDisp",
    "quoteSourceName",
    "triggerable",
    "customPriceAlertConfidence",
    "currency",
    "marketState",
    "exchange",
    "shortName",
    "longName",
    "messageBoardId",
    "exchangeTimezoneName",
    "exchangeTimezoneShortName",
    "gmtOffSetMilliseconds",
    "market",
    "esgPopulated",
    "regularMarketChangePercent",
    "regularMarketPrice",
    "firstTradeDateMilliseconds",
    "priceHint",
    "regularMarketChange",
    "regularMarketTime",
    "regularMarketDayHigh",
    "regularMarketDayRange",
    "regularMarketDayLow",
    "regularMarketVolume",
    "regularMarketPreviousClose",
    "bid",
    "ask",
    "bidSize",
    "askSize",
    "fullExchangeName",
    "financialCurrency",
    "regularMarketOpen",
    "averageDailyVolume3Month",
    "averageDailyVolume10Day",
    "fiftyTwoWeekLowChange",
    "fiftyTwoWeekLowChangePercent",
    "fiftyTwoWeekRange",
    "fiftyTwoWeekHighChange",
    "fiftyTwoWeekHighChangePercent",
    "fiftyTwoWeekLow",
    "fiftyTwoWeekHigh",
    "fiftyTwoWeekChangePercent",
    "dividendDate",
    "earningsTimestamp",
    "earningsTimestampStart",
    "earningsTimestampEnd",
    "trailingAnnualDividendRate",
    "trailingPE",
    "dividendRate",
    "trailingAnnualDividendYield",
    "dividendYield",
    "epsTrailingTwelveMonths",
    "epsForward",
    "epsCurrentYear",
    "priceEpsCurrentYear",
    "sharesOutstanding",
    "bookValue",
    "fiftyDayAverage",
    "fiftyDayAverageChange",
    "fiftyDayAverageChangePercent",
    "twoHundredDayAverage",
    "twoHundredDayAverageChange",
    "twoHundredDayAverageChangePercent",
    "marketCap",
    "forwardPE",
    "priceToBook",
    "sourceInterval",
    "exchangeDataDelayedBy",
    "averageAnalystRating",
    "tradeable",
    "cryptoTradeable",
    "displayName",
    "symbol",
    "ipoExpectedDate",
    "prevName",
    "nameChangeDate"
})
@Generated("jsonschema2pojo")
public class Result {

    @JsonProperty("language")
    private String language;
    @JsonProperty("region")
    private String region;
    @JsonProperty("quoteType")
    private String quoteType;
    @JsonProperty("typeDisp")
    private String typeDisp;
    @JsonProperty("quoteSourceName")
    private String quoteSourceName;
    @JsonProperty("triggerable")
    private Boolean triggerable;
    @JsonProperty("customPriceAlertConfidence")
    private String customPriceAlertConfidence;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("marketState")
    private String marketState;
    @JsonProperty("exchange")
    private String exchange;
    @JsonProperty("shortName")
    private String shortName;
    @JsonProperty("longName")
    private String longName;
    @JsonProperty("messageBoardId")
    private String messageBoardId;
    @JsonProperty("exchangeTimezoneName")
    private String exchangeTimezoneName;
    @JsonProperty("exchangeTimezoneShortName")
    private String exchangeTimezoneShortName;
    @JsonProperty("gmtOffSetMilliseconds")
    private Long gmtOffSetMilliseconds;
    @JsonProperty("market")
    private String market;
    @JsonProperty("esgPopulated")
    private Boolean esgPopulated;
    @JsonProperty("regularMarketChangePercent")
    private Double regularMarketChangePercent;
    @JsonProperty("regularMarketPrice")
    private Double regularMarketPrice;
    @JsonProperty("firstTradeDateMilliseconds")
    private Long firstTradeDateMilliseconds;
    @JsonProperty("priceHint")
    private Long priceHint;
    @JsonProperty("regularMarketChange")
    private Double regularMarketChange;
    @JsonProperty("regularMarketTime")
    private Long regularMarketTime;
    @JsonProperty("regularMarketDayHigh")
    private Double regularMarketDayHigh;
    @JsonProperty("regularMarketDayRange")
    private String regularMarketDayRange;
    @JsonProperty("regularMarketDayLow")
    private Double regularMarketDayLow;
    @JsonProperty("regularMarketVolume")
    private Long regularMarketVolume;
    @JsonProperty("regularMarketPreviousClose")
    private Double regularMarketPreviousClose;
    @JsonProperty("bid")
    private Double bid;
    @JsonProperty("ask")
    private Double ask;
    @JsonProperty("bidSize")
    private Long bidSize;
    @JsonProperty("askSize")
    private Long askSize;
    @JsonProperty("fullExchangeName")
    private String fullExchangeName;
    @JsonProperty("financialCurrency")
    private String financialCurrency;
    @JsonProperty("regularMarketOpen")
    private Double regularMarketOpen;
    @JsonProperty("averageDailyVolume3Month")
    private Long averageDailyVolume3Month;
    @JsonProperty("averageDailyVolume10Day")
    private Long averageDailyVolume10Day;
    @JsonProperty("fiftyTwoWeekLowChange")
    private Double fiftyTwoWeekLowChange;
    @JsonProperty("fiftyTwoWeekLowChangePercent")
    private Double fiftyTwoWeekLowChangePercent;
    @JsonProperty("fiftyTwoWeekRange")
    private String fiftyTwoWeekRange;
    @JsonProperty("fiftyTwoWeekHighChange")
    private Double fiftyTwoWeekHighChange;
    @JsonProperty("fiftyTwoWeekHighChangePercent")
    private Double fiftyTwoWeekHighChangePercent;
    @JsonProperty("fiftyTwoWeekLow")
    private Double fiftyTwoWeekLow;
    @JsonProperty("fiftyTwoWeekHigh")
    private Double fiftyTwoWeekHigh;
    @JsonProperty("fiftyTwoWeekChangePercent")
    private Double fiftyTwoWeekChangePercent;
    @JsonProperty("dividendDate")
    private Long dividendDate;
    @JsonProperty("earningsTimestamp")
    private Long earningsTimestamp;
    @JsonProperty("earningsTimestampStart")
    private Long earningsTimestampStart;
    @JsonProperty("earningsTimestampEnd")
    private Long earningsTimestampEnd;
    @JsonProperty("trailingAnnualDividendRate")
    private Double trailingAnnualDividendRate;
    @JsonProperty("trailingPE")
    private Double trailingPE;
    @JsonProperty("dividendRate")
    private Double dividendRate;
    @JsonProperty("trailingAnnualDividendYield")
    private Double trailingAnnualDividendYield;
    @JsonProperty("dividendYield")
    private Double dividendYield;
    @JsonProperty("epsTrailingTwelveMonths")
    private Double epsTrailingTwelveMonths;
    @JsonProperty("epsForward")
    private Double epsForward;
    @JsonProperty("epsCurrentYear")
    private Double epsCurrentYear;
    @JsonProperty("priceEpsCurrentYear")
    private Double priceEpsCurrentYear;
    @JsonProperty("sharesOutstanding")
    private Long sharesOutstanding;
    @JsonProperty("bookValue")
    private Double bookValue;
    @JsonProperty("fiftyDayAverage")
    private Double fiftyDayAverage;
    @JsonProperty("fiftyDayAverageChange")
    private Double fiftyDayAverageChange;
    @JsonProperty("fiftyDayAverageChangePercent")
    private Double fiftyDayAverageChangePercent;
    @JsonProperty("twoHundredDayAverage")
    private Double twoHundredDayAverage;
    @JsonProperty("twoHundredDayAverageChange")
    private Double twoHundredDayAverageChange;
    @JsonProperty("twoHundredDayAverageChangePercent")
    private Double twoHundredDayAverageChangePercent;
    @JsonProperty("marketCap")
    private Long marketCap;
    @JsonProperty("forwardPE")
    private Double forwardPE;
    @JsonProperty("priceToBook")
    private Double priceToBook;
    @JsonProperty("sourceInterval")
    private Long sourceInterval;
    @JsonProperty("exchangeDataDelayedBy")
    private Long exchangeDataDelayedBy;
    @JsonProperty("averageAnalystRating")
    private String averageAnalystRating;
    @JsonProperty("tradeable")
    private Boolean tradeable;
    @JsonProperty("cryptoTradeable")
    private Boolean cryptoTradeable;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("ipoExpectedDate")
    private String ipoExpectedDate;
    @JsonProperty("prevName")
    private String prevName;
    @JsonProperty("nameChangeDate")
    private String nameChangeDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    @JsonProperty("region")
    public void setRegion(String region) {
        this.region = region;
    }

    @JsonProperty("quoteType")
    public String getQuoteType() {
        return quoteType;
    }

    @JsonProperty("quoteType")
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    @JsonProperty("typeDisp")
    public String getTypeDisp() {
        return typeDisp;
    }

    @JsonProperty("typeDisp")
    public void setTypeDisp(String typeDisp) {
        this.typeDisp = typeDisp;
    }

    @JsonProperty("quoteSourceName")
    public String getQuoteSourceName() {
        return quoteSourceName;
    }

    @JsonProperty("quoteSourceName")
    public void setQuoteSourceName(String quoteSourceName) {
        this.quoteSourceName = quoteSourceName;
    }

    @JsonProperty("triggerable")
    public Boolean getTriggerable() {
        return triggerable;
    }

    @JsonProperty("triggerable")
    public void setTriggerable(Boolean triggerable) {
        this.triggerable = triggerable;
    }

    @JsonProperty("customPriceAlertConfidence")
    public String getCustomPriceAlertConfidence() {
        return customPriceAlertConfidence;
    }

    @JsonProperty("customPriceAlertConfidence")
    public void setCustomPriceAlertConfidence(String customPriceAlertConfidence) {
        this.customPriceAlertConfidence = customPriceAlertConfidence;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("marketState")
    public String getMarketState() {
        return marketState;
    }

    @JsonProperty("marketState")
    public void setMarketState(String marketState) {
        this.marketState = marketState;
    }

    @JsonProperty("exchange")
    public String getExchange() {
        return exchange;
    }

    @JsonProperty("exchange")
    public void setExchange(String exchange) {
        this.exchange = exchange;
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

    @JsonProperty("messageBoardId")
    public String getMessageBoardId() {
        return messageBoardId;
    }

    @JsonProperty("messageBoardId")
    public void setMessageBoardId(String messageBoardId) {
        this.messageBoardId = messageBoardId;
    }

    @JsonProperty("exchangeTimezoneName")
    public String getExchangeTimezoneName() {
        return exchangeTimezoneName;
    }

    @JsonProperty("exchangeTimezoneName")
    public void setExchangeTimezoneName(String exchangeTimezoneName) {
        this.exchangeTimezoneName = exchangeTimezoneName;
    }

    @JsonProperty("exchangeTimezoneShortName")
    public String getExchangeTimezoneShortName() {
        return exchangeTimezoneShortName;
    }

    @JsonProperty("exchangeTimezoneShortName")
    public void setExchangeTimezoneShortName(String exchangeTimezoneShortName) {
        this.exchangeTimezoneShortName = exchangeTimezoneShortName;
    }

    @JsonProperty("gmtOffSetMilliseconds")
    public Long getGmtOffSetMilliseconds() {
        return gmtOffSetMilliseconds;
    }

    @JsonProperty("gmtOffSetMilliseconds")
    public void setGmtOffSetMilliseconds(Long gmtOffSetMilliseconds) {
        this.gmtOffSetMilliseconds = gmtOffSetMilliseconds;
    }

    @JsonProperty("market")
    public String getMarket() {
        return market;
    }

    @JsonProperty("market")
    public void setMarket(String market) {
        this.market = market;
    }

    @JsonProperty("esgPopulated")
    public Boolean getEsgPopulated() {
        return esgPopulated;
    }

    @JsonProperty("esgPopulated")
    public void setEsgPopulated(Boolean esgPopulated) {
        this.esgPopulated = esgPopulated;
    }

    @JsonProperty("regularMarketChangePercent")
    public Double getRegularMarketChangePercent() {
        return regularMarketChangePercent;
    }

    @JsonProperty("regularMarketChangePercent")
    public void setRegularMarketChangePercent(Double regularMarketChangePercent) {
        this.regularMarketChangePercent = regularMarketChangePercent;
    }

    @JsonProperty("regularMarketPrice")
    public Double getRegularMarketPrice() {
        return regularMarketPrice;
    }

    @JsonProperty("regularMarketPrice")
    public void setRegularMarketPrice(Double regularMarketPrice) {
        this.regularMarketPrice = regularMarketPrice;
    }

    @JsonProperty("firstTradeDateMilliseconds")
    public Long getFirstTradeDateMilliseconds() {
        return firstTradeDateMilliseconds;
    }

    @JsonProperty("firstTradeDateMilliseconds")
    public void setFirstTradeDateMilliseconds(Long firstTradeDateMilliseconds) {
        this.firstTradeDateMilliseconds = firstTradeDateMilliseconds;
    }

    @JsonProperty("priceHint")
    public Long getPriceHint() {
        return priceHint;
    }

    @JsonProperty("priceHint")
    public void setPriceHint(Long priceHint) {
        this.priceHint = priceHint;
    }

    @JsonProperty("regularMarketChange")
    public Double getRegularMarketChange() {
        return regularMarketChange;
    }

    @JsonProperty("regularMarketChange")
    public void setRegularMarketChange(Double regularMarketChange) {
        this.regularMarketChange = regularMarketChange;
    }

    @JsonProperty("regularMarketTime")
    public Long getRegularMarketTime() {
        return regularMarketTime;
    }

    @JsonProperty("regularMarketTime")
    public void setRegularMarketTime(Long regularMarketTime) {
        this.regularMarketTime = regularMarketTime;
    }

    @JsonProperty("regularMarketDayHigh")
    public Double getRegularMarketDayHigh() {
        return regularMarketDayHigh;
    }

    @JsonProperty("regularMarketDayHigh")
    public void setRegularMarketDayHigh(Double regularMarketDayHigh) {
        this.regularMarketDayHigh = regularMarketDayHigh;
    }

    @JsonProperty("regularMarketDayRange")
    public String getRegularMarketDayRange() {
        return regularMarketDayRange;
    }

    @JsonProperty("regularMarketDayRange")
    public void setRegularMarketDayRange(String regularMarketDayRange) {
        this.regularMarketDayRange = regularMarketDayRange;
    }

    @JsonProperty("regularMarketDayLow")
    public Double getRegularMarketDayLow() {
        return regularMarketDayLow;
    }

    @JsonProperty("regularMarketDayLow")
    public void setRegularMarketDayLow(Double regularMarketDayLow) {
        this.regularMarketDayLow = regularMarketDayLow;
    }

    @JsonProperty("regularMarketVolume")
    public Long getRegularMarketVolume() {
        return regularMarketVolume;
    }

    @JsonProperty("regularMarketVolume")
    public void setRegularMarketVolume(Long regularMarketVolume) {
        this.regularMarketVolume = regularMarketVolume;
    }

    @JsonProperty("regularMarketPreviousClose")
    public Double getRegularMarketPreviousClose() {
        return regularMarketPreviousClose;
    }

    @JsonProperty("regularMarketPreviousClose")
    public void setRegularMarketPreviousClose(Double regularMarketPreviousClose) {
        this.regularMarketPreviousClose = regularMarketPreviousClose;
    }

    @JsonProperty("bid")
    public Double getBid() {
        return bid;
    }

    @JsonProperty("bid")
    public void setBid(Double bid) {
        this.bid = bid;
    }

    @JsonProperty("ask")
    public Double getAsk() {
        return ask;
    }

    @JsonProperty("ask")
    public void setAsk(Double ask) {
        this.ask = ask;
    }

    @JsonProperty("bidSize")
    public Long getBidSize() {
        return bidSize;
    }

    @JsonProperty("bidSize")
    public void setBidSize(Long bidSize) {
        this.bidSize = bidSize;
    }

    @JsonProperty("askSize")
    public Long getAskSize() {
        return askSize;
    }

    @JsonProperty("askSize")
    public void setAskSize(Long askSize) {
        this.askSize = askSize;
    }

    @JsonProperty("fullExchangeName")
    public String getFullExchangeName() {
        return fullExchangeName;
    }

    @JsonProperty("fullExchangeName")
    public void setFullExchangeName(String fullExchangeName) {
        this.fullExchangeName = fullExchangeName;
    }

    @JsonProperty("financialCurrency")
    public String getFinancialCurrency() {
        return financialCurrency;
    }

    @JsonProperty("financialCurrency")
    public void setFinancialCurrency(String financialCurrency) {
        this.financialCurrency = financialCurrency;
    }

    @JsonProperty("regularMarketOpen")
    public Double getRegularMarketOpen() {
        return regularMarketOpen;
    }

    @JsonProperty("regularMarketOpen")
    public void setRegularMarketOpen(Double regularMarketOpen) {
        this.regularMarketOpen = regularMarketOpen;
    }

    @JsonProperty("averageDailyVolume3Month")
    public Long getAverageDailyVolume3Month() {
        return averageDailyVolume3Month;
    }

    @JsonProperty("averageDailyVolume3Month")
    public void setAverageDailyVolume3Month(Long averageDailyVolume3Month) {
        this.averageDailyVolume3Month = averageDailyVolume3Month;
    }

    @JsonProperty("averageDailyVolume10Day")
    public Long getAverageDailyVolume10Day() {
        return averageDailyVolume10Day;
    }

    @JsonProperty("averageDailyVolume10Day")
    public void setAverageDailyVolume10Day(Long averageDailyVolume10Day) {
        this.averageDailyVolume10Day = averageDailyVolume10Day;
    }

    @JsonProperty("fiftyTwoWeekLowChange")
    public Double getFiftyTwoWeekLowChange() {
        return fiftyTwoWeekLowChange;
    }

    @JsonProperty("fiftyTwoWeekLowChange")
    public void setFiftyTwoWeekLowChange(Double fiftyTwoWeekLowChange) {
        this.fiftyTwoWeekLowChange = fiftyTwoWeekLowChange;
    }

    @JsonProperty("fiftyTwoWeekLowChangePercent")
    public Double getFiftyTwoWeekLowChangePercent() {
        return fiftyTwoWeekLowChangePercent;
    }

    @JsonProperty("fiftyTwoWeekLowChangePercent")
    public void setFiftyTwoWeekLowChangePercent(Double fiftyTwoWeekLowChangePercent) {
        this.fiftyTwoWeekLowChangePercent = fiftyTwoWeekLowChangePercent;
    }

    @JsonProperty("fiftyTwoWeekRange")
    public String getFiftyTwoWeekRange() {
        return fiftyTwoWeekRange;
    }

    @JsonProperty("fiftyTwoWeekRange")
    public void setFiftyTwoWeekRange(String fiftyTwoWeekRange) {
        this.fiftyTwoWeekRange = fiftyTwoWeekRange;
    }

    @JsonProperty("fiftyTwoWeekHighChange")
    public Double getFiftyTwoWeekHighChange() {
        return fiftyTwoWeekHighChange;
    }

    @JsonProperty("fiftyTwoWeekHighChange")
    public void setFiftyTwoWeekHighChange(Double fiftyTwoWeekHighChange) {
        this.fiftyTwoWeekHighChange = fiftyTwoWeekHighChange;
    }

    @JsonProperty("fiftyTwoWeekHighChangePercent")
    public Double getFiftyTwoWeekHighChangePercent() {
        return fiftyTwoWeekHighChangePercent;
    }

    @JsonProperty("fiftyTwoWeekHighChangePercent")
    public void setFiftyTwoWeekHighChangePercent(Double fiftyTwoWeekHighChangePercent) {
        this.fiftyTwoWeekHighChangePercent = fiftyTwoWeekHighChangePercent;
    }

    @JsonProperty("fiftyTwoWeekLow")
    public Double getFiftyTwoWeekLow() {
        return fiftyTwoWeekLow;
    }

    @JsonProperty("fiftyTwoWeekLow")
    public void setFiftyTwoWeekLow(Double fiftyTwoWeekLow) {
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    }

    @JsonProperty("fiftyTwoWeekHigh")
    public Double getFiftyTwoWeekHigh() {
        return fiftyTwoWeekHigh;
    }

    @JsonProperty("fiftyTwoWeekHigh")
    public void setFiftyTwoWeekHigh(Double fiftyTwoWeekHigh) {
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }

    @JsonProperty("fiftyTwoWeekChangePercent")
    public Double getFiftyTwoWeekChangePercent() {
        return fiftyTwoWeekChangePercent;
    }

    @JsonProperty("fiftyTwoWeekChangePercent")
    public void setFiftyTwoWeekChangePercent(Double fiftyTwoWeekChangePercent) {
        this.fiftyTwoWeekChangePercent = fiftyTwoWeekChangePercent;
    }

    @JsonProperty("dividendDate")
    public Long getDividendDate() {
        return dividendDate;
    }

    @JsonProperty("dividendDate")
    public void setDividendDate(Long dividendDate) {
        this.dividendDate = dividendDate;
    }

    @JsonProperty("earningsTimestamp")
    public Long getEarningsTimestamp() {
        return earningsTimestamp;
    }

    @JsonProperty("earningsTimestamp")
    public void setEarningsTimestamp(Long earningsTimestamp) {
        this.earningsTimestamp = earningsTimestamp;
    }

    @JsonProperty("earningsTimestampStart")
    public Long getEarningsTimestampStart() {
        return earningsTimestampStart;
    }

    @JsonProperty("earningsTimestampStart")
    public void setEarningsTimestampStart(Long earningsTimestampStart) {
        this.earningsTimestampStart = earningsTimestampStart;
    }

    @JsonProperty("earningsTimestampEnd")
    public Long getEarningsTimestampEnd() {
        return earningsTimestampEnd;
    }

    @JsonProperty("earningsTimestampEnd")
    public void setEarningsTimestampEnd(Long earningsTimestampEnd) {
        this.earningsTimestampEnd = earningsTimestampEnd;
    }

    @JsonProperty("trailingAnnualDividendRate")
    public Double getTrailingAnnualDividendRate() {
        return trailingAnnualDividendRate;
    }

    @JsonProperty("trailingAnnualDividendRate")
    public void setTrailingAnnualDividendRate(Double trailingAnnualDividendRate) {
        this.trailingAnnualDividendRate = trailingAnnualDividendRate;
    }

    @JsonProperty("trailingPE")
    public Double getTrailingPE() {
        return trailingPE;
    }

    @JsonProperty("trailingPE")
    public void setTrailingPE(Double trailingPE) {
        this.trailingPE = trailingPE;
    }

    @JsonProperty("dividendRate")
    public Double getDividendRate() {
        return dividendRate;
    }

    @JsonProperty("dividendRate")
    public void setDividendRate(Double dividendRate) {
        this.dividendRate = dividendRate;
    }

    @JsonProperty("trailingAnnualDividendYield")
    public Double getTrailingAnnualDividendYield() {
        return trailingAnnualDividendYield;
    }

    @JsonProperty("trailingAnnualDividendYield")
    public void setTrailingAnnualDividendYield(Double trailingAnnualDividendYield) {
        this.trailingAnnualDividendYield = trailingAnnualDividendYield;
    }

    @JsonProperty("dividendYield")
    public Double getDividendYield() {
        return dividendYield;
    }

    @JsonProperty("dividendYield")
    public void setDividendYield(Double dividendYield) {
        this.dividendYield = dividendYield;
    }

    @JsonProperty("epsTrailingTwelveMonths")
    public Double getEpsTrailingTwelveMonths() {
        return epsTrailingTwelveMonths;
    }

    @JsonProperty("epsTrailingTwelveMonths")
    public void setEpsTrailingTwelveMonths(Double epsTrailingTwelveMonths) {
        this.epsTrailingTwelveMonths = epsTrailingTwelveMonths;
    }

    @JsonProperty("epsForward")
    public Double getEpsForward() {
        return epsForward;
    }

    @JsonProperty("epsForward")
    public void setEpsForward(Double epsForward) {
        this.epsForward = epsForward;
    }

    @JsonProperty("epsCurrentYear")
    public Double getEpsCurrentYear() {
        return epsCurrentYear;
    }

    @JsonProperty("epsCurrentYear")
    public void setEpsCurrentYear(Double epsCurrentYear) {
        this.epsCurrentYear = epsCurrentYear;
    }

    @JsonProperty("priceEpsCurrentYear")
    public Double getPriceEpsCurrentYear() {
        return priceEpsCurrentYear;
    }

    @JsonProperty("priceEpsCurrentYear")
    public void setPriceEpsCurrentYear(Double priceEpsCurrentYear) {
        this.priceEpsCurrentYear = priceEpsCurrentYear;
    }

    @JsonProperty("sharesOutstanding")
    public Long getSharesOutstanding() {
        return sharesOutstanding;
    }

    @JsonProperty("sharesOutstanding")
    public void setSharesOutstanding(Long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    @JsonProperty("bookValue")
    public Double getBookValue() {
        return bookValue;
    }

    @JsonProperty("bookValue")
    public void setBookValue(Double bookValue) {
        this.bookValue = bookValue;
    }

    @JsonProperty("fiftyDayAverage")
    public Double getFiftyDayAverage() {
        return fiftyDayAverage;
    }

    @JsonProperty("fiftyDayAverage")
    public void setFiftyDayAverage(Double fiftyDayAverage) {
        this.fiftyDayAverage = fiftyDayAverage;
    }

    @JsonProperty("fiftyDayAverageChange")
    public Double getFiftyDayAverageChange() {
        return fiftyDayAverageChange;
    }

    @JsonProperty("fiftyDayAverageChange")
    public void setFiftyDayAverageChange(Double fiftyDayAverageChange) {
        this.fiftyDayAverageChange = fiftyDayAverageChange;
    }

    @JsonProperty("fiftyDayAverageChangePercent")
    public Double getFiftyDayAverageChangePercent() {
        return fiftyDayAverageChangePercent;
    }

    @JsonProperty("fiftyDayAverageChangePercent")
    public void setFiftyDayAverageChangePercent(Double fiftyDayAverageChangePercent) {
        this.fiftyDayAverageChangePercent = fiftyDayAverageChangePercent;
    }

    @JsonProperty("twoHundredDayAverage")
    public Double getTwoHundredDayAverage() {
        return twoHundredDayAverage;
    }

    @JsonProperty("twoHundredDayAverage")
    public void setTwoHundredDayAverage(Double twoHundredDayAverage) {
        this.twoHundredDayAverage = twoHundredDayAverage;
    }

    @JsonProperty("twoHundredDayAverageChange")
    public Double getTwoHundredDayAverageChange() {
        return twoHundredDayAverageChange;
    }

    @JsonProperty("twoHundredDayAverageChange")
    public void setTwoHundredDayAverageChange(Double twoHundredDayAverageChange) {
        this.twoHundredDayAverageChange = twoHundredDayAverageChange;
    }

    @JsonProperty("twoHundredDayAverageChangePercent")
    public Double getTwoHundredDayAverageChangePercent() {
        return twoHundredDayAverageChangePercent;
    }

    @JsonProperty("twoHundredDayAverageChangePercent")
    public void setTwoHundredDayAverageChangePercent(Double twoHundredDayAverageChangePercent) {
        this.twoHundredDayAverageChangePercent = twoHundredDayAverageChangePercent;
    }

    @JsonProperty("marketCap")
    public Long getMarketCap() {
        return marketCap;
    }

    @JsonProperty("marketCap")
    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    @JsonProperty("forwardPE")
    public Double getForwardPE() {
        return forwardPE;
    }

    @JsonProperty("forwardPE")
    public void setForwardPE(Double forwardPE) {
        this.forwardPE = forwardPE;
    }

    @JsonProperty("priceToBook")
    public Double getPriceToBook() {
        return priceToBook;
    }

    @JsonProperty("priceToBook")
    public void setPriceToBook(Double priceToBook) {
        this.priceToBook = priceToBook;
    }

    @JsonProperty("sourceInterval")
    public Long getSourceInterval() {
        return sourceInterval;
    }

    @JsonProperty("sourceInterval")
    public void setSourceInterval(Long sourceInterval) {
        this.sourceInterval = sourceInterval;
    }

    @JsonProperty("exchangeDataDelayedBy")
    public Long getExchangeDataDelayedBy() {
        return exchangeDataDelayedBy;
    }

    @JsonProperty("exchangeDataDelayedBy")
    public void setExchangeDataDelayedBy(Long exchangeDataDelayedBy) {
        this.exchangeDataDelayedBy = exchangeDataDelayedBy;
    }

    @JsonProperty("averageAnalystRating")
    public String getAverageAnalystRating() {
        return averageAnalystRating;
    }

    @JsonProperty("averageAnalystRating")
    public void setAverageAnalystRating(String averageAnalystRating) {
        this.averageAnalystRating = averageAnalystRating;
    }

    @JsonProperty("tradeable")
    public Boolean getTradeable() {
        return tradeable;
    }

    @JsonProperty("tradeable")
    public void setTradeable(Boolean tradeable) {
        this.tradeable = tradeable;
    }

    @JsonProperty("cryptoTradeable")
    public Boolean getCryptoTradeable() {
        return cryptoTradeable;
    }

    @JsonProperty("cryptoTradeable")
    public void setCryptoTradeable(Boolean cryptoTradeable) {
        this.cryptoTradeable = cryptoTradeable;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("ipoExpectedDate")
    public String getIpoExpectedDate() {
        return ipoExpectedDate;
    }

    @JsonProperty("ipoExpectedDate")
    public void setIpoExpectedDate(String ipoExpectedDate) {
        this.ipoExpectedDate = ipoExpectedDate;
    }

    @JsonProperty("prevName")
    public String getPrevName() {
        return prevName;
    }

    @JsonProperty("prevName")
    public void setPrevName(String prevName) {
        this.prevName = prevName;
    }

    @JsonProperty("nameChangeDate")
    public String getNameChangeDate() {
        return nameChangeDate;
    }

    @JsonProperty("nameChangeDate")
    public void setNameChangeDate(String nameChangeDate) {
        this.nameChangeDate = nameChangeDate;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Result.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("language");
        sb.append('=');
        sb.append(((this.language == null)?"<null>":this.language));
        sb.append(',');
        sb.append("region");
        sb.append('=');
        sb.append(((this.region == null)?"<null>":this.region));
        sb.append(',');
        sb.append("quoteType");
        sb.append('=');
        sb.append(((this.quoteType == null)?"<null>":this.quoteType));
        sb.append(',');
        sb.append("typeDisp");
        sb.append('=');
        sb.append(((this.typeDisp == null)?"<null>":this.typeDisp));
        sb.append(',');
        sb.append("quoteSourceName");
        sb.append('=');
        sb.append(((this.quoteSourceName == null)?"<null>":this.quoteSourceName));
        sb.append(',');
        sb.append("triggerable");
        sb.append('=');
        sb.append(((this.triggerable == null)?"<null>":this.triggerable));
        sb.append(',');
        sb.append("customPriceAlertConfidence");
        sb.append('=');
        sb.append(((this.customPriceAlertConfidence == null)?"<null>":this.customPriceAlertConfidence));
        sb.append(',');
        sb.append("currency");
        sb.append('=');
        sb.append(((this.currency == null)?"<null>":this.currency));
        sb.append(',');
        sb.append("marketState");
        sb.append('=');
        sb.append(((this.marketState == null)?"<null>":this.marketState));
        sb.append(',');
        sb.append("exchange");
        sb.append('=');
        sb.append(((this.exchange == null)?"<null>":this.exchange));
        sb.append(',');
        sb.append("shortName");
        sb.append('=');
        sb.append(((this.shortName == null)?"<null>":this.shortName));
        sb.append(',');
        sb.append("longName");
        sb.append('=');
        sb.append(((this.longName == null)?"<null>":this.longName));
        sb.append(',');
        sb.append("messageBoardId");
        sb.append('=');
        sb.append(((this.messageBoardId == null)?"<null>":this.messageBoardId));
        sb.append(',');
        sb.append("exchangeTimezoneName");
        sb.append('=');
        sb.append(((this.exchangeTimezoneName == null)?"<null>":this.exchangeTimezoneName));
        sb.append(',');
        sb.append("exchangeTimezoneShortName");
        sb.append('=');
        sb.append(((this.exchangeTimezoneShortName == null)?"<null>":this.exchangeTimezoneShortName));
        sb.append(',');
        sb.append("gmtOffSetMilliseconds");
        sb.append('=');
        sb.append(((this.gmtOffSetMilliseconds == null)?"<null>":this.gmtOffSetMilliseconds));
        sb.append(',');
        sb.append("market");
        sb.append('=');
        sb.append(((this.market == null)?"<null>":this.market));
        sb.append(',');
        sb.append("esgPopulated");
        sb.append('=');
        sb.append(((this.esgPopulated == null)?"<null>":this.esgPopulated));
        sb.append(',');
        sb.append("regularMarketChangePercent");
        sb.append('=');
        sb.append(((this.regularMarketChangePercent == null)?"<null>":this.regularMarketChangePercent));
        sb.append(',');
        sb.append("regularMarketPrice");
        sb.append('=');
        sb.append(((this.regularMarketPrice == null)?"<null>":this.regularMarketPrice));
        sb.append(',');
        sb.append("firstTradeDateMilliseconds");
        sb.append('=');
        sb.append(((this.firstTradeDateMilliseconds == null)?"<null>":this.firstTradeDateMilliseconds));
        sb.append(',');
        sb.append("priceHint");
        sb.append('=');
        sb.append(((this.priceHint == null)?"<null>":this.priceHint));
        sb.append(',');
        sb.append("regularMarketChange");
        sb.append('=');
        sb.append(((this.regularMarketChange == null)?"<null>":this.regularMarketChange));
        sb.append(',');
        sb.append("regularMarketTime");
        sb.append('=');
        sb.append(((this.regularMarketTime == null)?"<null>":this.regularMarketTime));
        sb.append(',');
        sb.append("regularMarketDayHigh");
        sb.append('=');
        sb.append(((this.regularMarketDayHigh == null)?"<null>":this.regularMarketDayHigh));
        sb.append(',');
        sb.append("regularMarketDayRange");
        sb.append('=');
        sb.append(((this.regularMarketDayRange == null)?"<null>":this.regularMarketDayRange));
        sb.append(',');
        sb.append("regularMarketDayLow");
        sb.append('=');
        sb.append(((this.regularMarketDayLow == null)?"<null>":this.regularMarketDayLow));
        sb.append(',');
        sb.append("regularMarketVolume");
        sb.append('=');
        sb.append(((this.regularMarketVolume == null)?"<null>":this.regularMarketVolume));
        sb.append(',');
        sb.append("regularMarketPreviousClose");
        sb.append('=');
        sb.append(((this.regularMarketPreviousClose == null)?"<null>":this.regularMarketPreviousClose));
        sb.append(',');
        sb.append("bid");
        sb.append('=');
        sb.append(((this.bid == null)?"<null>":this.bid));
        sb.append(',');
        sb.append("ask");
        sb.append('=');
        sb.append(((this.ask == null)?"<null>":this.ask));
        sb.append(',');
        sb.append("bidSize");
        sb.append('=');
        sb.append(((this.bidSize == null)?"<null>":this.bidSize));
        sb.append(',');
        sb.append("askSize");
        sb.append('=');
        sb.append(((this.askSize == null)?"<null>":this.askSize));
        sb.append(',');
        sb.append("fullExchangeName");
        sb.append('=');
        sb.append(((this.fullExchangeName == null)?"<null>":this.fullExchangeName));
        sb.append(',');
        sb.append("financialCurrency");
        sb.append('=');
        sb.append(((this.financialCurrency == null)?"<null>":this.financialCurrency));
        sb.append(',');
        sb.append("regularMarketOpen");
        sb.append('=');
        sb.append(((this.regularMarketOpen == null)?"<null>":this.regularMarketOpen));
        sb.append(',');
        sb.append("averageDailyVolume3Month");
        sb.append('=');
        sb.append(((this.averageDailyVolume3Month == null)?"<null>":this.averageDailyVolume3Month));
        sb.append(',');
        sb.append("averageDailyVolume10Day");
        sb.append('=');
        sb.append(((this.averageDailyVolume10Day == null)?"<null>":this.averageDailyVolume10Day));
        sb.append(',');
        sb.append("fiftyTwoWeekLowChange");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekLowChange == null)?"<null>":this.fiftyTwoWeekLowChange));
        sb.append(',');
        sb.append("fiftyTwoWeekLowChangePercent");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekLowChangePercent == null)?"<null>":this.fiftyTwoWeekLowChangePercent));
        sb.append(',');
        sb.append("fiftyTwoWeekRange");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekRange == null)?"<null>":this.fiftyTwoWeekRange));
        sb.append(',');
        sb.append("fiftyTwoWeekHighChange");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekHighChange == null)?"<null>":this.fiftyTwoWeekHighChange));
        sb.append(',');
        sb.append("fiftyTwoWeekHighChangePercent");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekHighChangePercent == null)?"<null>":this.fiftyTwoWeekHighChangePercent));
        sb.append(',');
        sb.append("fiftyTwoWeekLow");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekLow == null)?"<null>":this.fiftyTwoWeekLow));
        sb.append(',');
        sb.append("fiftyTwoWeekHigh");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekHigh == null)?"<null>":this.fiftyTwoWeekHigh));
        sb.append(',');
        sb.append("fiftyTwoWeekChangePercent");
        sb.append('=');
        sb.append(((this.fiftyTwoWeekChangePercent == null)?"<null>":this.fiftyTwoWeekChangePercent));
        sb.append(',');
        sb.append("dividendDate");
        sb.append('=');
        sb.append(((this.dividendDate == null)?"<null>":this.dividendDate));
        sb.append(',');
        sb.append("earningsTimestamp");
        sb.append('=');
        sb.append(((this.earningsTimestamp == null)?"<null>":this.earningsTimestamp));
        sb.append(',');
        sb.append("earningsTimestampStart");
        sb.append('=');
        sb.append(((this.earningsTimestampStart == null)?"<null>":this.earningsTimestampStart));
        sb.append(',');
        sb.append("earningsTimestampEnd");
        sb.append('=');
        sb.append(((this.earningsTimestampEnd == null)?"<null>":this.earningsTimestampEnd));
        sb.append(',');
        sb.append("trailingAnnualDividendRate");
        sb.append('=');
        sb.append(((this.trailingAnnualDividendRate == null)?"<null>":this.trailingAnnualDividendRate));
        sb.append(',');
        sb.append("trailingPE");
        sb.append('=');
        sb.append(((this.trailingPE == null)?"<null>":this.trailingPE));
        sb.append(',');
        sb.append("dividendRate");
        sb.append('=');
        sb.append(((this.dividendRate == null)?"<null>":this.dividendRate));
        sb.append(',');
        sb.append("trailingAnnualDividendYield");
        sb.append('=');
        sb.append(((this.trailingAnnualDividendYield == null)?"<null>":this.trailingAnnualDividendYield));
        sb.append(',');
        sb.append("dividendYield");
        sb.append('=');
        sb.append(((this.dividendYield == null)?"<null>":this.dividendYield));
        sb.append(',');
        sb.append("epsTrailingTwelveMonths");
        sb.append('=');
        sb.append(((this.epsTrailingTwelveMonths == null)?"<null>":this.epsTrailingTwelveMonths));
        sb.append(',');
        sb.append("epsForward");
        sb.append('=');
        sb.append(((this.epsForward == null)?"<null>":this.epsForward));
        sb.append(',');
        sb.append("epsCurrentYear");
        sb.append('=');
        sb.append(((this.epsCurrentYear == null)?"<null>":this.epsCurrentYear));
        sb.append(',');
        sb.append("priceEpsCurrentYear");
        sb.append('=');
        sb.append(((this.priceEpsCurrentYear == null)?"<null>":this.priceEpsCurrentYear));
        sb.append(',');
        sb.append("sharesOutstanding");
        sb.append('=');
        sb.append(((this.sharesOutstanding == null)?"<null>":this.sharesOutstanding));
        sb.append(',');
        sb.append("bookValue");
        sb.append('=');
        sb.append(((this.bookValue == null)?"<null>":this.bookValue));
        sb.append(',');
        sb.append("fiftyDayAverage");
        sb.append('=');
        sb.append(((this.fiftyDayAverage == null)?"<null>":this.fiftyDayAverage));
        sb.append(',');
        sb.append("fiftyDayAverageChange");
        sb.append('=');
        sb.append(((this.fiftyDayAverageChange == null)?"<null>":this.fiftyDayAverageChange));
        sb.append(',');
        sb.append("fiftyDayAverageChangePercent");
        sb.append('=');
        sb.append(((this.fiftyDayAverageChangePercent == null)?"<null>":this.fiftyDayAverageChangePercent));
        sb.append(',');
        sb.append("twoHundredDayAverage");
        sb.append('=');
        sb.append(((this.twoHundredDayAverage == null)?"<null>":this.twoHundredDayAverage));
        sb.append(',');
        sb.append("twoHundredDayAverageChange");
        sb.append('=');
        sb.append(((this.twoHundredDayAverageChange == null)?"<null>":this.twoHundredDayAverageChange));
        sb.append(',');
        sb.append("twoHundredDayAverageChangePercent");
        sb.append('=');
        sb.append(((this.twoHundredDayAverageChangePercent == null)?"<null>":this.twoHundredDayAverageChangePercent));
        sb.append(',');
        sb.append("marketCap");
        sb.append('=');
        sb.append(((this.marketCap == null)?"<null>":this.marketCap));
        sb.append(',');
        sb.append("forwardPE");
        sb.append('=');
        sb.append(((this.forwardPE == null)?"<null>":this.forwardPE));
        sb.append(',');
        sb.append("priceToBook");
        sb.append('=');
        sb.append(((this.priceToBook == null)?"<null>":this.priceToBook));
        sb.append(',');
        sb.append("sourceInterval");
        sb.append('=');
        sb.append(((this.sourceInterval == null)?"<null>":this.sourceInterval));
        sb.append(',');
        sb.append("exchangeDataDelayedBy");
        sb.append('=');
        sb.append(((this.exchangeDataDelayedBy == null)?"<null>":this.exchangeDataDelayedBy));
        sb.append(',');
        sb.append("averageAnalystRating");
        sb.append('=');
        sb.append(((this.averageAnalystRating == null)?"<null>":this.averageAnalystRating));
        sb.append(',');
        sb.append("tradeable");
        sb.append('=');
        sb.append(((this.tradeable == null)?"<null>":this.tradeable));
        sb.append(',');
        sb.append("cryptoTradeable");
        sb.append('=');
        sb.append(((this.cryptoTradeable == null)?"<null>":this.cryptoTradeable));
        sb.append(',');
        sb.append("displayName");
        sb.append('=');
        sb.append(((this.displayName == null)?"<null>":this.displayName));
        sb.append(',');
        sb.append("symbol");
        sb.append('=');
        sb.append(((this.symbol == null)?"<null>":this.symbol));
        sb.append(',');
        sb.append("ipoExpectedDate");
        sb.append('=');
        sb.append(((this.ipoExpectedDate == null)?"<null>":this.ipoExpectedDate));
        sb.append(',');
        sb.append("prevName");
        sb.append('=');
        sb.append(((this.prevName == null)?"<null>":this.prevName));
        sb.append(',');
        sb.append("nameChangeDate");
        sb.append('=');
        sb.append(((this.nameChangeDate == null)?"<null>":this.nameChangeDate));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.symbol == null)? 0 :this.symbol.hashCode()));
        result = ((result* 31)+((this.dividendDate == null)? 0 :this.dividendDate.hashCode()));
        result = ((result* 31)+((this.twoHundredDayAverageChangePercent == null)? 0 :this.twoHundredDayAverageChangePercent.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekLowChangePercent == null)? 0 :this.fiftyTwoWeekLowChangePercent.hashCode()));
        result = ((result* 31)+((this.averageAnalystRating == null)? 0 :this.averageAnalystRating.hashCode()));
        result = ((result* 31)+((this.language == null)? 0 :this.language.hashCode()));
        result = ((result* 31)+((this.dividendYield == null)? 0 :this.dividendYield.hashCode()));
        result = ((result* 31)+((this.regularMarketDayRange == null)? 0 :this.regularMarketDayRange.hashCode()));
        result = ((result* 31)+((this.earningsTimestampEnd == null)? 0 :this.earningsTimestampEnd.hashCode()));
        result = ((result* 31)+((this.epsForward == null)? 0 :this.epsForward.hashCode()));
        result = ((result* 31)+((this.regularMarketDayHigh == null)? 0 :this.regularMarketDayHigh.hashCode()));
        result = ((result* 31)+((this.twoHundredDayAverageChange == null)? 0 :this.twoHundredDayAverageChange.hashCode()));
        result = ((result* 31)+((this.askSize == null)? 0 :this.askSize.hashCode()));
        result = ((result* 31)+((this.twoHundredDayAverage == null)? 0 :this.twoHundredDayAverage.hashCode()));
        result = ((result* 31)+((this.bookValue == null)? 0 :this.bookValue.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekHighChange == null)? 0 :this.fiftyTwoWeekHighChange.hashCode()));
        result = ((result* 31)+((this.marketCap == null)? 0 :this.marketCap.hashCode()));
        result = ((result* 31)+((this.ipoExpectedDate == null)? 0 :this.ipoExpectedDate.hashCode()));
        result = ((result* 31)+((this.esgPopulated == null)? 0 :this.esgPopulated.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekRange == null)? 0 :this.fiftyTwoWeekRange.hashCode()));
        result = ((result* 31)+((this.fiftyDayAverageChange == null)? 0 :this.fiftyDayAverageChange.hashCode()));
        result = ((result* 31)+((this.firstTradeDateMilliseconds == null)? 0 :this.firstTradeDateMilliseconds.hashCode()));
        result = ((result* 31)+((this.averageDailyVolume3Month == null)? 0 :this.averageDailyVolume3Month.hashCode()));
        result = ((result* 31)+((this.exchangeDataDelayedBy == null)? 0 :this.exchangeDataDelayedBy.hashCode()));
        result = ((result* 31)+((this.dividendRate == null)? 0 :this.dividendRate.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekChangePercent == null)? 0 :this.fiftyTwoWeekChangePercent.hashCode()));
        result = ((result* 31)+((this.trailingAnnualDividendRate == null)? 0 :this.trailingAnnualDividendRate.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekLow == null)? 0 :this.fiftyTwoWeekLow.hashCode()));
        result = ((result* 31)+((this.market == null)? 0 :this.market.hashCode()));
        result = ((result* 31)+((this.regularMarketVolume == null)? 0 :this.regularMarketVolume.hashCode()));
        result = ((result* 31)+((this.quoteSourceName == null)? 0 :this.quoteSourceName.hashCode()));
        result = ((result* 31)+((this.messageBoardId == null)? 0 :this.messageBoardId.hashCode()));
        result = ((result* 31)+((this.priceHint == null)? 0 :this.priceHint.hashCode()));
        result = ((result* 31)+((this.exchange == null)? 0 :this.exchange.hashCode()));
        result = ((result* 31)+((this.regularMarketDayLow == null)? 0 :this.regularMarketDayLow.hashCode()));
        result = ((result* 31)+((this.sourceInterval == null)? 0 :this.sourceInterval.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.region == null)? 0 :this.region.hashCode()));
        result = ((result* 31)+((this.shortName == null)? 0 :this.shortName.hashCode()));
        result = ((result* 31)+((this.fiftyDayAverageChangePercent == null)? 0 :this.fiftyDayAverageChangePercent.hashCode()));
        result = ((result* 31)+((this.fullExchangeName == null)? 0 :this.fullExchangeName.hashCode()));
        result = ((result* 31)+((this.earningsTimestampStart == null)? 0 :this.earningsTimestampStart.hashCode()));
        result = ((result* 31)+((this.financialCurrency == null)? 0 :this.financialCurrency.hashCode()));
        result = ((result* 31)+((this.displayName == null)? 0 :this.displayName.hashCode()));
        result = ((result* 31)+((this.gmtOffSetMilliseconds == null)? 0 :this.gmtOffSetMilliseconds.hashCode()));
        result = ((result* 31)+((this.regularMarketOpen == null)? 0 :this.regularMarketOpen.hashCode()));
        result = ((result* 31)+((this.regularMarketTime == null)? 0 :this.regularMarketTime.hashCode()));
        result = ((result* 31)+((this.regularMarketChangePercent == null)? 0 :this.regularMarketChangePercent.hashCode()));
        result = ((result* 31)+((this.quoteType == null)? 0 :this.quoteType.hashCode()));
        result = ((result* 31)+((this.trailingAnnualDividendYield == null)? 0 :this.trailingAnnualDividendYield.hashCode()));
        result = ((result* 31)+((this.averageDailyVolume10Day == null)? 0 :this.averageDailyVolume10Day.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekLowChange == null)? 0 :this.fiftyTwoWeekLowChange.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekHighChangePercent == null)? 0 :this.fiftyTwoWeekHighChangePercent.hashCode()));
        result = ((result* 31)+((this.typeDisp == null)? 0 :this.typeDisp.hashCode()));
        result = ((result* 31)+((this.trailingPE == null)? 0 :this.trailingPE.hashCode()));
        result = ((result* 31)+((this.tradeable == null)? 0 :this.tradeable.hashCode()));
        result = ((result* 31)+((this.prevName == null)? 0 :this.prevName.hashCode()));
        result = ((result* 31)+((this.currency == null)? 0 :this.currency.hashCode()));
        result = ((result* 31)+((this.sharesOutstanding == null)? 0 :this.sharesOutstanding.hashCode()));
        result = ((result* 31)+((this.regularMarketPreviousClose == null)? 0 :this.regularMarketPreviousClose.hashCode()));
        result = ((result* 31)+((this.fiftyTwoWeekHigh == null)? 0 :this.fiftyTwoWeekHigh.hashCode()));
        result = ((result* 31)+((this.exchangeTimezoneName == null)? 0 :this.exchangeTimezoneName.hashCode()));
        result = ((result* 31)+((this.nameChangeDate == null)? 0 :this.nameChangeDate.hashCode()));
        result = ((result* 31)+((this.regularMarketChange == null)? 0 :this.regularMarketChange.hashCode()));
        result = ((result* 31)+((this.bidSize == null)? 0 :this.bidSize.hashCode()));
        result = ((result* 31)+((this.priceEpsCurrentYear == null)? 0 :this.priceEpsCurrentYear.hashCode()));
        result = ((result* 31)+((this.cryptoTradeable == null)? 0 :this.cryptoTradeable.hashCode()));
        result = ((result* 31)+((this.fiftyDayAverage == null)? 0 :this.fiftyDayAverage.hashCode()));
        result = ((result* 31)+((this.exchangeTimezoneShortName == null)? 0 :this.exchangeTimezoneShortName.hashCode()));
        result = ((result* 31)+((this.epsCurrentYear == null)? 0 :this.epsCurrentYear.hashCode()));
        result = ((result* 31)+((this.customPriceAlertConfidence == null)? 0 :this.customPriceAlertConfidence.hashCode()));
        result = ((result* 31)+((this.marketState == null)? 0 :this.marketState.hashCode()));
        result = ((result* 31)+((this.regularMarketPrice == null)? 0 :this.regularMarketPrice.hashCode()));
        result = ((result* 31)+((this.forwardPE == null)? 0 :this.forwardPE.hashCode()));
        result = ((result* 31)+((this.earningsTimestamp == null)? 0 :this.earningsTimestamp.hashCode()));
        result = ((result* 31)+((this.ask == null)? 0 :this.ask.hashCode()));
        result = ((result* 31)+((this.epsTrailingTwelveMonths == null)? 0 :this.epsTrailingTwelveMonths.hashCode()));
        result = ((result* 31)+((this.bid == null)? 0 :this.bid.hashCode()));
        result = ((result* 31)+((this.triggerable == null)? 0 :this.triggerable.hashCode()));
        result = ((result* 31)+((this.priceToBook == null)? 0 :this.priceToBook.hashCode()));
        result = ((result* 31)+((this.longName == null)? 0 :this.longName.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Result) == false) {
            return false;
        }
        Result rhs = ((Result) other);
        return ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((this.symbol == rhs.symbol)||((this.symbol!= null)&&this.symbol.equals(rhs.symbol)))&&((this.dividendDate == rhs.dividendDate)||((this.dividendDate!= null)&&this.dividendDate.equals(rhs.dividendDate))))&&((this.twoHundredDayAverageChangePercent == rhs.twoHundredDayAverageChangePercent)||((this.twoHundredDayAverageChangePercent!= null)&&this.twoHundredDayAverageChangePercent.equals(rhs.twoHundredDayAverageChangePercent))))&&((this.fiftyTwoWeekLowChangePercent == rhs.fiftyTwoWeekLowChangePercent)||((this.fiftyTwoWeekLowChangePercent!= null)&&this.fiftyTwoWeekLowChangePercent.equals(rhs.fiftyTwoWeekLowChangePercent))))&&((this.averageAnalystRating == rhs.averageAnalystRating)||((this.averageAnalystRating!= null)&&this.averageAnalystRating.equals(rhs.averageAnalystRating))))&&((this.language == rhs.language)||((this.language!= null)&&this.language.equals(rhs.language))))&&((this.dividendYield == rhs.dividendYield)||((this.dividendYield!= null)&&this.dividendYield.equals(rhs.dividendYield))))&&((this.regularMarketDayRange == rhs.regularMarketDayRange)||((this.regularMarketDayRange!= null)&&this.regularMarketDayRange.equals(rhs.regularMarketDayRange))))&&((this.earningsTimestampEnd == rhs.earningsTimestampEnd)||((this.earningsTimestampEnd!= null)&&this.earningsTimestampEnd.equals(rhs.earningsTimestampEnd))))&&((this.epsForward == rhs.epsForward)||((this.epsForward!= null)&&this.epsForward.equals(rhs.epsForward))))&&((this.regularMarketDayHigh == rhs.regularMarketDayHigh)||((this.regularMarketDayHigh!= null)&&this.regularMarketDayHigh.equals(rhs.regularMarketDayHigh))))&&((this.twoHundredDayAverageChange == rhs.twoHundredDayAverageChange)||((this.twoHundredDayAverageChange!= null)&&this.twoHundredDayAverageChange.equals(rhs.twoHundredDayAverageChange))))&&((this.askSize == rhs.askSize)||((this.askSize!= null)&&this.askSize.equals(rhs.askSize))))&&((this.twoHundredDayAverage == rhs.twoHundredDayAverage)||((this.twoHundredDayAverage!= null)&&this.twoHundredDayAverage.equals(rhs.twoHundredDayAverage))))&&((this.bookValue == rhs.bookValue)||((this.bookValue!= null)&&this.bookValue.equals(rhs.bookValue))))&&((this.fiftyTwoWeekHighChange == rhs.fiftyTwoWeekHighChange)||((this.fiftyTwoWeekHighChange!= null)&&this.fiftyTwoWeekHighChange.equals(rhs.fiftyTwoWeekHighChange))))&&((this.marketCap == rhs.marketCap)||((this.marketCap!= null)&&this.marketCap.equals(rhs.marketCap))))&&((this.ipoExpectedDate == rhs.ipoExpectedDate)||((this.ipoExpectedDate!= null)&&this.ipoExpectedDate.equals(rhs.ipoExpectedDate))))&&((this.esgPopulated == rhs.esgPopulated)||((this.esgPopulated!= null)&&this.esgPopulated.equals(rhs.esgPopulated))))&&((this.fiftyTwoWeekRange == rhs.fiftyTwoWeekRange)||((this.fiftyTwoWeekRange!= null)&&this.fiftyTwoWeekRange.equals(rhs.fiftyTwoWeekRange))))&&((this.fiftyDayAverageChange == rhs.fiftyDayAverageChange)||((this.fiftyDayAverageChange!= null)&&this.fiftyDayAverageChange.equals(rhs.fiftyDayAverageChange))))&&((this.firstTradeDateMilliseconds == rhs.firstTradeDateMilliseconds)||((this.firstTradeDateMilliseconds!= null)&&this.firstTradeDateMilliseconds.equals(rhs.firstTradeDateMilliseconds))))&&((this.averageDailyVolume3Month == rhs.averageDailyVolume3Month)||((this.averageDailyVolume3Month!= null)&&this.averageDailyVolume3Month.equals(rhs.averageDailyVolume3Month))))&&((this.exchangeDataDelayedBy == rhs.exchangeDataDelayedBy)||((this.exchangeDataDelayedBy!= null)&&this.exchangeDataDelayedBy.equals(rhs.exchangeDataDelayedBy))))&&((this.dividendRate == rhs.dividendRate)||((this.dividendRate!= null)&&this.dividendRate.equals(rhs.dividendRate))))&&((this.fiftyTwoWeekChangePercent == rhs.fiftyTwoWeekChangePercent)||((this.fiftyTwoWeekChangePercent!= null)&&this.fiftyTwoWeekChangePercent.equals(rhs.fiftyTwoWeekChangePercent))))&&((this.trailingAnnualDividendRate == rhs.trailingAnnualDividendRate)||((this.trailingAnnualDividendRate!= null)&&this.trailingAnnualDividendRate.equals(rhs.trailingAnnualDividendRate))))&&((this.fiftyTwoWeekLow == rhs.fiftyTwoWeekLow)||((this.fiftyTwoWeekLow!= null)&&this.fiftyTwoWeekLow.equals(rhs.fiftyTwoWeekLow))))&&((this.market == rhs.market)||((this.market!= null)&&this.market.equals(rhs.market))))&&((this.regularMarketVolume == rhs.regularMarketVolume)||((this.regularMarketVolume!= null)&&this.regularMarketVolume.equals(rhs.regularMarketVolume))))&&((this.quoteSourceName == rhs.quoteSourceName)||((this.quoteSourceName!= null)&&this.quoteSourceName.equals(rhs.quoteSourceName))))&&((this.messageBoardId == rhs.messageBoardId)||((this.messageBoardId!= null)&&this.messageBoardId.equals(rhs.messageBoardId))))&&((this.priceHint == rhs.priceHint)||((this.priceHint!= null)&&this.priceHint.equals(rhs.priceHint))))&&((this.exchange == rhs.exchange)||((this.exchange!= null)&&this.exchange.equals(rhs.exchange))))&&((this.regularMarketDayLow == rhs.regularMarketDayLow)||((this.regularMarketDayLow!= null)&&this.regularMarketDayLow.equals(rhs.regularMarketDayLow))))&&((this.sourceInterval == rhs.sourceInterval)||((this.sourceInterval!= null)&&this.sourceInterval.equals(rhs.sourceInterval))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.region == rhs.region)||((this.region!= null)&&this.region.equals(rhs.region))))&&((this.shortName == rhs.shortName)||((this.shortName!= null)&&this.shortName.equals(rhs.shortName))))&&((this.fiftyDayAverageChangePercent == rhs.fiftyDayAverageChangePercent)||((this.fiftyDayAverageChangePercent!= null)&&this.fiftyDayAverageChangePercent.equals(rhs.fiftyDayAverageChangePercent))))&&((this.fullExchangeName == rhs.fullExchangeName)||((this.fullExchangeName!= null)&&this.fullExchangeName.equals(rhs.fullExchangeName))))&&((this.earningsTimestampStart == rhs.earningsTimestampStart)||((this.earningsTimestampStart!= null)&&this.earningsTimestampStart.equals(rhs.earningsTimestampStart))))&&((this.financialCurrency == rhs.financialCurrency)||((this.financialCurrency!= null)&&this.financialCurrency.equals(rhs.financialCurrency))))&&((this.displayName == rhs.displayName)||((this.displayName!= null)&&this.displayName.equals(rhs.displayName))))&&((this.gmtOffSetMilliseconds == rhs.gmtOffSetMilliseconds)||((this.gmtOffSetMilliseconds!= null)&&this.gmtOffSetMilliseconds.equals(rhs.gmtOffSetMilliseconds))))&&((this.regularMarketOpen == rhs.regularMarketOpen)||((this.regularMarketOpen!= null)&&this.regularMarketOpen.equals(rhs.regularMarketOpen))))&&((this.regularMarketTime == rhs.regularMarketTime)||((this.regularMarketTime!= null)&&this.regularMarketTime.equals(rhs.regularMarketTime))))&&((this.regularMarketChangePercent == rhs.regularMarketChangePercent)||((this.regularMarketChangePercent!= null)&&this.regularMarketChangePercent.equals(rhs.regularMarketChangePercent))))&&((this.quoteType == rhs.quoteType)||((this.quoteType!= null)&&this.quoteType.equals(rhs.quoteType))))&&((this.trailingAnnualDividendYield == rhs.trailingAnnualDividendYield)||((this.trailingAnnualDividendYield!= null)&&this.trailingAnnualDividendYield.equals(rhs.trailingAnnualDividendYield))))&&((this.averageDailyVolume10Day == rhs.averageDailyVolume10Day)||((this.averageDailyVolume10Day!= null)&&this.averageDailyVolume10Day.equals(rhs.averageDailyVolume10Day))))&&((this.fiftyTwoWeekLowChange == rhs.fiftyTwoWeekLowChange)||((this.fiftyTwoWeekLowChange!= null)&&this.fiftyTwoWeekLowChange.equals(rhs.fiftyTwoWeekLowChange))))&&((this.fiftyTwoWeekHighChangePercent == rhs.fiftyTwoWeekHighChangePercent)||((this.fiftyTwoWeekHighChangePercent!= null)&&this.fiftyTwoWeekHighChangePercent.equals(rhs.fiftyTwoWeekHighChangePercent))))&&((this.typeDisp == rhs.typeDisp)||((this.typeDisp!= null)&&this.typeDisp.equals(rhs.typeDisp))))&&((this.trailingPE == rhs.trailingPE)||((this.trailingPE!= null)&&this.trailingPE.equals(rhs.trailingPE))))&&((this.tradeable == rhs.tradeable)||((this.tradeable!= null)&&this.tradeable.equals(rhs.tradeable))))&&((this.prevName == rhs.prevName)||((this.prevName!= null)&&this.prevName.equals(rhs.prevName))))&&((this.currency == rhs.currency)||((this.currency!= null)&&this.currency.equals(rhs.currency))))&&((this.sharesOutstanding == rhs.sharesOutstanding)||((this.sharesOutstanding!= null)&&this.sharesOutstanding.equals(rhs.sharesOutstanding))))&&((this.regularMarketPreviousClose == rhs.regularMarketPreviousClose)||((this.regularMarketPreviousClose!= null)&&this.regularMarketPreviousClose.equals(rhs.regularMarketPreviousClose))))&&((this.fiftyTwoWeekHigh == rhs.fiftyTwoWeekHigh)||((this.fiftyTwoWeekHigh!= null)&&this.fiftyTwoWeekHigh.equals(rhs.fiftyTwoWeekHigh))))&&((this.exchangeTimezoneName == rhs.exchangeTimezoneName)||((this.exchangeTimezoneName!= null)&&this.exchangeTimezoneName.equals(rhs.exchangeTimezoneName))))&&((this.nameChangeDate == rhs.nameChangeDate)||((this.nameChangeDate!= null)&&this.nameChangeDate.equals(rhs.nameChangeDate))))&&((this.regularMarketChange == rhs.regularMarketChange)||((this.regularMarketChange!= null)&&this.regularMarketChange.equals(rhs.regularMarketChange))))&&((this.bidSize == rhs.bidSize)||((this.bidSize!= null)&&this.bidSize.equals(rhs.bidSize))))&&((this.priceEpsCurrentYear == rhs.priceEpsCurrentYear)||((this.priceEpsCurrentYear!= null)&&this.priceEpsCurrentYear.equals(rhs.priceEpsCurrentYear))))&&((this.cryptoTradeable == rhs.cryptoTradeable)||((this.cryptoTradeable!= null)&&this.cryptoTradeable.equals(rhs.cryptoTradeable))))&&((this.fiftyDayAverage == rhs.fiftyDayAverage)||((this.fiftyDayAverage!= null)&&this.fiftyDayAverage.equals(rhs.fiftyDayAverage))))&&((this.exchangeTimezoneShortName == rhs.exchangeTimezoneShortName)||((this.exchangeTimezoneShortName!= null)&&this.exchangeTimezoneShortName.equals(rhs.exchangeTimezoneShortName))))&&((this.epsCurrentYear == rhs.epsCurrentYear)||((this.epsCurrentYear!= null)&&this.epsCurrentYear.equals(rhs.epsCurrentYear))))&&((this.customPriceAlertConfidence == rhs.customPriceAlertConfidence)||((this.customPriceAlertConfidence!= null)&&this.customPriceAlertConfidence.equals(rhs.customPriceAlertConfidence))))&&((this.marketState == rhs.marketState)||((this.marketState!= null)&&this.marketState.equals(rhs.marketState))))&&((this.regularMarketPrice == rhs.regularMarketPrice)||((this.regularMarketPrice!= null)&&this.regularMarketPrice.equals(rhs.regularMarketPrice))))&&((this.forwardPE == rhs.forwardPE)||((this.forwardPE!= null)&&this.forwardPE.equals(rhs.forwardPE))))&&((this.earningsTimestamp == rhs.earningsTimestamp)||((this.earningsTimestamp!= null)&&this.earningsTimestamp.equals(rhs.earningsTimestamp))))&&((this.ask == rhs.ask)||((this.ask!= null)&&this.ask.equals(rhs.ask))))&&((this.epsTrailingTwelveMonths == rhs.epsTrailingTwelveMonths)||((this.epsTrailingTwelveMonths!= null)&&this.epsTrailingTwelveMonths.equals(rhs.epsTrailingTwelveMonths))))&&((this.bid == rhs.bid)||((this.bid!= null)&&this.bid.equals(rhs.bid))))&&((this.triggerable == rhs.triggerable)||((this.triggerable!= null)&&this.triggerable.equals(rhs.triggerable))))&&((this.priceToBook == rhs.priceToBook)||((this.priceToBook!= null)&&this.priceToBook.equals(rhs.priceToBook))))&&((this.longName == rhs.longName)||((this.longName!= null)&&this.longName.equals(rhs.longName))));
    }

}
