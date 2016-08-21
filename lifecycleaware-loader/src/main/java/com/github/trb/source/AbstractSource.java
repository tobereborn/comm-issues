package com.github.trb.source;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import com.github.trb.Event;
import com.github.trb.configure.Configuration;
import com.github.trb.lifecycle.LifecycleState;

public abstract class AbstractSource implements Source {
	private LifecycleState state;
	private Configuration conf;
	private ExecutorService executor;
	private BlockingQueue<Event> queue;
	private ReentrantLock lock;

	public AbstractSource() {
		this.state = LifecycleState.IDLE;
		this.executor = Executors.newSingleThreadExecutor();
		this.lock = new ReentrantLock();
	}

	@Override
	public void configure(Configuration conf) {
		lock.lock();
		try {
			this.conf = conf;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Configuration getConfiguration() {
		lock.lock();
		try {
			return conf;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setQueue(BlockingQueue<Event> queue) {
		lock.lock();
		try {
			this.queue = queue;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public BlockingQueue<Event> getQueue() {
		lock.lock();
		try {
			return queue;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void start() {
		lock.lock();
		try {
			state = LifecycleState.START;
			executor.submit(new SourceRunner());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void stop() {
		executor.shutdown();
	}

	@Override
	public LifecycleState getState() {
		lock.lock();
		try {
			return state;
		} finally {
			lock.unlock();
		}
	}

	private class SourceRunner implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				if (hasNext()) {
					Event event = next();
					try {
						queue.put(event);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}
}