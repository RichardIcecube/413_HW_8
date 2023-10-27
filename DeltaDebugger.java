import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DeltaDebugger {
  static final int PASS = 0;
  static final int FAIL = 1;
  static final int UNRESOLVED = 2;


  public static List<Integer> dd(List<Integer> changes) {
    return ddRecursive(changes, new ArrayList<>());
  }
  public static List<Integer> ddRecursive(List<Integer> changes, List<Integer> remaining) {
    int n = changes.size();
    if (n == 1) {
      return changes;
    }

    List<Integer> c1 = new ArrayList<>();
    List<Integer> c2 = new ArrayList<>();
    divideChanges(changes, c1, c2);

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
    List<String> output = new ArrayList<>();
    // Start and wait for process to finish
    try {
      process = pb.start();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = br.readLine()) != null) {
              output.add(line);
          }
      }
      int errCode = process.waitFor();
      if (errCode == 0) {
          System.out.println("No differences found");
          return new ArrayList<>();
      }
    } catch (Exception e) {
        System.out.println("Error during diff: " + e.getMessage());
    }
    return output;
  }

  //new 
  public static void divideChanges(List<Integer> changes, List<Integer> c1, List<Integer> c2) {
    int halfSize = changes.size() / 2;
    for (int i = 0; i < changes.size(); i++) {
        if (i < halfSize) {
            c1.add(changes.get(i));
        } else {
            c2.add(changes.get(i));
        }
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
    List<Integer> joined = new ArrayList<>(l1);
    joined.addAll(l2);
    return joined;
  }

  //runs file name to see if it passes, fails, or returns unresolved.
  // Not quite sure what would constitute unresolved atm
  public static int test(List<Integer> changes) {
    String fileName = "YourFileNameHere.java"; // Replace this with a method to get the file name based on changes or input
    int result = compileAndRun(fileName);
    return result;
  }

  public static int compileAndRun(String fileName) {
    ProcessBuilder pb;
    int resultCode = UNRESOLVED;
    try {
        // Compile the program
        pb = new ProcessBuilder("javac", fileName);
        Process compileProcess = pb.start();
        int compileExitCode = compileProcess.waitFor();
        if (compileExitCode != 0) {
            return FAIL; // Compilation failed
        }

        // Run the program and set a timeout
        pb = new ProcessBuilder("java", fileName.replace(".java", ""));
        Process runProcess = pb.start();
        boolean completed = runProcess.waitFor(10, java.util.concurrent.TimeUnit.SECONDS); // 10 seconds timeout

        if (completed) {
            if (runProcess.exitValue() == 0) {
                resultCode = PASS;
            } else {
                resultCode = FAIL;
            }
        } else {
            runProcess.destroy();
        }
    } catch (Exception e) {
        System.out.println("Error during compile or run: " + e.getMessage());
        resultCode = FAIL;
    }
    return resultCode;
  }

  public static void main(String[] args) {
    // for testing or driver code
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
