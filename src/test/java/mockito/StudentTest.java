package mockito;
import mockito.exceptions.ExamGradeAlreadyRecordedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class StudentTest {

    private static final String ANY_EXAM_ID = "1123";
    private static final double ANY_WEIGHT_GRADE = 0.5;

    private Student student;

    @Mock
    StudentRecord studentRecordMock;
    @Mock
    GradePublisher gradePublisherMock;

    @BeforeEach
    public void createStudent(){
        student = new Student(studentRecordMock);
    }

    @Test
    public void givenExistingExamRecord_whenCalculatedExamGrade_thenThrowExamGradeAlreadyRecordedException() {
        when(studentRecordMock.containsExamRecord(ANY_EXAM_ID)).thenReturn(true);
        Student student = new Student(studentRecordMock);

        Executable calculateExamGrade = () -> student.calculateExamGrade(ANY_EXAM_ID, ANY_WEIGHT_GRADE);

        assertThrows(ExamGradeAlreadyRecordedException.class, calculateExamGrade);

    }

    @Test
    public void givenUnexistingExamRecord_whenCalculateExamGrade_thenRecordIsAddedToStudent() {
        when(studentRecordMock.containsExamRecord(ANY_EXAM_ID)).thenReturn(false);

        student.calculateExamGrade(ANY_EXAM_ID, ANY_WEIGHT_GRADE);

        verify(studentRecordMock).addExamRecord(ANY_EXAM_ID, ANY_WEIGHT_GRADE);
    }

    @Test
    public void givenNoExamGradeCalculated_whenPublishFinalGrade_thenPublish0() {
        student.publishFinalGrade(gradePublisherMock);

        verify(gradePublisherMock).publish(0.0);
    }

    @Test
    public void givenMultipleExamGradeCalculated_whenPublishFinalGrade_thenPublishSumsOfGrades() {
        when(studentRecordMock.containsExamRecord(ANY_EXAM_ID)).thenReturn(false);
        student.calculateExamGrade(ANY_EXAM_ID, ANY_WEIGHT_GRADE);
        String ANY_OTHER_EXAM_ID = "IFT-4006";
        double ANY_OTHER_WEIGHT_GRADE = 0.4;
        student.calculateExamGrade(ANY_OTHER_EXAM_ID, ANY_OTHER_WEIGHT_GRADE);
        double expectedSumsOfGrades = 0.9;

        student.publishFinalGrade(gradePublisherMock);

        verify(gradePublisherMock).publish(expectedSumsOfGrades);
    }
}
