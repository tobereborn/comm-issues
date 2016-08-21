package com.github.trb.source;

import java.util.concurrent.BlockingQueue;

import com.github.trb.Event;
import com.github.trb.configure.Configurable;
import com.github.trb.lifecycle.LifecycleAware;

public interface Source extends LifecycleAware, Configurable {

	public void setQueue(BlockingQueue<Event> queue);

	public BlockingQueue<Event> getQueue();

	public boolean hasNext();

	public Event next();

}
