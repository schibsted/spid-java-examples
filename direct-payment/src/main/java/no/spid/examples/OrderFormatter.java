package no.spid.examples;

import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;

public class OrderFormatter {
    /** Payment identifier types */
    private static Map<String, String> paymentIdentifierType = new HashMap<String, String>(){{
            put("2", "Credit card");
            put("4", "SMS");
            put("8", "PayEx Invoice");
            put("16", "Voucher");
            put("32", "Klarna Invoice");
        }};
    /**/

    public static String format(JSONObject order) {
        float capturedAmount = order.getInt("capturedAmount") / 100;

        return String.format(
            "User ID %s: Captured %s %.2f from %s",
            order.getString("userId"),
            order.getString("currency"),
            capturedAmount,
            paymentIdentifierType.get(order.getString("identifierType"))
        );
    }
}
