package org.fbcmd4j;

import java.awt.Desktop;
import java.net.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.auth.*;
import facebook4j.api.*;
import facebook4j.*;

public class Utils {
	private static final Logger logger = LogManager.getLogger(org.fbcmd4j.Utils.class);
	private static final String APP_ID = "1493868560645293";
	private static final String APP_SECRET = "de67f49ea16508bac9756cb1dc37a004";
	public static final String ACCESS_TOKEN = "EAAVOqmDlZCK0BAMe4VqFsTWACNSMEfhv"
			+ "8QoK88agMXGsQKnpBRcmthUP4jh4ZACrUWQ4G7JZBlxD5iiiOtBaC7h3WdP5F3b1V7"
			+ "8LrybSpednlq4Y2t9u0mHiAi10ijvyFjCZCQabccwpf4guYXOyW8ZB59cl6hs3H7MNGn4y3kuKfm02qLjIlvwccJCDRQCoZD";
	
	static void getAccessToken(String cfgDir, String cfgFile, Properties properties,
			Scanner scanner) throws FacebookException {
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(APP_ID, APP_SECRET);
		facebook.setOAuthPermissions(properties.getProperty("out.appPermissions="));
		AccessToken accessToken = facebook.getOAuthAccessToken(ACCESS_TOKEN);
		facebook.setOAuthAccessToken(accessToken);
		
		if(accessToken.toString() == null) {
			try {
				while(accessToken.toString() == null) {
					URL url = new URL("http://graph.facebook.com/oauth/access_token?client_id="+APP_ID+
							"&client_secret"+APP_SECRET+"&grant_type=client_credentials");
					System.out.println("\tVe al siguiente URL y autoriza tu cuenta si no"
							+ " se abre tu navegador automáticamente: ");
					logger.info("Obteniendo access token: ");
					System.out.println("\t"+url);
					try {
						Desktop.getDesktop().browse(new URI(url.toURI().toString()));
					} catch (UnsupportedOperationException|URISyntaxException|IOException ignore) {
					}
					
					System.out.println("Ingresa el código de seguridad: ");
					String cs = scanner.nextLine();
					AccessToken requestToken = facebook.getOAuthAccessToken();
					
					try {
						if (cs.length() > 0) {
							accessToken = facebook.getOAuthAccessToken(requestToken.toString(), cs);
						} else {
							accessToken = facebook.getOAuthAccessToken(requestToken.toString());
						}
					} catch (FacebookException fe) {
						logger.error(fe);
					}
					
					logger.info("Access token obtenido.");
					System.out.println("\tAccess token: " + accessToken.getToken());
					
					properties.setProperty("oauth.appAccessToken=", accessToken.getToken());
					
					saveProperties(cfgDir, cfgFile, properties);
					logger.info("Configuraión guardada exitosamente.");
				}
			}
			catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	public static Facebook configFB(Properties properties) throws FacebookException {
		logger.info("Iniciando instancia de Facebook...");
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(APP_ID, APP_SECRET);
		facebook.setOAuthPermissions(properties.getProperty("outh.appPermissions"));
		facebook.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN));
		return facebook;
	}
	
	public static Properties loadPropertiesFromFile(String cfgDir, String cfgFile) throws IOException {
		Properties properties = new Properties();
		Path propFile = Paths.get(cfgDir, cfgFile);
		if(Files.exists(propFile)) {
			properties.load(Files.newInputStream(propFile));
			BiConsumer<Object, Object> c = (x, y) -> x.toString().equals(y);
			c.accept(properties, null);
			properties.forEach(c);
			if(c.equals(false)) {
				logger.info("Archivo fbcmd4j.properties correcto.");
			}
			else {
				logger.error("Archivo fbcmd4j.properties vacío.");
			}
		}
		else {
			logger.info("Creando nuevo archivo de confifugración...");
			Files.copy(Paths.get("config", "fbcmd4j.properties"), propFile);
		}
		return properties;
	}
	
	public static void saveProperties(String cfgDir, String cfgFile, Properties properties) throws IOException {
		Path propFile = Paths.get(cfgDir, cfgFile);
		properties.store(Files.newOutputStream(propFile), "Guardado por getAccessToken");
	}
	
