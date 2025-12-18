import java.util.ArrayList;

import java.util.List;
public abstract class Submission { // an abstract class for all types of work
    private final int id;
    private final int studentId;
    private final String title;
    private final List<String> versions = new ArrayList<String>(); // versions - list of paths to files, 1 ver=1path
    protected Submission(int id, int studentId, String title){ //protected constructor only childs can create submission
        this.id = id;
        this.studentId = studentId;
        this.title = title;
    }
    public int getId() {
        return id;
    }
    public int getStudentId() {
        return studentId;
    }
    public String getTitle() {
        return title;
    }
    public void addVersion(String storedPath){
        versions.add(storedPath); // add a path to list
    }
    public List<String> getVersions(){
        return new ArrayList<String>(versions); // return a copy of the versions list not to change it
    }

    public abstract String typeKey(); // abstract method for each typ of submission (code or project)
    public abstract String extra(); // another abstract method (language or format)
    public String shortInfo(){
        return id + ") [" + typeKey() + "] " + title + " (student=" + studentId + ", versions=" + versions.size() + ")";
    }
}
