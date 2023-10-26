import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeltaDebugger {

  static final int PASS = 0;
  static final int FAIL = 1;
  static final int UNRESOLVED = 2;

  public static List<Integer> dd(List<Integer> changes, boolean baselinePasses, boolean allChangesFail) {

    List<Integer> ddRecursive(List<Integer> changes, List<Integer> remaining) {
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

    List<Integer> split(List<Integer> changes, List<Integer> c1, List<Integer> c2) {
      Random rand = new Random();
      for (Integer change : changes) {
        if (rand.nextBoolean()) {
          c1.add(change);
        } else {
          c2.add(change);
        }
      }
      return c1; 
    }

    List<Integer> join(List<Integer> l1, List<Integer> l2) {
      List<Integer> joined = new ArrayList<>();
      joined.addAll(l1);
      joined.addAll(l2);
      return joined;
    }

    int test(List<Integer> changes) {
      if (changes.isEmpty()) {
        return PASS;
      } else if (changes.containsAll(changes)) {
        return FAIL;
      } else {
        return UNRESOLVED;
      }
    }

    return ddRecursive(changes, new ArrayList<Integer>());
  } 
}