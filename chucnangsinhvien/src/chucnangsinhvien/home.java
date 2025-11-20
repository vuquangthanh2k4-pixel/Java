/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package chucnangsinhvien;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    }
 private void initDatabase() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLThuVien;encrypt=true;trustServerCertificate=true";
            String username = "sa";
            String password = "123456";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage());
        }
    }

    // Thiết lập bảng
    private void setupTable() {
        tableModel = new DefaultTableModel();
        // Tạo JTable và thêm vào pnlCard2 (thông tin mượn sách)
        javax.swing.JTable table = new javax.swing.JTable(tableModel);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
        
        pnlCard2.removeAll();
        pnlCard2.setLayout(new java.awt.BorderLayout());
        pnlCard2.add(scrollPane, java.awt.BorderLayout.CENTER);
        
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
            String searchTerm = JOptionPane.showInputDialog(this, "Nhập tên sách cần tìm:");
            if (searchTerm == null || searchTerm.trim().isEmpty()) return;
            
            // Xóa pnlCard3 và thêm bảng tìm kiếm
            pnlCard3.removeAll();
            pnlCard3.setLayout(new java.awt.BorderLayout());
            
            DefaultTableModel searchModel = new DefaultTableModel();
            searchModel.addColumn("Mã Sách");
            searchModel.addColumn("Tên Sách");
            searchModel.addColumn("Tác Giả");
            searchModel.addColumn("Tình Trạng");
            
            javax.swing.JTable searchTable = new javax.swing.JTable(searchModel);
            javax.swing.JScrollPane searchScroll = new javax.swing.JScrollPane(searchTable);
            pnlCard3.add(searchScroll, java.awt.BorderLayout.CENTER);
            
            String sql = "SELECT IdSach, TenSach, TacGia, TinhTrang FROM Sach " +
                        "WHERE TenSach LIKE ? OR TacGia LIKE ?";
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("IdSach"),
                    rs.getString("TenSach"),
                    rs.getString("TacGia"),
                    rs.getString("TinhTrang")
                };
                searchModel.addRow(row);
                count++;
            }
            
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách phù hợp!");
            } else {
                JOptionPane.showMessageDialog(this, "Tìm thấy " + count + " sách!");
                // Chuyển sang tab tìm sách
                java.awt.CardLayout cardLayout = (java.awt.CardLayout) pnlCards.getLayout();
                cardLayout.show(pnlCards, "card4");
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
            String bookIdStr = JOptionPane.showInputDialog(this, "Nhập mã sách muốn mượn:");
            if (bookIdStr == null || bookIdStr.trim().isEmpty()) return;
            
            int bookId = Integer.parseInt(bookIdStr);
            
            // Kiểm tra sách có tồn tại và có sẵn không
            String checkSql = "SELECT TenSach, TinhTrang FROM Sach WHERE IdSach = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách với mã: " + bookId);
                return;
            }
            
            String bookName = rs.getString("TenSach");
            String status = rs.getString("TinhTrang");
            
            if (!"Có sẵn".equals(status)) {
                JOptionPane.showMessageDialog(this, "Sách '" + bookName + "' hiện không có sẵn!");
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
            
            // Cập nhật trạng thái sách
            String updateSachSql = "UPDATE Sach SET TinhTrang = 'Đang mượn' WHERE IdSach = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSachSql);
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Mượn sách '" + bookName + "' thành công!\nMã phiếu: " + phieuMuonId);
            
            // Đóng các statement
            rs.close();
            checkStmt.close();
            phieuStmt.close();
            sachStmt.close();
            updateStmt.close();
            
            // Cập nhật lại danh sách
            showBorrowedBooks();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã sách phải là số!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi mượn sách: " + e.getMessage());
        }
    }

    // Đổi mật khẩu
    private void changePassword() {
        try {
            String currentPass = JOptionPane.showInputDialog(this, "Nhập mật khẩu hiện tại:");
            if (currentPass == null) return;
            
            String newPass = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:");
            if (newPass == null) return;
            
            String confirmPass = JOptionPane.showInputDialog(this, "Xác nhận mật khẩu mới:");
            if (confirmPass == null) return;
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
                return;
            }
            
            // Kiểm tra mật khẩu hiện tại (giả sử có bảng Users)
            String checkSql = "SELECT COUNT(*) FROM Users WHERE IdSinhVien = ? AND Password = ?";
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
            String updateSql = "UPDATE Users SET Password = ? WHERE IdSinhVien = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setString(1, newPass);
            updateStmt.setInt(2, currentUserId);
            updateStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            
            rs.close();
            checkStmt.close();
            updateStmt.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đổi mật khẩu: " + e.getMessage());
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

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlCards = new javax.swing.JPanel();
        pnlCard1 = new javax.swing.JPanel();
        pnlCard2 = new javax.swing.JPanel();
        pnlCard3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlflow = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btn3 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        pnlCards.setLayout(new java.awt.CardLayout());

        pnlCard1.setBackground(new java.awt.Color(153, 153, 255));

        javax.swing.GroupLayout pnlCard1Layout = new javax.swing.GroupLayout(pnlCard1);
        pnlCard1.setLayout(pnlCard1Layout);
        pnlCard1Layout.setHorizontalGroup(
            pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlCard1Layout.setVerticalGroup(
            pnlCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );

        pnlCards.add(pnlCard1, "card2");

        pnlCard2.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout pnlCard2Layout = new javax.swing.GroupLayout(pnlCard2);
        pnlCard2.setLayout(pnlCard2Layout);
        pnlCard2Layout.setHorizontalGroup(
            pnlCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlCard2Layout.setVerticalGroup(
            pnlCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );

        pnlCards.add(pnlCard2, "card3");

        pnlCard3.setBackground(new java.awt.Color(102, 102, 255));

        javax.swing.GroupLayout pnlCard3Layout = new javax.swing.GroupLayout(pnlCard3);
        pnlCard3.setLayout(pnlCard3Layout);
        pnlCard3Layout.setHorizontalGroup(
            pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlCard3Layout.setVerticalGroup(
            pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );

        pnlCards.add(pnlCard3, "card4");

        jSplitPane1.setRightComponent(pnlCards);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setToolTipText("");
        jPanel1.setFocusCycleRoot(true);
        jPanel1.setFocusable(false);
        jPanel1.setName("pnl1"); // NOI18N

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

        jPanel1.add(pnlflow);

        btn3.setText("tìm sách");
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });
        jPanel1.add(btn3);

        btn2.setText("thông tin mượn sách");
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });
        jPanel1.add(btn2);

        btn1.setText("đổi mật khẩu");
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });
        jPanel1.add(btn1);

        btn4.setText("đăng xuất");
        btn4.setToolTipText("");
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });
        jPanel1.add(btn4);

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 335, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        // TODO add your handling code here:
        showBorrowedBooks();
        java.awt.CardLayout cardLayout = (java.awt.CardLayout) pnlCards.getLayout();
        cardLayout.show(pnlCards, "card3");
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        // TODO add your handling code here:
        searchBooks();
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        // TODO add your handling code here:
        changePassword();
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
                e.printStackTrace();
            }
            System.exit(0);
        }
    }//GEN-LAST:event_btn4ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JPanel pnlCard2;
    private javax.swing.JPanel pnlCard3;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPanel pnlflow;
    // End of variables declaration//GEN-END:variables
}
