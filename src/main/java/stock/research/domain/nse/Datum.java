
package stock.research.domain.nse;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "priority",
    "symbol",
    "identifier",
    "open",
    "dayHigh",
    "dayLow",
    "lastPrice",
    "previousClose",
    "change",
    "pChange",
    "ffmc",
    "yearHigh",
    "yearLow",
    "totalTradedVolume",
    "totalTradedValue",
    "lastUpdateTime",
    "nearWKH",
    "nearWKL",
    "perChange365d",
    "date365dAgo",
    "chart365dPath",
    "date30dAgo",
    "perChange30d",
    "chart30dPath",
    "chartTodayPath",
    "series",
    "meta"
})
@Generated("jsonschema2pojo")
public class Datum {

    @JsonProperty("priority")
    private Integer priority;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("open")
    private Integer open;
    @JsonProperty("dayHigh")
    private Integer dayHigh;
    @JsonProperty("dayLow")
    private Double dayLow;
    @JsonProperty("lastPrice")
    private Integer lastPrice;
    @JsonProperty("previousClose")
    private Double previousClose;
    @JsonProperty("change")
    private Double change;
    @JsonProperty("pChange")
    private Double pChange;
    @JsonProperty("ffmc")
    private Double ffmc;
    @JsonProperty("yearHigh")
    private Integer yearHigh;
    @JsonProperty("yearLow")
    private Double yearLow;
    @JsonProperty("totalTradedVolume")
    private Integer totalTradedVolume;
    @JsonProperty("totalTradedValue")
    private Double totalTradedValue;
    @JsonProperty("lastUpdateTime")
    private String lastUpdateTime;
    @JsonProperty("nearWKH")
    private Double nearWKH;
    @JsonProperty("nearWKL")
    private Double nearWKL;
    @JsonProperty("perChange365d")
    private Double perChange365d;
    @JsonProperty("date365dAgo")
    private String date365dAgo;
    @JsonProperty("chart365dPath")
    private String chart365dPath;
    @JsonProperty("date30dAgo")
    private String date30dAgo;
    @JsonProperty("perChange30d")
    private Double perChange30d;
    @JsonProperty("chart30dPath")
    private String chart30dPath;
    @JsonProperty("chartTodayPath")
    private String chartTodayPath;
    @JsonProperty("series")
    private String series;
    @JsonProperty("meta")
    private Meta meta;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("open")
    public Integer getOpen() {
        return open;
    }

    @JsonProperty("open")
    public void setOpen(Integer open) {
        this.open = open;
    }

    @JsonProperty("dayHigh")
    public Integer getDayHigh() {
        return dayHigh;
    }

    @JsonProperty("dayHigh")
    public void setDayHigh(Integer dayHigh) {
        this.dayHigh = dayHigh;
    }

    @JsonProperty("dayLow")
    public Double getDayLow() {
        return dayLow;
    }

    @JsonProperty("dayLow")
    public void setDayLow(Double dayLow) {
        this.dayLow = dayLow;
    }

    @JsonProperty("lastPrice")
    public Integer getLastPrice() {
        return lastPrice;
    }

    @JsonProperty("lastPrice")
    public void setLastPrice(Integer lastPrice) {
        this.lastPrice = lastPrice;
    }

    @JsonProperty("previousClose")
    public Double getPreviousClose() {
        return previousClose;
    }

    @JsonProperty("previousClose")
    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }

    @JsonProperty("change")
    public Double getChange() {
        return change;
    }

    @JsonProperty("change")
    public void setChange(Double change) {
        this.change = change;
    }

    @JsonProperty("pChange")
    public Double getpChange() {
        return pChange;
    }

    @JsonProperty("pChange")
    public void setpChange(Double pChange) {
        this.pChange = pChange;
    }

    @JsonProperty("ffmc")
    public Double getFfmc() {
        return ffmc;
    }

    @JsonProperty("ffmc")
    public void setFfmc(Double ffmc) {
        this.ffmc = ffmc;
    }

    @JsonProperty("yearHigh")
    public Integer getYearHigh() {
        return yearHigh;
    }

    @JsonProperty("yearHigh")
    public void setYearHigh(Integer yearHigh) {
        this.yearHigh = yearHigh;
    }

    @JsonProperty("yearLow")
    public Double getYearLow() {
        return yearLow;
    }

    @JsonProperty("yearLow")
    public void setYearLow(Double yearLow) {
        this.yearLow = yearLow;
    }

    @JsonProperty("totalTradedVolume")
    public Integer getTotalTradedVolume() {
        return totalTradedVolume;
    }

    @JsonProperty("totalTradedVolume")
    public void setTotalTradedVolume(Integer totalTradedVolume) {
        this.totalTradedVolume = totalTradedVolume;
    }

    @JsonProperty("totalTradedValue")
    public Double getTotalTradedValue() {
        return totalTradedValue;
    }

    @JsonProperty("totalTradedValue")
    public void setTotalTradedValue(Double totalTradedValue) {
        this.totalTradedValue = totalTradedValue;
    }

    @JsonProperty("lastUpdateTime")
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    @JsonProperty("lastUpdateTime")
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @JsonProperty("nearWKH")
    public Double getNearWKH() {
        return nearWKH;
    }

    @JsonProperty("nearWKH")
    public void setNearWKH(Double nearWKH) {
        this.nearWKH = nearWKH;
    }

    @JsonProperty("nearWKL")
    public Double getNearWKL() {
        return nearWKL;
    }

    @JsonProperty("nearWKL")
    public void setNearWKL(Double nearWKL) {
        this.nearWKL = nearWKL;
    }

    @JsonProperty("perChange365d")
    public Double getPerChange365d() {
        return perChange365d;
    }

    @JsonProperty("perChange365d")
    public void setPerChange365d(Double perChange365d) {
        this.perChange365d = perChange365d;
    }

    @JsonProperty("date365dAgo")
    public String getDate365dAgo() {
        return date365dAgo;
    }

    @JsonProperty("date365dAgo")
    public void setDate365dAgo(String date365dAgo) {
        this.date365dAgo = date365dAgo;
    }

    @JsonProperty("chart365dPath")
    public String getChart365dPath() {
        return chart365dPath;
    }

    @JsonProperty("chart365dPath")
    public void setChart365dPath(String chart365dPath) {
        this.chart365dPath = chart365dPath;
    }

    @JsonProperty("date30dAgo")
    public String getDate30dAgo() {
        return date30dAgo;
    }

    @JsonProperty("date30dAgo")
    public void setDate30dAgo(String date30dAgo) {
        this.date30dAgo = date30dAgo;
    }

    @JsonProperty("perChange30d")
    public Double getPerChange30d() {
        return perChange30d;
    }

    @JsonProperty("perChange30d")
    public void setPerChange30d(Double perChange30d) {
        this.perChange30d = perChange30d;
    }

    @JsonProperty("chart30dPath")
    public String getChart30dPath() {
        return chart30dPath;
    }

    @JsonProperty("chart30dPath")
    public void setChart30dPath(String chart30dPath) {
        this.chart30dPath = chart30dPath;
    }

    @JsonProperty("chartTodayPath")
    public String getChartTodayPath() {
        return chartTodayPath;
    }

    @JsonProperty("chartTodayPath")
    public void setChartTodayPath(String chartTodayPath) {
        this.chartTodayPath = chartTodayPath;
    }

    @JsonProperty("series")
    public String getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(String series) {
        this.series = series;
    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
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
