package no.spid.examples;
/** Getting started */
import no.spid.api.client.SpidApiClient;
import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;

import org.apache.commons.codec.binary.Base64;

public class GettingStarted {

    public static void main(String[] args) {
        final String clientId = args[0];
        final String secret = args[1];
        final String signatureSecret = "";
        final String redirectUrl = "http://localhost:8080";
        final String spidBaseUrl = "https://stage.payment.schibsted.no";

        SpidApiClient client = new SpidApiClient.ClientBuilder(
                clientId,
                secret,
                signatureSecret,
                redirectUrl,
                spidBaseUrl
        ).build();

        try {
         SpidOAuthToken token = client.getServerToken();
            final String email = "some.email@google.com";   // check to see if this email exists
            String base64EncodedEmail = new String(Base64.encodeBase64(email.getBytes()));
            String responseJSON = client.GET(token, "/email/" + base64EncodedEmail + "/status", null).getRawData();
            System.out.println(responseJSON);
        } catch (SpidOAuthException e) {
            e.printStackTrace();
        } catch (SpidApiException e) {
            e.printStackTrace();
        }
    }
}
/**/