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
		String format = "%s %s\n";
		String prefix = "[CodeSizeUtility] ";
		File input = null;
		Integer size = new Integer(-1);
		Scanner sc = new Scanner(System.in);
		
		String in = null;
		do
		{
			if (args != null && args.length > 0)
			{
				in = args[0];
			}
			else
			{
				while (in == null)
				{
					if (in == null)
					{
						System.out.printf("%s %s", prefix, "Enter the directory or .jar file path\n> ");
						in = sc.nextLine();
					}
				}
				if (in.indexOf('"') != -1)
				{
					 in = in.replaceAll("\"", "");;
				}
			}
			
			
			input = new File(in);
			if (input.isFile() && (input.getName().endsWith(".jar") || input.getName().endsWith(".zip")))
			{
				System.out.printf(format, prefix, "Checking jarfile codesize...");
				size = getJarFileCodeSize(input);
			}
			else if (input.isDirectory())
			{
				System.out.printf(format, prefix, "Checking directory codesize...");
				size = getDirectoryCodeSize(input);
			}
			
			if (size == -1)
			{
				System.out.printf(format, prefix, "An error occoured. You specified the path: " + input.getAbsolutePath() +
						"\n\t\t   Check to make sure that the file path is correct and that CodeSizeUtility has read access to it.");
			}
			else
			{
				System.out.printf(format, prefix, "Codesize for '" + input.getAbsolutePath() + "': " + size + " bytes");
			}
	/*		sc.nextLine();
			sc.close();*/
			input = null;
			size = -1;
			args = null;
			System.out.println();
			System.out.printf("%s %s",prefix,"Enter the path of another file to check its codesize, or type \"exit\" to exit.\n> ");
			in=sc.nextLine();
		} while(!in.equalsIgnoreCase("exit"));
		sc.close();
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
