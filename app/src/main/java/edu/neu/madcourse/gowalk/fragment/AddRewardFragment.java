package edu.neu.madcourse.gowalk.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.Reward;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

public class AddRewardFragment extends DialogFragment {

    private RewardListViewModel viewModel;
    private AddRewardFragmentListener addRewardFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            addRewardFragmentListener = (AddRewardFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement AddRewardFragmentListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(requireActivity()).get(RewardListViewModel.class);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_reward, null);

        dialogBuilder.setTitle("Add your link")
                .setView(view)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", (dialog, which) -> dismiss());

        Dialog dialog = dialogBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                EditText nameEditText = view.findViewById(R.id.reward_name_text);
                EditText pointEditText = view.findViewById(R.id.reward_points_text);

                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(view -> {
                    String name = nameEditText.getText().toString().trim();
                    String pointStr = pointEditText.getText().toString().trim();
                    if (name.isEmpty() || pointStr.isEmpty()) {
                        addRewardFragmentListener.showAddRewardEmptyValueInfo();
                    } else {
                        Reward reward = new Reward(name, Integer.valueOf(pointStr));
                        viewModel.addReward(reward);
                        dismiss();
                        addRewardFragmentListener.showAddRewardSuccessInfo();
                    }
                });
            }
        });
        return dialog;
    }

    public interface AddRewardFragmentListener {
        void showAddRewardEmptyValueInfo();

        void showAddRewardSuccessInfo();
    }

}
