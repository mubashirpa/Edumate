package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CourseDto
import edumate.app.domain.model.courses.CourseState
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CoursesRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : CoursesRepository {
        override suspend fun create(courseDto: CourseDto): CourseDto? {
            val id = courseDto.id
            courses().document(id).set(courseDto.toMap()).await()
            // After a course created, add $courseId in users/$uid/teaching array
            firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
                .document(courseDto.ownerId)
                .update(FirebaseConstants.Firestore.TEACHING, FieldValue.arrayUnion(id)).await()
            return get(id)
        }

        override suspend fun delete(id: String) {
            courses().document(id).delete().await()
        }

        override suspend fun get(id: String): CourseDto? {
            val documentSnapshot = courses().document(id).get().await()
            return documentSnapshot.toObject<CourseDto>()
        }

        override suspend fun list(
            studentId: String?,
            teacherId: String?,
            courseState: CourseState,
            pageSize: Int?,
        ): List<CourseDto> {
            var query: Query = courses()
            query =
                when {
                    studentId != null -> {
                        query.whereArrayContains(FirebaseConstants.Firestore.COURSE_GROUP_ID, studentId)
                    }

                    teacherId != null -> {
                        query.whereArrayContains(FirebaseConstants.Firestore.TEACHER_GROUP_ID, teacherId)
                    }

                    else -> {
                        return emptyList()
                    }
                }
            query = query.orderBy(FirebaseConstants.Firestore.NAME, Query.Direction.ASCENDING)
            if (pageSize != null && pageSize > 0) {
                query = query.limit(pageSize.toLong())
            }
            return query.get().await().documents.mapNotNull { snapshot ->
                snapshot.toObject<CourseDto>()
            }
        }

        override suspend fun update(
            id: String,
            courseDto: CourseDto,
        ) {
            courses().document(id).update(courseDto.toMap()).await()
        }

        private fun courses(): CollectionReference {
            return firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
        }
    }
