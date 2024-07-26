package ptit.tvnkhanh.tutor_center_management.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ClickSpan extends ClickableSpan {
    private boolean withUnderline;
    private OnClickListener listener;
    private int textColor;

    public interface OnClickListener {
        void onClick();
    }

    public static void clickify(TextView view, final String clickableText,
                                final OnClickListener listener, int textColor) {
        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(false, listener, textColor);

        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) {
            return;
        }

        SpannableString spannableString = SpannableString.valueOf(text);
        spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spannableString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public ClickSpan(boolean withUnderline, OnClickListener listener, int textColor) {
        this.withUnderline = withUnderline;
        this.listener = listener;
        this.textColor = textColor;
    }

    @Override
    public void onClick(@NonNull View view) {
        if (listener != null) {
            listener.onClick();
        }
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(withUnderline);
        ds.setColor(textColor);
    }
}
