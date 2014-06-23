package no.spid.examples;

import no.spid.api.client.SpidApiClient;
import no.spid.api.client.SpidApiResponse;
import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This is an example webapp implementing a little online shop using SPiD
 * Paylinks to charge its customers.
 */
@Controller
@EnableAutoConfiguration
public class PaylinksController {
    private SpidApiClient spidClient;

    /** The entirety of our product catalog right here */
    private static Map<String, Product> products = new HashMap<String, Product>(){{
            put("sw4", new Product("Star Wars IV", 9900, 2400));
            put("sw5", new Product("Star Wars V", 9900, 2400));
            put("sw6", new Product("Star Wars VI", 9900, 2400));
        }};
    /**/

    /** Order status codes */
    private static Map<String, String> orderStatus = new HashMap<String, String>(){{
            put("-3", "Expired");
            put("-2", "Cancelled");
            put("-1", "Failed");
            put("0", "Created");
            put("1", "Pending");
            put("2", "Complete");
            put("3", "Credited");
            put("4", "Authorized");
        }};
    /**/

    private static Gson gson = new Gson();

    private String ourBaseUrl;

    public PaylinksController() throws IOException {
        // The client itself is immutable and can safely be shared in a multithreaded environment
        /** Create SPiD client */
        Properties prop = loadProperties("config.properties");
        spidClient = new SpidApiClient.ClientBuilder(
                                                     prop.getProperty("clientId"),
                                                     prop.getProperty("clientSecret"),
                                                     prop.getProperty("clientSignatureSecret"),
                                                     prop.getProperty("ourBaseUrl"),
                                                     prop.getProperty("spidBaseUrl")).build();
        /**/
        ourBaseUrl = prop.getProperty("ourBaseUrl");
    }

    @RequestMapping("/")
    String index() {
        return "index";
    }

    /** Create Paylink and redirect to SPiD */
    @RequestMapping("/checkout")
    String checkout(@RequestParam Map<String, String> params) throws SpidOAuthException, SpidApiException {
        JSONObject paylink = createPaylink(getPaylinkItems(params));
        return "redirect:" + paylink.get("shortUrl");
    }
    /**/

    @RequestMapping("/success")
    @ResponseBody
    String success(@RequestParam String orderId, HttpServletRequest request) throws SpidOAuthException, SpidApiException {
        JSONObject user = (JSONObject)request.getSession().getAttribute("userInfo");
        JSONObject order = getOrder(orderId);

        return "<h1>Welcome back, " + user.getString("displayName") + "!</h1>" +
            "<p>" +
            order.get("clientReference") +
            " is " +
            "<strong>" + orderStatus.get(order.get("status")) + "</strong>" +
            "</p>";
    }

    /** Handle callback from SPiD, make sure we've got the right user */
    @RequestMapping("/callback")
    String callback(@RequestParam String code,
                    @RequestParam String order_id,
                    HttpServletRequest request) throws SpidOAuthException, SpidApiException {
        // Retrieve this user's access token
        SpidOAuthToken token = spidClient.getUserToken(code);
        // Use the access token to get info about the user
        SpidApiResponse response = spidClient.GET(token, "/me", null);
        JSONObject user = response.getJsonData();

        // Save token and info in session
        request.getSession().setAttribute("userToken", token);
        request.getSession().setAttribute("userInfo", user);

        return "redirect:/success?orderId=" + order_id;
    }
    /**/

    @RequestMapping("/cancel")
    @ResponseBody
    String cancel(@RequestParam String spid_page) {
        return "<h1>Cancelled</h1>" +
            "<p>You left at " +
            "<strong>" + spid_page + "</strong>." +
            "</p>";
    }

    /** Fetch order info */
    private JSONObject getOrder(String orderId) throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = spidClient.getServerToken();
        SpidApiResponse response = spidClient.GET(token, "/order/" + orderId + "/status", null);
        return response.getJsonData();
    }
    /**/

    /** Create Paylink */
    private JSONObject createPaylink(List<PaylinkItem> items) throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = spidClient.getServerToken();
        SpidApiResponse response = spidClient.POST(token, "/paylink", createPaylinkData(items));
        return response.getJsonData();
    }
    /**/

    /** Create data to POST to /paylink */
    private Map createPaylinkData(List<PaylinkItem> items) {
        Map data = new HashMap();
        data.put("title", "Quality movies");
        data.put("redirectUri", ourBaseUrl + "/callback");
        data.put("cancelUri", ourBaseUrl + "/cancel");
        data.put("clientReference", "Order number " + System.currentTimeMillis());
        data.put("items", gson.toJson(items));
        return data;
    }

    private List<PaylinkItem> getPaylinkItems(Map<String, String> params) {
        List<PaylinkItem> items = new ArrayList<PaylinkItem>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            int quantity = Integer.parseInt(entry.getValue());
            Product p = products.get(entry.getKey());
            if (quantity > 0 && p != null) {
                items.add(new PaylinkItem(p, quantity));
            }
        }
        return items;
    }
    /**/

    private Properties loadProperties(String filename) throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            throw new FileNotFoundException(filename);
        }
        prop.load(input);
        return prop;
    }

    public static void main(String[] args) {
        SpringApplication.run(PaylinksController.class, args);
    }
}
