import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CanvasService {
    //class  where we add students. create submissions, add versions with copying them to storage
    // give lists or details. save and load through filestorage class
    private final FileStorage storage; // an object to save and load
    private int nextStudentId = 1;
    private int nextSubmissionId = 1;
    // students where key is id and value is student object
    private final Map<Integer, Student> students = new HashMap<Integer, Student>();
    //submissions where key is submission id and value is submission
    private final Map<Integer, Submission> submissions = new HashMap<Integer, Submission>();
    public CanvasService(String path) {
        this.storage = new FileStorage(path);
    }
    public Student addStudent(String ghNumber, String name, String email) throws CanvasException {
        ghNumber = ghNumber.trim();
        name = name.trim();
        email = email.trim();
        if (ghNumber.length() ==0) throw new CanvasException("Invalid Gh number");
        if (name.length() ==0) throw new CanvasException("Invalid name");
        if (email.length() ==0) throw new CanvasException("Invalid email");
        checkSeparators(ghNumber, "GH number");
        checkSeparators(name, "Name");
        checkSeparators(email, "Email");
        //check the unique of GH number
        for (Student s : students.values()) {
            if (s.getGhNumber().equals(ghNumber)) {
                throw new CanvasException("Student with this GH number already exists" +ghNumber);

            }
        }
        //create an object student
        Student student = new Student(nextStudentId, ghNumber, name, email);
        //put in map id
        students.put(nextStudentId, student);
        nextStudentId++;
        return student;
    }
    public Submission createSubmissionByGh(String ghNumber, int type, String title) throws CanvasException {
        int studentId = findStudentIdByGh(ghNumber); // find student id with gh numberr
        title = trim(title);
        if (title.length() ==0) throw new CanvasException("Invalid title");
        checkSeparators(title, "Title");
        int id = nextSubmissionId;
        nextSubmissionId++;
        Submission sub;
        if (type==1){
            sub = new CodeSubmission(id, studentId, title,  "Java");


        } else if (type==2){
            sub = new ProjectSubmission(id, studentId, title, "ZIP  ");
        } else {
            throw new CanvasException("Invalid type (1code or 2 project)");
        }
        submissions.put(id, sub);
        return sub;
    }
    //get list of submissions by student ghNumber
    public List<Submission> getSubmissionsByGh(String ghNumber) throws CanvasException{
        int studentId = findStudentIdByGh(ghNumber); //get studentid
        List<Submission> result = new ArrayList<>();
        for (Submission s : submissions.values()) { // go through all submissions to find submissions with studentId
            if (s.getStudentId()==studentId) result.add(s);
        }
        return result;
    }
    //add version where we copy file to storage and save path as a version
    public void addVersionFile(int submissionId, String filePath) throws CanvasException{
        //get sybmission
        Submission sub = submissions.get(submissionId);
        if (sub == null) {
            throw new CanvasException("Submission with this id does not exist");
        }
        //checking file path
        filePath = trim(filePath);
        if (filePath.length() == 0){
            throw new CanvasException("Invalid file path");
        }
        Path src = Paths.get(filePath);
        if (!Files.exists(src)) throw new CanvasException("File does not exist" + filePath);
        if (Files.isDirectory(src)) throw new CanvasException("File is a directory" + filePath);
        // create directory storage where we will save files
        Path storageDir = Paths.get("storage");
        try {
            Files.createDirectories(storageDir);
        } catch (Exception e) {
            throw new CanvasException("Could not create storage directory" + e.getMessage());
        }
        String originalName = src.getFileName().toString(); // original name of file
        int nextVer = sub.getVersions().size()+1;
        //create unique name of file inside storage
        String storedName = "sub_" + submissionId + "_v" + nextVer + "_" + originalName;
        Path dest = storageDir.resolve(storedName);
        //copy file to storage
        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            throw new CanvasException("Could not copy file to storage" + e.getMessage());
        }
        sub.addVersion(dest.toString());


    }
    // show students + submissions
    public String showAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n Students: \n");
        // go through all students
        for (Student s : students.values()) {
            sb.append(s.toString()).append("\n");
        }
        sb.append("\n Submissions: \n");
        // go through all submissions
        for (Submission sub : submissions.values()) {
            Student owner = students.get(sub.getStudentId());
            String gh;
            if (owner == null) {
                gh = "?";
            } else {
                gh = owner.getGhNumber();
            }
            sb.append(sub.shortInfo()).append(" (GH: ").append(gh).append(")\n");
        }
        return sb.toString();
    }
    public String details(int submissionId) throws CanvasException{
        Submission sub = submissions.get(submissionId);
        if (sub == null) throw new CanvasException("Submission with this id does not exist");
        StringBuilder sb = new StringBuilder();
        sb.append(sub.shortInfo()).append("\n");
        List<String> ver = sub.getVersions();
        if (ver.size() == 0){
            sb.append("No versions");
            return sb.toString();

        }
        for (int i = 0; i < ver.size(); i++) {
            sb.append("  v").append(i+1).append(": ").append(ver.get(i)).append("\n");

        }
        return sb.toString();

    }
    //save to file
    public void save() throws CanvasException{
        storage.save(nextStudentId, nextSubmissionId, students, submissions);
    }
    //load from file
    public void load() throws CanvasException{
        FileStorage.LoadedData data = storage.load();
        //restore ids
        nextStudentId = data.nextStudentId;
        nextSubmissionId = data.nextSubmissionId;
        //restore maps
        students.clear();
        students.putAll(data.students);
        submissions.clear();
        submissions.putAll(data.submissions);
    }

    private String trim(String s) {
        if (s == null) return "";
        return s.trim();
    }
    private void checkSeparators(String s,  String fieldName) throws CanvasException{
        if (s.indexOf(';') >=0 || s.indexOf('|') >=0){
            throw new CanvasException("Invalid " + fieldName);
        }
    }
    private int findStudentIdByGh(String ghNumber) throws CanvasException{
        ghNumber = ghNumber.trim();
        for (Student s : students.values()) {
            if (s.getGhNumber().equals(ghNumber)) return s.getId();
        }
        throw new CanvasException("Student with this GH number does not exist");
    }
}
