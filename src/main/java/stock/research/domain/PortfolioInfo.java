package stock.research.domain;

import java.util.Objects;


public class PortfolioInfo {

    public PortfolioInfo(){}

    private String symbol;
    private String qty;
    private String company;
    private String price;
    private String change;
    private String chgPct;
    private String marketValue;
    private String bookCost;
    private String gain;
    private String gainPct;
    private String shortName;
    private String averagePrice;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChgPct() {
        return chgPct;
    }

    public void setChgPct(String chgPct) {
        this.chgPct = chgPct;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    public String getBookCost() {
        return bookCost;
    }

    public void setBookCost(String bookCost) {
        this.bookCost = bookCost;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public String getGainPct() {
        return gainPct;
    }

    public void setGainPct(String gainPct) {
        this.gainPct = gainPct;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioInfo that = (PortfolioInfo) o;
        return Objects.equals(getSymbol(), that.getSymbol()) &&
                Objects.equals(getQty(), that.getQty()) &&
                Objects.equals(getCompany(), that.getCompany()) &&
                Objects.equals(getPrice(), that.getPrice()) &&
                Objects.equals(getChange(), that.getChange()) &&
                Objects.equals(getChgPct(), that.getChgPct()) &&
                Objects.equals(getMarketValue(), that.getMarketValue()) &&
                Objects.equals(getBookCost(), that.getBookCost()) &&
                Objects.equals(getGain(), that.getGain()) &&
                Objects.equals(getGainPct(), that.getGainPct()) &&
                Objects.equals(getShortName(), that.getShortName()) &&
                Objects.equals(getAveragePrice(), that.getAveragePrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol(), getQty(), getCompany(), getPrice(), getChange(), getChgPct(), getMarketValue(), getBookCost(), getGain(), getGainPct(), getShortName(), getAveragePrice());
    }

    @Override
    public String toString() {
        return "PortfolioInfo{" +
                "symbol='" + symbol + '\'' +
                ", qty='" + qty + '\'' +
                ", company='" + company + '\'' +
                ", price='" + price + '\'' +
                ", change='" + change + '\'' +
                ", chgPct='" + chgPct + '\'' +
                ", marketValue='" + marketValue + '\'' +
                ", bookCost='" + bookCost + '\'' +
                ", gain='" + gain + '\'' +
                ", gainPct='" + gainPct + '\'' +
                ", shortName='" + shortName + '\'' +
                ", averagePrice='" + averagePrice + '\'' +
                '}';
    }
}
