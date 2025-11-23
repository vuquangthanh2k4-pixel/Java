
package Controller;

import DAO.TaiKhoanAdminDK;
import Model.MaHoa;
import Model.TaiKhoanAdmin;
import View.Administrator;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class CheckTextDangKyAdminController {
    private TaiKhoanAdmin taiKhoanAdmin;
    private Administrator administrator;
    private TaiKhoanAdminDK taiKhoanAdminDK;
    public CheckTextDangKyAdminController(TaiKhoanAdmin model, Administrator view) {
        this.taiKhoanAdmin = model;
        this.administrator = view;
        this.taiKhoanAdminDK = new TaiKhoanAdminDK();
    }
    
    public boolean CheckText(){
        String taiKhoanDk = taiKhoanAdmin.getTaiKhoan();
        String matKhauDk = taiKhoanAdmin.getMatKhau();
        String emailDk = taiKhoanAdmin.getEmail();
        String hoTen = taiKhoanAdmin.getHoTen();
        String ngaySinh = taiKhoanAdmin.getNgaySinh();
        String diaChi = taiKhoanAdmin.getDiaChi();
        if(taiKhoanDk == null || taiKhoanDk.trim().isEmpty() 
              || matKhauDk == null || matKhauDk.trim().isEmpty() 
                || emailDk == null || emailDk.trim().isEmpty()
                || hoTen == null || hoTen.trim().isEmpty()
                || ngaySinh == null || ngaySinh.trim().isEmpty()
                || diaChi == null || diaChi.trim().isEmpty()){
            administrator.HienThiLoiTextDangKy("Vui lòng nhập đầy đủ thông tin");
            return true;
        }
        else{
            return false;
        }
    }
    public boolean CheckTaiKhoan(){
        String taiKhoan = taiKhoanAdmin.getTaiKhoan();
        return taiKhoanAdminDK.CheckDangKyTaiKhoanAdmin(taiKhoan);
    }
    
    public boolean CheckEmail(){
        String email = taiKhoanAdmin.getEmail();
        return taiKhoanAdminDK.CheckEmailDangKyAdmin(email); 
    }
    
    public boolean CheckMatKhau(){
        String matKhau = taiKhoanAdmin.getMatKhau();
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
    
    public boolean isValidNgaySinh(){
        String ngaySinh = taiKhoanAdmin.getNgaySinh();
        int minTuoi = 18;
        try {
            DateTimeFormatter Data_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ngayHienTai = LocalDate.now();
            LocalDate ngaySinhCheck = LocalDate.parse(ngaySinh, Data_format);
            Period age = Period.between(ngaySinhCheck, ngayHienTai);
            if (age.getYears() < minTuoi) {
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public boolean isValidEmail(){
        String email = taiKhoanAdmin.getEmail();
        String emCheck = "^[a-zA-Z0-9._%+-]+@epu\\.edu\\.vn$";
        Pattern pt = Pattern.compile(emCheck);
        Matcher mt = pt.matcher(email);
        if (mt.matches()) {
            return true;
        } 
        else {
            return false;
        }
    }
    
    public void Check() throws NoSuchAlgorithmException, UnsupportedEncodingException, Exception{
        if(CheckText()){
            return;
        }
        StringBuilder err = new StringBuilder();
        if(CheckTaiKhoan()){
            err.append("Tài khoản này đã được đăng ký.\n"); 
        }
        if(CheckEmail()){
            err.append("Email đã được đăng ký.\n");
        }
        if(!isValidEmail()){
            err.append("Email chưa đúng, muốn đăng ký admin phải có đuôi epu.edu.vn.\n");
        }
        if(!CheckMatKhau()){
            err.append("Mật khẩu cần có chữ hoa, chữ thường, chữ số, và ký tự đặc biệt.\n");
        }
        if(!isValidNgaySinh()){
            err.append("Ngày sinh cần đúng với format yyyy-MM-dd hoặc đúng với số tuổi quy định");
        }
        if(err.length() > 0){
            administrator.HienThiTongHopLoiDatabase(err.toString());
            return;
        }
        administrator.HienThiLoiTextDangKy(null);
        String matKhauGoc = taiKhoanAdmin.getMatKhau();
        MaHoa maHoaHelper = new MaHoa();
        String encryptMatKhau = maHoaHelper.encrypt(matKhauGoc); 
        taiKhoanAdmin.setMatKhau(encryptMatKhau);
        String hoTen = taiKhoanAdmin.getHoTen();
        String ngaySinh = taiKhoanAdmin.getNgaySinh();
        String diaChi = taiKhoanAdmin.getDiaChi();
        taiKhoanAdminDK.InsertTaiKhoanAdmin(taiKhoanAdmin.getTaiKhoan(), taiKhoanAdmin.getMatKhau(), taiKhoanAdmin.getEmail(), hoTen, ngaySinh, diaChi);
        administrator.HienThiThongBaoDangKyThanhCong();
    }

}
