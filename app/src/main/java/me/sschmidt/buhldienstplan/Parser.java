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
    static String gibMitarbeiterUrl = "http://old.buhl.de/support/_themes/dienstplan/dienstplanajax.asp?app=gibMitarbeiterX&val=02.07.2014&val2=1&gruppe=FINANZ";
    static String liesMitarbeiterUrl = "http://old.buhl.de/support/_themes/dienstplan/dienstplanajax.asp?app=liesMitarbeiterX&val=1455";

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    private static List<Cookie> cookies;

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
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
    private int uId;

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static String name;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public Parser(String username, String password, Context context) {
        setUsername(username);
        setPassword(password);
        setContext(context);
    }

    public boolean login() {
       AQuery aq = new AQuery(getContext());

        Map<String, Object> loginParams = new HashMap<String, Object>();
        loginParams.put("Name", this.username);
        loginParams.put("Password", this.password);
        loginParams.put("submit1", "Login");

        aq.ajax(loginUrl, loginParams, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {
                setCookies(status.getCookies()); // We are saving the cookies in our variable
            }
        });

        return true;
    }

    public void getMitarbeiterName() {
        AQuery aq = new AQuery(getContext());

        AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {

            public void callback(String url2, String json, AjaxStatus status) {
                setName(json.toString());
            }
        };

        for (Cookie cookie : getCookies()) {
            ajaxCallback.cookie(cookie.getName(), cookie.getValue());
        }

        aq.ajax(liesMitarbeiterUrl, String.class, ajaxCallback);
    }

    public void test( ) {


        AQuery aq = new AQuery(getContext());

        AjaxCallback<String> ajaxCallbackTEST = new AjaxCallback<String>() {

            public void callback(String url2, String json, AjaxStatus status) {
               // TextView txt = (TextView) findViewById(R.id.textView);
               // txt.setText(json.toString());
            }
        };

        for (Cookie cookie : getCookies()) {
            //ajaxCallbackTEST.cookie(cookie.getName(), cookie.getValue());
            //Here we are setting the cookie info.
        }

        //aq.ajax(url2, String.class, ajaxCallbackTEST);
    }
}