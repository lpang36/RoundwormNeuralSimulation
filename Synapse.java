//simulates synapse between two neurons
//uses spike timing dependent plasticity (stdp) to alter weights and delays
//basic principle:
//input neurons which fire immediately before output neuron have connection strengthened
//otherwise weakened

import java.util.Vector;

public class Synapse {
  private Neuron in; //input neuron
  private Neuron out; //output neuron
  private Vector<Double> memory; //contains all weight updates
  int inNum; //index in input neuron's synapse list
  int outNum; //index in output neuron's synapse list
  double weight; //multiply input by this factor
  //stdp weighting constant
  private double ampPos = 1; 
  private double ampNeg = 1; 
  private double timeFactor = 1; 
  private double scalingFactor = 0.01;
  double delay; //delay introduced from neuron firing
  public Synapse (Neuron A, Neuron B, double x, double y) {
    //initialize weight based on inherited value
    if (Math.random()>x) {
      weight = 1;
    }
    else {
      weight = 0;
    }
    //initialize delay based on inherited value
    delay = Math.max(0,Math.random()*y);
    in = A;
    out = B;
    A.addOutputSynapse(this);
    B.addInputSynapse(this);
    memory = new Vector<Double>();
  }
  //fire synapse
  public void fire(InputKey x) { 
    double output = x.value*weight;
    InputKey key = new InputKey(x.time+delay,output);
    out.read(key,outNum);
  }
  //update weights based on stdp
  public void reweigh (double x) {
    //exponential decay function for weight
    double delta;
    if (x<=0) {//equals needed
      delta = ampPos*Math.exp(x/timeFactor);
    }
    else {
      delta = -ampNeg*Math.exp(-x/timeFactor);
    }
    memory.add(delta);
    //update weight based on past values
    //more recent weights are valued more
    //for input neurons firing just before output, increase weight and decrease delay
    for (int i = 0; i<memory.size(); i++) {
      weight+=memory.get(i)*Math.pow(1/2,memory.size()-i-1)/(Math.pow(2,memory.size()));
      delay-=scalingFactor*memory.get(i)*Math.pow(1/2,memory.size()-i-1)/(Math.pow(2,memory.size()));
    }
    if (delay<0)
      delay=0;
  }
}