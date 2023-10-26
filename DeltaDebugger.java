import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DeltaDebugger {
  static final int PASS = 0;
  static final int FAIL = 1;
  static final int UNRESOLVED = 2;


  public static List<Integer> dd(List<Integer> changes, boolean baselinePasses, boolean allChangesFail) {
    return ddRecursive(changes, new ArrayList<Integer>());
  }
  public static List<Integer> ddRecursive(List<Integer> changes, List<Integer> remaining) {
    int n = changes.size();
    if (n == 1) {
      return changes;
    }

    List<Integer> c1 = new ArrayList<>();
    List<Integer> c2 = new ArrayList<>();
    split(changes, c1, c2);

    if (test(join(c1, remaining)) == FAIL) {
      return ddRecursive(c1, remaining);
    } else if (test(join(c2, remaining)) == FAIL) {
      return ddRecursive(c2, remaining);
    } else {
      List<Integer> result = new ArrayList<>();
      result.addAll(ddRecursive(c1, join(c2, remaining)));
      result.addAll(ddRecursive(c2, join(c1, remaining)));
      return result;
    }
  }

  //should generate a list of strings containing the differences between the two files.
  public static List<String> split(String fileName1, String fileName2){
    // Create process builder
    ProcessBuilder pb = new ProcessBuilder("diff", "-U", "0", fileName1, fileName2);
    Process process;
    int errCode;
    // Start and wait for process to finish
    try{
      process = pb.start();
      errCode = process.waitFor();

      // Check if error occurred
      if (errCode == 0) {
        System.out.println("No differences found");
        return new ArrayList<>();
      }

      // Read output
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      List<String> output = new ArrayList<>();
      while ((line = br.readLine()) != null) {
        output.add(line);
      }
      // Return output
      return output;
    }catch(Exception e){
      System.out.println("Error");
      return new ArrayList<>();
    }
  }
//  public static List<Integer> split(List<Integer> changes, List<Integer> c1, List<Integer> c2) {
//    Random rand = new Random();
//    for (Integer change : changes) {
//      if (rand.nextBoolean()) {
//        c1.add(change);
//      } else {
//        c2.add(change);
//      }
//    }
//    return c1;
//  }
  public static List<Integer> join(List<Integer> l1, List<Integer> l2) {
    List<Integer> joined = new ArrayList<>();
    joined.addAll(l1);
    joined.addAll(l2);
    return joined;
  }

  //runs file name to see if it passes, fails, or returns unresolved.
  // Not quite sure what would constitute unresolved atm
  public static int test(String fileName) {
    ProcessBuilder pb;
    Process p;
    int error;
    try{
      //compile the program
      pb = new ProcessBuilder("javac", fileName);
      p = pb.start();
      p.waitFor();

      //run the program
      pb = new ProcessBuilder("java", fileName);
      p = pb.start();
      p.waitFor(); //throws exception if an error occurs
    }
    catch(Exception e){
      return FAIL;
    }
    return PASS;
    //return UNRESOLVED???
  }

  //  public static int test(List<Integer> changes) {
//    if (changes.isEmpty()) {
//      return PASS;
//    } else if (changes.containsAll(changes)) {
//      return FAIL;
//    } else {
//      return UNRESOLVED;
//    }
//  }

}