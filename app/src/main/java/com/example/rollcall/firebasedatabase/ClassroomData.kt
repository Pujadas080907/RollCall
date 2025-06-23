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
data class StudentData(
    val studentId:   String = "",
    val fullName:    String = "",
    val enrollment:  String = "",
    val classroomId: String = "",
    val userEmail:   String = ""
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
            .whereEqualTo("userEmail", user.email)
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

    fun updateClassroomDetails(
        classroom: ClassroomData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (classroom.id.isBlank()) {
            onFailure(Exception("Invalid classroom ID"))
            return
        }
        db.collection("classrooms")
            .document(classroom.id)
            .set(classroom)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun addStudent(
        student: StudentData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { onFailure(Exception("User not logged in")); return }

        val doc = db.collection("students").document()
        val s   = student.copy(studentId = doc.id, userEmail = user.email ?: "")

        doc.set(s).addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getStudentsByClassroom(
        classroomId: String,
        onSuccess: (List<StudentData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { onFailure(Exception("User not logged in")); return }

        db.collection("students")
            .whereEqualTo("userEmail", user.email)
            .whereEqualTo("classroomId", classroomId)
            .get()
            .addOnSuccessListener { snap ->
                onSuccess(snap.documents.mapNotNull { it.toObject(StudentData::class.java) })
            }
            .addOnFailureListener { onFailure(it) }
    }

}
