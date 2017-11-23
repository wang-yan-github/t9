package t9.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class SignProvider {

    public static String id = null;
    public static String userCount = null;
    public static String registerDate = null;
    public static String expiresDate = null;
    public static String type = null;

    public static String softName = null;
    public static String softVersion = null;
    public static String softUnit = null;
    public static String softNet = null;
    public static String useUnit = null;

    public static String domain = null;

    public static boolean verify(String keyPath, String pubKeyName, String licenseName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(keyPath + File.separator
                    + pubKeyName));
            PublicKey pubKey = (PublicKey) in.readObject();
            in.close();
            in = new ObjectInputStream(new FileInputStream(keyPath + File.separator + licenseName));
            String info = (String) in.readObject();
            byte[] signed = (byte[]) in.readObject();
            in.close();
            java.security.Signature signetcheck = java.security.Signature.getInstance("MD5withRSA");
            signetcheck.initVerify(pubKey);
            signetcheck.update(info.getBytes("UTF-8"));
            if (signetcheck.verify(signed)) {
                // System.out.println("签名正常");
                parseStrToXML(info);
                return true;
            }
        } catch (Throwable e) {
            // System.out.println("校验签名失败");
            // e.printStackTrace();
        }
        return false;
    }

    public static boolean checkExpireValidDate() {
        try {
            if (SignProvider.expiresDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date expiresDate = sdf.parse(SignProvider.expiresDate + " 23:59:59");
                if (expiresDate.getTime() > new Date().getTime()) {
                    return true;
                }
            }
        } catch (Throwable e) {

        }
        return false;
    }

    public static boolean checkTrailVersion() {
        try {
            if (SignProvider.type.equals("trail")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date registerDate = sdf.parse(SignProvider.registerDate + " 23:59:59");
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(registerDate);
                calendar.add(calendar.DATE, 30);
                registerDate = calendar.getTime();
                if (registerDate.getTime() > new Date().getTime()) {
                    return true;
                }
            }
        } catch (Throwable e) {

        }
        return false;
    }

    public static long validDays() {
        try {
            if (SignProvider.type.equals("trail")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date registerDate = sdf.parse(SignProvider.registerDate + " 23:59:59");
                long day = (new Date().getTime() - registerDate.getTime()) / (24 * 60 * 60 * 1000);// 已使用天数
                if (day > 0) {
                    if (30 - day > 0) {
                        return 30 - day;
                    }
                }
            }
        } catch (Throwable e) {
        }
        return 0;
    }

    public static boolean validDomain(HttpServletRequest request) {
        try {
            if (SignProvider.type != null
                    && (SignProvider.type.equals("oem") || SignProvider.type.equals("trail"))) {
                String uri = request.getRequestURL().toString();
                if (SignProvider.domain != null) {
                    if (SignProvider.domain.equals("*")) {
                        return true;
                    }
                    String[] str = SignProvider.domain.split(",");
                    for (String s : str) {
                        if (uri.startsWith("http://" + s) || uri.startsWith("http://www." + s)
                                || uri.startsWith("https://" + s) || uri.startsWith("https://www." + s)) {
                            return true;
                        }
                    }
                }
            } else if (SignProvider.type != null && SignProvider.type.equals("free")) {
                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date registerDate;
        try {
            registerDate = sdf.parse("2017-02-01" + " 23:59:59");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(registerDate);
            calendar.add(calendar.DATE, 30);
            registerDate = calendar.getTime();
            System.out.println(sdf.format(registerDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void parseStrToXML(String paramString) {
        SAXBuilder localSAXBuilder = new SAXBuilder();
        try {
            StringReader localStringReader = new StringReader(paramString);
            Document localDocument = localSAXBuilder.build(localStringReader);
            localStringReader.close();
            Element e = localDocument.getRootElement();
            id = e.getChild("id").getText();
            userCount = e.getChild("userCount").getText();
            registerDate = e.getChild("registerDate").getText();
            expiresDate = e.getChild("expiresDate").getText();
            type = e.getChild("type").getText();

            softName = e.getChild("softName").getText();
            softVersion = e.getChild("softVersion").getText();
            softUnit = e.getChild("softUnit").getText();
            softNet = e.getChild("softNet").getText();
            useUnit = e.getChild("useUnit").getText();

            domain = e.getChild("domain").getText();
        } catch (JDOMException localJDOMException) {
            System.out.println("init:" + localJDOMException.getMessage());
        } catch (IOException localIOException) {
            System.out.println("init:" + localIOException.getMessage());
        }
    }
}
