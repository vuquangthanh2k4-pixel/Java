
package Controller;

import DAO.TaiKhoanDAO;
import Model.TaiKhoanAdmin;
import View.Administrator;

public class TaiKhoanAdminController {
    private TaiKhoanDAO taiKhoanDAO;
    private Administrator administrator;
    private TaiKhoanAdmin taiKhoanAdmin;

    public TaiKhoanAdminController(TaiKhoanAdmin model, Administrator view) {
        this.taiKhoanAdmin = model;
        this.administrator = view;
        this.taiKhoanDAO = new TaiKhoanDAO();
    }
    public void CheckTaiKhoanAdmin(){
        boolean isValid = taiKhoanDAO.CheckTaiKhoan(taiKhoanAdmin.getTaiKhoan(), taiKhoanAdmin.getMatKhau());
        if(isValid){
            administrator.HienThiThongBaoTaiKhoanDung();
        }
        else{
            administrator.HienThiThongBaoTaiKhoanSai();
        }
    }
}
