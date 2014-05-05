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

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is an example webapp where you can log in and log out.
 * When logged the user information from SPiD(/me) will be displayed.
 */
@Controller
@EnableAutoConfiguration
public class LoginController {
    private SpidApiClient spidClient;

    public LoginController() throws IOException {
        // Create a spid API client, the client itself is immutable and can safely be shared in a multithreaded environment
        Properties prop = loadProperties("config.properties");
        spidClient = new SpidApiClient.ClientBuilder(
                prop.getProperty("clientId"),
                prop.getProperty("clientSecret"),
                prop.getProperty("clientSignatureSecret"),
                prop.getProperty("ourBaseUrl"),
                prop.getProperty("spidBaseUrl")).build();
    }

    @RequestMapping("/")
    @ResponseBody
    String index(HttpServletRequest request) throws SpidOAuthException, SpidApiException {
        SpidOAuthToken token = (SpidOAuthToken)request.getSession().getAttribute("userToken");

        if ( token != null) {
            // Use the access token to get info about the user
            SpidApiResponse response = spidClient.GET(token, "/me", null);
            JSONObject data = response.getJsonData();

            return "Hello " + data.getString("displayName") + ". Want to log out? <a href=\"/logout\">Click here!</a>";
        } else {
            // Create a login URL
            String loginUrl = spidClient.getAuthorizationURL("http://localhost:8080/login");
            return "<a href=\"" + loginUrl + "\">Click here to login with SPiD</a>";
        }
    }

    @RequestMapping("/login")
    String login( @RequestParam String code, HttpServletRequest request) throws SpidOAuthException {
        // Retrieve this users access token
        SpidOAuthToken token = spidClient.getUserToken(code);
        // Save it in session
        request.getSession().setAttribute("userToken", token);

        return "redirect:/";
    }

    /** Log user out */
    @RequestMapping("/logout")
    String logout( HttpServletRequest request) throws SpidOAuthException {
        SpidOAuthToken token = (SpidOAuthToken)request.getSession().getAttribute("userToken");
        String logoutURL = spidClient.getLogoutURL( token, "http://localhost:8080");

        request.getSession().removeAttribute("userToken");

        return "redirect:" + logoutURL;
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
        SpringApplication.run(LoginController.class, args);
    }
}
