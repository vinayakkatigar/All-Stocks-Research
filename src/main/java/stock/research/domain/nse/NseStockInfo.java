
package stock.research.domain.nse;

import java.util.LinkedHashMap;
import java.util.List;
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
    "name",
    "advance",
    "timestamp",
    "data",
    "metadata",
    "marketStatus",
    "date30dAgo",
    "date365dAgo"
})
@Generated("jsonschema2pojo")
public class NseStockInfo {

    @JsonProperty("name")
    private String name;
    @JsonProperty("advance")
    private Advance advance;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("data")
    private List<Datum> data;
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("marketStatus")
    private MarketStatus marketStatus;
    @JsonProperty("date30dAgo")
    private String date30dAgo;
    @JsonProperty("date365dAgo")
    private String date365dAgo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("advance")
    public Advance getAdvance() {
        return advance;
    }

    @JsonProperty("advance")
    public void setAdvance(Advance advance) {
        this.advance = advance;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("data")
    public List<Datum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("marketStatus")
    public MarketStatus getMarketStatus() {
        return marketStatus;
    }

    @JsonProperty("marketStatus")
    public void setMarketStatus(MarketStatus marketStatus) {
        this.marketStatus = marketStatus;
    }

    @JsonProperty("date30dAgo")
    public String getDate30dAgo() {
        return date30dAgo;
    }

    @JsonProperty("date30dAgo")
    public void setDate30dAgo(String date30dAgo) {
        this.date30dAgo = date30dAgo;
    }

    @JsonProperty("date365dAgo")
    public String getDate365dAgo() {
        return date365dAgo;
    }

    @JsonProperty("date365dAgo")
    public void setDate365dAgo(String date365dAgo) {
        this.date365dAgo = date365dAgo;
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
