package stock.research.utility;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import stock.research.domain.StockInfo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static java.time.LocalDateTime.ofInstant;

public class StockResearchUtility {
    @Value("${component}")
    private static String component;

    public static final String SWTHKRW_URL = "https://www.value.today/headquarters/south-korea?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer SWTHKRW_CNT = 15;

    public static final String HK_URL = "https://www.value.today/headquarters/hong-kong?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer HK_CNT = 12;

    public static final String SWZLD_200_URL = "https://www.value.today/stock-exchange-companies/switzerland?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer SWZLD_CNT = 10;

    public static final String SNGR_URL = "https://www.value.today/headquarters/singapore?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer SNGR_CNT = 8;

    public static final String AUSTRIA_URL = "https://www.value.today/headquarters/austria?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer AUSTRIA_CNT = 3;

    public static final String AUSTRALIA_URL = "https://www.value.today/headquarters/australia?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer AUSTRALIA_CNT = 16;

    public static final String EURO_URL = "https://www.value.today/stocks/sp-europe-350?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer EURO_CNT = 34;

    public static final String CANADA_URL = "https://www.value.today/stocks/tsx-60?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer CANADA_CNT = 5;

    public static final String DENMARK_URL = "https://www.value.today/headquarters/denmark?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer DENMARK_CNT = 7;

    public static final String FINLAND_URL = "https://www.value.today/headquarters/finland?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer FINLAND_CNT = 7;

    public static final String GERMANY_URL = "https://www.value.today/headquarters/germany?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer GERMANY_CNT = 22;

    public static final String NETHERLANDS_URL = "https://www.value.today/headquarters/netherlands?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer NETHERLANDS_CNT = 6;

    public static final String NORWAY_URL = "https://www.value.today/headquarters/norway?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer NORWAY_CNT = 7;

    public static final String WORLD_1000_URL = "https://www.value.today/world-top-1000-companies-as-on-aug-2020?title=&field_headquarters_02_target_id=All&field_company_category_primary_target_id&field_stock_exchange_lc_target_id=All&field_market_cap_aug_22_2020_value=&page=";
    public static final Integer WORLD_1000_CNT = 99;

    public static final String SWEDEN_URL = "https://www.value.today/headquarters/sweden?title=&field_company_category_primary_target_id&field_market_value_jan_2020_value_1=&page=";
    public static final Integer SWEDEN_CNT = 15;

    public static final String FRANCE_URL = "https://www.value.today/headquarters/france?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer FRANCE_CNT = 10;

    public static final String BELGIUM_URL = "https://www.value.today/headquarters/belgium?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer BELGIUM_CNT = 4;

    public static final String JAPAN_URL = "https://www.value.today/headquarters/japan?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer JAPAN_CNT = 30;

    public static final String SPAIN_URL = "https://www.value.today/headquarters/spain?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer SPAIN_CNT = 6;

    public static final String ITALY_URL = "https://www.value.today/headquarters/italy?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer ITALY_CNT = 10;

    public static final String INDIA_URL = "https://www.value.today/headquarters/india?title=&field_headquarters_of_company_target_id&field_company_category_primary_target_id&field_company_website_uri=&field_market_cap_aug_01_2021__value=&page=";
    public static final Integer INDIA_CNT = 40;

    public static final Double TRILLION = 1000000000000.0;
    public static final Double BILLION = 1000000000.0;
    public static final Double MILLION = 1000000.0;

    private static Set<String> excludedIsinCodeSet = new HashSet<>();

