package com.github.trb.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.github.trb.Event;
import com.github.trb.configure.Configuration;
import com.github.trb.lifecycle.LifecycleState;

public abstract class AbstractBatchLoader implements BatchLoader {
	private LifecycleState state;
	private Configuration conf;
	private AtomicLong rows;
	private int batchSize;
	private BlockingQueue<Event> queue;
	private ExecutorService executor;
	private ReentrantLock lock;

	public AbstractBatchLoader() {
		this.state = LifecycleState.IDLE;
		this.lock = new ReentrantLock();
		this.rows = new AtomicLong();
		this.executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void configure(Configuration conf) {
		lock.lock();
		try {
			this.conf = conf;
			this.batchSize = conf.get("", 1000);
			this.queue = new LinkedBlockingQueue<Event>(2 * batchSize);
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
	public void start() {
		lock.lock();
		try {
			state = LifecycleState.START;
			executor.submit(new BatchRunner());
		} catch (Exception e) {
			state = LifecycleState.ERROR;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void addBatch(Event event) {
		try {
			doAddBatch(event);
		} catch (Exception e) {

		}
	}

	abstract protected void doAddBatch(Event event) throws IOException;

	@Override
	public void flushCommit() {
		try {
			doFlushCommit();
		} catch (Exception e) {

		}
	}

	abstract protected void doFlushCommit();

	private void drainQueueAndFlushCommit() {
		List<Event> events = new ArrayList<Event>();
		queue.drainTo(events);
		for (Event event : events) {
			addBatch(event);
		}
		flushCommit();
	}

	@Override
	public void stop() {
		executor.shutdownNow();
		drainQueueAndFlushCommit();
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

	private class BatchRunner implements Runnable {

		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Event event = queue.poll(5000, TimeUnit.MILLISECONDS);
					addBatch(event);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					if (rows.incrementAndGet() % batchSize == 0) {
						flushCommit();
					}
				}
			}
		}
	}
}
