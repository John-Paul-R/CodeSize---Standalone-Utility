import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public final class CodeSizeUtility
{
	/**
	 * Copyright (c) 2001-2018 Mathew A. Nelson and Robocode contributors
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://robocode.sourceforge.net/license/epl-v10.html
	 */
	/**
	 * Tool used for calculating the code size of a directory or jar file.
	 * 
	 * @author Flemming N. Larsen (original)
	 * @author John Paul Rutigliano (Conversion to standalone program)
	 */
	public static void main(String[] args)
	{
		String format = "%s %s";
		String prefix = "[CodeSizeUtility] ";
		File input = null;
		Integer size = new Integer(-1);
		
		if (args.length > 0)
		{
			input = new File(args[0]);

		}
		else
		{
			Scanner sc = new Scanner(System.in);
			System.out.println(String.format(format, prefix, "Enter the directory or .jar file path > "));
			boolean completedOnce = false;
			while (input == null)
			{
				if (completedOnce)
				{
					System.out.println(String.format(format, prefix, "Invalid file or directory path. "));
					System.out.println(String.format(format, prefix, "Enter the directory or .jar file path > "));
				}
				String in = sc.nextLine().replaceAll("\"", "");;
				input = new File(in);
			}
			sc.close();
		}

		if (input.isFile() && (input.getName().endsWith(".jar") || input.getName().endsWith(".zip")))
		{
			System.out.println(String.format(format, prefix, "Checking jarfile codesize."));
			size = getJarFileCodeSize(input);
		}
		else if (input.isDirectory())
		{
			System.out.println(String.format(format, prefix, "Checking directory codesize.  (Note: The code size of a directory will be larger than the code size of a packaged robot. (.jar files use zip compression))"));
			size = getDirectoryCodeSize(input);
		}
		
		System.out.println(String.format(format, prefix, "Codesize for '" + input.getName() + "': " + size + " bytes"));
/*		sc.nextLine();
		sc.close();*/
	}



	public static Integer getDirectoryCodeSize(File dir) {
		return getCodeSize("processDirectory", dir);
	}

	public static Integer getJarFileCodeSize(File jarFile) {
		return getCodeSize("processZipFile", jarFile);
	}

	private static Integer getCodeSize(String invokeMethod, File jarFile) {
		Integer codesize;
		try {
			// Call the code size utility using reflection
			Class<?> classType = Class.forName("codesize.Codesize");

			Method method = classType.getMethod(invokeMethod, new Class[] { File.class });
			Object item = method.invoke(null/* static method */, jarFile);

			// Calls Codesize.Item.getCodeSize()
			method = item.getClass().getMethod("getCodeSize", (Class[]) null);
			codesize = (Integer) method.invoke(item, (Object[]) null);

		} catch (IllegalAccessException e) {
			codesize = null;
			System.out.println("IllegalAccessException");
		} catch (InvocationTargetException e) {
			codesize = null;
			System.out.println("InvocationTargetException");
		} catch (NoSuchMethodException e) {
			codesize = null;
			System.out.println("NoSuchMethodException");
		} catch (ClassNotFoundException e) {
			codesize = null;
			System.out.println("ClassNotFoundException");
		}
		return codesize;
	}
	
}
