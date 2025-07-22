package com.pujadas.rollcall.firebasedatabase

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

data class AttendanceData(
    val date: String = "",
    val studentId: String = "",
    val fullName: String = "",
    val enrollment: String = "",
    val classroomId: String = "",
    val status: String = "",  // "P" or "A"
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

fun deleteStudent(
    student: StudentData,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    if (student.studentId.isBlank()) {
        onFailure(Exception("Invalid student ID"))
        return
    }

    val db = Firebase.firestore
    val batch = db.batch()

    // Reference to student document
    val studentRef = db.collection("students").document(student.studentId)
    batch.delete(studentRef)

    // Query and delete all related attendance records
    db.collection("attendance")
        .whereEqualTo("studentId", student.studentId)
        .get()
        .addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }

            // Commit all deletions
            batch.commit()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
        .addOnFailureListener { onFailure(it) }
}


    fun updateStudent(
        student: StudentData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (student.studentId.isBlank()) {
            onFailure(Exception("Invalid student ID"))
            return
        }

        db.collection("students")
            .document(student.studentId)
            .set(student)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun saveAttendance(
        attendanceList: List<AttendanceData>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val batch = Firebase.firestore.batch()
        val collection = Firebase.firestore.collection("attendance")

        for (attendance in attendanceList) {
            val doc = collection.document()
            batch.set(doc, attendance.copy(userEmail = user.email ?: ""))
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAttendanceByClassroom(
        classroomId: String,
        onSuccess: (List<AttendanceData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        Firebase.firestore.collection("attendance")
            .whereEqualTo("userEmail", user.email)
            .whereEqualTo("classroomId", classroomId)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.toObject(AttendanceData::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun getAttendanceForDate(
        classroomId: String,
        date: String,
        onSuccess: (List<AttendanceData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        Firebase.firestore.collection("attendance")
            .whereEqualTo("userEmail", user.email)
            .whereEqualTo("classroomId", classroomId)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.toObject(AttendanceData::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it) }
    }



}