    public static final String HYPHEN = "-";
    public static final String COMMA = ",";
    public static final Integer LARGE_CAP = 150;
    public static final String START_BRACKET = "(";
    public static final String END_BRACKET = ")";
    public static final String HTML_START = "<html><head>\n"
            + "<style>\n" +  "<link rel=\"icon\" type=\"image/png\" href=\"C:\\Code-Base\\All-Stocks-Research\\connection.png\">\n" +
            "/* TABLE BACKGROUND color (match the original theme) */\n" +
            "table.hover-highlight td:before {\n" +
            "  background: #ffffff;\n" +
            "}\n" +
            "\n" +
            "/* ODD ZEBRA STRIPE color (needs zebra widget) */\n" +
            ".hover-highlight .odd td:before, .hover-highlight .odd th:before {\n" +
            "  background: #ffffff;\n" +
            "}\n" +
            "/* EVEN ZEBRA STRIPE color (needs zebra widget) */\n" +
            ".hover-highlight .even td:before, .hover-highlight .even th:before{\n" +
            "  background-color: #ffffff;\n" +
            "}\n" +
            "\n" +
            "/* HOVER ROW highlight colors */\n" +
            "table.hover-highlight tbody > tr:hover > td, /* override tablesorter theme row hover */\n" +
            "table.hover-highlight tbody > tr.odd:hover > td,\n" +
            "table.hover-highlight tbody > tr.even:hover > td {\n" +
            "  background-color: #ffffe6;\n" +
            "}\n" +
            "/* HOVER COLUMN highlight colors */\n" +
            ".hover-highlight tbody tr td:hover::after,\n" +
            ".hover-highlight tbody tr th:hover::after {\n" +
            "  \n" +
            "}\n" +
            "\n" +
            "/* ************************************************* */\n" +
            "/* **** No need to modify the definitions below **** */\n" +
            "/* ************************************************* */\n" +
            "\n" +
            ".hover-highlight td:hover::after, .hover-highlight th:hover::after {\n" +
            "  content: '';\n" +
            "  position: absolute;\n" +
            "  width: 100%;\n" +
            "  height: 999em;\n" +
            "  left: 0;\n" +
            "  top: -555em;\n" +
            "  z-index: -1;\n" +
            "}\n" +
            "/* required styles */\n" +
            ".hover-highlight {\n" +
            "  overflow: hidden;\n" +
            "}\n" +
            ".hover-highlight td, .hover-highlight th {\n" +
            "  position: relative;\n" +
            "  outline: 0;\n" +
            "}\n" +
            "/* override the tablesorter theme styling */\n" +
            "table.hover-highlight, table.hover-highlight tbody > tr > td,\n" +
            "\n" +
            "/* override zebra styling */\n" +
            "table.hover-highlight tbody tr.even > th,\n" +
            "table.hover-highlight tbody tr.even > td,\n" +
            "table.hover-highlight tbody tr.odd > th,\n" +
            "table.hover-highlight tbody tr.odd > td{\n" +
            "  background: transparent;\n" +
            "}\n" +
            "/* table background positioned under the highlight */\n" +
            "table.hover-highlight td:before{\n" +
            "  content: '';\n" +
            "  position: absolute;\n" +
            "  width: 100%;\n" +
            "  height: 100%;\n" +
            "  left: 0;\n" +
            "  top: 0;\n" +
            "  z-index: -3;\n" +
            "}\n" +
            "</style>" +
            "<style>"+ component + "</style>" +
            "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\" integrity=\"sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0=\" crossorigin=\"anonymous\"></script>\n"+
            "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/floatthead/2.1.1/jquery.floatThead.min.js\"></script>\n" +
            "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/css/theme.default.min.css\" integrity=\"sha512-wghhOJkjQX0Lh3NSWvNKeZ0ZpNn+SPVXX1Qyc9OCaogADktxrBiBdKGDoqVUOyhStvMBmJQ8ZdMHiR3wuEq8+w==\" crossorigin=\"anonymous\" />\n" +
            "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/js/jquery.tablesorter.min.js\" integrity=\"sha512-qzgd5cYSZcosqpzpn7zF2ZId8f/8CHmFKZ8j7mU4OUXTNRd5g+ZHBPsgKEwoqxCtdQvExE5LprwwPAgoicguNg==\" crossorigin=\"anonymous\"></script>\n" +
            "\n <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/js/jquery.tablesorter.widgets.min.js\" integrity=\"sha512-dj/9K5GRIEZu+Igm9tC16XPOTz0RdPk9FGxfZxShWf65JJNU2TjbElGjuOo3EhwAJRPhJxwEJ5b+/Ouo+VqZdQ==\" crossorigin=\"anonymous\"></script>\n" +
            "</head><body><table id=\"myTable\"  border=\"1\" class=\"tablesorter hover-highlight\"><thead><tr><td>RankIndex</td><td>Name-Code(MktCap)<td>Market Price</td><td>52 Week High</td><td>52 Week Low</td><td>Year H/L Diff(%)</td><td>Year Low Diff(%)\n" +
            " Buy</td><td>Year High Diff(%)\n" +
            " Sell</td>"
            +"<td>EPS</td>"
            +"<td>PE</td>"
            +"</tr></thead>\n" +
            "<tbody>";

    public static final String HTML_END = "</tbody>\n" +
            "    </table>\n" +
            "</body>\n" +
            "<script>\n" +
            "$(function() {\n" +
            "\n" +
            "  var $table = $('table').tablesorter({\n" +
            "    \n" +
            "    widgets: [\"zebra\", \"filter\"],\n" +
            "    widgetOptions : {\n" +
            "      // filter_anyMatch replaced! Instead use the filter_external option\n" +
            "      // Set to use a jQuery selector (or jQuery object) pointing to the\n" +
            "      // external filter (column specific or any match)\n" +
            "      filter_external : '.search',\n" +
            "      // add a default type search to the first name column\n" +
            "      filter_defaultFilter: { 1 : '~{query}' },\n" +
            "\t  // include column filters\n" +
            "      filter_columnFilters: true,\n" +
            "      filter_placeholder: { search : 'Search...' },\n" +
            "      filter_saveFilters : true,\n" +
            "      filter_reset: '.reset'\n" +
            "    }\n" +
            "  });\n" +
            "\n" +
            "  // make demo search buttons work\n" +
            "  $('button[data-column]').on('click', function() {\n" +
            "    var $this = $(this),\n" +
            "      totalColumns = $table[0].config.columns,\n" +
            "      col = $this.data('column'), // zero-based index or \"all\"\n" +
            "      filter = [];\n" +
            "\n" +
            "    // text to add to filter\n" +
            "    filter[ col === 'all' ? totalColumns : col ] = $this.text();\n" +
            "    $table.trigger('search', [ filter ]);\n" +
            "    return false;\n" +
            "  });\n" +
            "\n" +
            "});" +
            "$(function(){ //shorthand for making this function run at doc ready\n" +
            "// select the table, init floatThead on it\n" +
            "\t$('#myTable').floatThead();\n" +
            "})" +
            "</script>\n" +
            "</html>";

