package ptit.tvnkhanh.tutor_center_management.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ptit.tvnkhanh.tutor_center_management.UserSession;
import ptit.tvnkhanh.tutor_center_management.databinding.FragmentClassScreenBinding;

public class ClassScreenFragment extends Fragment {

    private FragmentClassScreenBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClassScreenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}