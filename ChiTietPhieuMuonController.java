
package Controller;

import DAO.GetDataChiTietPhieuMuonDAO;
import Model.ChiTietPhieuMuonModel;
import Model.ComboBox;
import Model.SachModel;
import View.ChucNangCuaAdmin;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class ChiTietPhieuMuonController {
    private ChucNangCuaAdmin chucNangCuaAdmin;
    private GetDataChiTietPhieuMuonDAO getDataChiTietPhieuMuonDAO;
    private ChiTietPhieuMuonModel chiTietPhieuMuonModel;
    public ChiTietPhieuMuonController(ChucNangCuaAdmin view){
        this.chucNangCuaAdmin = view;
        this.getDataChiTietPhieuMuonDAO = new GetDataChiTietPhieuMuonDAO();
    }
    public ChiTietPhieuMuonController(ChiTietPhieuMuonModel model, ChucNangCuaAdmin view){
        this.chiTietPhieuMuonModel = model;
        this.chucNangCuaAdmin = view;
        this.getDataChiTietPhieuMuonDAO = new GetDataChiTietPhieuMuonDAO();
    }
    
    public void SetDataToComboBoxIdSachChiTietPhieuMuon(){
        List<SachModel> list = getDataChiTietPhieuMuonDAO.SelectDataToComboBoxIdSach();
        chucNangCuaAdmin.AddDataToComboBoxIdSachChiTietPhieuMuon(list);
    }
    
    public void SetDataToComboBoxIdPhieuMuonChiTietPhieuMuon(){
        List<Integer> list = getDataChiTietPhieuMuonDAO.SelectDataToComboBoxIdPhieuMuon();
        chucNangCuaAdmin.AddDataToComboBoxIdPhieuMuonChiTietPhieuMuon(list);
    }
    
    public void ViewDataToTableChiTietPhieuMuon(){
        List<ChiTietPhieuMuonModel> list = getDataChiTietPhieuMuonDAO.SelectDataToTableChiTietPhieuMuon();
        chucNangCuaAdmin.AddDataToTableChiTietPhieuMuon(list);
    }
    
    public boolean CheckIsValidNgay(String date){
        if(date == null || date.trim().isEmpty()){
            return false;
        }
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        simple.setLenient(false);
        try{
            Date dateNgay = simple.parse(date);
            return true;
        }
        catch(ParseException ex)
        {
            return false;
        }
    }
    
    public void InsertDataToDBSachDuocMuon(String ngayMuon, String ngayTra, String trangThaiSach, int idSach, int idPhieuMuon){
        StringBuilder str = new StringBuilder();
        if(this.CheckIsValidNgay(ngayMuon) == false){
            str.append("Ngày trả cần đúng định dạng yyyy-MM-dd");
            chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
            return;
        }
        java.sql.Date sqlNgayMuon = java.sql.Date.valueOf(ngayMuon);
        java.sql.Date sqlNgayTra = null;
        String ngayTraXoa = ngayTra.replace("-", "").replace("_", "").trim();
        if(!ngayTraXoa.isEmpty()){
            if(this.CheckIsValidNgay(ngayTra) == false){
                str.append("Ngày trả cần đúng định dạng yyyy-MM-dd");
                chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
                return;
            }
            sqlNgayTra = java.sql.Date.valueOf(ngayTra);
            if(sqlNgayTra.before(sqlNgayMuon)){
                str.append("Ngày trả không được trước ngày mượn");
                chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
                return;
            }
        }
        
        if(getDataChiTietPhieuMuonDAO.CheckSachDaDuocMuon(idSach, idPhieuMuon)){
            str.append("Sách đã được mượn");
            chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
            return;
        }
        
        try{
            getDataChiTietPhieuMuonDAO.InsertDataToSachDuocMuon(sqlNgayMuon, sqlNgayTra, trangThaiSach, idSach, idPhieuMuon);
            str.append("Thêm thành công sách vào phiếu mượn");
            chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
            this.ViewDataToTableChiTietPhieuMuon();
        }
        catch(Exception ex){
            System.out.print("Lỗi Controller: " + ex.getMessage());
        }     
        
    }
    
    public void DeleteChiTietPhieuMuon(){
        StringBuilder str = new StringBuilder();
        int idSach = chiTietPhieuMuonModel.getIdSach();
        int idPhieuMuon = chiTietPhieuMuonModel.getIdPhieuMuon();
        
        if(JOptionPane.showConfirmDialog(chucNangCuaAdmin, "Xác nhận trả sách", "Thông báo", JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION){
            getDataChiTietPhieuMuonDAO.DeleteChiTietPhieuMuon(idSach, idPhieuMuon);
            str.append("Trả sách thành công");
            chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
            this.ViewDataToTableChiTietPhieuMuon();
        }
    }
    
}
