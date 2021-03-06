package com.ejsfbu.app_main.DialogFragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ejsfbu.app_main.Models.BankAccount;
import com.ejsfbu.app_main.Models.Goal;
import com.ejsfbu.app_main.Models.User;
import com.ejsfbu.app_main.R;

import java.util.ArrayList;
import java.util.List;

public class SetUpAutoPaymentDialogFragment extends DialogFragment {

    Context context;
    static User currentUser;
    Button bSetUpAutoPaymentConfirm;
    Button bSetUpAutoPaymentCancel;
    EditText etAutoPaymentAmount;
    Spinner spFrequency;
    Spinner spTimesRepeated;
    Spinner spAutoPayBanks;
    String bankName;
    Double amount;
    String timesRepeated;
    String frequency;

    public SetUpAutoPaymentDialogFragment() {

    }

    public static SetUpAutoPaymentDialogFragment newInstance(Goal goal, User user) {
        SetUpAutoPaymentDialogFragment setUpAutoPaymentDialogFragment = new SetUpAutoPaymentDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("goal", goal);
        args.putParcelable("user", user);
        setUpAutoPaymentDialogFragment.setArguments(args);
        currentUser = user;
        return setUpAutoPaymentDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.fragment_automatic_payment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bSetUpAutoPaymentConfirm = view.findViewById(R.id.bSetUpAutoPaymentConfirm);
        bSetUpAutoPaymentCancel = view.findViewById(R.id.bSetUpAutoPaymentCancel);
        spFrequency = view.findViewById(R.id.spFrequency);
        spTimesRepeated = view.findViewById(R.id.spTimesRepeated);
        spAutoPayBanks = view.findViewById(R.id.spAutoPayBanks);
        etAutoPaymentAmount = view.findViewById(R.id.etAutoPaymentAmount);

        setOnClickListeners();
        setAdapters();
    }

    public void setOnClickListeners() {
        bSetUpAutoPaymentConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bankName = spAutoPayBanks.getSelectedItem().toString();

                String amountString = etAutoPaymentAmount.getText().toString();
                if (amountString.equals("")) {
                    Toast.makeText(context, "Please enter a value.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    amount = Double.valueOf(amountString);
                }

                timesRepeated = spTimesRepeated.getSelectedItem().toString();
                frequency = spFrequency.getSelectedItem().toString();
                if (Integer.valueOf(timesRepeated) != 1) {
                    frequency = frequency + "s";
                }

                sendBackResult();
                return;
            }
        });

        bSetUpAutoPaymentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setAdapters() {
        ArrayList<String> numArray = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            StringBuilder num = new StringBuilder();
            num.append(i);
            numArray.add(num.toString());
        }
        ArrayAdapter<String> numAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, numArray);
        numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimesRepeated.setAdapter(numAdapter);

        ArrayList<String> frequency = new ArrayList<>();
        frequency.add("Day");
        frequency.add("Week");
        frequency.add("Month");
        frequency.add("Year");
        ArrayAdapter<String> frequencyAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, frequency);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(frequencyAdapter);

        List<BankAccount> banks = new ArrayList<>();
        banks = currentUser.getVerifiedBanks();
        ArrayList<String> array = new ArrayList<>();
        if (banks != null) {
            for (BankAccount bank : banks) {
                array.add(bank.getBankName());
            }
        }
        if (array.size() == 0) {
            array.add("No verified banks available");
        }
        ArrayAdapter<String> bankAdapter =
                new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, array);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAutoPayBanks.setAdapter(bankAdapter);
    }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.99), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public interface SetUpAutoPaymentDialogListener {
        void onFinishSetUpAutoPaymentDialog(String bankName, Double amount,
                                            String timesRepeated, String frequency);
    }

    public void sendBackResult() {
        SetUpAutoPaymentDialogFragment.SetUpAutoPaymentDialogListener listener
                = (SetUpAutoPaymentDialogFragment.SetUpAutoPaymentDialogListener)
                getFragmentManager()
                        .findFragmentById(R.id.flMainContainer);
        listener.onFinishSetUpAutoPaymentDialog(bankName, amount, timesRepeated, frequency);
        Toast.makeText(context, "Successfully Set Up Auto Pay", Toast.LENGTH_LONG).show();
        dismiss();
        return;
    }

}
