package utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileUtils {

	public final static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}	
		return ext;
	}
	
	
	public static final class ExtFileFilter extends FileFilter {
		
		private String extension;
		private String description;
		
		public ExtFileFilter(String extension, String description) {
			this.extension = extension;
			this.description = description;
		}
		
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo(extension) == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return description;
		} 
	}
	
		
	
	public static final class ClipboardFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo("clipboard") == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return "Saved boundary parameters (*.clipboard)";
		} 
	}
	
	
	public static final class GBDistFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo("dist") == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return "Computed distributions (*.dist)";
		} 
	}
	
	
	public static final class GBDatFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo("gbdat") == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return "Grain boundary data (*.gbdat)";
		} 
	}
	
	public static final class PNGFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo("png") == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return "Portable Network Graphics (*.png)";
		} 
	}
	
	
	public final static class EPSFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo("eps") == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return "Encapsulated PostScript (*.eps)";
		} 
	}
	
	
	public static final class VTKFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
		
			if(f.isDirectory()) return true;
		
			String ext = getExtension(f);
		
			if(ext != null) {
				if(ext.compareTo("vtk") == 0) return true; else return false;
			} else return false;
		}
		@Override
		public String getDescription() {
			return "VTK file (*.vtk)";
		} 
	}
}
