package com.example.rollcall.firebasedatabase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

data class ClassroomData(
    val degree: String = "",
    val year: String = "",
    val section: String = "",
    val id: String = "",
    val userEmail: String = ""
)

object FirebaseDbHelper {
    private val db = Firebase.firestore

fun saveDegreeDetails(
    classroom: ClassroomData,
    onSuccess: (ClassroomData) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onFailure(Exception("User not logged in"))
        return
    }

    val newDocRef = db.collection("classrooms").document()
    val classroomWithIdAndUser = classroom.copy(id = newDocRef.id, userEmail = user.email ?: "")

    newDocRef.set(classroomWithIdAndUser)
        .addOnSuccessListener { onSuccess(classroomWithIdAndUser) }
        .addOnFailureListener { onFailure(it) }
}

    fun getAllDegreeDetails(
        onSuccess: (List<ClassroomData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection("classrooms")
            .whereEqualTo("userEmail", user.email) // fetch only current user's data
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(ClassroomData::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
    fun deleteClassroom(
        classroom: ClassroomData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (classroom.id.isBlank()) {
            onFailure(Exception("Invalid classroom ID"))
            return
        }

        Firebase.firestore.collection("classrooms")
            .document(classroom.id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

}
