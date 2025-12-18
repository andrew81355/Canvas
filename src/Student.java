public class Student {
    private final int id;
    private final String ghNumber;
    private final String name;
    private final String email;
    //constructor for object student
    public Student(int id, String ghNumber, String name, String email){
        this.id = id;
        this.ghNumber = ghNumber;
        this.name = name;
        this.email = email;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getGhNumber() {
        return ghNumber;
    }
    @Override
    public String toString() { // change output for student
        return id + ") GH=" + ghNumber + " " + name + " " + email;
    }
}
