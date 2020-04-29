package com.stjerncraft.controlpanel.core.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL Database used by the Core, Client Modules and Services for storing data.
 * 
 * All blocking calls are queued and run on a separate DB thread.
 * 
 * TODO: Setup proper error handling and notification for the Database. If it fails or disconnects we should do something.
 */
public class Database {
	private static final Logger logger = LoggerFactory.getLogger(Database.class);
	
	class QueueEntry <T> {
		public Function<Connection, T> handler;
		public CompletableFuture<T> future;
	}
	
	class DatabaseThread implements Runnable {
		public boolean stop = false;
		
		@Override
		public void run() {
			while(!stop) {
				try {
					if(connection.isClosed()) {
						//TODO: Log and properly shutdown
						stop = true;
						break;
					}
					
					@SuppressWarnings("unchecked") //We know that the handler returns the type that the future accepts
					QueueEntry<Object> entry = (QueueEntry<Object>) queue.take();
					Object data = entry.handler.apply(connection);
					
					//TODO: Queue complete call for running on Main Thread
					entry.future.complete(data);
				} catch (InterruptedException e) {
					//TODO: Properly handle interrupt(Log and properly shutdown the database)
					e.printStackTrace();
					stop = true;
				} catch (SQLException e) {
					e.printStackTrace();
					stop = true;
				}
			}
		}
		
	}
	
	LinkedBlockingQueue<QueueEntry<?>> queue;
	Connection connection;
	DatabaseThread threadRunnable;
	Thread thread;
	
	public Database(String url) throws SQLException {
		queue = new LinkedBlockingQueue<>();
		
		//Setup Threads and connect to the database
		threadRunnable = new DatabaseThread();
		
		connection = DriverManager.getConnection(url);
		if(connection != null)
		{
			logger.info("Connected to Database: " + url);
			thread = new Thread(threadRunnable);
			thread.start();
		} else {
			logger.error("Failed to connect to Database: " + url);
			//TODO: Properly handle connection failure
		}
	}
	
	/**
	 * Run given code on the DATABASE THREAD, allowing to access the Database without blocking the main thread.
	 * It is possible to do multiple operations in one handler, but it is recommended to reQueue for longer operations
	 * to avoid the Database becoming locked for others.
	 * 
	 * @param Handler Run on the DATABASE THREAD, providing the Connection object for the Database.
	 * @return Future which will be called on the MAIN THREAD once the handler is finished running(Returns), with the data returned by the handler.
	 */
	public <T extends Object> CompletableFuture<T> queueQuery(Function<Connection, T> handler) {
		QueueEntry<T> entry = new QueueEntry<T>();
		CompletableFuture<T> future = new CompletableFuture<T>();
		entry.future = future;
		entry.handler = handler;
		
		queue.add(entry);
		
		return future;
	}
}
