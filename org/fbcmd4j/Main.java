package org.fbcmd4j;																				// Paquete org.fbcmd4j

import java.io.IOException;																			// Excepci�n IO
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;															// Error de entrada
import java.util.Scanner;																			// Scanner de entradas
import java.util.Properties;																		// Asistente de propiedades

import org.apache.logging.log4j.Logger;																// Interfaz Logger

import org.apache.logging.log4j.LogManager;															// Manejo del Logger

import facebook4j.*;

public class Main {																					// Clase Main
	private static final Logger logger = LogManager.getLogger(org.fbcmd4j.Main.class);				// Se inicia logger para Main
	
	private static final String CFG_DIR = "config";													// Directorio de configuraci�n
	private static final String CFG_FILE = "fbcmd4j.properties";									// Archivo de configuraci�n
	private static final String APP_VERSION = "v1.0"; 												// Versi�n de aplicaci�n
		
	public static void main(String[]args) throws FacebookException {
		logger.info("Iniciando la app...");															// Inicio del log	
		System.out.format("Cliente de Facebook FBcmd4J %s\n\n", APP_VERSION);						// Encabezado de la app
				
		Scanner scanner = new Scanner(System.in);
		
		Facebook facebook = null;
		Properties properties = null;																// Propiedades
		
		try {
			properties = org.fbcmd4j.Utils.loadPropertiesFromFile(CFG_DIR, CFG_FILE);				// Buscando propiedades
		}
		catch (IOException ex) {																	// Manejo excepci�n
			ex.printStackTrace();																	// Impresi�n de excepci�n
			logger.error(ex.toString());															// Log de excepci�n
		}
		
		if(properties == null) {																	// Propiedades no existe
			System.out.println("Ahora se configurara el cliente de Facebook para acceder"			// Mensaje de configuraci�n
							+" a tu cuenta...");
			logger.info("Realizando configuraci�n de cliente de Facebook...");						// Log de config 
			org.fbcmd4j.Utils.configFB(properties);													// Cargar configuraci�n
		}
		if(org.fbcmd4j.Utils.ACCESS_TOKEN == null) {
			System.out.print("Parece que falta el token de acceso, vamos a obtenerlo...");
			logger.info("Obteniendo token de acceso...");
			org.fbcmd4j.Utils.getAccessToken(CFG_DIR, CFG_FILE, properties, scanner);
		}
		System.out.println("Validaci�n de configuraci�n correcta.");
		logger.info("Inicio correcto");
		
		System.out.println("Bienvenido al cliente para Facebook FBcmd4J "+APP_VERSION);
		
		int seleccion;
		
		try {
			while(true) {
				facebook = Utils.configFB(properties);
				
				System.out.println("Opciones: ");
				System.out.println("(0) Salir");
				System.out.println("(1) Newsfeed");
				System.out.println("(2) Wall");
				System.out.println("(3) Publicar");
				System.out.println("(4) Guardar Newsfeed");
				System.out.println("(5) Guardar Wall");
				
				try {
				seleccion = scanner.nextInt();
				scanner.nextLine();
				
				switch(seleccion) {
				case 0:
					System.out.println("Saliendo...");
					logger.info("Saliendo...");
					System.exit(0);
				case 1: 
					@SuppressWarnings("rawtypes") 
					ResponseList listf = facebook.getHome();
					System.out.println("�Cu�ntas publicaciones deseas que se muestren?  ");
					int numf = scanner.nextInt();
					scanner.nextLine();
					org.fbcmd4j.Utils.getNewsFeed(listf, numf);
					break;
				case 2: 
					@SuppressWarnings("rawtypes") 
					ResponseList listw = facebook.getPosts();
					System.out.println("�Cu�ntas publicaciones deseas que se muestren?  ");
					int numw = scanner.nextInt();
					scanner.nextLine();
					org.fbcmd4j.Utils.getWall(listw, numw);
					break;
				case 3: 
					System.out.println("Ingresa el mensaje que quieres publicar: ");
					facebook.postStatusMessage(scanner.nextLine());
					break;
				case 4: 
					@SuppressWarnings("rawtypes") 
					ResponseList listff = facebook.getHome();
					System.out.println("�Cu�ntas publicaciones deseas guardar?  ");
					int numff = scanner.nextInt();
					scanner.nextLine();
					System.out.println("Ingresa el directorio donde deseas guardar el archivo:");
					Path dirf = Paths.get(scanner.nextLine());
					org.fbcmd4j.Utils.printNewsFeed(listff, numff, dirf);
					break;
				case 5: 
					@SuppressWarnings("rawtypes") 
					ResponseList listwf = facebook.getPosts();
					System.out.println("�Cu�ntas publicaciones deseas guardar?  ");
					int numwf = scanner.nextInt();
					scanner.nextLine();
					System.out.println("Ingresa el directorio donde deseas guardar el archivo:");
					Path dirw = Paths.get(scanner.nextLine());
					org.fbcmd4j.Utils.printWall(listwf, numwf, dirw);
					break;
				default: 
					System.out.println("Opci�n inv�lida, int�ntalo de nuevo.");
					logger.error("Selecci�n inv�lida por el usuario.");
					break;
				}
				}
				catch (InputMismatchException ex) {
					System.out.println("Ocurri� un error. Consulta el log de la aplicaci�n para m�s"
							+ " informaci�n.");
					logger.error(ex);
				}
			}
		}
		catch (Exception ex) {
			System.out.println("Ocurri� un error. Consulta el log de la aplicaci�n para m�s"
					+ " informaci�n.");
			logger.error(ex);
		}
		}
}
