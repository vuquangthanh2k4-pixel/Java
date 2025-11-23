
package DAO;

import Database.ConnectDatabase;
import Model.NhaXuatBanModel;
import Model.TacGiaModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetDataNhaXuatBanDAO {

    public GetDataNhaXuatBanDAO() {
    }

    public List<NhaXuatBanModel> SelectDataNhaXuatBan() throws SQLException {
        Connection con = null;
        String query = null;
        List<NhaXuatBanModel> list = new ArrayList<NhaXuatBanModel>();
        try {
            con = ConnectDatabase.Connection();
            query = "select IdNhaXuatBan, TenNhaXuatBan, DiaChi from NhaXuatBan";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idNhaXuatBan = rs.getInt("IdNhaXuatBan");
                String tenNhaXuatBan = rs.getString("TenNhaXuatBan");
                String diaChi = rs.getString("DiaChi");

                NhaXuatBanModel model = new NhaXuatBanModel();
                model.setIdNhaXuatBan(idNhaXuatBan);
                model.setTenNhaXuatBan(tenNhaXuatBan);
                model.setDiaChiNhaXuatBan(diaChi);
                list.add(model);
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi sql khi kiểm tra dữ liệu: " + ex.getMessage());

        } catch (Exception ex) {
            System.out.println("Không thể lấy nên tài khoản:" + ex.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("Lỗi đóng kết nối: " + ex.getMessage());
            }
        }
        return list;
    }
    public void InsertData(String tenNhaXuatBan, String diaChiNhaXuatBan) throws SQLException{
        Connection con = null;
        String query;
        try{
            con = ConnectDatabase.Connection();
            query = "insert into NhaXuatBan(TenNhaXuatBan, DiaChi) values (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, tenNhaXuatBan);
            ps.setString(2, diaChiNhaXuatBan);
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
    
     public boolean CheckNhaXuatBanIsValid(String tenNhaXuatBan, String diaChiNhaXuatBan) throws SQLException {
        String query = "Select TenNhaXuatBan, DiaChi from NhaXuatBan where TenNhaXuatBan = ? and DiaChi = ?";
        try (Connection con = ConnectDatabase.Connection()) {
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, tenNhaXuatBan);
                ps.setString(2, diaChiNhaXuatBan);
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
    
     public void UpdateThongTinNhaXuatBan(String tenNhaXuatBan, String diaChiNhaXuatBan, int idNhaXuatBan) throws SQLException{
        try(Connection con = ConnectDatabase.Connection()){
            String query = "Update NhaXuatBan set TenNhaXuatBan = ?, DiaChi = ? where IdNhaXuatBan = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, tenNhaXuatBan);
                ps.setString(2, diaChiNhaXuatBan);
                ps.setInt(3, idNhaXuatBan);
                ps.executeUpdate();
            }
        }catch(SQLException ex){
            System.out.println("Lỗi SQL khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }
    }
    
     public void DeleteThongTinNhaXuatBan(String tenNhaXuatBan, String diaChiNhaXuatBan) throws SQLException{
        try(Connection con = ConnectDatabase.Connection()){
            String query = "DELETE FROM NhaXuatBan WHERE tenNhaXuatBan = ?  and DiaChi = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, tenNhaXuatBan);
                ps.setString(2, diaChiNhaXuatBan);
                ps.executeUpdate();
            }
        }catch(SQLException ex){
            System.out.println("Lỗi SQL khi kiểm tra dữ liệu: " + ex.getMessage());
            throw ex;
        }
    }
     
    public boolean CheckForiegnKeyNhaXuatToSach(String tenNhaXuatBan, String diaChiNhaXuatBan) throws SQLException{
        int idNhaXuatBan = -1;
        try(Connection con = ConnectDatabase.Connection()){
            String queryId = "select IdNhaXuatBan from NhaXuatBan where TenNhaXuatBan = ?  and DiaChi = ?";
            try(PreparedStatement ps = con.prepareStatement(queryId)){
                ps.setString(1, tenNhaXuatBan);
                ps.setString(2, diaChiNhaXuatBan);
                ResultSet rs = ps.executeQuery();
                if(rs.next())
                {
                    idNhaXuatBan = rs.getInt("IdNhaXuatBan");
                }
                
            }
            String query = "select 1 from Sach where IdNhaXuatBan = ?";
            try(PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, idNhaXuatBan);
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
