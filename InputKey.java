//class for neural impulses
//each contains a time and signal strength

import java.util.Comparator;  

public class InputKey implements Comparable <InputKey> {
    int index; //index in list of input neurons
    double time; //time
    double value; //signal strength
    InputKey(double b, double c) {
      time = b;
      value = c;
      //corner case
      if (b==Double.POSITIVE_INFINITY||b==Double.NEGATIVE_INFINITY) {
        value = 0;
      }
    }
    //compare based on time
    @Override
    public int compareTo(InputKey A) {
      if (A.time<this.time) 
        return 1;
      if (this.time<A.time)
        return -1;
      return 0;
    }
  }