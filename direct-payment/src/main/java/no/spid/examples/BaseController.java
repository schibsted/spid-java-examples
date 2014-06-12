package no.spid.examples;

import no.spid.api.client.SpidApiClient;
import no.spid.api.security.SpidSecurityHelper;
import no.spid.api.exceptions.SpidApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map;

public class BaseController {
    private SpidApiClient spidClient;
    private SpidSecurityHelper security;
    private String ourBaseUrl;

    public BaseController() {
        try {
            Properties prop = loadProperties("config.properties");
            spidClient = new SpidApiClient.ClientBuilder(
                    prop.getProperty("clientId"),
                    prop.getProperty("clientSecret"),
                    prop.getProperty("clientSignatureSecret"),
                    prop.getProperty("ourBaseUrl"),
                    prop.getProperty("spidBaseUrl")).build();
            /** Creating the security helper */
            security = new SpidSecurityHelper(prop.getProperty("clientSignatureSecret"));
            /**/
            ourBaseUrl = prop.getProperty("ourBaseUrl");
        } catch (IOException e) {
            System.err.println("Failed to load configuration " + e.getMessage());
            System.exit(1);
        }
    }

    public String getOurBaseUrl() {
        return ourBaseUrl;
    }

    /** Signing parameters */
    public void signParams(Map<String, String> params) throws SpidApiException {
        security.addHash(params);
    }
    /**/

    public SpidApiClient getSpidClient() {
        return spidClient;
    }

    private Properties loadProperties(String filename) throws IOException {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            throw new FileNotFoundException(filename);
        }
        prop.load(input);
        return prop;
    }
}
