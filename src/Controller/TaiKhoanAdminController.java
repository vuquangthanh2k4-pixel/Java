
package Controller;

import DAO.TaiKhoanDAO;
import Model.MaHoa;
import Model.TaiKhoanAdmin;
import View.Administrator;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class TaiKhoanAdminController {
    private TaiKhoanDAO taiKhoanDAO;
    private Administrator administrator;
    private TaiKhoanAdmin taiKhoanAdmin;

    public TaiKhoanAdminController(TaiKhoanAdmin model, Administrator view) {
        this.taiKhoanAdmin = model;
        this.administrator = view;
        this.taiKhoanDAO = new TaiKhoanDAO();
    }
    public void CheckTaiKhoanAdmin() throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MaHoa maHoa = new MaHoa();
        String matKhau = taiKhoanAdmin.getMatKhau();
        String maKhauMaHoa = maHoa.encrypt(matKhau);
        boolean isValid = taiKhoanDAO.CheckTaiKhoan(taiKhoanAdmin.getTaiKhoan(), maKhauMaHoa);
        if(isValid){
            administrator.HienThiThongBaoTaiKhoanDung();
        }
        else{
            administrator.HienThiThongBaoTaiKhoanSai();
        }
    }
}
