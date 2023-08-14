
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
    "quoteSummary"
})
@Generated("jsonschema2pojo")
public class YFinance {

    @JsonProperty("quoteSummary")
    private QuoteSummary quoteSummary;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("quoteSummary")
    public QuoteSummary getQuoteSummary() {
        return quoteSummary;
    }

    @JsonProperty("quoteSummary")
    public void setQuoteSummary(QuoteSummary quoteSummary) {
        this.quoteSummary = quoteSummary;
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
