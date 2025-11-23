/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.GetDataNhaXuatBanDAO;
import Model.NhaXuatBanModel;
import View.ChucNangCuaAdmin;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.sql.SQLException;
/**
 *
 * @author My Computer
 */
public class NhaXuatBanController {
    public GetDataNhaXuatBanDAO getDataNhaXuatBanDAO;
    public ChucNangCuaAdmin chucNangCuaAdmin;
    public NhaXuatBanModel nhaXuatBanModel;
    public NhaXuatBanController(ChucNangCuaAdmin view){
        this.chucNangCuaAdmin = view;
        this.getDataNhaXuatBanDAO = new GetDataNhaXuatBanDAO();
    }
    public NhaXuatBanController(NhaXuatBanModel model, ChucNangCuaAdmin view){
        this.nhaXuatBanModel = model;
        this.chucNangCuaAdmin = view;
        this.getDataNhaXuatBanDAO = new GetDataNhaXuatBanDAO();
    }
    
    public void SetDataViewNhaXuatBan(){
        try {
            List<NhaXuatBanModel> list = getDataNhaXuatBanDAO.SelectDataNhaXuatBan(); 
            chucNangCuaAdmin.AddRowToTableNhaXuatBan(list);
        } catch (SQLException ex) {
            System.err.println("Database lỗi trong khi tải: " + ex.getMessage());
            JOptionPane.showMessageDialog(chucNangCuaAdmin, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            chucNangCuaAdmin.AddRowToTableNhaXuatBan(new ArrayList<>());
        }
    }
    public boolean CheckNhaXuatBan() throws SQLException{
        String tenNhaXuatBan = nhaXuatBanModel.getTenNhaXuatBan();
        String diaChi = nhaXuatBanModel.getDiaChiNhaXuatBan();
        if(getDataNhaXuatBanDAO.CheckNhaXuatBanIsValid(tenNhaXuatBan, diaChi)){
            return false;
        }
        return true;
    }
    
    public boolean CheckIsNullNhaXuatBan(){
        if(nhaXuatBanModel.getTenNhaXuatBan() == null || nhaXuatBanModel.getTenNhaXuatBan().trim().isEmpty()
                || nhaXuatBanModel.getDiaChiNhaXuatBan() == null || nhaXuatBanModel.getDiaChiNhaXuatBan().trim().isEmpty()){
            chucNangCuaAdmin.HienThiThongBaoLoiThongTinNhaXuatBanNULL();
            return true;
        }
        else{
            return false;
        }
    }
    
    public void SetDataToDB() throws SQLException{
        String tenNhaXuatBan = nhaXuatBanModel.getTenNhaXuatBan();
        String diaChiNhaXuatBan = nhaXuatBanModel.getDiaChiNhaXuatBan();
        StringBuilder str = new StringBuilder();
        if(CheckIsNullNhaXuatBan()){
            return;
        }
        if(!CheckNhaXuatBan()){
            str.append("Nhà xuất bản đã tồn tại");
            chucNangCuaAdmin.HienThiThongBaoLoiTacGiaDaCo(str);
            return;
        }
        try {
            getDataNhaXuatBanDAO.InsertData(tenNhaXuatBan, diaChiNhaXuatBan);
            this.SetDataViewNhaXuatBan();
            chucNangCuaAdmin.ThongBaoThanhCongThemNhaSanXuat();
        } catch (SQLException ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi Database: " + ex.getMessage());
        } catch (Exception ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi hệ thống không xác định: " + ex.getMessage());
        }
    }
    
    public void UpdateDataToDB() throws SQLException{
        String tenNhaXuatBan = nhaXuatBanModel.getTenNhaXuatBan();
        String diaChiNhaXuatBan = nhaXuatBanModel.getDiaChiNhaXuatBan();
        int idNhaXuatBan = nhaXuatBanModel.getIdNhaXuatBan();
        if (CheckIsNullNhaXuatBan()) {
            return;
        }
        StringBuilder str = new StringBuilder();
        if (!CheckNhaXuatBan()) {
            str.append("Thông tin nhà xuất bản đã có, không thể sửa");
            chucNangCuaAdmin.HienThiThongBaoLoiTacGiaDaCo(str);
            return;
        }
        try {
            getDataNhaXuatBanDAO.UpdateThongTinNhaXuatBan(tenNhaXuatBan, diaChiNhaXuatBan, idNhaXuatBan);
            SetDataViewNhaXuatBan();
            chucNangCuaAdmin.ThongBaoUpdateThanhCongNhaXuatBan();
        } catch (SQLException ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi Database: " + ex.getMessage());
        } catch (Exception ex) {
            chucNangCuaAdmin.HienThiTongHopLoiDatabase("Lỗi hệ thống không xác định: " + ex.getMessage());
        }
    }
    
    public void DeleteDataToDB() throws SQLException{
        String tenNhaXuatBan = nhaXuatBanModel.getTenNhaXuatBan();
        String diaChiNhaXuatBan = nhaXuatBanModel.getDiaChiNhaXuatBan();
        StringBuilder str = new StringBuilder();
        if(!getDataNhaXuatBanDAO.CheckForiegnKeyNhaXuatToSach(tenNhaXuatBan, diaChiNhaXuatBan)){
            str.append("Tác giả đang được tham chiếu, vui lòng chọn nhà xuất bản khác");
            chucNangCuaAdmin.HienThiThongBaoTacGiaDangDuocThamChieu(str);
            return;
        }
        getDataNhaXuatBanDAO.DeleteThongTinNhaXuatBan(tenNhaXuatBan, diaChiNhaXuatBan);
        SetDataViewNhaXuatBan();
    }
    
}
