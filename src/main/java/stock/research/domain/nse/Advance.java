
package stock.research.domain.nse;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "declines",
    "advances",
    "unchanged"
})
@Generated("jsonschema2pojo")
public class Advance {

    @JsonProperty("declines")
    private String declines;
    @JsonProperty("advances")
    private String advances;
    @JsonProperty("unchanged")
    private String unchanged;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("declines")
    public String getDeclines() {
        return declines;
    }

    @JsonProperty("declines")
    public void setDeclines(String declines) {
        this.declines = declines;
    }

    @JsonProperty("advances")
    public String getAdvances() {
        return advances;
    }

    @JsonProperty("advances")
    public void setAdvances(String advances) {
        this.advances = advances;
    }

    @JsonProperty("unchanged")
    public String getUnchanged() {
        return unchanged;
    }

    @JsonProperty("unchanged")
    public void setUnchanged(String unchanged) {
        this.unchanged = unchanged;
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
