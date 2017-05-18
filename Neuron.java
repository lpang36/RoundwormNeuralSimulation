//simulation of a neuron

import java.util.Vector;
import java.util.Arrays;
import java.util.Comparator;
import edu.princeton.cs.algs4.MinPQ;

public class Neuron {
  private Vector<Synapse> inputs = new Vector<Synapse> (); //input synapses
  private Vector<Synapse> outputs = new Vector<Synapse> (); //output synapses
  private int inputCount = 0; //number of inputs
  private int outputCount = 0; //number of outputs
  boolean[] readFrom; //inputs that have been read
  double[] times; //times of read inputs
  InputKey[] initReadIn; //list of initial read inputs
  MinPQ<InputKey> readIn; //priority queue with all inputs
  double maxTime; //last input to arrive
  double time; //time when neuron outputs
  private double threshold; //above this limit, neuron outputs
  public Neuron (double x) {
    time = 0;
    threshold = x;
  }
  //add input synapse
  public void addInputSynapse (Synapse S) {
    inputs.add(S);
    S.outNum = inputCount;
    inputCount++;
  }
  //add output synapse
  public void addOutputSynapse (Synapse S) {
    outputs.add(S);
    S.inNum = outputCount;
    outputCount++;
  }
  //create data structures after all inputs and outputs have been added
  public void initialize() {
    readFrom = new boolean[inputCount];
    initReadIn = new InputKey[inputCount];
    times = new double[inputCount];
    maxTime = 0;
    readIn = new MinPQ<InputKey> ();
  }
  //read in input from synapse
  public void read(InputKey x, int pos) {
    //add to data structures
    readFrom[pos] = true;
    initReadIn[pos] = x;
    times[pos] = x.time;
    //update max firing time
    if (x.value!=0&&x.time>maxTime) {
      maxTime = x.time;
    }
    //process if all inputs have been recieved
    boolean allRead = true;
    for (int i = 0; i<inputCount; i++) {
      if (!readFrom[i]) {
        allRead = false;
        return;
      }
    }
    if (allRead) {
      this.process();
    }
  }
  //read in input from map 
  public void read(double x) { 
    //higher value means faster output rate
    InputKey key = new InputKey (1/x,1); 
    this.send(key);
  }
  //process inputs
  public void process () {
    //add more inputs to simulate faster firing rates
    //e.g. if one input was sent at a rate of 1/s and another at 2/s
    //this creates another input of the 2/s type at t=1s
    for (int i = 0; i<inputCount; i++) {
      if (initReadIn[i].value!=0) {
        double time = initReadIn[i].time;
        while (time<=maxTime) { 
          InputKey key = new InputKey (time,initReadIn[i].value);
          time+=initReadIn[i].time;
          key.index = i;
          readIn.insert(key);
        }
      }
    }
    //loop through inputs until threshold is reached
    double sum = 0;
    int count = 0;
    while (!readIn.isEmpty()) {
      InputKey key = readIn.delMin();
      sum = sum+key.value;
      if (sum>=threshold) {
        //output once threshold reached
        InputKey out = new InputKey (key.time,1);
        for (int i = 0; i<inputCount; i++) {
          //update synapse weights
          inputs.get(i).reweigh(times[i]-key.time);
        }
        this.send(out);
        return;
      }
      count++;
    }
    //if threshold not reached, send placeholder output
    InputKey out = new InputKey (Double.POSITIVE_INFINITY,0);
    this.send(out);
  }
  //send output to all output synapses
  public void send(InputKey x) {
    time = x.time;
    for (int i = 0; i<outputCount; i++) {
      outputs.get(i).fire(x);
    }
  }
}
