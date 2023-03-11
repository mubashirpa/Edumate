package edumate.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class CourseWorkRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseWorkRepository {

    override suspend fun courseWorks(courseId: String): List<CourseWorkDto> {
        return firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
            .document(courseId).collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION).get()
            .await().documents.mapNotNull { snapshot ->
                snapshot.toObject<CourseWorkDto>()
            }
    }
}