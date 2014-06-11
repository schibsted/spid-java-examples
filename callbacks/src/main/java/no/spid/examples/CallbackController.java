package no.spid.examples;

import no.spid.api.exceptions.SpidApiException;
import no.spid.api.security.SpidSecurityHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@EnableAutoConfiguration
public class CallbackController {
    SpidSecurityHelper securityHelper = new SpidSecurityHelper("payment");

    @ResponseBody
    @RequestMapping("/callback")
    public String handleCallback(@RequestBody String body, HttpServletRequest request) {
        String data;
        try {
            data = securityHelper.decryptAndValidateSignedRequest(body);
        } catch (SpidApiException e) {
            return "Handling of callback failed: " + e.getMessage();
        }
        return "Callback handled!<br>" + data;
    }

    @ResponseBody
    @RequestMapping("/")
    public String index() {

        // Html snippet that sends a text/plain http post to our callback method and
        // displays the result in an alert box
        return "<form><input type=\"submit\" /></form>\n" +
                "<script type=\"text/javascript\">\n" +
                "var form = document.getElementsByTagName(\"form\")[0];\n" +
                "form.addEventListener(\"submit\", function (e) {\n" +
                "    e.preventDefault();\n" +
                "    var req = new XMLHttpRequest();\n" +
                "    req.open(\"POST\", \"/callback\");\n" +
                "    req.onreadystatechange = function () {\n" +
                "        if (this.readyState === 4) {\n" +
                "            alert(this.status + \" - \" + this.responseText);\n" +
                "        }\n" +
                "    };\n" +
                "    req.send(\"enyHpdFvtWru_aoeXMlftGdbHjSO01M_No3hSUZHYf0.eyJvYmplY3QiOiJvcmRlciIsImVudHJ5IjpbeyJvcmRlcklkIjoiMTYzMTE4NyIsImNoYW5nZWRGaWVsZHMiOiJzdGF0dXMiLCJ0aW1lIjoiMjAxNC0wMi0xOCAxNDozMzozNyJ9XSwiYWxnb3JpdGhtIjoiSE1BQy1TSEEyNTYifQ\");\n" +
                "});\n" +
                "</script>\n";
    }

    public static void main(String[] args) {
        SpringApplication.run(CallbackController.class, args);
    }
}
