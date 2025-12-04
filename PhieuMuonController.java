
package Controller;

import DAO.GetDataLapPhieuMuonDAO;
import Model.PhieuMuonModel;
import View.ChucNangCuaAdmin;
import java.util.List;
import javax.swing.JOptionPane;

public class PhieuMuonController {
    private GetDataLapPhieuMuonDAO getDataLapPhieuMuonDAO;
    private PhieuMuonModel phieuMuonModel;
    private ChucNangCuaAdmin chucNangCuaAdmin;

    public PhieuMuonController(ChucNangCuaAdmin view) {
        this.chucNangCuaAdmin = view;
        this.getDataLapPhieuMuonDAO = new GetDataLapPhieuMuonDAO();
    }
    
    public PhieuMuonController(PhieuMuonModel model, ChucNangCuaAdmin view) {
        this.phieuMuonModel = model;
        this.chucNangCuaAdmin = view;
        this.getDataLapPhieuMuonDAO = new GetDataLapPhieuMuonDAO();
    }
    
    public void SetDataToComboBoxIdAdminLapPhieuMuon(){
        List<Integer> list = getDataLapPhieuMuonDAO.GetDataToComboBoxIdAdminLapPhieuMuon();
        chucNangCuaAdmin.AddDataToComboBoxAdminLapPhieuMuon(list);
    }
    
    public void SetDataToComboBoxIdSinhVienLapPhieuMuon(){
        List<Integer> list = getDataLapPhieuMuonDAO.GetDataToComboBoxIdSinhVienLapPhieuMuon();
        chucNangCuaAdmin.AddDataToComboBoxIdSinhVienLapPhieuMuon(list);
    }
    
    public void InsertDataToPhieuMuon(){
        int idAdmin = phieuMuonModel.getIdAdmin();
        int idSinhVien = phieuMuonModel.getIdSinhVien();
        StringBuilder str = new StringBuilder();
        if(getDataLapPhieuMuonDAO.CheckPhieuMuonIsExist(idAdmin, idSinhVien) == true){
            str.append("Phiếu mượn đã có vui lòng nhập phiếu khác");
            chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
            return;
        }
        getDataLapPhieuMuonDAO.InsertDataToPhieuMuon(idAdmin, idSinhVien);
        str.append("Thêm thành công phiếu mượn");
        chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
        this.ViewDataToTablePhieuMuon();
    }
    
    public void ViewDataToTablePhieuMuon(){
        List<PhieuMuonModel> list = getDataLapPhieuMuonDAO.SelectALLSach();
        chucNangCuaAdmin.AddDataToTablePhieuMuon(list);
    }
    
    public void DeleteDataToPhieuMuon(){
        int idAdmin = phieuMuonModel.getIdAdmin();
        int idSinhVien = phieuMuonModel.getIdSinhVien();
        StringBuilder str = new StringBuilder();
        if(getDataLapPhieuMuonDAO.CheckPhieuMuonCoSachDangDuocMuon(idAdmin, idSinhVien)){
            str.append("Phiếu mượn đang được dùng, không thể xoá được");
            chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
            return;
        }
        if (JOptionPane.showConfirmDialog(chucNangCuaAdmin, "Xác nhận xoá phiếu mượn chứ?", "Thông báo", JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
            getDataLapPhieuMuonDAO.DeleteToLapPhieuMuon(idAdmin, idSinhVien);
            str.append("Xoá phiếu mượn thành công");
            chucNangCuaAdmin.HienThiThongBaoPhieuMuon(str);
            this.ViewDataToTablePhieuMuon();
        }
        
    }
    
    
}
