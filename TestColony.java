//class to test colony evolution

import java.io.*;

public class TestColony {
  //return string of colony statistics
  public static String colonyData (Colony C) {
    String out;
    out=C.colonyHealth()+" ";
    double[] genome = C.colonyGenome();
    for (int i = 0; i<genome.length; i++) {
      out+=genome[i]+" ";
    }
    out+=C.colonyWeight()+" ";
    out+=C.colonyDelay()+" ";
    out+=C.colonyThreshold()+" ";
    out+=C.colonyAge()+"\n";
    return out;
  }
  //Experiment testing learning
  public static void main(String[] args) {
    int trials = 1;
    int generations = 100;
    FileWriter fw = null;
    BufferedWriter bw = null;
    try {
      fw = new FileWriter("learningInputsData4.txt");
      bw = new BufferedWriter(fw);
      bw.write(trials+" trials "+generations+" generations\n");
      bw.write("Determine if organism can form associations between inputs."); //description of test
    } catch (IOException e) {}
    //generate map
    double[][][] m = new double[100][100][3];
    for (int i = 0; i<100; i++) { 
      for (int j = 0; j<100; j++) {
        m[i][j][0] = 0.2+Math.random()*i*0.008;
        m[i][j][1] = 1-m[i][j][0];
        m[i][j][2] = m[i][j][0];
      }
    }
    //run trials
    for (int i = 0; i<trials; i++) {
      try {
        bw.write("Trial "+i+"\n");
      } catch (IOException e) {}
      Colony C = new Colony(m,2);
      C.inputNum = 3;
      C.display = false;
      for (int j = 0; j<generations/2; j++) {
        C.evolve();
      }
      C.evolving = false; //test organisms without evolving them
      //change map
      for (int k = 0; k<100; k++) { 
        for (int l = 0; l<100; l++) {
          m[k][l][2] = l/100+0.005;
        }
      }
      C.map = m;
      for (int j = 0; j<generations/2; j++) {
        C.evolve();
        try {
          bw.write(C.colonyPosition()[1]+"\n");
        } catch (IOException e) {}
      }
    }
    try {
      bw.close();
    } catch (IOException e) {}
  }
  //Standard experiment
  /*
  public static void main(String[] args) {
    int trials = 5;
    int generations = 100;
    FileWriter fw = null;
    BufferedWriter bw = null;
    try {
      fw = new FileWriter("standardTestData.txt");
      bw = new BufferedWriter(fw);
      bw.write(trials+" trials "+generations+" generations\n");
      bw.write("Monitor various statistics across generations."); //description of test
    } catch (IOException e) {}
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
      try {
        bw.write("Trial "+i+"\n");
      } catch (IOException e) {}
      Colony C = new Colony(m,2);
      C.display = false;
      for (int j = 0; j<generations; j++) {
        C.evolve();
        try {
          bw.write(colonyData(C));
        } catch (IOException e) {}
      }
    }
    try {
      bw.close();
    } catch (IOException e) {}
  }
  */
}