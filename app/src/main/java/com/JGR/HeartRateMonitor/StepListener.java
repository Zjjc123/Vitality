package com.JGR.HeartRateMonitor;

// Will listen to step alerts
public interface StepListener {

    public void step(long timeNs);

}