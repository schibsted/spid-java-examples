package no.spid.examples;

import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;
import no.spid.api.client.SpidApiResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Controller
public class ShopController extends BaseController {
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

    /** Payment identifier types */
    private static Map<String, String> paymentIdentifierType = new HashMap<String, String>(){{
            put("2", "Credit card");
            put("4", "SMS");
            put("8", "PayEx Invoice");
            put("16", "Voucher");
            put("32", "Klarna Invoice");
        }};
    /**/

    private static Gson gson = new Gson();

    @RequestMapping("/")
    String index(HttpServletRequest request, Map<String, Object> model) {
        JSONObject user = (JSONObject) request.getSession().getAttribute("userInfo");

        if (user == null) {
            return "redirect:/login";
        } else {
            model.put("userName", user.getString("displayName"));
            return "index";
        }
    }

    @RequestMapping("/success")
    String success(@RequestParam String order_id,
                   Map<String, Object> model) throws SpidOAuthException, SpidApiException {
        populateOrderModel(getOrder(order_id), model);
        return "receipt";
    }

    /** Preparing order data for the receipt view */
    private void populateOrderModel(JSONObject order, Map<String, Object> model) {
        model.put("clientReference", order.get("clientReference"));
        model.put("status", orderStatus.get(order.getString("status")));
        model.put("currency", order.get("currency"));
        model.put("capturedAmount", order.getInt("capturedAmount") / 100);
        model.put("paymentIdentifierType",
                  paymentIdentifierType.get(order.getString("identifierType")));
    }
    /**/

    /** Attempting the direct payment, with a Paylink fallback */
    @RequestMapping("/checkout")
    String checkout(HttpServletRequest request,
                    @RequestParam Map<String, String> params,
                    Map<String, Object> model) throws SpidOAuthException, SpidApiException {
        JSONObject user = (JSONObject) request.getSession().getAttribute("userInfo");

        try {
            populateOrderModel(chargeOrder(user, getOrderItems(params)), model);
            return "receipt";
        } catch (SpidApiException err) {
            JSONObject paylink = createPaylink(getPaylinkItems(params));
            return "redirect:" + paylink.get("shortUrl");
        }
    }
    /**/

    /** Fetch order info */
    private JSONObject getOrder(String orderId) throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = getSpidClient().getServerToken();
        SpidApiResponse response = getSpidClient().GET(token, "/order/" + orderId + "/status", null);
        return response.getJsonData();
    }
    /**/

    /** Create Paylink */
    private JSONObject createPaylink(List<PaylinkItem> items) throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = getSpidClient().getServerToken();
        SpidApiResponse response = getSpidClient().POST(token, "/paylink", createPaylinkData(items));
        return response.getJsonData();
    }
    /**/

    /** Create data to POST to /paylink */
    private Map createPaylinkData(List<PaylinkItem> items) {
        Map data = new HashMap();
        data.put("title", "Quality movies");
        data.put("redirectUri", getOurBaseUrl() + "/success");
        data.put("cancelUri", getOurBaseUrl() + "/cancel");
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

    private JSONObject chargeOrder(JSONObject user, List<OrderItem> items) throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = getSpidClient().getServerToken();
        SpidApiResponse response = getSpidClient().POST(token,
                                                        "/user/" + user.get("userId") + "/charge",
                                                        createOrderData(items));
        return response.getJsonData();
    }

    /** Create data to POST to /user/{userId}/charge */
    private Map createOrderData(List<OrderItem> items) throws SpidApiException {
        Map<String, String> data = new HashMap<String, String>();
        data.put("requestReference", "Order #" + System.currentTimeMillis());
        data.put("items", gson.toJson(items));
        signParams(data);
        return data;
    }

    private List<OrderItem> getOrderItems(Map<String, String> params) {
        List<OrderItem> items = new ArrayList<OrderItem>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            int quantity = Integer.parseInt(entry.getValue());
            Product p = products.get(entry.getKey());
            if (quantity > 0 && p != null) {
                items.add(new OrderItem(p, quantity));
            }
        }
        return items;
    }
    /**/
}