	public static void getNewsFeed (Object list, int num) {
		logger.info("Obteniendo newsfeed del usuario...");
		PostMethods feed = (PostMethods) list;
		try {
			System.out.println("Publicaciones del Feed: ");
			HashMap<Integer, List<Object>> feedMap = new HashMap<Integer, List<Object>>();
			Object[] feedA = feed.getHome().toArray();
			for(int i = 0; i <= num; i++) {
				feedMap.put(i, new ArrayList<Object>());
				for(int j = 0; j <= num; j++) {
					feedMap.get(j).add(feedA[j]);
				}
			}
			feedMap.forEach((k, v) -> System.out.println("Post: "+ k + "\n" + v));
			}
		catch (FacebookException ex) {
			System.out.println("Ocurrió un error al obtener el newsfeed. Consulta el "
					+ "log para más información");
			logger.error(ex);
		}
	}
	
	public static void getWall (Object list, int num) {
		logger.info("Obteniendo wall del usuario...");
		PostMethods wall = (PostMethods) list;
			try {
				if(wall.getPosts() != null) {
					System.out.println("Publicaciones del Wall: ");
					HashMap<Integer, List<Object>> wallMap = new HashMap<Integer, List<Object>>();
					Object[] wallA = wall.getPosts().toArray();
					for(int i = 0; i <= num; i++) {
						wallMap.put(i, new ArrayList<Object>());
						for(int j = 0; j <= num; j++) {
							wallMap.get(j).add(wallA[j]);
						}
					}
					wallMap.forEach((k, v) -> System.out.println("Post: "+ k + "\n" + v));
				}
			}
			catch (FacebookException ex) {
			System.out.println("Ocurrió un error al obtener el wall. Consulta el log "
					+ "para más información");
			logger.error(ex);
			}
		}
	
	public static void printNewsFeed (Object list, int num, Path dir) {
		logger.info("Obteniendo newsfeed del usuario...");
		PostMethods feed = (PostMethods) list;
		try {
			if (Files.exists(dir) && Files.isDirectory(dir)){ 
				if(feed.getHome() != null) {
					System.out.println("Publicaciones del Feed: ");
					Path guardarDirf = Paths.get(dir.getFileName() + ".txt");
					BufferedWriter writer = Files.newBufferedWriter(guardarDirf);
					HashMap<Integer, List<Object>> feedMap = new HashMap<Integer, List<Object>>();
					Object[] feedA = feed.getHome().toArray();
					for(int i = 0; i <= num; i++) {
						feedMap.put(i, new ArrayList<Object>());
						for(int j = 0; j <= num; j++) {
							feedMap.get(j).add(feedA[j]);
						}
					}
					feedMap.forEach((k, v) -> {
						try {
							writer.write("Post: "+ k + "\n" + v);
						} catch (IOException e) {	
							System.out.println("Ocurrió un error al exportar datos al archivo. Consulta"
									+ " el log de la aplicación para más información.");	
							logger.error(e);
						}
						});
					writer.close();
				}
			}
		}
		catch (FacebookException|IOException ex) {
			System.out.println("Ocurrió un error al obtener el newsfeed. Consulta el "
					+ "log para más información");
			logger.error(ex);
		}
	}
	
	public static void printWall (Object list, int num, Path dir) {
		logger.info("Obteniendo wall del usuario...");
		PostMethods wall = (PostMethods) list;
		try {
			if(wall.getPosts() != null) {
				System.out.println("Publicaciones del Wall: ");
				Path guardarDirw = Paths.get(dir.getFileName() + ".txt");
				BufferedWriter writer = Files.newBufferedWriter(guardarDirw);
				HashMap<Integer, List<Object>> wallMap = new HashMap<Integer, List<Object>>();
				Object[] wallA = wall.getPosts().toArray();
				for(int i = 0; i <= num; i++) {
					wallMap.put(i, new ArrayList<Object>());
					for(int j = 0; j <= num; j++) {
						wallMap.get(j).add(wallA[j]);
					}
				}
				wallMap.forEach((k, v) -> {
					try {
						writer.write("Post: "+ k + "\n" + v);
					} catch (IOException e) {	
						System.out.println("Ocurrió un error al exportar datos al archivo. Consulta"
								+ " el log de la aplicación para más información.");	
						logger.error(e);
					}
					});
				writer.close();
			}
		}
		catch (FacebookException|IOException ex) {
			System.out.println("Ocurrió un error al obtener el wall. Consulta el log "
					+ "para más información");
			logger.error(ex);
		}
	}
}
