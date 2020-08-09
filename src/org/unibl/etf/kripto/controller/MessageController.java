package org.unibl.etf.kripto.controller;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.unibl.etf.kripto.util.MessageHandler;
import org.unibl.etf.kripto.util.MessageUtil;
import org.unibl.etf.kripto.util.PropertiesUtil;
import org.unibl.etf.kripto.view.Main;

public class MessageController extends Thread {
	
private static final Properties PROPS = PropertiesUtil.loadProperties();
	
	private final WatchService watcher;
	private final Map<WatchKey,Path> keys;
	public static boolean isRunning = true;
	
	public MessageController() throws IOException{
		String userInbox = PROPS.getProperty("inbox.path") + File.separator + Main.USER.getUsername();
		Path dir = Paths.get(userInbox);
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey,Path>();
		WatchKey key = dir.register(watcher, ENTRY_CREATE);
		keys.put(key, dir);
		
	}
	
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	public void run() {
		while(isRunning) {
			// wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
 
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
 
            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();
 
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
 
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
 
                // print out event
                System.out.format("%s: %s\n", "Message received:", child);
                if(event.kind().equals(ENTRY_CREATE)) {
                	String message = MessageUtil.readMessage(child);
                	System.out.println(message);
                	MessageHandler.handleMessage(message);
                	try {
						Files.delete(child);
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
                synchronized(LoginController.obj) {
                	LoginController.obj.notify();
                }
                
             // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
     
                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
