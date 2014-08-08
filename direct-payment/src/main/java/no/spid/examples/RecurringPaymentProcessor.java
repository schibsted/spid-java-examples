package no.spid.examples;

import no.spid.examples.User;
import no.spid.examples.Subscription;
import no.spid.examples.ApiClient;

import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;
import no.spid.api.client.SpidApiResponse;

import org.json.JSONObject;
import com.google.gson.Gson;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class RecurringPaymentProcessor {
    final private Subscription subscription;
    final private ApiClient client;

    public RecurringPaymentProcessor(Subscription subscription) {
        this.subscription = subscription;
        ApiClient client = null;

        try {
            client = ApiClient.fromProperties("config.properties");
        } catch (IOException e) {
            System.err.println("Failed to load configuration " + e.getMessage());
            System.exit(1);
        }

        this.client = client;
    }

    private static Gson gson = new Gson();

    public JSONObject chargeSubscriber(User user)
        throws SpidApiException, SpidOAuthException {
        Map order = createOrderData(subscription);
        String url = "/user/" + user.userId + "/charge";
        return client.POST(url, order).getJsonData();
    }

    /** Create data to POST to /user/{userId}/charge */
    private Map createOrderData(final Subscription subscription) throws SpidApiException {
        Map<String, String> data = new HashMap<String, String>();
        data.put("requestReference", "Order #" + System.currentTimeMillis());
        List<Subscription> items = new ArrayList<Subscription>();
        items.add(subscription);
        data.put("items", gson.toJson(items));
        client.signParams(data);
        return data;
    }
    /**/

    public static void main(String[] args) {
        List<User> users = new ArrayList() {{ add(new User(238342)); }};
        Subscription subscription = new Subscription("Ants Monthly", 9900, 2400);
        RecurringPaymentProcessor processor = new RecurringPaymentProcessor(subscription);

        for (User user: users) {
            try {
                JSONObject order = processor.chargeSubscriber(user);
                System.out.println(OrderFormatter.format(order));
            } catch (SpidApiException | SpidOAuthException e) {
                System.out.println("Failed to charge user ID " + user.userId + ":");
                System.out.println("    " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
