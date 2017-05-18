//class for reproducing pair of organisms

import java.util.Comparator;

public class Pair implements Comparable<Pair> {
  //organisms
  Worm A;
  Worm B;
  double totalHealth; //combined health
  public Pair (Worm M, Worm N) {
    A = M;
    B = N;
    totalHealth = A.health+B.health;
  }
  //compare based on combined health
  public int compareTo(Pair P) {
    double result = this.totalHealth-P.totalHealth;
    if (result>0) {
      return 1;
    }
    else if (result==0) {
      return 0;
    }
    return -1;
  }
  public Comparator<Pair> PairCompare () {
    return new Comparator<Pair> () {
      public int compare(Pair X, Pair Y) {
        return X.compareTo(Y);
      }
    };
  }
  public Worm reproduce () {
    return new Worm (A,B);
  }
}