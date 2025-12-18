import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //canvas service and path where to store data
        CanvasService service = new CanvasService("data/canvas.txt");
        //try to load data from file
        try {
            service.load();
        } catch (CanvasException ignored) {
        }
        //menu
        while (true) {
            System.out.println(
                    "\n=== Canvas ====\n"+
                            "1)Add student\n"
                            +"2)Create submission\n"
                            +"3)Add version\n"
                            +"4)Show all\n"
                            +"5)Submission details\n"
                            + "6)Save\n"+
                            "0)Exit"

            );
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            try {
                //1) add student
                if (choice.equals("1")) {
                    System.out.print("GH number: ");
                    String gh = sc.nextLine();
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Email ");
                    String email = sc.nextLine();
                    Student s = service.addStudent(gh, name, email);
                    System.out.println("Ok: " + s.toString());
                }
                // 2) create submission
                else if (choice.equals("2")) {
                    System.out.print("GH number: ");
                    String gh = sc.nextLine();
                    System.out.print("Type (1=Code, 2=Project): ");
                    int type = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Title: ");
                    String title = sc.nextLine();
                    Submission sub = service.createSubmissionByGh(gh, type, title);
                    System.out.println("Created: " + sub.shortInfo());
                }
                //3 add version on gh number
                else if (choice.equals("3")){
                    System.out.print("GH number:");
                    String gh = sc.nextLine();
                    //get list of works of student
                    List<Submission> list = service.getSubmissionsByGh(gh);
                    if (list.size()==0){
                        System.out.println("No submissions found");
                        continue;
                    }
                    //print all submissions
                    System.out.println("Submissions:");
                    for (Submission s : list) {
                        System.out.println(" " + s.getId() + "->" + s.shortInfo());
                    }
                    System.out.print("Choose submission id: ");
                    int subId = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Path to file: ");
                    String path = sc.nextLine();
                    service.addVersionFile(subId, path);
                    System.out.println(service.showAll());

                }
                // 4 show all
                else if (choice.equals("4")) {
                    System.out.println(service.showAll());
                }
                // 5 details submission
                else if (choice.equals("5")){
                    System.out.print("Submission ID: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    System.out.println(service.details(id));


                }
                //6 save
                else if (choice.equals("6")) {
                    service.save();
                    System.out.println("Saved");
                }
                else if (choice.equals("0")) {

                    try {
                        service.save();
                    } catch (Exception ignored) {}
                    System.out.println("Bye");
                    return;

                }
                else {
                    System.out.println("Invalid choice");
                }
            } catch (CanvasException e) { // to catch programs error
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) { // to catch any other errors
                System.out.println("unexpected errors: " + e.getMessage());
            }
        }
    }

}