package edumate.app.domain.repository

import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.model.course_work.AssigneeMode
import edumate.app.domain.model.course_work.AssigneeMode.INDIVIDUAL_STUDENTS
import edumate.app.domain.model.course_work.CourseWorkState
import edumate.app.domain.model.course_work.IndividualStudentsOptions

interface CourseWorkRepository {

    /**
     * Creates course work.
     * @param courseId Identifier of the course.
     * @param courseWorkDto Instance of [CourseWorkDto].
     * @return If successful, the response body contains a newly created instance of [CourseWorkDto].
     */
    suspend fun create(courseId: String, courseWorkDto: CourseWorkDto): CourseWorkDto?

    /**
     * Deletes a course work.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work to delete.
     */
    suspend fun delete(courseId: String, id: String)

    /**
     * Returns course work.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work.
     * @return If successful, the response body contains an instance of [CourseWorkDto].
     */
    suspend fun get(courseId: String, id: String): CourseWorkDto?

    /**
     * Returns a list of course work that the requester is permitted to view.
     * @param courseId Identifier of the course.
     * @param courseWorkState Restriction on the work status to return.
     * @param orderBy Optional sort ordering for results. Supported fields are updateTime and dueDate. Supported direction keywords are asc and desc. If not specified, updateTime desc is the default behavior. Examples: dueDate asc, updateTime desc, updateTime, dueDate desc.
     * @param pageSize Maximum number of items to return.
     * @return If successful, the response body contains a list of [CourseWorkDto].
     */
    suspend fun list(
        courseId: String,
        courseWorkState: CourseWorkState = CourseWorkState.PUBLISHED,
        orderBy: String = "updateTime desc",
        pageSize: Int? = null
    ): List<CourseWorkDto>

    /**
     * Modifies assignee mode and options of a coursework.
     * @param courseId Identifier of the course.
     * @param id Identifier of the coursework.
     * @param assigneeMode Mode of the coursework describing whether it will be assigned to all students or specified individual students.
     * @param modifyIndividualStudentsOptions Set which students are assigned or not assigned to the coursework. Must be specified only when [assigneeMode] is [INDIVIDUAL_STUDENTS].
     * @return If successful, the response body contains an instance of [CourseWorkDto].
     */
    suspend fun modifyAssignees(
        courseId: String,
        id: String,
        assigneeMode: AssigneeMode,
        modifyIndividualStudentsOptions: IndividualStudentsOptions?
    ): CourseWorkDto?

    /**
     * Updates one or more fields of a course work.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work.
     * @param courseWorkDto Instance of [CourseWorkDto].
     * @return If successful, the response body contains an instance of [CourseWorkDto].
     */
    suspend fun patch(courseId: String, id: String, courseWorkDto: CourseWorkDto): CourseWorkDto?
}