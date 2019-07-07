package com.spyzviridian.markovbot.files;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.exceptions.NewFileException;
import com.spyzviridian.markovbot.learning.IVersionable;

public class FileSaver<T extends IVersionable> {
	
	private static final String HIDDEN_DOT = ".";
	private static final String SEC_EXTENSION = ".sec";
	
	private String subpath;
	private String fileName;
	private String securityFileName;
	private String fullPath;
	private String fullSecurityPath;
	private boolean controlVersion;
	
	public FileSaver(String subpath, String fileName, boolean controlVersion){
		this.subpath = subpath;
		this.fileName = fileName;
		this.fullPath = subpath+File.separator+fileName;
		this.securityFileName = HIDDEN_DOT+fileName+SEC_EXTENSION;
		this.fullSecurityPath = subpath+File.separator+securityFileName;
		this.controlVersion = controlVersion;
	}
	
	private void createSubdirectory(){
		File directory = new File(subpath);
		if(!directory.exists()) {
			directory.mkdirs();
		}
	}
	
	private boolean delete(){
		Path path = FileSystems.getDefault().getPath(subpath, securityFileName);
		try {
			Files.delete(path);
			return true;
		} catch (IOException e) {
			Output.getInstance().printLine("Couldn't delete file '"+path.toString()+"'+, bot consistency may be compromised.", Output.Type.WARNING);
		}
		return false;
	}
	
	private boolean deleteSecurityCopy(){
		Path copyPath = FileSystems.getDefault().getPath(subpath, securityFileName);
		try {
			Files.delete(copyPath);
			return true;
		} catch (IOException e) {
			Output.getInstance().printLine("Couldn't delete security copy, consistency may be compromised.", Output.Type.WARNING);
		}
		return false;
	}
	
	private T create(Supplier<T> supplier){
		File file = new File(fullPath);
		try {
			file.createNewFile();
			return supplier.get();
		} catch (IOException e) {
			Output.getInstance().printLine("Couldn't create file '"+fileName+"'.", Output.Type.ERROR);
			System.exit(-1);
		}
		return null;
	}
	
	// El par�metro es la funci�n con la que se podr�a crear el objeto
	@SuppressWarnings("unchecked")
	public T load(Supplier<T> supplier) throws NewFileException{
		// Lo primero de todo, crear el directorio si no existe
		createSubdirectory();
		File file = new File(fullPath);
		T object = null;
		// �Existe el fichero?
		if(file.exists()){
			// Si existe...
			FileInputStream fileIn = null;
			try {fileIn = new FileInputStream(file);} catch (FileNotFoundException e) {}
			ObjectInputStream in = null;
			
			try {
				// Leemos el fichero
				in = new ObjectInputStream(fileIn);
				object = (T) in.readObject();
			} catch (EOFException | InvalidClassException | StreamCorruptedException | ClassNotFoundException | OptionalDataException e) {
				// El fichero no se puede leer bien
				Output.getInstance().printToLog("File '"+fileName+"' is CORRUPTED or HEAVILY OUTDATED. Attempting to fix it...", Output.Type.ERROR);
				// �Existe copia de seguridad?
				File secCopy = new File(fullSecurityPath);
				if(secCopy.exists()){
					// Existe
					if(fileIn != null) try {fileIn.close();} catch (IOException e1) {Output.getInstance().printToLog(e1.getMessage(), Output.Type.ERROR);}
					Output.getInstance().printToLog("Attempting to recover file '"+fileName+"'.", Output.Type.WARNING);
					// Borramos el corrupto
					delete();
					// Cargamos la copia
					secCopy.renameTo(file);
					return load(supplier);
				}
			} catch (IOException e){
				// El fichero no se puede leer
				Output.getInstance().printToLog("Couldn't load file '"+fileName+"'.", Output.Type.ERROR);
				return null;
			}
		}
		if(object == null){
			// Aunque exist�a el archivo, no se ha podido crear el objeto, as� que tenemos que hacerlo nosotros.
			Output.getInstance().printToLog("Creating new object for file '"+fileName+"'...", Output.Type.ERROR);
			object = create(supplier);
			throw new NewFileException("New object created.", object);
		} else if(controlVersion){
			// �La versi�n es buena?
			if(!object.isUpToDate()){
				object = create(supplier);
				throw new NewFileException("New object created.", object);
			}
		}
		
		// Devolver el objeto ya creado
		return object;
	}
	
	public void save(T object){
		// Vamos a asegurarnos de que el subdirectorio existe
		createSubdirectory();
		// Hacer la copia de seguridad
		Path objectPath = FileSystems.getDefault().getPath(subpath, fileName);
		Path copyPath = FileSystems.getDefault().getPath(subpath, securityFileName);
		try {
			Files.copy(objectPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// No se ha podido hacer esta copia, s�lo vamos a lanzar un warning a�n as�.
			Output.getInstance().printLine("Couldn't make a security copy first, consistency may be vulnerable.", Output.Type.WARNING);
		}
		File file = new File(fullPath);
		// Vamos a intentar guardar el archivo
		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(object);
			out.close();
			fileOut.close();
		} catch (IOException e){
			Output.getInstance().printLine("Couldn't save file '"+fullPath+"'.", Output.Type.ERROR);
			// Ya no hacemos nada m�s
			return;
		}
		// Si hemos llegado aqu� es porque el archivo se ha guardado correctamente
		// por tanto podemos borrar la copia, ya innecesaria.
		deleteSecurityCopy();
	}
}
