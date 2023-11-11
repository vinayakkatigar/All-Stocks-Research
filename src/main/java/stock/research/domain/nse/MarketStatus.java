
package stock.research.domain.nse;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "market",
    "marketStatus",
    "tradeDate",
    "index",
    "last",
    "variation",
    "percentChange",
    "marketStatusMessage"
})
@Generated("jsonschema2pojo")
public class MarketStatus {

    @JsonProperty("market")
    private String market;
    @JsonProperty("marketStatus")
    private String marketStatus;
    @JsonProperty("tradeDate")
    private String tradeDate;
    @JsonProperty("index")
    private String index;
    @JsonProperty("last")
    private Double last;
    @JsonProperty("variation")
    private Double variation;
    @JsonProperty("percentChange")
    private Double percentChange;
    @JsonProperty("marketStatusMessage")
    private String marketStatusMessage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("market")
    public String getMarket() {
        return market;
    }

    @JsonProperty("market")
    public void setMarket(String market) {
        this.market = market;
    }

    @JsonProperty("marketStatus")
    public String getMarketStatus() {
        return marketStatus;
    }

    @JsonProperty("marketStatus")
    public void setMarketStatus(String marketStatus) {
        this.marketStatus = marketStatus;
    }

    @JsonProperty("tradeDate")
    public String getTradeDate() {
        return tradeDate;
    }

    @JsonProperty("tradeDate")
    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    @JsonProperty("index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("last")
    public Double getLast() {
        return last;
    }

    @JsonProperty("last")
    public void setLast(Double last) {
        this.last = last;
    }

    @JsonProperty("variation")
    public Double getVariation() {
        return variation;
    }

    @JsonProperty("variation")
    public void setVariation(Double variation) {
        this.variation = variation;
    }

    @JsonProperty("percentChange")
    public Double getPercentChange() {
        return percentChange;
    }

    @JsonProperty("percentChange")
    public void setPercentChange(Double percentChange) {
        this.percentChange = percentChange;
    }

    @JsonProperty("marketStatusMessage")
    public String getMarketStatusMessage() {
        return marketStatusMessage;
    }

    @JsonProperty("marketStatusMessage")
    public void setMarketStatusMessage(String marketStatusMessage) {
        this.marketStatusMessage = marketStatusMessage;
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
