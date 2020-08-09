package org.unibl.etf.kripto.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultListModel;

import org.unibl.etf.kripto.util.PropertiesUtil;


public class UserController extends Thread{
	
	private static final Properties PROPS = PropertiesUtil.loadProperties();
	
	private final WatchService watcher;
	private final Map<WatchKey,Path> keys;
	public static ArrayList<String> onlineUsers = new ArrayList<>();
	public static DefaultListModel<String> listModel = new DefaultListModel<>();
	public static boolean isRunning = true;
	
	public UserController() throws IOException{
		File[] directories = new File(PROPS.getProperty("inbox.path")).listFiles(File::isDirectory);
		for(File dir: directories) {
			onlineUsers.add(dir.getName());
		}
		if(onlineUsers.size() > 0) {
			for(String user: onlineUsers) {
				listModel.addElement(user);
			}
		}
		Path dir = Paths.get(PROPS.getProperty("inbox.path"));
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey,Path>();
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
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
                //System.out.format("%s: %s\n", event.kind().name(), child);
                if(event.kind().equals(ENTRY_CREATE)) {
                	System.out.format("%s: %s\n", "User logged in", child.getFileName().toString());
                	onlineUsers.add(child.getFileName().toString());
                	listModel.addElement(child.getFileName().toString());
                } else {
                	System.out.format("%s: %s\n", "User logged out", child.getFileName().toString());
                	onlineUsers.remove(child.getFileName().toString());
                	listModel.removeElement(child.getFileName().toString());
                }
                synchronized(LoginController.obj) {
                	LoginController.obj.notify();
                }
                for(String user:onlineUsers) {
                	System.out.println(user);
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
	
	public static void main(String[] args) throws IOException{
		new UserController();
	}
}
