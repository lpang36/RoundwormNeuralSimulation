//simulates an organism with about 200 neurons
//based on Caenorhabditis elegans, a roundworm

import java.util.Arrays; 
import java.util.Comparator;
import java.util.Vector;

public class Worm implements Comparable<Worm> {
  double xPos; //x position
  double yPos; //y position 
  double speed; //speed
  double direction; //direction
  int layers = 5; //number of layers of neurons, each layer is completely connected to those before and after it
  private double speedLimit = 0.05; //max speed
  private Vector<Double> pastHealth = new Vector<Double>(); //past organism health updates
  double health = 0; //organism health
  boolean isDead = false; //state
  int age = 0; //age
  private int inputNum; //number of input neurons
  private int outputNum; //number of output neurons (speed +/-, direction +/-)
  private int num = 200; //approximate number of neurons
  private Neuron[] inputArray; //input neurons
  private Neuron[] outputArray; //output neurons
  private Vector<Vector<Neuron>> array = new Vector<Vector<Neuron>>(layers); //all intermediate neurons
  int[] genome = new int [layers]; //specifies number of neurons per layer
  double weightP; //initial synaptic weight
  double delayP; //initial synaptic delay
  double thresh; //initial neuron threshold
  private int geneMutationFactor = 5; //variation in number of neurons per layer 
  private double weightMutationFactor = 0.05; //variation in synaptic weight
  private double delayMutationFactor = 0.01; //variation in synaptic delay
  private double threshMutationFactor = 0.05; //variation in neural threshold
  private double minThresh = 0.01; //minimum neural threshold
  private int synapseNum = 0; //count synapses
  Colony C; //parent colony
  //speed and direction conversion constants
  private double speedScaling = 1; 
  private double directionScaling = 0.1;
  public Worm (Colony X) {
    xPos = 0;
    yPos = 0; 
    C = X;
    inputNum = C.inputNum;
    outputNum = C.outputNum;
    for (int i = 0; i<layers; i++) {
      genome[i] = (int)(Math.random()*num/(layers/2.0))+1;
    }
    weightP = Math.random();
    delayP = Math.random();
    thresh = 1;
    initialize();
  } 
  public Worm (Worm A, Worm B) {
    C = A.C;
    inputNum = C.inputNum;
    outputNum = C.outputNum;
    genome = new int [layers];
    //mutate genome, weight, delay, and threshold based on parent values
    //randomly select one of parent values
    //add random variation scaled to constants above
    for (int i = 0; i<layers; i++) {
      int x;
      if (Math.random()>0.5) 
        x = A.genome[i];
      else
        x = B.genome[i];
      genome[i] = (int)Math.max(1,Math.round(x+Math.random()*2*geneMutationFactor-geneMutationFactor));
    }
    if (Math.random()>0.5)
      weightP = A.weightP;
    else
      weightP = B.weightP;
    weightP+=Math.random()*2*weightMutationFactor-weightMutationFactor;
    if (Math.random()>0.5)
      delayP = A.delayP;
    else
      delayP = B.delayP;
    delayP+=Math.random()*2*delayMutationFactor-delayMutationFactor;
    if (Math.random()>0.5) 
      thresh = A.thresh;
    else
      thresh = B.thresh;
    thresh+=Math.random()*2*threshMutationFactor-threshMutationFactor;
    if (thresh<minThresh)
      thresh = minThresh;
    initialize();
    xPos = (A.xPos+B.xPos)/2;
    yPos = (A.yPos+B.yPos)/2;
  }
  //create neurons and synapses
  public void initialize () {
    speed = Math.random()*speedLimit;
    direction = Math.random()*6.28;
    inputArray = new Neuron[inputNum];
    outputArray = new Neuron[outputNum];
    //input, output, intermediate neurons
    for (int i = 0; i<inputNum; i++) {
      Neuron N = new Neuron (thresh);
      inputArray[i] = N;
    }
    for (int i = 0; i<outputNum; i++) {
      Neuron N = new Neuron (thresh);
      outputArray[i] = N;
    }
    for (int i = 0; i<layers; i++) {
      Vector<Neuron> newLayer = new Vector<Neuron>();
      for (int j = 0; j<genome[i]; j++) {
            Neuron N = new Neuron (thresh);
            newLayer.add(N);
      }
      array.add(newLayer);
    }
    //create synapses from input to 1st layer
    for (int i = 0; i<inputNum; i++) {
      for (int j = 0; j<array.get(0).size(); j++) {
        Synapse S = new Synapse(inputArray[i],array.get(0).get(j),weightP,delayP);
        synapseNum++;
      }
    }
    //create synapses from layer n to n+1
    for (int i = 0; i<layers-1; i++) {
      for (int j = 0; j<array.get(i).size(); j++) {
        for (int k = 0; k<array.get(i+1).size(); k++) {
          Synapse S = new Synapse(array.get(i).get(j),array.get(i+1).get(k),weightP,delayP);
          synapseNum++;
        }
      }
    }
    //create synapses within a layer
    /*
    for (int i = 0; i<layers; i++) {
      for (int j = 0; j<array.get(i).size(); j++) {
        for (int k = j+1; k<array.get(i).size(); k++) {
          Synapse S = new Synapse(array.get(i).get(j),array.get(i).get(k),weightP,delayP);
          synapseNum++;
        }
      }
    }
    */
    //create synapses from last layer to output
    for (int i = 0; i<outputNum; i++) {
      for (int j = 0; j<array.get(layers-1).size(); j++) {
        Synapse S = new Synapse(array.get(layers-1).get(j),outputArray[i],weightP,delayP);
        synapseNum++;
      }
    }
    initializeNeurons();
  }
  //initialize neurons
  public void initializeNeurons() {
    for (int i = 0; i<inputNum; i++) {
      inputArray[i].initialize();
    }
    for (int i = 0; i<outputNum; i++) {
      outputArray[i].initialize();
    }
    for (int i = 0; i<layers; i++) {
      for (int j = 0; j<array.get(i).size(); j++) {
        array.get(i).get(j).initialize();
      }
    }
  }
  public void move() {
    //cause all inputs to read in environment values
    for (int i = 0; i<inputNum; i++) {
      inputArray[i].read(C.map[posToMapX()][posToMapY()][i]);
    }
    //record output firing rate
    double[] times = new double[outputNum];
    for (int i = 0; i<outputNum; i++) {
      if (outputArray[i].time!=0) {
        times[i] = 1/outputArray[i].time;
      }
      else {
        times[i] = 0;
      }
    }
    //adjust speed, direction based on output firing rate
    if ((times[0]-times[1])/speedScaling>0)
      speed = Math.min(speed+(times[0]-times[1])/speedScaling,speedLimit);
    else
      speed = Math.max(speed+(times[0]-times[1])/speedScaling,-speedLimit);
    direction = direction+(times[2]-times[3])/directionScaling;
    updatePositions();
    //reinitialize neurons
    initializeNeurons();
    //update health
    this.updateHealth();
    age++;
  }
  //update positions from speed and direction
  public void updatePositions() {
    xPos = xPos+speed*Math.cos(direction);
    yPos = yPos+speed*Math.sin(direction);
  }
  //convert x position to location in environment
  public int posToMapX () {
    if (xPos<0) {
      xPos = 0;
    }
    else if (xPos>=1) {
      xPos = 1;
      return C.map.length-1;
    }
    return (int)(xPos*C.map.length);
  }
  //convert y position to location in environment
  public int posToMapY () {
    if (yPos<0) {
      yPos = 0;
    }
    else if (yPos>=1) {
      yPos = 1;
      return C.map[0].length-1;
    }
    return (int)(yPos*C.map[0].length);
  }
  public void updateHealth() {
    //current environment effect on health
    //map third dimension contains concentration of various inputs
    //here, 0th input is beneficial, 1st input harmful
    double current = C.map[posToMapX()][posToMapY()][0]-C.map[posToMapX()][posToMapY()][1];
    //update map due to organism activity
    if (C.dynamic) {
      for (int i = 0; i<C.map[0][0].length; i++) {
        if (C.map[posToMapX()][posToMapY()][i]<0)
          C.map[posToMapX()][posToMapY()][i]=0;
      }
    }
    //recalculate health, weighing recent events more heavily
    pastHealth.add(current);
    health = 0;
    for (int i = 0; i<pastHealth.size(); i++) {
      health = health+pastHealth.get(i)*Math.pow(0.5,pastHealth.size()-i)/(Math.pow(2.0,pastHealth.size())-1);
    }
  }
  //display colour of organism based on health
  public int healthColor() {
    int shade = 128+(int)((health)*128);
    if (shade<0)
      shade=0;
    else if (shade>255)
      shade=255;
    return shade;
  }
  //display colour of organism based on age
  public int ageColor() {
    if (age<=1) 
      return 255;
    return 0;
  }
  //compare based on health
  public int compareTo(Worm W) {
    double result = this.health-W.health;
    if (result>0) {
      return 1;
    }
    else if (result==0) {
      return 0;
    }
    return -1;
  }
  public Comparator<Worm> WormCompare () {
    return new Comparator<Worm> () {
      public int compare(Worm X, Worm Y) {
        return X.compareTo(Y);
      }
    };
  }
  //death due to low health
  public void die() {
    health = Double.NEGATIVE_INFINITY;
    isDead = true;
  }
}