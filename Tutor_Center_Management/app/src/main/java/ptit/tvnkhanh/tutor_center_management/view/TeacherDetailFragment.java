package ptit.tvnkhanh.tutor_center_management.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ptit.tvnkhanh.tutor_center_management.databinding.FragmentTeacherDetailBinding;

public class TeacherDetailFragment extends Fragment {

    private FragmentTeacherDetailBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}