package ptit.tvnkhanh.tutor_center_management;

import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.models.Staff;

public class UserSession {
    private static UserSession instance;
    private Account account;
    private Staff staff;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
