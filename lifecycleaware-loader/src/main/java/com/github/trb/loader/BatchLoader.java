package com.github.trb.loader;

import java.util.concurrent.BlockingQueue;

import com.github.trb.Event;
import com.github.trb.configure.Configurable;
import com.github.trb.lifecycle.LifecycleAware;

public interface BatchLoader extends LifecycleAware, Configurable {

	public void setQueue(BlockingQueue<Event> queue);

	public BlockingQueue<Event> getQueue();

	public void addBatch(Event event);

	public void flushCommit();
}
