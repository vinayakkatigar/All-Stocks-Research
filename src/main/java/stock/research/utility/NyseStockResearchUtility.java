package stock.research.utility;

import stock.research.domain.NyseStockInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class NyseStockResearchUtility {

    public static final Double TRILLION = 1000000000000.0;
    public static final Double BILLION = 1000000000.0;
    public static final Double MILLION = 1000000.0;
    public static final String NASDAQ_URL = "https://www.advfn.com/nasdaq/nasdaq.asp?companies=";
    public static final String NYSE_URL = "https://www.advfn.com/nyse/newyorkstockexchange.asp?companies=";
    public static final String NYSE_1000_URL = "https://companiesmarketcap.com/usa/largest-companies-in-the-usa-by-market-cap/?page=";
    public static final int NYSE_1000_CNT = 15;
    public static final String NYSE_MISSED_URL = "https://www.theglobeandmail.com/investing/markets/stocks/";

    public static final String HYPHEN = "-";
    public static final String START_BRACKET = "(";
    public static final String END_BRACKET = ")";
    public static final String HTML_START = "<html><head>\n"
            + "<style>\n" +
            "/* TABLE BACKGROUND color (match the original theme) */\n" +
            "table.hover-highlight td:before {\n" +
            "  background: #fff;\n" +
            "}\n" +
            "\n" +
            "/* ODD ZEBRA STRIPE color (needs zebra widget) */\n" +
            ".hover-highlight .odd td:before, .hover-highlight .odd th:before {\n" +
            "  background: #ebf2fa;\n" +
            "}\n" +
            "/* EVEN ZEBRA STRIPE color (needs zebra widget) */\n" +
            ".hover-highlight .even td:before, .hover-highlight .even th:before{\n" +
            "  background-color: #fff;\n" +
            "}\n" +
            "\n" +
            "/* HOVER ROW highlight colors */\n" +
            "table.hover-highlight tbody > tr:hover > td, /* override tablesorter theme row hover */\n" +
            "table.hover-highlight tbody > tr.odd:hover > td,\n" +
            "table.hover-highlight tbody > tr.even:hover > td {\n" +
            "  background-color: #ffa;\n" +
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
            "<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>\n" +
            "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/floatthead/2.1.1/jquery.floatThead.min.js\"></script>\n" +
            "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/css/theme.default.min.css\" integrity=\"sha512-wghhOJkjQX0Lh3NSWvNKeZ0ZpNn+SPVXX1Qyc9OCaogADktxrBiBdKGDoqVUOyhStvMBmJQ8ZdMHiR3wuEq8+w==\" crossorigin=\"anonymous\" />\n" +
            "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/js/jquery.tablesorter.min.js\" integrity=\"sha512-qzgd5cYSZcosqpzpn7zF2ZId8f/8CHmFKZ8j7mU4OUXTNRd5g+ZHBPsgKEwoqxCtdQvExE5LprwwPAgoicguNg==\" crossorigin=\"anonymous\"></script>\n" +
            "\n <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/js/jquery.tablesorter.widgets.min.js\" integrity=\"sha512-dj/9K5GRIEZu+Igm9tC16XPOTz0RdPk9FGxfZxShWf65JJNU2TjbElGjuOo3EhwAJRPhJxwEJ5b+/Ouo+VqZdQ==\" crossorigin=\"anonymous\"></script> \n" +
            "</head><body><table id=\"myTable\"  border=\"1\" class=\"tablesorter hover-highlight\"><thead><tr><td>RankIndex</td><td>Name-Code(MktCap)<td>Market Price</td><td>52 Week High</td><td>52 Week Low</td><td>Year H/L Diff(%)</td><td>Year Low Diff(%)\n" +
            " Buy</td><td>Year High Diff(%)\n" +
            " Sell</td><td>P/E</td><td>Sector/Industry</td></tr></thead>\n" +
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
            "});\n" +
            "$(function(){ //shorthand for making this function run at doc ready\n" +
            "// select the table, init floatThead on it\n" +
            "\t$('#myTable').floatThead();\n" +
            "})" +
            "</script>\n" +
            "</html>";


    private NyseStockResearchUtility(){
    }

    public static Map<String, List<NyseStockInfo>> getPopulatedNYSEStockMap() {
        return populatedNYSEStockMap;
    }

    public static void setPopulatedNYSEStockMap(Map<String, List<NyseStockInfo>> populatedNYSEStockMap) {
        NyseStockResearchUtility.populatedNYSEStockMap = populatedNYSEStockMap;
    }

    private static class NyseStockResearchUtilityHelper {
        private static final NyseStockResearchUtility INSTANCE = new NyseStockResearchUtility();
    }

    private  static Map<String, List<NyseStockInfo>> populatedNYSEStockMap;

    public static NyseStockResearchUtility getInstance(){
        return NyseStockResearchUtilityHelper.INSTANCE;
    }

    public static Double getDoubleFromString(String input){
       try {
           if (input != null){
               input = input.replace('$', ' ').replaceAll(" ", "");

               input = input.trim();
               input = input.replace(",","");
           }
           return new Double(input);
       }catch (Exception e){
           return new Double(0);
       }
    }

    public static BigDecimal getBigDecimalFromString(String input){
       try {
           if (input != null){
               input = input.trim();
               input = input.replace(",","");
           }
           return new BigDecimal(input);
       }catch (Exception e){
           return new BigDecimal(0);
       }
    }

    public synchronized static void createTableContents(StringBuilder dataBuffer, NyseStockInfo x) {
        if (x.get_52WeekLowPrice().compareTo(x.getCurrentMarketPrice()) >= 0){
            dataBuffer.append("<tr style=\"background-color:#FFBA75\">");
        }else if (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0 ){
            dataBuffer.append("<tr style=\"background-color:#A7D971\">");
        }else {
            dataBuffer.append("<tr>");
        }
        dataBuffer.append("<td>" + x.getStockRankIndex() + "</td>");
        dataBuffer.append("<td><a href=" +x.getStockURL() +" target=\"_blank\">"
                +  x.getStockCode()
                + START_BRACKET + x.getStockMktCap() + END_BRACKET + "</a></td>");
        dataBuffer.append("<td>" + x.getCurrentMarketPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekLowPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighLowPriceDiff().setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekLowPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekHighPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        try {
            if (x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 && (x.getEps()) > 0){
                dataBuffer.append("<td>" + x.getCurrentMarketPrice().divide(BigDecimal.valueOf((x.getEps())), 2 , RoundingMode.HALF_EVEN) + "</td>");
            }else {
                dataBuffer.append("<td>0</td>");
            }
        }catch (Exception e){
            dataBuffer.append("<td>0</td>");
        }
        dataBuffer.append("<td>" + x.getSectorIndustry() + "- ( " + String.format("%,.2f",x.getMktCapRealValue()) + " )</td>");
        dataBuffer.append("</tr>");
    }

}
