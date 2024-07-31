package ptit.tvnkhanh.tutor_center_management.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import ptit.tvnkhanh.tutor_center_management.R;

public class CustomToolbar extends Toolbar {
    private ImageView backButton;
    private TextView toolbarTitle;
    private OnBackPressListener listener;

    public CustomToolbar(Context context, OnBackPressListener listener) {
        super(context);
        initialize();
        this.listener = listener;
    }

    public CustomToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        backButton = new ImageView(getContext());
        backButton.setImageResource(R.drawable.ic_back_arrow);
        backButton.setPadding(16, 0, 16, 0);
        backButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onBackPress();
            }
        });

        toolbarTitle = new TextView(getContext());
        toolbarTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        toolbarTitle.setTextSize(16);
        toolbarTitle.setTypeface(toolbarTitle.getTypeface(), Typeface.BOLD);
        toolbarTitle.setGravity(View.TEXT_ALIGNMENT_CENTER);

        addView(backButton);
        addView(toolbarTitle);
    }

    public void setTitle(String title) {
        toolbarTitle.setText(title);
    }

    public void setListener(OnBackPressListener listener) {
        this.listener = listener;
    }

    public interface OnBackPressListener {
        void onBackPress();
    }
}
