
package Controller;

import DAO.TaiKhoanDAO;
<<<<<<< HEAD
import Model.MaHoa;
import Model.TaiKhoanAdmin;
import Model.TaiKhoanAdminCheck;
import View.Administrator;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
=======
import Model.TaiKhoanAdmin;
import View.Administrator;
>>>>>>> origin/master

public class TaiKhoanAdminController {
    private TaiKhoanDAO taiKhoanDAO;
    private Administrator administrator;
    private TaiKhoanAdmin taiKhoanAdmin;

    public TaiKhoanAdminController(TaiKhoanAdmin model, Administrator view) {
        this.taiKhoanAdmin = model;
        this.administrator = view;
        this.taiKhoanDAO = new TaiKhoanDAO();
    }
<<<<<<< HEAD
    public void CheckTaiKhoanAdmin() throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MaHoa maHoa = new MaHoa();
        String matKhau = taiKhoanAdmin.getMatKhau();
        String maKhauMaHoa = maHoa.encrypt(matKhau);
        boolean isValid = taiKhoanDAO.CheckTaiKhoan(taiKhoanAdmin.getTaiKhoan(), maKhauMaHoa);
        if(isValid){
            TaiKhoanAdminCheck.user = taiKhoanAdmin.getTaiKhoan();
=======
    public void CheckTaiKhoanAdmin(){
        boolean isValid = taiKhoanDAO.CheckTaiKhoan(taiKhoanAdmin.getTaiKhoan(), taiKhoanAdmin.getMatKhau());
        if(isValid){
>>>>>>> origin/master
            administrator.HienThiThongBaoTaiKhoanDung();
        }
        else{
            administrator.HienThiThongBaoTaiKhoanSai();
        }
    }
}
