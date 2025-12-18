import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class FileStorage {
    //format of file: data/canvas.txt
    //INFO;nextStudentId;nextSubmissionId;
    //STUDENT;id;gh;name;email
    //SUBMISSION;id;type;studentId;title;extra;path1|path2|path3
    private final String path;
    public FileStorage(String path) {
        this.path = path;
    }
    public void save(int nextStudentId, int nextSubmissionId, Map<Integer, Student> students,
                     Map<Integer, Submission> submissions)
        throws CanvasException{
        try{
            Path p = Paths.get(path);
            //create directory data/
            if (p.getParent() !=null){
                Files.createDirectories(p.getParent());
            }
            BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
            //1 string with INFO
            w.write("INFO;"+nextStudentId+";"+nextSubmissionId);
            w.newLine();
            //all students
            for (Student s : students.values()){
                w.write("STUDENT;"+s.getId()+";"+ s.getGhNumber() + ";" + s.getName()+";"+s.getEmail());
                w.newLine();
            }
            //all submissions
            for (Submission sub : submissions.values()){
                //make all versions in 1 string(all paths separated by |)
                List<String> ver = sub.getVersions();
                String joined = "";
                for (int i = 0; i < ver.size(); i++) {
                    if (i > 0) joined += "|";
                    joined += ver.get(i);
                }
                //write submission with all info
                w.write("SUBMISSION;"+sub.getId()+";"+sub.typeKey()+";"+sub.getStudentId()+";"
                        +sub.getTitle()+";"+sub.extra()+";"+joined);
                w.newLine();
                w.close();
            }

        } catch (IOException e) {
            throw new CanvasException("Could not save file" +e.getMessage(), e);
        }
    }
// load data from file
    public LoadedData load() throws CanvasException {
        Path p = Paths.get(path); // get path to file
        if (!Files.exists(p)) { //exception if file does not exist
            throw new CanvasException("File does not exist");
        }
        int nextStudentId = 1;
        int nextSubmissionId = 1;
        Map<Integer, Student> students = new HashMap<Integer, Student>(); //map of students
        Map<Integer, Submission> submissions = new HashMap<Integer, Submission>(); //map of submissions
        try {
            BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8);
            String line;
            //read file line by line
            while ((line= r.readLine()) != null) {
                String[] a = line.split(";", -1); // split a string with ;
                if (a.length == 0) continue;
                //INFO
                if (a[0].equals("INFO")) {
                    nextStudentId = Integer.parseInt(a[1]); // fill nextStudentId
                    nextSubmissionId = Integer.parseInt(a[2]);
                }
                //STUDENT
                else if (a[0].equals("STUDENT")) {
                    int id = Integer.parseInt(a[1]);
                    String gh = a[2];
                    String name = a[3];
                    String email = a[4];
                    students.put(id, new Student(id, gh, name, email)); // add student to map
                }
                //SUBMISSION
                else if (a[0].equals("SUBMISSION")) {
                    int id = Integer.parseInt(a[1]);
                    String type = a[2];
                    int studentId = Integer.parseInt(a[3]);
                    String title = a[4];
                    String extra = a[5];
                    String joined;
                    if (a.length >= 7) {
                        joined = a[6];
                    } else {
                        joined = "";
                    }
                    Submission sub;
                    //create  a new object based on type we have
                    if (type.equals("CODE")) {
                        sub = new CodeSubmission(id, studentId, title, extra);

                    } else {
                        sub = new ProjectSubmission(id, studentId, title, extra);
                    }
                    // check if there are versions and split on them
                    if (joined != null && !joined.isEmpty()) {
                        String[] vs = joined.split("\\|", -1);
                        for (int i = 0; i < vs.length; i++) {
                            sub.addVersion(vs[i]);
                        }
                    }
                    submissions.put(id, sub);
                }
            }
            r.close();
        } catch (IOException e) {
            throw new CanvasException("Could not load file" +e.getMessage(), e);
        }
        return new LoadedData(nextStudentId, nextSubmissionId, students, submissions);
    }
    // create a container class loaded data for data after loading
    public static class LoadedData{
        public final int nextStudentId;
        public final int nextSubmissionId;
        public final Map<Integer, Student> students;
        public final Map<Integer, Submission> submissions;
        public LoadedData(int nextStudentId, int nextSubmissionId, Map<Integer, Student> students, Map<Integer, Submission> submissions) {
            this.nextStudentId = nextStudentId;
            this.nextSubmissionId = nextSubmissionId;
            this.students = students;
            this.submissions = submissions;
        }
    }

}
