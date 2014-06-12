package no.spid.examples;

import no.spid.api.client.SpidApiResponse;
import no.spid.api.exceptions.SpidApiException;
import no.spid.api.exceptions.SpidOAuthException;
import no.spid.api.oauth.SpidOAuthToken;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController extends BaseController {
    @RequestMapping("/login")
    @ResponseBody
    String login(HttpServletRequest request) throws SpidOAuthException {
        String loginUrl = getSpidClient().getAuthorizationURL("http://localhost:8080/create-session");
        return "<a href=\"" + loginUrl + "\">Click here to login with SPiD</a>";
    }

    /** Fetch user information and add to session */
    @RequestMapping("/create-session")
    String createSession( @RequestParam String code, HttpServletRequest request) throws SpidOAuthException, SpidApiException {
        // Retrieve this user's access token
        SpidOAuthToken token = getSpidClient().getUserToken(code);
        // Use the access token to get info about the user
        SpidApiResponse response = getSpidClient().GET(token, "/me", null);
        JSONObject user = response.getJsonData();

        // Save token and info in session
        request.getSession().setAttribute("userToken", token);
        request.getSession().setAttribute("userInfo", user);

        return "redirect:/";
    }
    /**/

    /** Log user out */
    @RequestMapping("/logout")
    String logout( HttpServletRequest request) throws SpidOAuthException {
        SpidOAuthToken token = (SpidOAuthToken)request.getSession().getAttribute("userToken");
        String logoutURL = getSpidClient().getLogoutURL( token, "http://localhost:8080");

        request.getSession().removeAttribute("userToken");
        request.getSession().removeAttribute("userInfo");

        return "redirect:" + logoutURL;
    }
    /**/
}
