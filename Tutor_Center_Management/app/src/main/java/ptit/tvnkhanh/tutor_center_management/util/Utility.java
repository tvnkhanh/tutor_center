package ptit.tvnkhanh.tutor_center_management.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class Utility {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static float dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return dp * displayMetrics.density;
    }

    public static SpannableString boldText(String text) {
        int startIndex = text.indexOf('#');
        int endIndex = text.indexOf('#', startIndex + 1);

        if (startIndex == -1 || endIndex == -1 || startIndex == endIndex) {
            return new SpannableString(text);
        }

        SpannableString spannableString = new SpannableString(text.replace("#", ""));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
