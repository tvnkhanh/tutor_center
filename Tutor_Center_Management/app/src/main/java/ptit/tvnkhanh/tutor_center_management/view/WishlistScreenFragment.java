package ptit.tvnkhanh.tutor_center_management.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ptit.tvnkhanh.tutor_center_management.R;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentWishlistScreenBinding;

public class WishlistScreenFragment extends Fragment {

    private FragmentWishlistScreenBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWishlistScreenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}