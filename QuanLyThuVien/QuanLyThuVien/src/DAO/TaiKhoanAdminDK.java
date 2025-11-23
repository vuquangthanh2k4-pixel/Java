/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import Database.ConnectDatabase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author My Computer
 */
public class TaiKhoanAdminDK {

    public TaiKhoanAdminDK() {
    }
    
    public void InsertTaiKhoanAdmin(String taiKhoanDk, String matKhauDk, String emailDk,String hoTen, String ngaySinh, String diaChi)throws SQLException, Exception{
        Connection con = null;
        String query = null;
        String queryAdmin = null;
        int Id = -1;
        try{
            con = ConnectDatabase.Connection();
            query = "INSERT INTO taikhoan(TaiKhoan, Matkhau, Email, LoaiTaiKhoan) values (?, ?, ?, ?)";
                       
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, taiKhoanDk);
            ps.setString(2, matKhauDk);
            ps.setString(3, emailDk);
            ps.setInt(4, 1);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                Id = rs.getInt(1);
            }
            queryAdmin = "INSERT INTO administrator(IdAdmin, HoTen, NgaySinh, DiaChi) VALUES (?, ?, ?, ?)";
            try(PreparedStatement psAdmin = con.prepareStatement(queryAdmin)){
                psAdmin.setInt(1, Id);
                psAdmin.setString(2, hoTen);
                psAdmin.setString(3, ngaySinh);
                psAdmin.setString(4, diaChi);
                psAdmin.executeUpdate();
            }
        }
        catch(SQLException ex){
            System.out.println("Lỗi sql khi kiểm tra dữ liệu: " + ex.getMessage());
        }
        catch(Exception ex){
            System.out.println("Không thể thêm tài khoản mật khẩu:" + ex.getMessage());
        }
        finally{
            try{
                con.close();
            }
            catch(SQLException ex){
                System.out.println("Lỗi đóng kết nối: " + ex.getMessage());
            }
        }
    }
    public boolean CheckDangKyTaiKhoanAdmin(String taiKhoan){
        Connection con = null;
        ResultSet rs = null;
        try{
            con = ConnectDatabase.Connection();
            String query = "Select taikhoan from taikhoan where LoaiTaiKhoan = ? and taikhoan = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, 1);
                ps.setString(2, taiKhoan);
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
            System.out.println("Không thể lấy nên tài khoản:" + ex.getMessage());
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
    
    public boolean CheckEmailDangKyAdmin(String email){
        Connection con = null;
        ResultSet rs = null;
        try{
            con = ConnectDatabase.Connection();
            String query = "Select Email from taikhoan where LoaiTaiKhoan = ? and email = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, 1);
                ps.setString(2, email);
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
            System.out.println("Không thể lấy nên email:" + ex.getMessage());
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
