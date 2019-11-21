package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Subject;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SubjectTransactionAccess {
  List getSubjects();

  Subject addSubject(Subject subject);

  Subject findSubjectByTitle(String title);

  List<Subject> findAllSubjectsByTitleList(List<String> subject);

  String deleteSubject(String title);

//  List<Student> listStudentsBySubject(String subject);
}
