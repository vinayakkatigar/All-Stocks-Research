package stock.research.domain;

import java.util.Objects;


public class PortfolioInfo {

    public PortfolioInfo(){}


    private String symbol;
    private String stockName;
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

    private String iSIN;
    private String averageCostPrice;
    private String currentMarketPrice;
    private String changeoverprevclosePct;
    private String valueAtCost;
    private String valueAtMarketPrice;
    private String daysGain;
    private String daysGainPct;
    private String realizedProfitNLoss;
    private String unrealizedProfitNLoss;
    private String unrealizedProfitNLossPct;

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
                ", iSIN='" + iSIN + '\'' +
                ", averageCostPrice='" + averageCostPrice + '\'' +
                ", currentMarketPrice='" + currentMarketPrice + '\'' +
                ", changeoverprevclosePct='" + changeoverprevclosePct + '\'' +
                ", valueAtCost='" + valueAtCost + '\'' +
                ", valueAtMarketPrice='" + valueAtMarketPrice + '\'' +
                ", daysGain='" + daysGain + '\'' +
                ", daysGainPct='" + daysGainPct + '\'' +
                ", realizedProfitNLoss='" + realizedProfitNLoss + '\'' +
                ", unrealizedProfitNLoss='" + unrealizedProfitNLoss + '\'' +
                ", unrealizedProfitNLossPct='" + unrealizedProfitNLossPct + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioInfo that = (PortfolioInfo) o;
        return Objects.equals(getSymbol(), that.getSymbol()) && Objects.equals(getQty(), that.getQty()) && Objects.equals(getCompany(), that.getCompany()) && Objects.equals(getPrice(), that.getPrice()) && Objects.equals(getChange(), that.getChange()) && Objects.equals(getChgPct(), that.getChgPct()) && Objects.equals(getMarketValue(), that.getMarketValue()) && Objects.equals(getBookCost(), that.getBookCost()) && Objects.equals(getGain(), that.getGain()) && Objects.equals(getGainPct(), that.getGainPct()) && Objects.equals(getShortName(), that.getShortName()) && Objects.equals(getAveragePrice(), that.getAveragePrice()) && Objects.equals(getiSIN(), that.getiSIN()) && Objects.equals(getAverageCostPrice(), that.getAverageCostPrice()) && Objects.equals(getCurrentMarketPrice(), that.getCurrentMarketPrice()) && Objects.equals(getChangeoverprevclosePct(), that.getChangeoverprevclosePct()) && Objects.equals(getValueAtCost(), that.getValueAtCost()) && Objects.equals(getValueAtMarketPrice(), that.getValueAtMarketPrice()) && Objects.equals(getDaysGain(), that.getDaysGain()) && Objects.equals(getDaysGainPct(), that.getDaysGainPct()) && Objects.equals(getRealizedProfitNLoss(), that.getRealizedProfitNLoss()) && Objects.equals(getUnrealizedProfitNLoss(), that.getUnrealizedProfitNLoss()) && Objects.equals(getUnrealizedProfitNLossPct(), that.getUnrealizedProfitNLossPct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol(), getQty(), getCompany(), getPrice(), getChange(), getChgPct(), getMarketValue(), getBookCost(), getGain(), getGainPct(), getShortName(), getAveragePrice(), getiSIN(), getAverageCostPrice(), getCurrentMarketPrice(), getChangeoverprevclosePct(), getValueAtCost(), getValueAtMarketPrice(), getDaysGain(), getDaysGainPct(), getRealizedProfitNLoss(), getUnrealizedProfitNLoss(), getUnrealizedProfitNLossPct());
    }

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

    public String getiSIN() {
        return iSIN;
    }

    public void setiSIN(String iSIN) {
        this.iSIN = iSIN;
    }

    public String getAverageCostPrice() {
        return averageCostPrice;
    }

    public void setAverageCostPrice(String averageCostPrice) {
        this.averageCostPrice = averageCostPrice;
    }

    public String getCurrentMarketPrice() {
        return currentMarketPrice;
    }

    public void setCurrentMarketPrice(String currentMarketPrice) {
        this.currentMarketPrice = currentMarketPrice;
    }

    public String getChangeoverprevclosePct() {
        return changeoverprevclosePct;
    }

    public void setChangeoverprevclosePct(String changeoverprevclosePct) {
        this.changeoverprevclosePct = changeoverprevclosePct;
    }

    public String getValueAtCost() {
        return valueAtCost;
    }

    public void setValueAtCost(String valueAtCost) {
        this.valueAtCost = valueAtCost;
    }

    public String getValueAtMarketPrice() {
        return valueAtMarketPrice;
    }

    public void setValueAtMarketPrice(String valueAtMarketPrice) {
        this.valueAtMarketPrice = valueAtMarketPrice;
    }

    public String getDaysGain() {
        return daysGain;
    }

    public void setDaysGain(String daysGain) {
        this.daysGain = daysGain;
    }

    public String getDaysGainPct() {
        return daysGainPct;
    }

    public void setDaysGainPct(String daysGainPct) {
        this.daysGainPct = daysGainPct;
    }

    public String getRealizedProfitNLoss() {
        return realizedProfitNLoss;
    }

    public void setRealizedProfitNLoss(String realizedProfitNLoss) {
        this.realizedProfitNLoss = realizedProfitNLoss;
    }

    public String getUnrealizedProfitNLoss() {
        return unrealizedProfitNLoss;
    }

    public void setUnrealizedProfitNLoss(String unrealizedProfitNLoss) {
        this.unrealizedProfitNLoss = unrealizedProfitNLoss;
    }

    public String getUnrealizedProfitNLossPct() {
        return unrealizedProfitNLossPct;
    }

    public void setUnrealizedProfitNLossPct(String unrealizedProfitNLossPct) {
        this.unrealizedProfitNLossPct = unrealizedProfitNLossPct;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
}
