import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DeltaDebugger {
  static final int PASS = 0;
  static final int FAIL = 1;
  static final int UNRESOLVED = 2;


  public static List<String> dd(List<String> changes, String fileName) {
    return ddRecursive(changes, new ArrayList<>(), fileName);
  }
  public static List<String> ddRecursive(List<String> changes, List<String> remaining, String fileName) {
    int n = changes.size();
    if (n == 1) {
      return changes;
    }

    List<String> c1 = new ArrayList<>();
    List<String> c2 = new ArrayList<>();
    divideChanges(changes, c1, c2);

    if (test(join(c1, remaining), fileName) == FAIL) {
      return ddRecursive(c1, remaining, fileName);
    } else if (test(join(c2, remaining), fileName) == FAIL) {
      return ddRecursive(c2, remaining, fileName);
    } else {
      List<String> result = new ArrayList<>();
      result.addAll(ddRecursive(c1, join(c2, remaining), fileName));
      result.addAll(ddRecursive(c2, join(c1, remaining), fileName));
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
  public static void divideChanges(List<String> changes, List<String> c1, List<String> c2) {
    int halfSize = changes.size() / 2;
    for (int i = 0; i < changes.size(); i++) {
        if (i < halfSize) {
            c1.add(changes.get(i));
        } else {
            c2.add(changes.get(i));
        }
    }
  }

  public static List<String> join(List<String> l1, List<String> l2) {
    List<String> joined = new ArrayList<>(l1);
    joined.addAll(l2);
    return joined;
  }

  //runs file name to see if it passes, fails, or returns unresolved.
  public static int test(List<String> changes, String fileName) {
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
    List<String> output;
    if(args.length > 1) {
      output = dd(split(args[0], args[1]), args[0]);
      //loop to print results. Alternatively, we can output the results as they are computed.
    }
    else System.out.println("Not enough file names provided");
  }

}
