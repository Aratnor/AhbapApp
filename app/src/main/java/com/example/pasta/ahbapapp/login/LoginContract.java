package com.example.pasta.ahbapapp.login;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface LoginContract {

    interface View{

        void showProgress();
        void hideProgress();
        void updateUI();
    }

    interface Presenter{

        void logInWithFirebase(GoogleSignInAccount account);
        void checkUserExists();
        void addUser();
    }
}