    private StockResearchUtility(){
    }

    public static Map<String, List<StockInfo>> getPopulatedStockMap() {
        return populatedStockMap;
    }

    public static void setPopulatedStockMap(Map<String, List<StockInfo>> populatedStockMap) {
        StockResearchUtility.populatedStockMap = populatedStockMap;
    }

    private static class StockResearchUtilityHelper {
        private static final StockResearchUtility INSTANCE = new StockResearchUtility();
    }

    private  static Map<String, List<StockInfo>> populatedStockMap;

    public static StockResearchUtility getInstance(){
        return StockResearchUtilityHelper.INSTANCE;
    }

    public static Double getDoubleFromString(String input){
        try {
            input = input.trim();
            input = input.replace(",", "");
            String rupee = "\u20B9";
            byte[] utf8 = rupee.getBytes("UTF-8");

            rupee = new String(utf8, "UTF-8");
            input = input.replace(rupee, "");
            input = input.replace("%", "");
            input = input.replace("Cr.", "");
            input = input.trim();
            input = input.replace(",", "");
            input = input.replace("$", "");
            input = input.replace("£","");
            input = input.replace("€","");
            return Double.valueOf(input);
        }catch (Exception e){
            return Double.valueOf(0);
        }
    }

    public static BigDecimal getBigDecimalFromString(String input){
       try {
           if (input != null && input.trim() != null){
               input = input.trim();
               input = input.replace(",", "");
               input = input.replace("$", "");
               input = input.replace("£","");
               input = input.replace("€","");
           }
           return new BigDecimal(input);
       }catch (Exception e){
           return new BigDecimal(0);
       }
    }

    public synchronized static void createTableContents(StringBuilder dataBuffer, StockInfo x) {
        if (x.get_52WeekLowPrice().compareTo(x.getCurrentMarketPrice()) >= 0){
            dataBuffer.append("<tr style=\"background-color:#FFBA75\">");
        }else if (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0 ){
            dataBuffer.append("<tr style=\"background-color:#A7D971\">");
        }else {
            dataBuffer.append("<tr>");
        }
        dataBuffer.append("<td>" + x.getStockRankIndex() + "</td>");
        dataBuffer.append("<td><a href="  + x.getStockURL() +" target=\"_blank\">" + x.getStockName() + HYPHEN + x.getStockCode()
                + START_BRACKET + x.getStockMktCapStr() + END_BRACKET + "</a></td>");
        dataBuffer.append("<td>" + x.getCurrentMarketPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekLowPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighLowPriceDiff().setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekLowPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekHighPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.getEps() + "</td>");
        dataBuffer.append("<td>" + x.getP2e() + "</td>");
        dataBuffer.append("</tr>");
    }


    public static void killProcess(String process, WebDriver webDriver) {
        try {
            try {
                Runtime.getRuntime().exec("TASKKILL /IM  chromedriver.exe /F");
            }catch (Exception e){
                e.printStackTrace();
            }
        try {
                Runtime.getRuntime().exec("TASKKILL /IM  "+ process + ".exe /F");
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                Files.walk(Paths.get("C:\\Users\\vinka\\AppData\\Local\\Temp"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);        }catch (Exception e){ }
            try {
                Path path = Paths.get("C:\\Users\\vinka\\AppData\\Local\\Temp");
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                            .forEach(x -> {
                                try {
                                    Files.deleteIfExists(x);
                                } catch (IOException e) {
                                }
                            });
                }

                Runtime.getRuntime().exec("RD %temp%");
            }catch (Exception e){
            }
            try {
                Runtime.getRuntime().exec("RMDIR /Q/S %temp%");
            }catch (Exception e){
            }
            if (webDriver != null){
                webDriver = null;
            }
        }catch (Exception e){ }

    }


    public static boolean checkIfWeekend() {
        if (ofInstant(Instant.now(), ZoneId.systemDefault()).getDayOfWeek() == DayOfWeek.SATURDAY
                || ofInstant(Instant.now(), ZoneId.systemDefault()).getDayOfWeek() == DayOfWeek.SUNDAY){
            return true;
        }
        return false;
    }
    public static String friendlyMktCap(double x) {
        return x < MILLION ?  String.valueOf(x) :
                x < BILLION ?  String.format("%.2f", x / MILLION) + "M" :
                        x < TRILLION ? String.format("%.2f", x / BILLION) + "B" :
                                String.format("%.2f", x / TRILLION) + "T";
    }

}