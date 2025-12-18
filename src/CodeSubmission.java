public class CodeSubmission extends Submission{
    private final String language; //language of programming
    //constructor with id, studentid, title, language
    public CodeSubmission(int id, int studentId, String title, String language){
        super(id, studentId, title); //call the constructor of the parent class
        this.language = language;
    }
    @Override
    public String typeKey() { //abstract method type of work
        return "CODE";
    }
    @Override
    public String extra() { //abstract method extra inf
        return language;
    }
}
