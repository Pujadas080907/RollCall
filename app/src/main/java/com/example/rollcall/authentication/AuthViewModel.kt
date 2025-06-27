//package com.example.rollcall.authentication
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.UserProfileChangeRequest
//
//class AuthViewModel : ViewModel(){
//
//    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
//    private val _authState = MutableLiveData<AuthState>(AuthState.Loading)
//    val authState : LiveData<AuthState> = _authState
//
//    init {
//        // ✅ NEW ADDED — Persistent auth tracking
////        auth.addAuthStateListener { firebaseAuth ->
////            val user = firebaseAuth.currentUser
////            if (user != null) {
////                _authState.value = AuthState.Authenticated(user.displayName ?: "")
////            } else {
////                _authState.value = AuthState.Unauthenticated
////            }
////        }
//        println("🔥 AuthViewModel init: currentUser = ${auth.currentUser?.email}")
//
//        auth.addAuthStateListener { firebaseAuth ->
//            val currentUser = firebaseAuth.currentUser
//            if (currentUser != null) {
//                println("✅ User is authenticated: ${currentUser.email}")
//                _authState.value = AuthState.Authenticated(currentUser.displayName ?: "")
//            } else {
//                println("❌ No user authenticated")
//                _authState.value = AuthState.Unauthenticated
//            }
//        }
//    }
//
//    fun checkAuthStatus() {
//        if (auth.currentUser == null) {
//            _authState.value = AuthState.Unauthenticated
//        } else {
//            _authState.value = AuthState.Authenticated(auth.currentUser?.displayName ?: "")
//        }
//    }
//
//
//    fun login(email: String, password: String) {
//        if (email.isEmpty() || password.isEmpty()) {
//            _authState.value = AuthState.Error("Email or password can't be empty")
//            return
//        }
//        _authState.value = AuthState.Loading
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val username = auth.currentUser?.displayName ?: "User"
//                    _authState.value = AuthState.Authenticated(username)
//                } else {
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
//                }
//            }
//    }
//
//
//    fun signup(username: String, email: String, password: String) {
//        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
//            _authState.value = AuthState.Error("Username, Email or password can't be empty")
//            return
//        }
//        _authState.value = AuthState.Loading
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setDisplayName(username)
//                        .build()
//
//                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            _authState.value = AuthState.Authenticated(username)
//                        } else {
//                            _authState.value = AuthState.Error("Failed to update profile")
//                        }
//                    }
//                } else {
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
//                }
//            }
//    }
//
//    fun signout(context: Context) {
//
//        FirebaseAuth.getInstance().signOut()
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1014241138424-b69ibh57cdperg3ha2ag6t43ubm0fhek.apps.googleusercontent.com") // ✅ Your Web Client ID
//            .requestEmail()
//            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(context, gso)
//        googleSignInClient.signOut().addOnCompleteListener {
//            _authState.value = AuthState.Unauthenticated
//        }
//    }
//
//}
//
//sealed class AuthState {
//    data class Authenticated(val username: String) : AuthState()
//    object Unauthenticated : AuthState()
//    object Loading : AuthState()
//    data class Error(val message: String) : AuthState()
//}


package com.example.rollcall.authentication

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider   // ✅ ADDED
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel (application: Application) : AndroidViewModel(application){

//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>(AuthState.Loading)
    val authState: LiveData<AuthState> = _authState

    init {
        // ✅ ADDED: Check immediately if user is signed in
        val currentUser = auth.currentUser
        println("🔥 AuthViewModel init: currentUser = ${currentUser?.email}")
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser.displayName ?: "")
        } else {
            _authState.value = AuthState.Unauthenticated
        }

        // ✅ ADDED: Track changes dynamically
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                println("✅ AuthStateListener: Authenticated ${user.email}")
                _authState.value = AuthState.Authenticated(user.displayName ?: "")
            } else {
                println("❌ AuthStateListener: Unauthenticated")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated(currentUser.displayName ?: "")
        } else {
            AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val username = auth.currentUser?.displayName ?: "User"
                    _authState.value = AuthState.Authenticated(username)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(username: String, email: String, password: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Username, Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            _authState.value = AuthState.Authenticated(username)
                        } else {
                            _authState.value = AuthState.Error("Failed to update profile")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    // ✅ NEW FUNCTION: Handles Google ID token sign-in
    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val username = auth.currentUser?.displayName ?: "User"
                    _authState.value = AuthState.Authenticated(username)
                } else {
                    _authState.value = AuthState.Error("Google Sign-In failed")
                }
            }
    }

    fun signout(context: Context) {
        auth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1014241138424-b69ibh57cdperg3ha2ag6t43ubm0fhek.apps.googleusercontent.com") // ✅ Make sure this matches your JSON
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

// ✅ Unchanged sealed class
sealed class AuthState {
    data class Authenticated(val username: String) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
