
package Controller;

import DAO.GetDataSachDAO;
import Model.SachModel;
import View.ChucNangCuaAdmin;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class SachController {
    private GetDataSachDAO getDataSachDao;
    private SachModel sachModel;
    private ChucNangCuaAdmin chucNangCuaAdmin;

    public SachController(SachModel model, ChucNangCuaAdmin view) {
        this.sachModel = model;
        this.chucNangCuaAdmin = view;
        getDataSachDao = new GetDataSachDAO();
    }
    
    public SachController(ChucNangCuaAdmin view){
        this.chucNangCuaAdmin = view;
        this.getDataSachDao = new GetDataSachDAO();
    }
    
    public void SetDataViewSach(){
        List<SachModel> list = getDataSachDao.SelectALLSach();
        chucNangCuaAdmin.AddRowToTableSach(list);
    }
    
    public void ViewSachTheoIdSach() throws SQLException{
        int idSach = sachModel.getIdSach();
        List<SachModel> list = getDataSachDao.TimKiemTheoIdSach(idSach);
        chucNangCuaAdmin.AddRowToTableSach(list);
    }
    public void ViewSachTheoTenSach() throws SQLException{
        String tenSach = sachModel.getTenSach();
        List<SachModel> list = getDataSachDao.TimKiemTheoTenSach(tenSach);
        chucNangCuaAdmin.AddRowToTableSach(list);
    }
    
    public void SetDataToComboBoxNXB(){
        List<Integer> list = getDataSachDao.SetDataToComboBoxIdNhaXuatBan();
        chucNangCuaAdmin.AddDataToComboBoxNhaXuatBan(list);
    }
    
    public void SetDataToComboBoxTacGia(){
        List<Integer> list = getDataSachDao.SetDataToComboBoxIdTacGia();
        chucNangCuaAdmin.AddDataToComboBoxTacGia(list);
    }
    
    public void SetDataToComboBoxAdmin(){
        List<Integer> list = getDataSachDao.SetDataToComboBoxIdAdmin();
        chucNangCuaAdmin.AddDataToComboBoxAdmin(list);
    }
    
    
    public boolean CheckIsNullSach(){
        if(sachModel.getTenSach() == null || sachModel.getTenSach().trim().isEmpty() 
                || sachModel.getTheLoai() == null || sachModel.getTheLoai().trim().isEmpty()){
            return true;
        }
        return false;
    }
    
    public boolean CheckIsNullNamXuatBan(){
        LocalDate date = LocalDate.now();
        int nam = date.getYear();
        if(sachModel.getNamXuatBan() < 0 || sachModel.getNamXuatBan() > nam || String.valueOf(sachModel.getNamXuatBan())  == null ||  String.valueOf(sachModel.getNamXuatBan()).trim().isEmpty()){
            return true;
        }
        return false;
    }
    
    public void InSertDataToDB(){
        StringBuilder str = new StringBuilder();
        String tenSach = sachModel.getTenSach();
        String theLoai = sachModel.getTheLoai();
        int namXuatBan = sachModel.getNamXuatBan();
        int idNXB = sachModel.getIdNhaXuatBan();
        int idTacGia = sachModel.getIdTacGia();
        int idAdmin = sachModel.getIdAdmin();
        if(CheckIsNullSach()){
            str.append("Vui lòng điền đầy đủ tên sách và thể loại sách");
            chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
            return; 
        }
        if(CheckIsNullNamXuatBan()){
            str.append("Năm xuất bản phải lớn hơn 0 hoặc nhỏ hơn năm hiện tại hoặc không bỏ trống");
            chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
            return;
        }
        if(getDataSachDao.CheckSachIsExist(tenSach, theLoai, namXuatBan, idNXB, idTacGia, idAdmin)){
            str.append("Thông tin sách đã tồn tại");
            chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
            return;
        }
        getDataSachDao.InsertDateToSach(tenSach, theLoai, namXuatBan, idNXB, idTacGia, idAdmin);
        str.append("Thêm thành công sách");
        chucNangCuaAdmin.HienThiThongBaoThemSachThanhCong(str);
        this.SetDataViewSach();
    }
    
   public void UpdateDataToSach(){
       StringBuilder str = new StringBuilder();
       int idSach = sachModel.getIdSach();
       String tenSach = sachModel.getTenSach();
       String theLoai = sachModel.getTheLoai();
       int namXuatBan = sachModel.getNamXuatBan();
       int idNXB = sachModel.getIdNhaXuatBan();
       int idTacGia = sachModel.getIdTacGia();
       int idAdmin = sachModel.getIdAdmin();
       if (CheckIsNullSach()) {
           str.append("Vui lòng chọn thông tin sách để sửa");
           chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
           return;
       }
       if (CheckIsNullNamXuatBan()) {
           str.append("Năm xuất bản phải lớn hơn 0 hoặc nhỏ hơn năm hiện tại hoặc không bỏ trống");
           chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
           return;
       }
       if (getDataSachDao.CheckSachIsExist(tenSach, theLoai, namXuatBan, idNXB, idTacGia, idAdmin)) {
           str.append("Thông tin sách đã tồn tại");
           chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
           return;
       }
       getDataSachDao.UpdateDataToSach(idSach, tenSach, theLoai, namXuatBan, idNXB, idTacGia, idAdmin);
       str.append("Sửa sách thành công");
       chucNangCuaAdmin.HienThiThongBaoThemSachThanhCong(str);
       this.SetDataViewSach();
   }
   
   public boolean CheckIndex(){
        if(sachModel.getIdSach() <= 0){
            return false;
        }
        return true;
   }
   
   public void DeleteDataToSach(){
       StringBuilder str = new StringBuilder();
       int idSach = sachModel.getIdSach();
       if(!CheckIndex()){
           str.append("Vui lòng chọn thông tin sách để xoá");
           chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
           return;
       }
       if(CheckIsNullNamXuatBan()){
           str.append("Năm xuất bản phải lớn hơn 0 hoặc nhỏ hơn năm hiện tại hoặc không bỏ trống");
           chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
           return;
       }
       if(getDataSachDao.CheckSachDaCoTrongSachMuon(idSach)){
           str.append("Sách đang được mượn");
           chucNangCuaAdmin.HienThiThongBaoLoiSach(str);
           return;
       }
       getDataSachDao.DeleteDataToSach(idSach);
       str.append("Xoá thành công sách");
       chucNangCuaAdmin.HienThiThongBaoThemSachThanhCong(str);
       this.SetDataViewSach();
   }
    
}
