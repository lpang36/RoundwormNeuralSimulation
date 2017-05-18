//Colony of organisms
//Capable of evolving to adapt to environment

import java.util.Vector;
import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;

public class Colony {
  private Vector<Worm> organisms; //list of organisms
  private int population; //number of alive organisms
  double[][][] map; //contains environment conditions
  private int generation = 0; //number of generations that have passed
  double deathRate = 0.3; //proportion of organisms that die per generation
  double reproductionRate = 0.5; //proportion of organisms that are born per generation
  boolean dynamic = false; //do organisms affect environment
  boolean evolving = true; //does colony evolve
  boolean display = true; //does organism location display
  int inputNum = 2; //number of input stimuli in environment
  int outputNum = 4; //number of outputs from organism
  public Colony (double[][][] m, int num) {
    map = m.clone();
    population = num;
    organisms = new Vector<Worm>(population);
    //create new organisms at origin
    for (int i = 0; i<population; i++) {
      Worm W = new Worm(this);
      W.xPos = 0;
      W.yPos = 0; 
      W.updateHealth();
      organisms.add(W);
    }
  }
  //evolve colony by one generation
  public void evolve () {
    MaxPQ<Pair> pairs = new MaxPQ<Pair> (); //list of reproducing pairs
    MinPQ<Worm> worms = new MinPQ<Worm> (); //list of organisms
    //move each organism
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        organisms.get(i).move(); 
        worms.insert(organisms.get(i));
      }
    }
    //display colony
    if (display)
      this.draw();
    //evolve colony
    if (evolving) {
      //kill off organisms with lowest health
      int deathLim;
      if (population<=2/deathRate) {
        deathLim = 0;
      }
      else {
        deathLim = (int)(population*deathRate);
      }
      for (int i = 0; i<deathLim; i++) {
        Worm W = worms.delMin();
        W.die();
        population--;
      }
      //create list of reproducing pairs
      for (int i = 0; i<organisms.size(); i++) {
        if (!organisms.get(i).isDead) {
          for (int j = i+1; j<organisms.size(); j++) {
            if (!organisms.get(j).isDead) {
              Pair P = new Pair(organisms.get(i),organisms.get(j));
              pairs.insert(P); 
            }
          }
        }
      }
      //select fittest pairs to reproduce
      int reproductionLim = (int)Math.min(Math.max(1,population*reproductionRate),pairs.size());
      for (int i = 0; i<reproductionLim; i++) { 
        Pair P = pairs.delMax();
        Worm W = P.reproduce();
        organisms.add(W);
        population++;
      }
    }
    //dynamic map
    if (dynamic) {
      for (int i = 0; i<map.length; i++) {
        for (int j = 0; j<map[0].length; j++) {
          for (int k = 0; k<map[0][0].length; k++) {
            map[i][j][k]+=Math.random()*0.05;
            if (map[i][j][k]>1) {
              map[i][j][k]=1;
            }
            if (map[i][j][k]<0) {
              map[i][j][k]=0;
            }
          }
        }
      }
    }
    generation++;
  }
  //draw organisms
  public void draw() {
    StdDraw.setPenRadius(0.01);
    //clear draw panel
    StdDraw.setPenColor(StdDraw.WHITE);
    StdDraw.filledRectangle(0.5,0.5,0.5,0.5);
    //draw position of living organisms
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        StdDraw.setPenColor(organisms.get(i).ageColor(),0,0);
        double x = organisms.get(i).xPos;
        double y = organisms.get(i).yPos;
        StdDraw.point(x,y);
      }
    }
    //delay (usually not necessary)
    /*
    try {
      Thread.sleep(10);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    */
  }
  //draw environment
  public void drawMap () {
    double xUnit = 1.0/map.length;
    double yUnit = 1.0/map[0].length;
    for (int i = 0; i<map.length; i++) {
      for (int j = 0; j<map[0].length; j++) {
        //colour dependent on nutrient value
        StdDraw.setPenColor(0,0,(int)(map[i][j][0]*255));
        StdDraw.filledRectangle((i+0.5)*xUnit,(j+0.5)*yUnit,0.5*xUnit,0.5*yUnit);
      }
    }
  }
  //average health of colony (should increase or stabilize over time)
  public double colonyHealth() {
    double total = 0;
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        total+=organisms.get(i).health;
      }
    }
    total = total/population;
    return total;
  }
  //average values in genome
  public double[] colonyGenome() {
    double[] total = new double[organisms.get(0).layers];
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        for (int j = 0; j<organisms.get(0).layers; j++) {
          total[j]+=organisms.get(i).genome[j];
        }
      }
    }
    for (int j = 0; j<organisms.get(0).layers; j++) {
      total[j] = total[j]/population;
    }
    return total;
  }
  //average initial synapse weight
  public double colonyWeight() {
    double total = 0;
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        total+=organisms.get(i).weightP;
      }
    }
    total = total/population;
    return total;
  }
  //average initial synapse delay
  public double colonyDelay() {
    double total = 0;
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        total+=organisms.get(i).delayP;
      }
    }
    total = total/population;
    return total;
  }
  //average initial neuron threshold
  public double colonyThreshold() {
    double total = 0;
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        total+=organisms.get(i).thresh;
      }
    }
    total = total/population;
    return total;
  }
  //average colony age
  public double colonyAge() {
    double total = 0;
    for (int i = 0; i<organisms.size(); i++) {
      if (!organisms.get(i).isDead) {
        total+=organisms.get(i).age;
      }
    }
    total = total/population;
    return total;
  }
}