public class ProjectSubmission extends Submission {
    private final String format; //format of project
    //constructor that takes id, studentId, title, format
    public ProjectSubmission(int id, int studentId, String title, String format){
        super(id, studentId, title); // constructor of parent class
        this.format = format;
    }
    // abstract method for type of key
    @Override
    public String typeKey() {
        return "PROJECT";
    }
    //extra information method in Project submission
    @Override
    public String extra() {
        return format;
    }
}
