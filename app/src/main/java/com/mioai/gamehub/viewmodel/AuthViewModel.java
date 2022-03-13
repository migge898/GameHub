package com.mioai.gamehub.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;
import com.mioai.gamehub.User;
import com.mioai.gamehub.repository.AuthRepository;

// https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336 [10.02.2022]
public class AuthViewModel extends AndroidViewModel
{
    private AuthRepository authRepository;
    private LiveData<User> authenticatedUserLiveData;
    private LiveData<User> createdUserLiveData;

    public AuthViewModel(Application application)
    {
        super(application);
        authRepository = new AuthRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential)
    {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public LiveData<User> getAuthenticatedUserLiveData()
    {
        return authenticatedUserLiveData;
    }

    public void createUser(User authenticatedUser)
    {
        createdUserLiveData = authRepository.createUserInFirebaseDBIfNotExists(authenticatedUser);
    }

    public LiveData<User> getCreatedUserLiveData()
    {
        return createdUserLiveData;
    }

    public void signInWithEmailAndPassword(String email, String password)
    {
        authenticatedUserLiveData = authRepository.firebaseSignInWithEmailAndPassword(email, password);
    }
}
