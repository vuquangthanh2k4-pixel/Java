
package Database;

import com.mysql.cj.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDatabase {
    public static Connection Connection(){
        Connection con = null;
        try{
            DriverManager.registerDriver(new Driver());
            String url = "jdbc:mySQL://127.0.0.1:3306/ThuVien";
            String username = "root";
            String password = "thanh2k5";
            con = DriverManager.getConnection(url, username, password);
        }catch(Exception ex){
            System.out.println("Lỗi kết nối" + ex.getMessage());
        }
        return con;
    }
    public static void closeConnection(Connection con){
        try{
            if(con != null){
                con.close();
            }
        }catch(Exception e){
            System.out.println("Không thể đóng kết nối" + e.getMessage());
        }
    }
}
