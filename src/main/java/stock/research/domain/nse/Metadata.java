
package stock.research.domain.nse;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "indexName",
    "open",
    "high",
    "low",
    "previousClose",
    "last",
    "percChange",
    "change",
    "timeVal",
    "yearHigh",
    "yearLow",
    "totalTradedVolume",
    "totalTradedValue",
    "ffmc_sum"
})
@Generated("jsonschema2pojo")
public class Metadata {

    @JsonProperty("indexName")
    private String indexName;
    @JsonProperty("open")
    private Double open;
    @JsonProperty("high")
    private Double high;
    @JsonProperty("low")
    private Double low;
    @JsonProperty("previousClose")
    private Double previousClose;
    @JsonProperty("last")
    private Double last;
    @JsonProperty("percChange")
    private Double percChange;
    @JsonProperty("change")
    private Double change;
    @JsonProperty("timeVal")
    private String timeVal;
    @JsonProperty("yearHigh")
    private Double yearHigh;
    @JsonProperty("yearLow")
    private Double yearLow;
    @JsonProperty("totalTradedVolume")
    private Integer totalTradedVolume;
    @JsonProperty("totalTradedValue")
    private Double totalTradedValue;
    @JsonProperty("ffmc_sum")
    private Double ffmcSum;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("indexName")
    public String getIndexName() {
        return indexName;
    }

    @JsonProperty("indexName")
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @JsonProperty("open")
    public Double getOpen() {
        return open;
    }

    @JsonProperty("open")
    public void setOpen(Double open) {
        this.open = open;
    }

    @JsonProperty("high")
    public Double getHigh() {
        return high;
    }

    @JsonProperty("high")
    public void setHigh(Double high) {
        this.high = high;
    }

    @JsonProperty("low")
    public Double getLow() {
        return low;
    }

    @JsonProperty("low")
    public void setLow(Double low) {
        this.low = low;
    }

    @JsonProperty("previousClose")
    public Double getPreviousClose() {
        return previousClose;
    }

    @JsonProperty("previousClose")
    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }

    @JsonProperty("last")
    public Double getLast() {
        return last;
    }

    @JsonProperty("last")
    public void setLast(Double last) {
        this.last = last;
    }

    @JsonProperty("percChange")
    public Double getPercChange() {
        return percChange;
    }

    @JsonProperty("percChange")
    public void setPercChange(Double percChange) {
        this.percChange = percChange;
    }

    @JsonProperty("change")
    public Double getChange() {
        return change;
    }

    @JsonProperty("change")
    public void setChange(Double change) {
        this.change = change;
    }

    @JsonProperty("timeVal")
    public String getTimeVal() {
        return timeVal;
    }

    @JsonProperty("timeVal")
    public void setTimeVal(String timeVal) {
        this.timeVal = timeVal;
    }

    @JsonProperty("yearHigh")
    public Double getYearHigh() {
        return yearHigh;
    }

    @JsonProperty("yearHigh")
    public void setYearHigh(Double yearHigh) {
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

    @JsonProperty("ffmc_sum")
    public Double getFfmcSum() {
        return ffmcSum;
    }

    @JsonProperty("ffmc_sum")
    public void setFfmcSum(Double ffmcSum) {
        this.ffmcSum = ffmcSum;
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
