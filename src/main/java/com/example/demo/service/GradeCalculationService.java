package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.CourseAttempt;
import com.example.demo.entity.CourseAttemptStatus;
import com.example.demo.entity.Grade;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeCalculationService {

  private static final double MAX_GRADE = 20.0;
  private static final double PASSING_GRADE = 10.0;

  private final GradeService gradeService;
  private final CourseAttemptService courseAttemptService;
  private final CourseService courseService;
  private final BonusService bonusService;

  public Double calculateCourseFinalGrade(UUID studentId, UUID examSessionId) {
    List<Grade> grades = gradeService.findByStudentId(studentId);

    double weightedSum = 0.0;
    double totalCoefficients = 0.0;

    for (Grade grade : grades) {
      if (grade.getExam() != null
          && grade.getExam().getExamSession() != null
          && grade.getExam().getExamSession().getId().equals(examSessionId)) {

        Double coefficient = grade.getExam().getCoefficient();
        if (coefficient == null) coefficient = 1.0;

        weightedSum += (grade.getValue() != null ? grade.getValue() : 0) * coefficient;
        totalCoefficients += coefficient;
      }
    }

    return totalCoefficients > 0 ? weightedSum / totalCoefficients : null;
  }

  public CourseAttempt calculateAndUpdateFinalGrade(UUID courseAttemptId) {
    CourseAttempt attempt = courseAttemptService.findById(courseAttemptId);

    Double weightedAverage =
        calculateCourseFinalGrade(attempt.getStudent().getId(), attempt.getExamSession().getId());

    if (weightedAverage != null) {
      Double totalBonus = bonusService.getTotalBonusForAttempt(courseAttemptId);
      if (totalBonus == null) totalBonus = 0.0;

      double finalGrade = weightedAverage + totalBonus;
      finalGrade = Math.min(finalGrade, MAX_GRADE);

      attempt.setFinalGrade(finalGrade);

      if (finalGrade >= PASSING_GRADE) {
        attempt.setStatus(CourseAttemptStatus.PASSED);
      } else {
        attempt.setStatus(CourseAttemptStatus.FAILED);
      }

      return courseAttemptService.save(attempt);
    }

    return attempt;
  }

  public Double calculateSemesterAverage(UUID studentId, UUID semesterId) {
    List<Grade> grades = gradeService.findByStudentAndSemester(studentId, semesterId);

    if (grades.isEmpty()) {
      return 0.0;
    }

    double weightedSum = 0.0;
    double totalCoefficients = 0.0;

    for (Grade grade : grades) {
      if (grade.getValue() != null) {
        Double coefficient = grade.getExam() != null ? grade.getExam().getCoefficient() : 1.0;
        if (coefficient == null) coefficient = 1.0;

        weightedSum += grade.getValue() * coefficient;
        totalCoefficients += coefficient;
      }
    }

    return totalCoefficients > 0 ? weightedSum / totalCoefficients : 0.0;
  }

  public Integer calculateTotalCredits(UUID studentId) {
    List<CourseAttempt> attempts =
        courseAttemptService.findByStudentIdAndStatus(studentId, CourseAttemptStatus.PASSED);

    int totalCredits = 0;
    for (CourseAttempt attempt : attempts) {
      Course course = attempt.getCourse();
      if (course != null && course.getCredits() != null) {
        totalCredits += course.getCredits();
      }
    }

    return totalCredits;
  }

  public Double calculateGeneralAverage(UUID studentId) {
    List<Grade> allGrades = gradeService.findByStudentId(studentId);

    if (allGrades.isEmpty()) {
      return 0.0;
    }

    double weightedSum = 0.0;
    double totalCoefficients = 0.0;

    for (Grade grade : allGrades) {
      if (grade.getValue() != null) {
        Double coefficient = grade.getExam() != null ? grade.getExam().getCoefficient() : 1.0;
        if (coefficient == null) coefficient = 1.0;

        weightedSum += grade.getValue() * coefficient;
        totalCoefficients += coefficient;
      }
    }

    return totalCoefficients > 0 ? weightedSum / totalCoefficients : 0.0;
  }

  public boolean hasPassedCourse(UUID studentId, UUID courseId) {
    List<CourseAttempt> attempts =
        courseAttemptService.findByStudentIdAndCourseId(studentId, courseId);

    for (CourseAttempt attempt : attempts) {
      if (attempt.getStatus() == CourseAttemptStatus.PASSED) {
        return true;
      }
    }

    return false;
  }

  public Double getBestCourseGrade(UUID studentId, UUID courseId) {
    List<CourseAttempt> attempts =
        courseAttemptService.findByStudentIdAndCourseId(studentId, courseId);

    Double bestGrade = null;
    for (CourseAttempt attempt : attempts) {
      if (attempt.getFinalGrade() != null) {
        if (bestGrade == null || attempt.getFinalGrade() > bestGrade) {
          bestGrade = attempt.getFinalGrade();
        }
      }
    }

    return bestGrade;
  }
}
