package no.spid.examples;
/** Getting started */
import no.spid.api.client.SpidApiClient;
import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;

public class GettingStarted {

    public static void main(String[] args) {
        String clientId = args[0];
        String secret = args[1];
        String signatureSecret = "";
        String redirectUrl = "http://localhost:8080";
        String spidBaseUrl = "https://stage.payment.schibsted.no";

        try {
            SpidApiClient client = new SpidApiClient.ClientBuilder(
                    clientId,
                    secret,
                    signatureSecret,
                    redirectUrl,
                    spidBaseUrl
            ).build();

            SpidOAuthToken token = client.getServerToken();
            String responseJSON = client.GET(token, "/endpoints", null).getRawData();
            System.out.println(responseJSON);
        } catch (SpidOAuthException e) {
            e.printStackTrace();
        } catch (SpidApiException e) {
            e.printStackTrace();
        }
    }
}
/**/