package stock.research.gfinance.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import stock.research.gfinance.domain.GFinanceStockInfo;
import stock.research.gfinance.utility.GFinanceNyseStockUtility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.NyseStockResearchUtility.*;

@Service
public class GFinanceStockService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(GFinanceStockService.class);

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "gfinance/credentials.json";


    public List<GFinanceStockInfo> getGFStockInfoList(Map<String, String> urlInfo) {
        final List<GFinanceStockInfo> gfStockInfoList = new ArrayList<>();
        List<GFinanceStockInfo> gfStockInfoFilteredList = null;

        urlInfo.forEach((k, v) -> {
            LOGGER.info("key ->" + k + ", value ->" + v);
            try {
                // Build a new authorized API client service.
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//                final String spreadsheetId = "1V89w-xI5urpoBIcCAHeIoho0J1cJxkFzSQXmG04U85w";
                //    final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";

                final String range = k + "!A2:L";

                Credential credential = getCredentials(HTTP_TRANSPORT);
                int retry = 5;
                ValueRange response = null;

                while (retry-- >= 0 && response == null){
                    response = runGFQuery(v, HTTP_TRANSPORT, range, credential);
                }

                List<List<Object>> values = response.getValues();
                if (values == null || values.isEmpty()) {
                    ERROR_LOGGER.error("No data found.");
                } else {
                    for (List row : values) {
                        GFinanceStockInfo gFinanceStockInfo = null;
                        if (("Vin-Watchlist".equalsIgnoreCase(k)) || ("Vin-portfolio".equalsIgnoreCase(k))){
                            gFinanceStockInfo = new GFinanceStockInfo((String) row.get(0), GFinanceNyseStockUtility.getDoubleFromString((String) row.get(9)), ((String) row.get(10)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(1)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(3)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(4)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(6)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(5)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(2)), GFinanceNyseStockUtility.getDoubleFromString((String) row.get(8)), Instant.now(), Timestamp.from(Instant.now()));
                        }else {
                            gFinanceStockInfo = new GFinanceStockInfo((String) row.get(0), GFinanceNyseStockUtility.getDoubleFromString((String) row.get(9)), ((String) row.get(10)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(1)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(2)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(3)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(6)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(5)), GFinanceNyseStockUtility.getBigDecimalFromString((String) row.get(4)), GFinanceNyseStockUtility.getDoubleFromString((String) row.get(8)), Instant.now(), Timestamp.from(Instant.now()));
                        }
                        gFinanceStockInfo.setChangePct(getDoubleFromString((String) row.get(7)));
                        gfStockInfoList.add(gFinanceStockInfo);
                    }
                }

            } catch (Exception e) {
                ERROR_LOGGER.error("Error Google Finance", e);
            }
        });

        try {
            gfStockInfoFilteredList = gfStockInfoList.stream().filter(q -> (
                            (q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                                    && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                                    && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                                    && ((q.getMktCapRealValue() != null && q.getMktCapRealValue().doubleValue() > 0) || q.getMktCapRealValue() != null)))
                    .distinct().collect(toList());

            gfStockInfoFilteredList.sort(Comparator.comparing(GFinanceStockInfo::getMktCapRealValue,
                    nullsFirst(naturalOrder())).reversed());

            int i = 1;
            for (GFinanceStockInfo x : gfStockInfoFilteredList) {
                x.setStockRankIndex(i++);
                if (x.getMktCapRealValue() != null) x.setMktCapFriendyValue(truncateNumber(x.getMktCapRealValue()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("gfStockInfoFilteredList::Size -> " + gfStockInfoFilteredList.size());

        LOGGER.info("gfStockInfoList::Size -> " + gfStockInfoList.size());

        return gfStockInfoFilteredList;
    }

    private ValueRange runGFQuery(String v, NetHttpTransport HTTP_TRANSPORT, String range, Credential credential) throws IOException {
        try{
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, setTimeout(credential, (60000 * 5)))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values()
                    .get(v, range)
                    .execute();
            return response;
        }catch (Exception e){
            ERROR_LOGGER.error("Error in runGFQuery -> ", e);
            return null;
        }
    }


    private String truncateNumber(double x) {
        return x < MILLION ?  String.valueOf(x) :
                x < BILLION ?  String.format("%.2f", x / MILLION) + "M" :
                        x < TRILLION ? String.format("%.2f", x / BILLION) + "B" :
                                String.format("%.2f", x / TRILLION) + "T";
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream();

        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("vkatigar@gmail.com");
    }

    private HttpRequestInitializer setTimeout(final HttpRequestInitializer initializer, final int timeout) {
        return request -> {
            initializer.initialize(request);
            request.setReadTimeout(timeout);
            request.setConnectTimeout(timeout);
        };
    }
}