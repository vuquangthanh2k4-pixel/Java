
package DAO;

import java.sql.Connection;
import Database.ConnectDatabase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {

    public TaiKhoanDAO() {
    }
    
    public  boolean CheckTaiKhoan(String taiKhoan, String matKhau){
        Connection con = null;
        ResultSet rs = null;
        try{
            con = ConnectDatabase.Connection();
            String query = "Select taikhoan, matkhau from taikhoan where LoaiTaiKhoan = ? and taikhoan = ? and matkhau = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, 1);
                ps.setString(2, taiKhoan);
                ps.setString(3, matKhau);
                rs = ps.executeQuery();
                if(rs.next()){
                    return true;
                }
            }
        }
        catch(SQLException ex){
            System.out.println("Lỗi sql khi kiểm tra dữ liệu: " + ex.getMessage());
        }
        catch(Exception ex){
            System.out.println("Không thể lấy nên tài khoản mật khẩu:" + ex.getMessage());
        }
        finally{
            try{
                con.close();
            }
            catch(SQLException ex){
                System.out.println("Lỗi đóng kết nối: " + ex.getMessage());
            }
        }
        return false;
    }
}
