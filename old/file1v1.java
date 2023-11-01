package test_case;

public class file1v1 {

  public int fun(int a, int b, String type) {
    int x = 0;
    if (type.equals("summation")) {
      return a + b;
    }

    int y;
    if (type.equals("minus")) {

      return a - b;
    }

    if (type.equals("modulous")) {
      a--;
      a++;
      return a % b;
    }

    int bcbc = 1;

    if (type.equals("subsquare")) {
      return (a * a - b * b);
    }
    int v = 0;

    return 0;
  }

  public static void main(String[] args) {
    new file1v1().fun(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
        args[2]);
  }
}
