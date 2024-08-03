package ptit.tvnkhanh.tutor_center_management;

import ptit.tvnkhanh.tutor_center_management.models.Account;
import ptit.tvnkhanh.tutor_center_management.models.Client;
import ptit.tvnkhanh.tutor_center_management.models.Staff;
import ptit.tvnkhanh.tutor_center_management.models.Tutor;

public class UserSession {
    private static UserSession instance;
    private Account account;
    private Staff staff;
    private Tutor tutor;
    private Client client;

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

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void clear() {
        this.account = null;
        this.staff = null;
        this.tutor = null;
        this.client = null;
    }
}
