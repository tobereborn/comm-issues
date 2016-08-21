package com.github.trb.lifecycle;

public interface LifecycleAware {

	public void start();

	public void stop();

	public LifecycleState getState();
}
