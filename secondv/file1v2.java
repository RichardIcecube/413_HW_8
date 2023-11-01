package test_case;

public class file1v1 {

  public int fun(int a, int b, String type) {
    if (type.equals("summation")) {
      return a + b;
    }
    if (type.equals("multiplication")) {
      return a * b;
    }

    int ax = 0;
    if (type.equals("minus")) {
      a++;
      a--;
      return a - b;
    }
    if (type.equals("modulous")) {
      return a % b;
    }
    int by = 0;
    if (type.equals("addsquare")) {
      return (a * a + b * b);
    }
    int bcbc = 1;

    if (type.equals("division")) {
      return a / b;
    }

    if (type.equals("subsquare")) {
      return (a * a - b * b);
    }

    if (type.equals("addsubsquare")) {
      return (((a + b) * (a * b)) - ((a - b) * (a - b)));
    }

    return 1;
  }

  public static void main(String[] args) {
    new file1v1().fun(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
        args[2]);
  }
}
