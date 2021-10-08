package com.thomariobros.finance.financereport.datasource;

import com.thomariobros.finance.financereport.process.Quotation;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class DataSourceBoursorama {

  private static final Logger LOGGER = Logger.getLogger(DataSourceBoursorama.class.getName());
  
  private static final int timeoutSeconds = 15;

  private static final Collection<Integer> REDIRECT_CODES = Arrays.asList(HttpStatus.SC_MOVED_PERMANENTLY, HttpStatus.SC_MOVED_TEMPORARILY);

  private final DataSourceBoursoramaType type;
  private final CloseableHttpClient httpClient;

  public DataSourceBoursorama(final DataSourceBoursoramaType type) throws Exception {
    this.type = type;
    // timeouts
    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectTimeout(DataSourceBoursorama.timeoutSeconds * 1000)
      .setConnectionRequestTimeout(DataSourceBoursorama.timeoutSeconds * 1000)
      .setSocketTimeout(DataSourceBoursorama.timeoutSeconds * 1000)
      // allow circular redirect
      .setCircularRedirectsAllowed(true)
      .build();
    this.httpClient = HttpClientBuilder.create()
      // allow redirect
      .setRedirectStrategy(new LaxRedirectStrategy())
      .setDefaultRequestConfig(requestConfig)
      .build();
  }

  private static void logRequest(final HttpRequestBase request) {
    LOGGER.info("executing request " + request.getRequestLine());
  }

  private static void logResponse(final HttpResponse response) {
    String log = response.getStatusLine().toString();
    if (REDIRECT_CODES.contains(response.getStatusLine().getStatusCode())) {
      log += " (Location: " + response.getFirstHeader("Location").getValue() + ")";
    }
    LOGGER.info(log);
  }

  private static void sleep() {
    Random random = new Random();
    int value = random.nextInt(5000) + 1;
    LOGGER.info("sleep a little (" + value + "ms)");
    try {
      Thread.sleep(value);
    } catch (Exception e) {}
  }

  public void login(final String username, final String password) throws Exception {
    final HttpPost httpPost = new HttpPost("https://www.boursorama.com/connexion/");
    final List<NameValuePair> postParams = new ArrayList<NameValuePair>(0);
    postParams.add(new BasicNameValuePair("login_member[login]", username));
    postParams.add(new BasicNameValuePair("login_member[password]", password));
    httpPost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
    logRequest(httpPost);
    final HttpResponse response = this.httpClient.execute(httpPost);
    final HttpEntity entity = response.getEntity();
    logResponse(response);
    EntityUtils.consume(entity);
  }

  public void logout() throws Exception {
    DataSourceBoursorama.sleep();
    final HttpGet httpGet = new HttpGet("https://www.boursorama.com/se-deconnecter");
    logRequest(httpGet);
    try {
      this.httpClient.execute(httpGet);
    } catch (Exception e) {}
    this.httpClient.close();
  }

  public Map<String, Map<Date, Quotation>> load(int nbWeeks) throws Exception {
    final Map<String, Map<Date, Quotation>> quotations = new TreeMap<String, Map<Date, Quotation>>();
    final Calendar calendar = Calendar.getInstance();
    if (calendar.get(Calendar.DAY_OF_WEEK) < Calendar.FRIDAY || (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) < 19)) {
      calendar.add(Calendar.WEEK_OF_MONTH, -1);
    }
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    Date date = calendar.getTime();
    final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    for (int i = 0; i < nbWeeks; ++i) {
      DataSourceBoursorama.sleep();
      calendar.setTime(date);
      calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
      final Date date1 = date;
      final Date date2 = calendar.getTime();
      HttpPost httpPost = null;
      final List<NameValuePair> postParams = new ArrayList<NameValuePair>(0);
      if (this.type == DataSourceBoursoramaType.LIST) {
        httpPost = new HttpPost("https://www.boursorama.com/espace-membres/telecharger-cours/listes");
        postParams.add(new BasicNameValuePair("quote_search[type]", "list"));
        postParams.add(new BasicNameValuePair("quote_search[index]", "LISTE"));
        postParams.add(new BasicNameValuePair("quote_search[method]", "mnemonic"));
        postParams.add(new BasicNameValuePair("quote_search[fileFormat]", "LIBRE"));
        postParams.add(new BasicNameValuePair("quote_search[decimalFormat]", "POINT"));
        postParams.add(new BasicNameValuePair("quote_search[startDate]", dateFormat.format(date1)));
        postParams.add(new BasicNameValuePair("quote_search[endDate]", dateFormat.format(date2)));
        postParams.add(new BasicNameValuePair("quote_search[code]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[label]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[date]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[high]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[low]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[close]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[currency]", "1"));
        postParams.add(new BasicNameValuePair("989507546", "Télécharger"));
      } else if (this.type == DataSourceBoursoramaType.SBF_120) {
        httpPost = new HttpPost("https://www.boursorama.com/espace-membres/telecharger-cours/paris");
        postParams.add(new BasicNameValuePair("quote_search[type]", "index"));
        postParams.add(new BasicNameValuePair("quote_search[index]", "1rPPX4"));
        postParams.add(new BasicNameValuePair("quote_search[method]", "mnemonic"));
        postParams.add(new BasicNameValuePair("quote_search[fileFormat]", "LIBRE"));
        postParams.add(new BasicNameValuePair("quote_search[decimalFormat]", "POINT"));
        postParams.add(new BasicNameValuePair("quote_search[startDate]", dateFormat.format(date1)));
        postParams.add(new BasicNameValuePair("quote_search[endDate]", dateFormat.format(date2)));
        postParams.add(new BasicNameValuePair("quote_search[code]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[label]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[date]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[high]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[low]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[close]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[currency]", "1"));
        postParams.add(new BasicNameValuePair("989507546", "Télécharger"));
      } else if (this.type == DataSourceBoursoramaType.UK) {
        httpPost = new HttpPost("https://www.boursorama.com/espace-membres/telecharger-cours/international");
        postParams.add(new BasicNameValuePair("quote_search[type]", "index"));
        postParams.add(new BasicNameValuePair("quote_search[index]", "UKX.L"));
        postParams.add(new BasicNameValuePair("quote_search[method]", "mnemonic"));
        postParams.add(new BasicNameValuePair("quote_search[fileFormat]", "LIBRE"));
        postParams.add(new BasicNameValuePair("quote_search[decimalFormat]", "POINT"));
        postParams.add(new BasicNameValuePair("quote_search[startDate]", dateFormat.format(date1)));
        postParams.add(new BasicNameValuePair("quote_search[endDate]", dateFormat.format(date2)));
        postParams.add(new BasicNameValuePair("quote_search[code]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[label]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[date]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[high]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[low]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[close]", "1"));
        postParams.add(new BasicNameValuePair("quote_search[currency]", "1"));
        postParams.add(new BasicNameValuePair("989507546", "Télécharger"));
      }
      httpPost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
      logRequest(httpPost);
      final HttpResponse response = this.httpClient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      logResponse(response);
      if (entity != null) {
        final StringWriter writer = new StringWriter();
        IOUtils.copy(entity.getContent(), writer, "UTF-8");
        final String content = writer.toString();
        LOGGER.info("Response content:\n" + content);
        final String[] lines = content.split("\r\n");
        for (int j = 1; j < lines.length; ++j) {
          String[] values = lines[j].split("\t");
          if (values.length >= 6) {
            final String companyCode = values[0];
            final String companyLabel = values[1];
            Date currentDate = dateFormat.parse(values[2]);
            double high = Double.parseDouble(values[3]);
            double low = Double.parseDouble(values[4]);
            double close = Double.parseDouble(values[5]);
            if (!quotations.containsKey(companyCode)) {
              quotations.put(companyCode, new TreeMap<Date, Quotation>());
            }
            final Map<Date, Quotation> map = quotations.get(companyCode);
            final Quotation quotation = new Quotation(companyCode, companyLabel, low, high, close);
            map.put(currentDate, quotation);
          }
        }
      }
      EntityUtils.consume(entity);

      calendar.add(Calendar.WEEK_OF_MONTH, -1);
      calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
      date = calendar.getTime();
    }

    return quotations;
  }

}
