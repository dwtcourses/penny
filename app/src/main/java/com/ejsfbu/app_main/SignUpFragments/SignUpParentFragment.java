package com.ejsfbu.app_main.SignUpFragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ejsfbu.app_main.Activities.ParentActivity;
import com.ejsfbu.app_main.Models.User;
import com.ejsfbu.app_main.R;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static com.ejsfbu.app_main.Activities.SignUpActivity.user;

public class SignUpParentFragment extends Fragment {

    public static final String TAG = "SignUpParentFragment";

    @BindView(R.id.etSignUpParentFirstName)
    EditText etSignUpParentFirstName;
    @BindView(R.id.etSignUpParentLastName)
    EditText etSignUpParentLastName;
    @BindView(R.id.etSignUpParentEmail)
    EditText etSignUpParentEmail;
    @BindView(R.id.etSignUpParentUsername)
    EditText etSignUpParentUsername;
    @BindView(R.id.etSignUpParentPassword)
    EditText etSignUpParentPassword;
    @BindView(R.id.etSignUpParentConfirmPassword)
    EditText etSignUpParentConfirmPassword;
    @BindView(R.id.etSignUpParentChildCode)
    EditText etSignUpParentChildCode;
    @BindView(R.id.bSignUpParent)
    Button bSignUpParent;

    private Unbinder unbinder;
    private boolean emailUnique;
    private boolean usernameUnique;
    private User child;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_parent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
    }

    // When change fragment unbind view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.bSignUpParent)
    public void clickSignupParent() {

        final String firstName = etSignUpParentFirstName.getText().toString();
        if (firstName.equals("")) {
            Toast.makeText(getContext(), "Please enter a first name",
                    Toast.LENGTH_LONG).show();
            return;
        }

        final String lastName = etSignUpParentLastName.getText().toString();

        final String name;
        if (lastName.equals("")) {
            name = firstName;
        } else {
            name = firstName + " " + lastName;
        }
        user.setName(name);

        final String childCode = etSignUpParentChildCode.getText().toString();
        if (childCode.equals("")) {
            Toast.makeText(getContext(), "Please enter your child's code",
                    Toast.LENGTH_LONG).show();
            return;
        }
        getChildFromCode(childCode);
    }

    private void finishSignUp() {

        final String email = etSignUpParentEmail.getText().toString();
        if (email.equals("")) {
            Toast.makeText(getContext(), "Please enter an email", Toast.LENGTH_LONG).show();
            return;
        }
        if (!emailUnique) {
            Toast.makeText(getContext(), "Email is already associated with an account",
                    Toast.LENGTH_LONG).show();
            return;
        }
        user.setEmail(email);

        final String username = etSignUpParentUsername.getText().toString();
        if (username.equals("")) {
            Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_LONG).show();
            return;
        }
        if (!usernameUnique) {
            Toast.makeText(getContext(), "Username is taken", Toast.LENGTH_LONG).show();
            return;
        }
        user.setUsername(username);

        final String password = etSignUpParentPassword.getText().toString();
        final String confirmPassword = etSignUpParentConfirmPassword.getText().toString();
        if (password.equals("") || confirmPassword.equals("")) {
            Toast.makeText(getContext(), "Please enter a password", Toast.LENGTH_LONG).show();
            return;
        }
        if (confirmPasswordsMatch(password, confirmPassword)) {
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(SignUpParentFragment.this.getContext(),
                                ParentActivity.class);
                        intent.putExtra("isFirstLogin", true);
                        SignUpParentFragment.this.getContext().startActivity(intent);
                        SignUpParentFragment.this.getActivity().finish();

                    } else {
                        Toast.makeText(getActivity(), "Sign Up Failure",
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Sign Up Failure");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Passwords do not match",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean confirmPasswordsMatch(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        } else {
            return false;
        }
    }

    @OnTextChanged(R.id.etSignUpParentEmail)
    public void checkEmailUnique() {
        String email = etSignUpParentEmail.getText().toString();
        User.Query userQuery = new User.Query();
        userQuery.testEmail(email);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        etSignUpParentEmail.setTextColor(SignUpParentFragment.this.getResources()
                                .getColor(android.R.color.holo_green_dark));
                        emailUnique = true;
                    } else {
                        etSignUpParentEmail.setTextColor(SignUpParentFragment.this.getResources()
                                .getColor(android.R.color.holo_red_dark));
                        emailUnique = false;
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnTextChanged(R.id.etSignUpParentUsername)
    public void checkUsernameUnique() {
        String username = etSignUpParentUsername.getText().toString();
        User.Query userQuery = new User.Query();
        userQuery.testUsername(username);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        etSignUpParentUsername.setTextColor(SignUpParentFragment.this
                                .getResources().getColor(android.R.color.holo_green_dark));
                        usernameUnique = true;
                    } else {
                        etSignUpParentUsername.setTextColor(SignUpParentFragment.this
                                .getResources().getColor(android.R.color.holo_red_dark));
                        usernameUnique = false;
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getChildFromCode(String childCode) {
        User.Query userQuery = new User.Query();
        userQuery.whereEqualTo("objectId", childCode);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (objects.size() == 0) {
                    Toast.makeText(SignUpParentFragment.this.getContext(),
                            "Invalid Child Code", Toast.LENGTH_LONG).show();
                } else {
                    child = objects.get(0);
                    if (!child.getIsParent()) {
                        user.addChild(child);

                        ParseACL parseACL = new ParseACL();
                        parseACL.setPublicReadAccess(true);
                        parseACL.setPublicWriteAccess(true);
                        //parseACL.setReadAccess(child.getObjectId(), true);
                        //parseACL.setWriteAccess(child.getObjectId(), true);
                        user.setACL(parseACL);

                        finishSignUp();
                    } else {
                        Toast.makeText(SignUpParentFragment.this.getContext(),
                                "Invalid Child Code", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
