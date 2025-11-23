
package DAO;

import java.util.List;
import java.sql.Connection;
import Database.ConnectDatabase;
import Model.TacGiaModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetDataTacGiaDAO {

    public GetDataTacGiaDAO() {
    }
    
    public List<TacGiaModel> SelectDataTacGia() throws SQLException{
        Connection con = null;
        String query = null;
        List<TacGiaModel> list = new ArrayList<TacGiaModel>();
        try{
            con = ConnectDatabase.Connection();
            query = "select IdTacGia, TenTacGia, NgaySinh, DiaChi from TacGia";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int idTacGia = rs.getInt("IdTacGia");
                String tenTacGia = rs.getString("TenTacGia");
                String ngaySinh = rs.getString("NgaySinh");
                String diaChi = rs.getString("DiaChi");
                
                TacGiaModel model = new TacGiaModel();
                model.setIdTacGia(idTacGia);
                model.setTenTacGia(tenTacGia);
                model.setNgaySinh(ngaySinh);
                model.setDiaChi(diaChi);
                list.add(model);
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
        return list;
    }
    
    
    public void InsertData(String tenTacGia, String ngaySinh, String diaChi) throws SQLException{
        Connection con = null;
        String query = null;
        try{
            con = ConnectDatabase.Connection();
            query = "insert into tacgia(TenTacGia, NgaySinh, DiaChi) values (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, tenTacGia);
            ps.setString(2, ngaySinh);
            ps.setString(3, diaChi);
            ps.executeUpdate();
        }
        catch(SQLException ex){
            System.out.println("Lỗi sql khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }
        catch(Exception ex){
            System.out.println("Không thể lấy nên tài khoản:" + ex.getMessage());
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(SQLException ex){
                System.out.println("Lỗi đóng kết nối: " + ex.getMessage());
            }
        }
    }
    
    public boolean CheckTacGiaIsValid(String tenTacGia, String ngaySinh, String diaChi) throws SQLException {
        String query = "Select TenTacGia, NgaySinh, DiaChi from TacGia where TenTacGia = ? and NgaySinh = ? and DiaChi = ?";
        try (Connection con = ConnectDatabase.Connection()) {
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, tenTacGia);
                ps.setString(2, ngaySinh);
                ps.setString(3, diaChi);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }

        return false;
    }
    
    
    public void UpdateThongTinTacGia(String tenTacGia, String ngaySinh, String diaChi, int idTacGia) throws SQLException{
        try(Connection con = ConnectDatabase.Connection()){
            String query = "Update TacGia set TenTacGia = ?, NgaySinh = ?, DiaChi = ? where idTacGia = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, tenTacGia);
                ps.setString(2, ngaySinh);
                ps.setString(3, diaChi);
                ps.setInt(4, idTacGia);
                ps.executeUpdate();
            }
        }catch(SQLException ex){
            System.out.println("Lỗi SQL khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }
    }
    
    public void DeleteThongTinTacGia(String tenTacGia, String ngaySinh, String diaChi) throws SQLException{
        try(Connection con = ConnectDatabase.Connection()){
            String query = "DELETE FROM TacGia WHERE TenTacGia = ? and NgaySinh = ? and DiaChi = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, tenTacGia);
                ps.setString(2, ngaySinh);
                ps.setString(3, diaChi);
                ps.executeUpdate();
            }
        }catch(SQLException ex){
            System.out.println("Lỗi SQL khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }
    }
    
    public boolean CheckForiegnKeyTacGiaToSach(String tenTacGia, String ngaySinh, String diaChi) throws SQLException{
        int idTacGia = -1;
        try(Connection con = ConnectDatabase.Connection()){
            String queryId = "select IdTacGia from TacGia where TenTacgia = ? and NgaySinh = ? and DiaChi = ?";
            try(PreparedStatement ps = con.prepareStatement(queryId)){
                ps.setString(1, tenTacGia);
                ps.setString(2, ngaySinh);
                ps.setString(3, diaChi);
                ResultSet rs = ps.executeQuery();
                if(rs.next())
                {
                    idTacGia = rs.getInt("IdTacGia");
                }
                
            }
            String query = "select 1 from Sach where IdTacGia = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, idTacGia);
                ResultSet rs = ps.executeQuery();
                if(rs.next())
                {
                    return false;
                }else{
                    return true;
                }
            }
        }
        catch(SQLException ex){
            System.out.println("Lỗi SQL khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }
    }
    
}   

    
    
