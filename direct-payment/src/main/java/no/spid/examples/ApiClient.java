package no.spid.examples;

import no.spid.api.client.SpidApiClient;
import no.spid.api.client.SpidApiResponse;
import no.spid.api.security.SpidSecurityHelper;
import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map;

public class ApiClient {
    final private SpidApiClient client;
    final private SpidSecurityHelper security;

    public ApiClient(SpidApiClient client, SpidSecurityHelper security) {
        this.client = client;
        this.security = security;
    }

    public static ApiClient fromProperties(String propertiesFileName) throws IOException {
        Properties prop = loadProperties(propertiesFileName);
        SpidApiClient spidClient = new SpidApiClient.ClientBuilder(
            prop.getProperty("clientId"),
            prop.getProperty("clientSecret"),
            prop.getProperty("clientSignatureSecret"),
            prop.getProperty("ourBaseUrl"),
            prop.getProperty("spidBaseUrl")).build();
        /** Creating the security helper */
        SpidSecurityHelper security = new SpidSecurityHelper(prop.getProperty("clientSignatureSecret"));
        /**/
        return new ApiClient(spidClient, security);
    }

    /** Signing parameters */
    public void signParams(Map<String, String> params) throws SpidApiException {
        security.addHash(params);
    }
    /**/

    public SpidApiResponse POST(String path, Map<String, String> params)
        throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = client.getServerToken();
        return client.POST(token, path, params);
    }

    private static Properties loadProperties(String filename) throws IOException {
        Properties prop = new Properties();
        InputStream input = ApiClient.class.getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            throw new FileNotFoundException(filename);
        }
        prop.load(input);
        return prop;
    }
}
