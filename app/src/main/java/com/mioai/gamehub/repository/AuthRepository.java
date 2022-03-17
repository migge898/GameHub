package com.mioai.gamehub.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mioai.gamehub.User;

public class AuthRepository
{
    private final static String FIREBASE_DB_URL = "https://gamehub-4786a-default-rtdb.europe-west1.firebasedatabase.app/";
    private final static String USERS = "Users";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase rootRef = FirebaseDatabase.getInstance(FIREBASE_DB_URL);
    private final DatabaseReference usersRef = rootRef.getReference(USERS);


    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential)
    {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask ->
        {
            if (authTask.isSuccessful())
            {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null && firebaseUser.isEmailVerified())
                {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    User user = new User(uid, name, email);
                    user.setNew(isNewUser);
                    authenticatedUserMutableLiveData.setValue(user);
                } else if (firebaseUser != null && !firebaseUser.isEmailVerified())
                {
                    firebaseUser.sendEmailVerification();
                }

            } else
            {
                Log.e(this.getClass().getSimpleName(), "!!!!!!!!!!!!!!!!!!!!" + authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> createUserInFirebaseDBIfNotExists(User authenticatedUser)
    {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DatabaseReference uidRef = usersRef.child(authenticatedUser.getUid());
        uidRef.get().addOnCompleteListener(uidTask ->
        {
            if (uidTask.isSuccessful())
            {
                DataSnapshot data = uidTask.getResult();
                if (!data.exists())
                {
                    uidRef.setValue(authenticatedUser).addOnCompleteListener(userCreationTask ->
                    {
                        if (userCreationTask.isSuccessful())
                        {
                            authenticatedUser.setCreated(true);
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else
                        {
                            Log.e(this.getClass().getSimpleName(), userCreationTask.getException().getMessage());
                        }
                    });
                } else
                {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else
            {
                Log.e(this.getClass().getSimpleName(), uidTask.getException().getMessage());

            }
        });
        return newUserMutableLiveData;
    }

    public MutableLiveData<User> firebaseSignInWithEmailAndPassword(String login_email, String login_password)
    {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(login_email, login_password).addOnCompleteListener(authTask ->
        {
            if (authTask.isSuccessful())
            {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null)
                {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    User user = new User(uid, name, email);
                    user.setNew(isNewUser);
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else
            {
                Log.e(this.getClass().getSimpleName(), "!!!!!!!!!!!!!!!!!!!!" + authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }
}
