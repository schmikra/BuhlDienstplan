package me.sschmidt.buhldienstplan;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sebastian on 08.07.2014.
 */
public class Parser {

    static String loginUrl = "http://old.buhl.de/support/_themes/dienstplan/default.asp";
    static String dienstplanUrl = "http://old.buhl.de/support/_themes/dienstplan/dienstplan.asp";
    static String gibMitarbeiterUrl = "http://old.buhl.de/support/_themes/dienstplan/dienstplanajax.asp?app=gibMitarbeiterX";
    static String liesMitarbeiterUrl = "http://old.buhl.de/support/_themes/dienstplan/dienstplanajax.asp?app=liesMitarbeiterX&val=";

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    private static List<Cookie> cookies;

    public static String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String username;
    private String password;
    private static String uId;

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static String name;

    private static String firstName;

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Parser.lastName = lastName;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Parser.firstName = firstName;
    }

    private static String lastName;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    private String refererUrl;

    public Parser(String username, String password, Context context) {
        setUsername(username);
        setPassword(password);
        setContext(context);
    }

    public boolean login() {

        setCookies(null);

        AQuery aq = new AQuery(getContext());

        Map<String, Object> loginParams = new HashMap<String, Object>();
        loginParams.put("Name", getUsername());
        loginParams.put("Password", getPassword());
        loginParams.put("submit1", "Login");

        AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
            public void callback(String url, String json, AjaxStatus status) {
                setCookies(status.getCookies());
            }
        };

        //aq.ajax(loginUrl, loginParams, String.class, ajaxCallback);
        ajaxCallback.type(String.class);
        ajaxCallback.url(loginUrl);
        ajaxCallback.params(loginParams);

        aq.sync(ajaxCallback);

        String result = ajaxCallback.getStatus().getMessage().toString();
        String content = ajaxCallback.getResult();

        if(result.equals("OK") && !content.contains("<TITLE>Login</TITLE>")) {
            return true;
        } else {
            return false;
        }
    }

    private void getDienstplanPage() {
        AQuery aq = new AQuery(getContext());

        AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {

            public void callback(String url, String json, AjaxStatus status) {
                getUserId(json);
            }
        };

        for (Cookie cookie : getCookies()) {
            ajaxCallback.cookie(cookie.getName(), cookie.getValue());
        }

        ajaxCallback.url(dienstplanUrl);
        ajaxCallback.type(String.class);

        aq.sync(ajaxCallback);
    }

    private void getUserId(String json) {
        int positionOfUId = json.indexOf("Benutzer mit ID: ");
        String uId = json.substring(positionOfUId + 17, positionOfUId + 22);
        setuId(uId);
    }

    public boolean getMitarbeiterName() {
        // load Dienstplan to get the uId
        getDienstplanPage();

        AQuery aq = new AQuery(getContext());

        AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {

            public void callback(String url, String json, AjaxStatus status) {
                int endOfName = json.indexOf("<br />");
                String name = json.substring(0, endOfName);
                setName(name);
                String[] separated = name.split(" ");
                setFirstName(separated[0]);
                setLastName(separated[1].trim());
            }
        };

        for (Cookie cookie : getCookies()) {
            ajaxCallback.cookie(cookie.getName(), cookie.getValue());
        }

        ajaxCallback.url(liesMitarbeiterUrl + getuId());
        ajaxCallback.type(String.class);

        aq.sync(ajaxCallback);

        String result = ajaxCallback.getStatus().getMessage().toString();

        if(result.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    public void getWorkingHoursByDate() {
        AQuery aq = new AQuery(getContext());

        AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {

            public void callback(String url, String json, AjaxStatus status) {
                int endOfName = json.indexOf("<br />");
                String name = json.substring(0, endOfName);
                setName(name);
                String[] separated = name.split(" ");
                setFirstName(separated[0]);
                setLastName(separated[1].trim());
            }
        };

        for (Cookie cookie : getCookies()) {
            ajaxCallback.cookie(cookie.getName(), cookie.getValue());
        }

        // val=02.07.2014&val2=1

        ajaxCallback.url(liesMitarbeiterUrl + getuId());
        ajaxCallback.type(String.class);

        aq.sync(ajaxCallback);
    }
}
