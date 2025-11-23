
package Controller;

import DAO.GetDataTacGiaDAO;
import Model.TacGiaModel;
import View.ChucNangCuaAdmin;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class TacGiaController {
    private TacGiaModel tacGiaModel;
    private ChucNangCuaAdmin chucNangCuaAdmin;
    private GetDataTacGiaDAO getDataTacGiaDAO;
    public TacGiaController(ChucNangCuaAdmin view){
        this.chucNangCuaAdmin = view;
        this.getDataTacGiaDAO = new GetDataTacGiaDAO();
    }
    public TacGiaController(TacGiaModel model, ChucNangCuaAdmin view){
        this.tacGiaModel = model;
        this.chucNangCuaAdmin = view;
        this.getDataTacGiaDAO = new GetDataTacGiaDAO();
    }
    
    public void SetDataView(){
        try {
            List<TacGiaModel> list = getDataTacGiaDAO.SelectDataTacGia(); 
            chucNangCuaAdmin.AddRowToTable(list);
        } catch (SQLException ex) {
            System.err.println("Database lỗi trong khi tải: " + ex.getMessage());
            JOptionPane.showMessageDialog(chucNangCuaAdmin, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            chucNangCuaAdmin.AddRowToTable(new ArrayList<>());
        }
    }
    
    public boolean CheckTacGia() throws SQLException{
        String tenTacGia = tacGiaModel.getTenTacGia();
        String ngaySinh = tacGiaModel.getNgaySinh();
        String diaChi = tacGiaModel.getDiaChi();
        if(getDataTacGiaDAO.CheckTacGiaIsValid(tenTacGia, ngaySinh, diaChi)){
            return false;
        }
        return true;
    }
    
    public boolean CheckIsNullTacGia(){
        if(tacGiaModel.getTenTacGia() == null || tacGiaModel.getTenTacGia().trim().isEmpty()
                || tacGiaModel.getNgaySinh() == null || tacGiaModel.getNgaySinh().trim().isEmpty()
                || tacGiaModel.getDiaChi() == null || tacGiaModel.getDiaChi().trim().isEmpty()){
            chucNangCuaAdmin.HienThiThongBaoLoiThongTinTacGiaNULL();
            return true;
        }
        else{
            return false;
        }
    }
    
    public void SetDataToDB() throws SQLException{
        String tenTacGia = tacGiaModel.getTenTacGia();
        String ngaySinh = tacGiaModel.getNgaySinh();
        String diaChi = tacGiaModel.getDiaChi();
        StringBuilder str = new StringBuilder();
        if(CheckIsNullTacGia()){
            return;
        }
        if(!CheckTacGia()){
            str.append("Tác giả đã tồn tại");
            chucNangCuaAdmin.HienThiThongBaoLoiTacGiaDaCo(str);
            return;
        }
        try {
            getDataTacGiaDAO.InsertData(tenTacGia, ngaySinh, diaChi);
            SetDataView();
            chucNangCuaAdmin.ThongBaoThanhCong();
        } catch (SQLException ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi Database: " + ex.getMessage());
        } catch (Exception ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi hệ thống không xác định: " + ex.getMessage());
        }
    }
    
    public void UpdateDataToDB() throws SQLException{
        String tenTacGia = tacGiaModel.getTenTacGia();
        String ngaySinh = tacGiaModel.getNgaySinh();
        String diaChi = tacGiaModel.getDiaChi();
        int idTacGia = tacGiaModel.getIdTacGia();
        if (CheckIsNullTacGia()) {
            return;
        }
        StringBuilder str = new StringBuilder();
        if (!CheckTacGia()) {
            str.append("Thông tin tác giả đã có, không thể sửa");
            chucNangCuaAdmin.HienThiThongBaoLoiTacGiaDaCo(str);
            return;
        }
        try {
            getDataTacGiaDAO.UpdateThongTinTacGia(tenTacGia, ngaySinh, diaChi, idTacGia);
            SetDataView();
            chucNangCuaAdmin.ThongBaoUpdateThanhCong();
        } catch (SQLException ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi Database: " + ex.getMessage());
        } catch (Exception ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi hệ thống không xác định: " + ex.getMessage());
        }
    }
    
    public void DeleteDataToDB() throws SQLException{
        String tenTacGia = tacGiaModel.getTenTacGia();
        String ngaySinh = tacGiaModel.getNgaySinh();
        String diaChi = tacGiaModel.getDiaChi();
        StringBuilder str = new StringBuilder();
        if(!getDataTacGiaDAO.CheckForiegnKeyTacGiaToSach(tenTacGia, ngaySinh, diaChi)){
            str.append("Tác giả đang được tham chiếu, vui lòng chọn tác giả khác");
            chucNangCuaAdmin.HienThiThongBaoTacGiaDangDuocThamChieu(str);
            return;
        }
        getDataTacGiaDAO.DeleteThongTinTacGia(tenTacGia, ngaySinh, diaChi);
        SetDataView();
    }
    
}
