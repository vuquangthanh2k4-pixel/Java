/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.TaiKhoanAdminDK;
import Model.TaiKhoanAdmin;
import View.Administrator;

/**
 *
 * @author My Computer
 */
public class EncryptMatKhau {
    private TaiKhoanAdmin taiKhoanAdmin;
    private Administrator administrator;
    private TaiKhoanAdminDK taiKhoanAdminDK;
    public EncryptMatKhau(TaiKhoanAdmin model, Administrator view) {
        this.taiKhoanAdmin = model;
        this.administrator = view;
        this.taiKhoanAdminDK = new TaiKhoanAdminDK();
    }

}
