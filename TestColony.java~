//class to test colony evolution

public class TestColony {
  public static void main(String[] args) {
    int trials = 10;
    int generations = 1000;
    //generate map
    double[][][] m = new double[100][100][2];
    for (int i = 0; i<100; i++) { 
      for (int j = 0; j<100; j++) {
        m[i][j][0] = 0.2+Math.random()*i*0.008;
        m[i][j][1] = 1-m[i][j][0];
      }
    }
    //run trials
    for (int i = 0; i<trials; i++) {
      Colony C = new Colony(m,2);
      for (int j = 0; j<generations; j++) {
        C.evolve();
      }
    }
  }
}