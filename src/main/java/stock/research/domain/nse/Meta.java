
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
    "symbol",
    "companyName",
    "activeSeries",
    "debtSeries",
    "tempSuspendedSeries",
    "isFNOSec",
    "isCASec",
    "isSLBSec",
    "isDebtSec",
    "isSuspended",
    "isETFSec",
    "isDelisted",
    "isin",
    "industry"
})
@Generated("jsonschema2pojo")
public class Meta {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("activeSeries")
    private List<String> activeSeries;
    @JsonProperty("debtSeries")
    private List<Object> debtSeries;
    @JsonProperty("tempSuspendedSeries")
    private List<String> tempSuspendedSeries;
    @JsonProperty("isFNOSec")
    private Boolean isFNOSec;
    @JsonProperty("isCASec")
    private Boolean isCASec;
    @JsonProperty("isSLBSec")
    private Boolean isSLBSec;
    @JsonProperty("isDebtSec")
    private Boolean isDebtSec;
    @JsonProperty("isSuspended")
    private Boolean isSuspended;
    @JsonProperty("isETFSec")
    private Boolean isETFSec;
    @JsonProperty("isDelisted")
    private Boolean isDelisted;
    @JsonProperty("isin")
    private String isin;
    @JsonProperty("industry")
    private String industry;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("companyName")
    public String getCompanyName() {
        return companyName;
    }

    @JsonProperty("companyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @JsonProperty("activeSeries")
    public List<String> getActiveSeries() {
        return activeSeries;
    }

    @JsonProperty("activeSeries")
    public void setActiveSeries(List<String> activeSeries) {
        this.activeSeries = activeSeries;
    }

    @JsonProperty("debtSeries")
    public List<Object> getDebtSeries() {
        return debtSeries;
    }

    @JsonProperty("debtSeries")
    public void setDebtSeries(List<Object> debtSeries) {
        this.debtSeries = debtSeries;
    }

    @JsonProperty("tempSuspendedSeries")
    public List<String> getTempSuspendedSeries() {
        return tempSuspendedSeries;
    }

    @JsonProperty("tempSuspendedSeries")
    public void setTempSuspendedSeries(List<String> tempSuspendedSeries) {
        this.tempSuspendedSeries = tempSuspendedSeries;
    }

    @JsonProperty("isFNOSec")
    public Boolean getIsFNOSec() {
        return isFNOSec;
    }

    @JsonProperty("isFNOSec")
    public void setIsFNOSec(Boolean isFNOSec) {
        this.isFNOSec = isFNOSec;
    }

    @JsonProperty("isCASec")
    public Boolean getIsCASec() {
        return isCASec;
    }

    @JsonProperty("isCASec")
    public void setIsCASec(Boolean isCASec) {
        this.isCASec = isCASec;
    }

    @JsonProperty("isSLBSec")
    public Boolean getIsSLBSec() {
        return isSLBSec;
    }

    @JsonProperty("isSLBSec")
    public void setIsSLBSec(Boolean isSLBSec) {
        this.isSLBSec = isSLBSec;
    }

    @JsonProperty("isDebtSec")
    public Boolean getIsDebtSec() {
        return isDebtSec;
    }

    @JsonProperty("isDebtSec")
    public void setIsDebtSec(Boolean isDebtSec) {
        this.isDebtSec = isDebtSec;
    }

    @JsonProperty("isSuspended")
    public Boolean getIsSuspended() {
        return isSuspended;
    }

    @JsonProperty("isSuspended")
    public void setIsSuspended(Boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    @JsonProperty("isETFSec")
    public Boolean getIsETFSec() {
        return isETFSec;
    }

    @JsonProperty("isETFSec")
    public void setIsETFSec(Boolean isETFSec) {
        this.isETFSec = isETFSec;
    }

    @JsonProperty("isDelisted")
    public Boolean getIsDelisted() {
        return isDelisted;
    }

    @JsonProperty("isDelisted")
    public void setIsDelisted(Boolean isDelisted) {
        this.isDelisted = isDelisted;
    }

    @JsonProperty("isin")
    public String getIsin() {
        return isin;
    }

    @JsonProperty("isin")
    public void setIsin(String isin) {
        this.isin = isin;
    }

    @JsonProperty("industry")
    public String getIndustry() {
        return industry;
    }

    @JsonProperty("industry")
    public void setIndustry(String industry) {
        this.industry = industry;
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
