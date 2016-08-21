package com.github.trb;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.trb.configure.Configuration;
import com.github.trb.loader.BatchLoader;
import com.github.trb.source.Source;

public class Application {
	private Configuration conf;
	private Source source;
	private BatchLoader loader;

	public Application(String file) {
		this.conf = new Configuration(file);
	}

	public void start() {
		BlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
		source.configure(conf);
		source.setQueue(queue);
		source.start();
		loader.configure(conf);
		loader.setQueue(queue);
		loader.start();
	}

	public void stop() {
		source.stop();
		loader.stop();
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.exit(1);
		}
		String file = args[0].trim();
		final Application app = new Application(file);
		app.start();
		Runtime.getRuntime().addShutdownHook(new Thread("batch-loader-shut-down-hook") {
			@Override
			public void run() {
				app.stop();
			}
		});
	}
}
