package dao;
import java.util.List;
import jakarta.persistence.EntityManager;
import jpa.business.Prof;
import jpa.business.Student;
import jpa.business.User;

public class DAO {
    private EntityManager manager;
    public DAO(EntityManager manager) {
        this.manager = manager;
    }

    public void populateStudents() {
        User student1 = new Student("Kate", "123");
        User student2 = new Student("Lesha", "123");
        User student3 = new Student("Sergio", "999");
        manager.persist(student1);
        manager.persist(student2);
        manager.persist(student3);
    }

    public void populateProfs() {
        User prof1 = new Prof("Noel", "istic");
        User prof2 = new Prof("Olivier", "istic");
        User prof3 = new Prof("Capy", "hneu");
        manager.persist(prof1);
        manager.persist(prof2);
        manager.persist(prof3);
    }

    public void createStudent(String name, String group) {
        User student = new Student(name, group);
        manager.persist(student);
    }

    public void removeStudent(long student_id) {
        manager.remove(manager.find(Student.class, student_id));
    }

    public void updateStudent(long student_id, String name, String group) {
        Student student = manager.find(Student.class,student_id);
        student.setName(name);
        student.setGroup(group);
    }

    public void createProf(String name, String department) {
        User prof = new Prof(name, department);
        manager.persist(prof);
    }

    public void removeProf(long prof_id) {
        manager.remove(manager.find(Prof.class, prof_id));
    }

    public void updateProf(long prof_id, String name, String department) {
        Prof prof = manager.find(Prof.class, prof_id);
        prof.setName(name);
        prof.setDepartment(department);
    }

    public void listUsers() {
        List<String> resultList = manager
                .createQuery("SELECT u.name FROM User u").getResultList();

        for (String name : resultList) {
            System.out.println(name);
        }
    }
}
