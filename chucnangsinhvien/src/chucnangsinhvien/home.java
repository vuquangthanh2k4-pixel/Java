/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package chucnangsinhvien;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Administrator
 */
public class home extends javax.swing.JFrame {
    private Connection connection;
    private DefaultTableModel tableModel;
    private int currentUserId = 3;
    /**
     * Creates new form home
     */
    public home() {
   
        initComponents();
        initDatabase();
        setupTable();
        showBorrowedBooks();
        setupEventListeners();
        
    }

    private void initDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quanlythuvien", "root", "phamq20579");
            System.out.println("Kết nối database thành công!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(home.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi driver MySQL: " + ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(home.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + ex.getMessage());
        }
    }



    // Thiết lập bảng
    private void setupTable() {
        tableModel = new DefaultTableModel();
        
        // Sử dụng tbl2 có sẵn trong form
        tbl2.setModel(tableModel);
        
        // Thêm các cột cho bảng
        tableModel.addColumn("Mã Phiếu");
        tableModel.addColumn("Ngày Mượn");
        tableModel.addColumn("Ngày Trả");
        tableModel.addColumn("Số Lượng");
        tableModel.addColumn("Trạng Thái");
        tableModel.addColumn("Tên Sách");
    }

    // Hiển thị sách đang mượn
    private void showBorrowedBooks() {
        try {
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không có kết nối database!");
                return;
            }
            
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            
            String sql = "SELECT pm.IdPhieuMuon, sdm.NgayMuon, sdm.NgayTraThucTe, " +
                        "sdm.SoLuong, sdm.TrangThaiSach, s.TenSach " +
                        "FROM SachDuocMuon sdm " +
                        "JOIN PhieuMuon pm ON sdm.IdPhieuMuon = pm.IdPhieuMuon " +
                        "JOIN Sach s ON sdm.IdSach = s.IdSach " +
                        "WHERE pm.IdSinhVien = ? " +
                        "ORDER BY sdm.NgayMuon DESC";
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, currentUserId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("IdPhieuMuon"),
                    rs.getDate("NgayMuon"),
                    rs.getDate("NgayTraThucTe"),
                    rs.getInt("SoLuong"),
                    rs.getString("TrangThaiSach"),
                    rs.getString("TenSach")
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    // Tìm sách
    private void searchBooks() {
        try {
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không có kết nối database!");
                return;
            }
            
            String searchTerm = txt1.getText().trim();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên sách cần tìm!");
                return;
            }
            
            // Sử dụng tb1 có sẵn trong form
            DefaultTableModel searchModel = new DefaultTableModel();
            searchModel.addColumn("Mã Sách");
            searchModel.addColumn("Tên Sách");
            searchModel.addColumn("Tác Giả");
            searchModel.addColumn("Thể Loại");
            searchModel.addColumn("Năm XB");
            
            tb1.setModel(searchModel);
            
            // Query tìm sách
            String sql = "SELECT s.IdSach, s.TenSach, tg.TenTacGia, s.TheLoai, s.NamXuatBan " +
                        "FROM Sach s " +
                        "JOIN TacGia tg ON s.IdTacGia = tg.IdTacGia " +
                        "WHERE s.TenSach LIKE ? OR tg.TenTacGia LIKE ? OR s.TheLoai LIKE ?";
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            pstmt.setString(3, "%" + searchTerm + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("IdSach"),
                    rs.getString("TenSach"),
                    rs.getString("TenTacGia"),
                    rs.getString("TheLoai"),
                    rs.getInt("NamXuatBan")
                };
                searchModel.addRow(row);
                count++;
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách phù hợp!");
            } else {
                JOptionPane.showMessageDialog(this, "Tìm thấy " + count + " sách!");
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm sách: " + e.getMessage());
        }
    }

    // Mượn sách mới
    private void borrowNewBook() {
        try {
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không có kết nối database!");
                return;
            }
            
            String bookIdStr = JOptionPane.showInputDialog(this, "Nhập mã sách muốn mượn:");
            if (bookIdStr == null || bookIdStr.trim().isEmpty()) return;
            
            int bookId = Integer.parseInt(bookIdStr);
            
            // Kiểm tra sách có tồn tại không
            String checkSql = "SELECT TenSach FROM Sach WHERE IdSach = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách với mã: " + bookId);
                return;
            }
            
            String bookName = rs.getString("TenSach");
            
            // Kiểm tra sách đã được mượn chưa
            String checkBorrowedSql = "SELECT COUNT(*) FROM SachDuocMuon WHERE IdSach = ? AND TrangThaiSach = 'Đang mượn'";
            PreparedStatement checkBorrowedStmt = connection.prepareStatement(checkBorrowedSql);
            checkBorrowedStmt.setInt(1, bookId);
            ResultSet borrowedRs = checkBorrowedStmt.executeQuery();
            borrowedRs.next();
            int borrowedCount = borrowedRs.getInt(1);
            
            if (borrowedCount > 0) {
                JOptionPane.showMessageDialog(this, "Sách '" + bookName + "' đang được mượn!");
                return;
            }
            
            // Kiểm tra sinh viên đã mượn quá 3 sách chưa
            String checkLimitSql = "SELECT COUNT(*) FROM SachDuocMuon sdm " +
                                 "JOIN PhieuMuon pm ON sdm.IdPhieuMuon = pm.IdPhieuMuon " +
                                 "WHERE pm.IdSinhVien = ? AND sdm.TrangThaiSach = 'Đang mượn'";
            PreparedStatement checkLimitStmt = connection.prepareStatement(checkLimitSql);
            checkLimitStmt.setInt(1, currentUserId);
            ResultSet limitRs = checkLimitStmt.executeQuery();
            limitRs.next();
            int borrowedBooksCount = limitRs.getInt(1);
            
            if (borrowedBooksCount >= 3) {
                JOptionPane.showMessageDialog(this, "Bạn đã mượn tối đa 3 sách. Vui lòng trả sách trước khi mượn thêm!");
                return;
            }
            
            // Tạo phiếu mượn mới
            String insertPhieuSql = "INSERT INTO PhieuMuon (NgayLapPhieu, IdSinhVien) VALUES (?, ?)";
            PreparedStatement phieuStmt = connection.prepareStatement(insertPhieuSql, Statement.RETURN_GENERATED_KEYS);
            phieuStmt.setDate(1, new java.sql.Date(new Date().getTime()));
            phieuStmt.setInt(2, currentUserId);
            phieuStmt.executeUpdate();
            
            // Lấy ID phiếu mượn vừa tạo
            ResultSet generatedKeys = phieuStmt.getGeneratedKeys();
            int phieuMuonId = 0;
            if (generatedKeys.next()) {
                phieuMuonId = generatedKeys.getInt(1);
            }
            
            // Thêm vào bảng SachDuocMuon
            String insertSachSql = "INSERT INTO SachDuocMuon (NgayMuon, NgayTraThucTe, SoLuong, TrangThaiSach, IdSach, IdPhieuMuon) " +
                                 "VALUES (?, NULL, 1, 'Đang mượn', ?, ?)";
            PreparedStatement sachStmt = connection.prepareStatement(insertSachSql);
            sachStmt.setDate(1, new java.sql.Date(new Date().getTime()));
            sachStmt.setInt(2, bookId);
            sachStmt.setInt(3, phieuMuonId);
            sachStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Mượn sách '" + bookName + "' thành công!\nMã phiếu: " + phieuMuonId);
            
            // Đóng các statement
            rs.close();
            borrowedRs.close();
            limitRs.close();
            checkStmt.close();
            checkBorrowedStmt.close();
            checkLimitStmt.close();
            phieuStmt.close();
            sachStmt.close();
            
            // Cập nhật lại danh sách
            showBorrowedBooks();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã sách phải là số!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mượn sách: " + e.getMessage());
        }
    }

    // Trả sách
    private void returnBook() {
        try {
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không có kết nối database!");
                return;
            }
            
            String phieuIdStr = JOptionPane.showInputDialog(this, "Nhập mã phiếu mượn cần trả:");
            if (phieuIdStr == null || phieuIdStr.trim().isEmpty()) return;
            
            int phieuId = Integer.parseInt(phieuIdStr);
            
            // Kiểm tra phiếu mượn có tồn tại và thuộc về sinh viên này không
            String checkSql = "SELECT sdm.IdSach, s.TenSach " +
                            "FROM SachDuocMuon sdm " +
                            "JOIN PhieuMuon pm ON sdm.IdPhieuMuon = pm.IdPhieuMuon " +
                            "JOIN Sach s ON sdm.IdSach = s.IdSach " +
                            "WHERE pm.IdPhieuMuon = ? AND pm.IdSinhVien = ? AND sdm.TrangThaiSach = 'Đang mượn'";
            
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, phieuId);
            checkStmt.setInt(2, currentUserId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu mượn hợp lệ!");
                return;
            }
            
            int bookId = rs.getInt("IdSach");
            String bookName = rs.getString("TenSach");
            
            // Cập nhật trạng thái trả sách
            String updateSachMuonSql = "UPDATE SachDuocMuon SET NgayTraThucTe = ?, TrangThaiSach = 'Đã trả' WHERE IdPhieuMuon = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSachMuonSql);
            updateStmt.setDate(1, new java.sql.Date(new Date().getTime()));
            updateStmt.setInt(2, phieuId);
            updateStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Trả sách '" + bookName + "' thành công!");
            
            // Đóng các statement
            rs.close();
            checkStmt.close();
            updateStmt.close();
            
            // Cập nhật lại danh sách
            showBorrowedBooks();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã phiếu phải là số!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi trả sách: " + e.getMessage());
        }
    }

    // Đổi mật khẩu
    private void changePassword() {
        try {
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không có kết nối database!");
                return;
            }
            
            String currentPass = new String(ptxt1.getPassword());
            String newPass = new String(ptxt2.getPassword());
            String confirmPass = new String(ptxt3.getPassword());
            
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
                return;
            }
            
            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!");
                return;
            }
            
            // Kiểm tra mật khẩu hiện tại - sử dụng bảng taikhoan
            String checkSql = "SELECT COUNT(*) FROM taikhoan WHERE IdTaiKhoan = ? AND MatKhau = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, currentUserId);
            checkStmt.setString(2, currentPass);
            ResultSet rs = checkStmt.executeQuery();
            
            rs.next();
            if (rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!");
                return;
            }
            
            // Cập nhật mật khẩu mới
            String updateSql = "UPDATE taikhoan SET MatKhau = ? WHERE IdTaiKhoan = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setString(1, newPass);
            updateStmt.setInt(2, currentUserId);
            int rowsUpdated = updateStmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                // Xóa trắng các trường
                ptxt1.setText("");
                ptxt2.setText("");
                ptxt3.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!");
            }
            
            rs.close();
            checkStmt.close();
            updateStmt.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }

    // Thiết lập sự kiện
    private void setupEventListeners() {
        // Sự kiện cho nút tìm kiếm
        btntk.addActionListener(e -> {
            if (rdbtn1.isSelected()) {
                searchBooks();
            } else if (rdbtn2.isSelected()) {
                searchByCategory();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn loại tìm kiếm!");
            }
        });
        
        // Sự kiện cho checkbox hiển thị mật khẩu
        check1.addActionListener(e -> {
            if (check1.isSelected()) {
                ptxt1.setEchoChar((char)0); // Hiển thị mật khẩu
            } else {
                ptxt1.setEchoChar('*'); // Ẩn mật khẩu
            }
        });
        
        check2.addActionListener(e -> {
            if (check2.isSelected()) {
                ptxt2.setEchoChar((char)0); // Hiển thị mật khẩu
            } else {
                ptxt2.setEchoChar('*'); // Ẩn mật khẩu
            }
        });
        
        check3.addActionListener(e -> {
            if (check3.isSelected()) {
                ptxt3.setEchoChar((char)0); // Hiển thị mật khẩu
            } else {
                ptxt3.setEchoChar('*'); // Ẩn mật khẩu
            }
        });
        
        // Sự kiện cho nút đổi mật khẩu
        btn6.addActionListener(e -> changePassword());
        
        // Sự kiện cho nút hủy đổi mật khẩu
        btn7.addActionListener(e -> {
            ptxt1.setText("");
            ptxt2.setText("");
            ptxt3.setText("");
            // Đặt lại về trạng thái ẩn mật khẩu
            ptxt1.setEchoChar('*');
            ptxt2.setEchoChar('*');
            ptxt3.setEchoChar('*');
            check1.setSelected(false);
            check2.setSelected(false);
            check3.setSelected(false);
        });
        
        // Sự kiện cho radio button tìm kiếm
        rdbtn1.addActionListener(e -> {
            if (rdbtn1.isSelected()) {
                txt1.setEnabled(true);
                txt2.setEnabled(false);
                cb1.setEnabled(false);
                txt1.requestFocus();
            }
        });
        
        rdbtn2.addActionListener(e -> {
            if (rdbtn2.isSelected()) {
                txt1.setEnabled(false);
                txt2.setEnabled(true);
                cb1.setEnabled(true);
                loadCategories();
                txt2.requestFocus();
            }
        });
    }

    // Tải danh mục thể loại
    private void loadCategories() {
        try {
            if (connection == null || connection.isClosed()) return;
            
            String sql = "SELECT DISTINCT TheLoai FROM Sach";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            cb1.removeAllItems();
            cb1.addItem("-- Tất cả thể loại --");
            while (rs.next()) {
                cb1.addItem(rs.getString("TheLoai"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thể loại: " + e.getMessage());
        }
    }

    // Tìm sách theo thể loại
    private void searchByCategory() {
        try {
            if (connection == null || connection.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không có kết nối database!");
                return;
            }
            
            String category = cb1.getSelectedItem().toString();
            String author = txt2.getText().trim();
            
            if (category.equals("-- Tất cả thể loại --") && author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tác giả hoặc chọn thể loại cụ thể!");
                return;
            }
            
            // Sử dụng tb1 có sẵn trong form
            DefaultTableModel searchModel = new DefaultTableModel();
            searchModel.addColumn("Mã Sách");
            searchModel.addColumn("Tên Sách");
            searchModel.addColumn("Tác Giả");
            searchModel.addColumn("Thể Loại");
            searchModel.addColumn("Năm XB");
            
            tb1.setModel(searchModel);
            
            // Query tìm sách theo thể loại và tác giả
            String sql;
            PreparedStatement pstmt;
            
            if (category.equals("-- Tất cả thể loại --")) {
                sql = "SELECT s.IdSach, s.TenSach, tg.TenTacGia, s.TheLoai, s.NamXuatBan " +
                      "FROM Sach s " +
                      "JOIN TacGia tg ON s.IdTacGia = tg.IdTacGia " +
                      "WHERE tg.TenTacGia LIKE ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, "%" + author + "%");
            } else {
                sql = "SELECT s.IdSach, s.TenSach, tg.TenTacGia, s.TheLoai, s.NamXuatBan " +
                      "FROM Sach s " +
                      "JOIN TacGia tg ON s.IdTacGia = tg.IdTacGia " +
                      "WHERE s.TheLoai = ? AND tg.TenTacGia LIKE ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, category);
                pstmt.setString(2, "%" + author + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("IdSach"),
                    rs.getString("TenSach"),
                    rs.getString("TenTacGia"),
                    rs.getString("TheLoai"),
                    rs.getInt("NamXuatBan")
                };
                searchModel.addRow(row);
                count++;
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách phù hợp!");
            } else {
                JOptionPane.showMessageDialog(this, "Tìm thấy " + count + " sách!");
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm sách: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlCards = new javax.swing.JPanel();
        pnlstart = new javax.swing.JPanel();
        pnlCard1 = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        rdbtn1 = new javax.swing.JRadioButton();
        rdbtn2 = new javax.swing.JRadioButton();
        txt2 = new javax.swing.JTextField();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        cb1 = new javax.swing.JComboBox<>();
        btntk = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb1 = new javax.swing.JTable();
        pnlCard2 = new javax.swing.JPanel();
        lbl5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl2 = new javax.swing.JTable();
        pnlCard3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ptxt1 = new javax.swing.JPasswordField();
        ptxt2 = new javax.swing.JPasswordField();
        ptxt3 = new javax.swing.JPasswordField();
        check1 = new javax.swing.JCheckBox();
        check2 = new javax.swing.JCheckBox();
        check3 = new javax.swing.JCheckBox();
        btn6 = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        pnlmenu = new javax.swing.JPanel();
        pnlflow = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btn3 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        pnlCards.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlCards.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlstart.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                pnlstartAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        javax.swing.GroupLayout pnlstartLayout = new javax.swing.GroupLayout(pnlstart);
        pnlstart.setLayout(pnlstartLayout);
        pnlstartLayout.setHorizontalGroup(
            pnlstartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
        );
        pnlstartLayout.setVerticalGroup(
            pnlstartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );

        pnlCards.add(pnlstart, new org.netbeans.lib.awtextra.AbsoluteConstraints(-5, -8, 930, 560));

        pnlCard1.setBackground(new java.awt.Color(255, 255, 255));

        lbl1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl1.setText("TÌM KIẾM");

        rdbtn1.setText("theo tên");
        rdbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtn1ActionPerformed(evt);
            }
        });

        rdbtn2.setText("theo loại");
        rdbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtn2ActionPerformed(evt);
            }
        });

        lbl2.setText("Tên Sách");

        lbl3.setText("Tác giả");

        lbl4.setText("Thể loại");

        cb1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb1ActionPerformed(evt);
            }
        });

        btntk.setText("tìm kiếm");
        btntk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntkActionPerformed(evt);
            }
        });

        tb1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Năm XB"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tb1);

        javax.swing.GroupLayout pnlCard1Layout = new javax.swing.GroupLayout(pnlCard1);
        pnlCard1.setLayout(pnlCard1Layout);
        pnlCard1Layout.setHorizontalGroup(
            pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard1Layout.createSequentialGroup()
                .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCard1Layout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(btntk))
                    .addGroup(pnlCard1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlCard1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl2)
                            .addComponent(lbl3)
                            .addComponent(lbl4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCard1Layout.createSequentialGroup()
                                .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt1)
                                    .addComponent(txt2)
                                    .addComponent(cb1, 0, 271, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdbtn1)
                                    .addComponent(rdbtn2)))
                            .addGroup(pnlCard1Layout.createSequentialGroup()
                                .addGap(98, 98, 98)
                                .addComponent(lbl1)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCard1Layout.setVerticalGroup(
            pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard1Layout.createSequentialGroup()
                .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCard1Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(rdbtn1))
                    .addGroup(pnlCard1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(lbl1)
                        .addGap(58, 58, 58)
                        .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl3)
                            .addComponent(rdbtn2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl4)
                    .addComponent(cb1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btntk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlCards.add(pnlCard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 870, -1));

        pnlCard2.setBackground(new java.awt.Color(255, 255, 255));

        lbl5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl5.setText("THÔNG TIN MƯỢN SÁCH");

        tbl2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã Sách", "Tên Sách", "Tác Giả", "Thể Loại", "Năm XB"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbl2);

        javax.swing.GroupLayout pnlCard2Layout = new javax.swing.GroupLayout(pnlCard2);
        pnlCard2.setLayout(pnlCard2Layout);
        pnlCard2Layout.setHorizontalGroup(
            pnlCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(pnlCard2Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(lbl5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCard2Layout.setVerticalGroup(
            pnlCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbl5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlCards.add(pnlCard2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 870, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("ĐỔI MẬT KHẨU");

        jLabel3.setText("nhập mật khẩu cũ: ");

        jLabel4.setText("nhập mật khẩu mới: ");

        jLabel5.setText("nhập lại mật khẩu: ");

        check1.setText("Hiển thị");
        check1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check1ActionPerformed(evt);
            }
        });

        check2.setText("Hiển thị");
        check2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check2ActionPerformed(evt);
            }
        });

        check3.setText("Hiển thị");
        check3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check3ActionPerformed(evt);
            }
        });

        btn6.setText("đổi");
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn6ActionPerformed(evt);
            }
        });

        btn7.setText("huỷ");
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlCard3Layout = new javax.swing.GroupLayout(pnlCard3);
        pnlCard3.setLayout(pnlCard3Layout);
        pnlCard3Layout.setHorizontalGroup(
            pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard3Layout.createSequentialGroup()
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCard3Layout.createSequentialGroup()
                        .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ptxt3)
                            .addComponent(ptxt2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(check2)
                            .addComponent(check3)))
                    .addGroup(pnlCard3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ptxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(check1))
                    .addGroup(pnlCard3Layout.createSequentialGroup()
                        .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCard3Layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(jLabel1))
                            .addGroup(pnlCard3Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(btn6)
                                .addGap(39, 39, 39)
                                .addComponent(btn7)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlCard3Layout.setVerticalGroup(
            pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ptxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(ptxt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(ptxt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn6)
                    .addComponent(btn7)))
        );

        pnlCards.add(pnlCard3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pnlmenu.setBackground(new java.awt.Color(0, 0, 0));
        pnlmenu.setToolTipText("");
        pnlmenu.setFocusCycleRoot(true);
        pnlmenu.setFocusable(false);
        pnlmenu.setName("pnl1"); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_1.jpg"))); // NOI18N

        javax.swing.GroupLayout pnlflowLayout = new javax.swing.GroupLayout(pnlflow);
        pnlflow.setLayout(pnlflowLayout);
        pnlflowLayout.setHorizontalGroup(
            pnlflowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlflowLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(0, 0, 0))
        );
        pnlflowLayout.setVerticalGroup(
            pnlflowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlflowLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlmenu.add(pnlflow);

        btn3.setText("tìm sách");
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });
        pnlmenu.add(btn3);

        btn2.setText("thông tin mượn sách");
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });
        pnlmenu.add(btn2);

        btn1.setText("đổi mật khẩu");
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });
        pnlmenu.add(btn1);

        btn4.setText("đăng xuất");
        btn4.setToolTipText("");
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });
        pnlmenu.add(btn4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlmenu, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(553, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 165, Short.MAX_VALUE)
                    .addComponent(pnlCards, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlmenu, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        // TODO add your handling code here:
        pnlCard1.setVisible(false);
        pnlCard2.setVisible(true);
        pnlCard3.setVisible(false);
        pnlstart.setVisible(false);
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        // TODO add your handling code here:
        pnlCard1.setVisible(true);
        pnlCard2.setVisible(false);
        pnlCard3.setVisible(false);
        pnlstart.setVisible(false);
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        // TODO add your handling code here:
        pnlCard1.setVisible(false);
        pnlCard2.setVisible(false);
        pnlCard3.setVisible(true);
        pnlstart.setVisible(false);
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        // TODO add your handling code here:
         int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn đăng xuất?", 
            "Xác nhận đăng xuất", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
            System.exit(0);
        }
    }

    // Thêm nút mượn sách và trả sách vào menu
    /*private void addMenuButtons() {
        javax.swing.JButton btnMuonSach = new javax.swing.JButton("Mượn sách");
        btnMuonSach.addActionListener(e -> borrowNewBook());
        jPanel1.add(btnMuonSach);
        
        javax.swing.JButton btnTraSach = new javax.swing.JButton("Trả sách");
        btnTraSach.addActionListener(e -> returnBook());
        jPanel1.add(btnTraSach);
        
        // Refresh layout
        jPanel1.revalidate();
        jPanel1.repaint();
    }//GEN-LAST:event_btn4ActionPerformed
*/
    private void rdbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtn2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbtn2ActionPerformed

    private void btntkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntkActionPerformed
        // TODO add your handling code here:
        if (rdbtn1.isSelected()) {
            searchBooks();
        } else if (rdbtn2.isSelected()) {
            searchByCategory();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại tìm kiếm!");
        }
    }//GEN-LAST:event_btntkActionPerformed

    private void rdbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtn1ActionPerformed
        // TODO add your handling code here:
        if (rdbtn1.isSelected()) {
            txt1.setEnabled(true);
            txt2.setEnabled(false);
            cb1.setEnabled(false);
            txt1.requestFocus();
        }
    }//GEN-LAST:event_rdbtn1ActionPerformed

    private void cb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb1ActionPerformed

    private void pnlstartAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_pnlstartAncestorAdded
        // TODO add your handling code here:
        pnlCard1.setVisible(false);
        pnlCard2.setVisible(false);
        pnlCard3.setVisible(false);
        pnlstart.setVisible(true);
    }//GEN-LAST:event_pnlstartAncestorAdded

    private void check3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check3ActionPerformed
        // TODO add your handling code here:
         if (check3.isSelected()) {
            ptxt3.setEchoChar((char)0); // Hiển thị mật khẩu
        } else {
            ptxt3.setEchoChar('*'); // Ẩn mật khẩu
        }
    }//GEN-LAST:event_check3ActionPerformed

    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
        // TODO add your handling code here:
         changePassword();
    }//GEN-LAST:event_btn6ActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        // TODO add your handling code here:
        ptxt1.setText("");
        ptxt2.setText("");
        ptxt3.setText("");
        // Đặt lại về trạng thái ẩn mật khẩu
        ptxt1.setEchoChar('*');
        ptxt2.setEchoChar('*');
        ptxt3.setEchoChar('*');
        check1.setSelected(false);
        check2.setSelected(false);
        check3.setSelected(false);
    }//GEN-LAST:event_btn7ActionPerformed

    private void check1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check1ActionPerformed
        // TODO add your handling code here:
         if (check1.isSelected()) {
            ptxt1.setEchoChar((char)0); // Hiển thị mật khẩu
        } else {
            ptxt1.setEchoChar('*'); // Ẩn mật khẩu
        }
    }//GEN-LAST:event_check1ActionPerformed

    private void check2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check2ActionPerformed
        // TODO add your handling code here:
         if (check2.isSelected()) {
            ptxt2.setEchoChar((char)0); // Hiển thị mật khẩu
        } else {
            ptxt2.setEchoChar('*'); // Ẩn mật khẩu
        }
    }//GEN-LAST:event_check2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
             @Override
             public void run() {
                 new home().setVisible(true);
             }
         });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btntk;
    private javax.swing.JComboBox<String> cb1;
    private javax.swing.JCheckBox check1;
    private javax.swing.JCheckBox check2;
    private javax.swing.JCheckBox check3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JPanel pnlCard2;
    private javax.swing.JPanel pnlCard3;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPanel pnlflow;
    private javax.swing.JPanel pnlmenu;
    private javax.swing.JPanel pnlstart;
    private javax.swing.JPasswordField ptxt1;
    private javax.swing.JPasswordField ptxt2;
    private javax.swing.JPasswordField ptxt3;
    private javax.swing.JRadioButton rdbtn1;
    private javax.swing.JRadioButton rdbtn2;
    private javax.swing.JTable tb1;
    private javax.swing.JTable tbl2;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt2;
    // End of variables declaration//GEN-END:variables
}
