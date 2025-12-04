/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.GetDataChiTietPhieuMuonDAO;
import DAO.GetDataDoiMatKhauDAO;
import Model.MaHoa;
import Model.TaiKhoanAdmin;
import static Model.TaiKhoanAdminCheck.user;
import Model.TaiKhoanAdminDoiMatKhauModel;
import View.ChucNangCuaAdmin;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DoiMatKhauAdminController {
    private ChucNangCuaAdmin chucNangCuaAdmin;
    private GetDataDoiMatKhauDAO getDataDoiMatKhauDAO;
    private TaiKhoanAdminDoiMatKhauModel taiKhoanAdminDoiMatKhauModel;

    public DoiMatKhauAdminController(TaiKhoanAdminDoiMatKhauModel model, ChucNangCuaAdmin view) {
        this.taiKhoanAdminDoiMatKhauModel = model;
        this.chucNangCuaAdmin = view;
        this.getDataDoiMatKhauDAO = new GetDataDoiMatKhauDAO();
    }
    
    public boolean CheckIsNull(){
        String matKhauCu = taiKhoanAdminDoiMatKhauModel.getMatKhauCu();
        String matKhauMoi = taiKhoanAdminDoiMatKhauModel.getMatKhauMoi();
        String matKhauXacThuc = taiKhoanAdminDoiMatKhauModel.getMatKhauXacThuc();
        if(matKhauCu == null || matKhauCu.trim().isEmpty() || matKhauMoi == null || matKhauMoi.trim().isEmpty()
                || matKhauXacThuc == null || matKhauXacThuc.trim().isEmpty()){
            return true;
        }
        return false;
    }
    
    public boolean CheckMatKhau(String matKhau){     
        String mkCheck = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!%*?&])[A-Za-z\\d@!%*?&]{6,}$";
        Pattern pt = Pattern.compile(mkCheck);
        Matcher mt = pt.matcher(matKhau);
        if (mt.matches()){
            return true;
        } 
        else {
            return false;
        }
    }
    
    public void CheckTaiKhoanDoiMatKhauAdmin() throws NoSuchAlgorithmException, UnsupportedEncodingException{
        StringBuilder str = new StringBuilder();
        String matKhauSql = getDataDoiMatKhauDAO.GetDataMatKhau(user);
        int idTaiKhoan = getDataDoiMatKhauDAO.GetDataIdTaiKhoan(user);
        String matKhauCu = taiKhoanAdminDoiMatKhauModel.getMatKhauCu();
        String matKhauMoi = taiKhoanAdminDoiMatKhauModel.getMatKhauMoi();
        String matKhauXacThuc = taiKhoanAdminDoiMatKhauModel.getMatKhauXacThuc();
        MaHoa maHoa = new MaHoa();
        String matKhauCuEncrypt = maHoa.encrypt(matKhauCu);
        String matKhauMoiEncrypt = maHoa.encrypt(matKhauMoi);
        if(CheckIsNull()){
            str.append("Vui lòng điền đầy đủ thông tin");
            chucNangCuaAdmin.HienThiThongBaoDoiMatKhau(str);
            return;
        }
        if(matKhauSql == matKhauCuEncrypt){
            str.append("Mật khẩu cũ không đúng");
            chucNangCuaAdmin.HienThiThongBaoDoiMatKhau(str);
            return;
            
        }
        if(matKhauCuEncrypt.equals(matKhauMoiEncrypt)){
            str.append("Mật khẩu mới không được trùng với mật khẩu cũ");
            chucNangCuaAdmin.HienThiThongBaoDoiMatKhau(str);
            return;
        }
        if(CheckMatKhau(matKhauMoi) == false){
            str.append("Mật khẩu phải có đủ chữ hoa, chữ thường, chữ số, và ký tự đặc biệt");
            chucNangCuaAdmin.HienThiThongBaoDoiMatKhau(str);
            return;
        }
        
        if(matKhauMoi.equals(matKhauXacThuc) == false){
            str.append("Cần nhập đúng mật khẩu xác thực trùng với mật khẩu mới");
            chucNangCuaAdmin.HienThiThongBaoDoiMatKhau(str);
            return;
        }
        getDataDoiMatKhauDAO.UpdateTaiKhoanAdmin(idTaiKhoan, matKhauMoiEncrypt);
        str.append("Đổi mật khẩu thành công");
        chucNangCuaAdmin.HienThiThongBaoDoiMatKhau(str);
    }
    
    
}
