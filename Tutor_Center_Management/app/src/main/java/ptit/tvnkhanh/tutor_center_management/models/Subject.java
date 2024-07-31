package ptit.tvnkhanh.tutor_center_management.models;

public class Subject {
    private String _id;
    private String subjectName;
    private String grade;

    public Subject() {
    }

    public Subject(String _id, String subjectName, String grade) {
        this._id = _id;
        this.subjectName = subjectName;
        this.grade = grade;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
