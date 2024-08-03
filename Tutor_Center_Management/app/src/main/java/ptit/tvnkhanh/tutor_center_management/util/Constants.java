package ptit.tvnkhanh.tutor_center_management.util;

import java.util.Arrays;
import java.util.List;

public class Constants {
    // URL
    public static final String BASE_URL = "http://192.168.1.37:3000/";

    // Login role
    public static final String ROLE_ADMIN_ID = "668806bab4ac3adff4591bf1";
    public static final String ROLE_CLIENT_ID = "6688f413b73f723b03f26385";
    public static final String ROLE_TUTOR_ID = "6688f420b73f723b03f26386";

    // Navigation
    public static final String NAVIGATION_HOME = "HOME";
    public static final String NAVIGATION_CLASSES = "CLASSES";
    public static final String NAVIGATION_TUTORS = "TUTORS";
    public static final String NAVIGATION_COURSES = "COURSES";
    public static final String NAVIGATION_STATISTICS = "STATISTICS";
    public static final String NAVIGATION_PROFILE = "PROFILE";
    public static final String NAVIGATION_TEACHER_DETAIL = "TEACHER_DETAIL";
    public static final String NAVIGATION_HIRE_TUTOR_DETAIL = "NAVIGATION_HIRE_TUTOR_DETAIL";
    public static final String NAVIGATION_PAYMENT = "NAVIGATION_PAYMENT";
    public static final String NAVIGATION_SEARCH = "NAVIGATION_SEARCH";

    // Sign up role
    public static final String SIGN_UP_ROLE = "ROLE";
    public static final String SIGN_UP_ROLE_STUDENT = "Student";
    public static final String SIGN_UP_ROLE_TUTOR = "Tutor";

    public static final String X_AUTH_TOKEN = "x-auth-token";

    public static final String ACTION_USER_DATA_UPDATED = "ACTION_USER_DATA_UPDATED";
    public static final String ACTION_TUTOR_DATA_CLICKED = "ACTION_TUTOR_DATA_CLICKED";

    public static final String CLASS_STATUS_ACTIVE = "active";
    public static final String CLASS_STATUS_INACTIVE = "inactive";
    public static final String CLASS_STATUS_PENDING = "pending";
    public static final String CLASS_STATUS_REJECTED = "rejected";
    public static final String CLASS_STATUS_COMPLETED = "completed";;

    public static final String TUTOR_STATUS_PENDING = "pending";
    public static final String TUTOR_STATUS_APPROVED = "approved";
    public static final String TUTOR_STATUS_REJECTED = "rejected";

    public static final String PAYMENT_STATUS_UNPAID = "unpaid";
    public static final String PAYMENT_STATUS_PAID = "paid";

    public static final String PAYMENT_METHOD_CASH_ID = "66ad24a54636a1a7f67fd444";
    public static final String PAYMENT_METHOD_MOMO_ID = "66ad24d14636a1a7f67fd446";
    public static final String PAYMENT_METHOD_VISA_ID = "66ad24d74636a1a7f67fd448";
    public static final String PAYMENT_METHOD_CASH_NAME = "Cash";
    public static final String PAYMENT_METHOD_MOMO_NAME = "MOMO";
    public static final String PAYMENT_METHOD_VISA_NAME = "VISA";
    public static final List<String> PAYMENT_METHOD_LIST = Arrays.asList(PAYMENT_METHOD_CASH_NAME, PAYMENT_METHOD_MOMO_NAME, PAYMENT_METHOD_VISA_NAME);

    public static final String CLASS_FORM_ONLINE = "Online";
    public static final String CLASS_FORM_OFFLINE = "Offline";
    public static final List<String> CLASS_FORM_LIST = Arrays.asList(CLASS_FORM_OFFLINE, CLASS_FORM_ONLINE);

    public static final String TUTOR_DATA_EXTRA_KEY = "TUTOR_DATA_EXTRA_KEY";
}
