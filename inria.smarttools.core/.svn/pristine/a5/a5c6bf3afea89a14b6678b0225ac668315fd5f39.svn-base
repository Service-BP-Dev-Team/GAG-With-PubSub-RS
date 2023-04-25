/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
 * 
 *    This file is part of SmartTools project
 *    <a href="http://www-sop.inria.fr/smartool/">SmartTools</a>
 * 
 *     SmartTools is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     SmartTools is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SmartTools; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: StringUtil.java 2819 2009-04-14 12:27:41Z bboussema $
package inria.smarttools.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contain some usefull (static) methods to deal with String.
 * 
 * @author <a href="mailto:Didier.Parigot@sophia.inria.fr">Didier Parigot</a>
 * @version $Revision: 2819 $
 */
public class StringUtil {

	/**
	 * add dir-component to current path i.e res = path + "/" + dir + "/"
	 * 
	 * @param path
	 *            a value of type 'String'
	 * @param dir
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String addDirToPath(String path, final String dir) {
		String dirfs = dir;
		if (!dir.endsWith(File.separator)) {
			dirfs += File.separator;
		}
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		return path + dirfs;
	}

	/**
	 * Return last component of a string composed by element separated by <sep>
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @param sep
	 *            a <code>String</code> value : separator value
	 * @return a <code>String</code> value : last component or String it-self
	 */
	public static String baseDir(String name, final String sep) {
		int indice = name.lastIndexOf(sep);
		name = name.substring(0, indice);
		indice = name.lastIndexOf(sep);
		if (indice >= 0) {
			final String withoutSep = name.substring(indice + 1);
			return withoutSep;
		}
		return name;
	}

	/**
	 * Return the basename of a file name (i.e. the filename without extention)
	 * 
	 * @param filename
	 *            a <code>String</code> value
	 * @return a <code>String</code> value
	 */
	public static String baseName(final String filename) {
		int indice = filename.lastIndexOf(File.separator);
		if (indice >= 0) {
			final String withoutPath = filename.substring(indice + 1);
			indice = withoutPath.lastIndexOf(".");
			if (indice >= 0) {
				final String basename = withoutPath.substring(0, indice);
				return basename;
			}
		}
		return filename;
	}

	/**
	 * Capitalize a string.
	 * 
	 * @param str
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String capitalize(final String str) {
		if (str.equals("")) {
			return "";
		}
		final String upperFirst = str.substring(0, 1).toUpperCase();
		final String capitalizeStr = upperFirst + str.substring(1);
		return capitalizeStr;
	}

	/**
	 * Look if pathname ends with File.separator if not add it !
	 * 
	 * @param pathname
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String cleanPath(final String pathname) {
		String res = pathname;
		if (!pathname.endsWith(File.separator)) {
			res = pathname + File.separator;
		}
		return res;
	}

	/**
	 * Create the <path> directory.
	 * 
	 * @param path
	 *            a <code>String</code> value : the path denoated the directiry
	 *            to create
	 * @return a <code>boolean</code> value : true if success
	 */
	public static boolean createDir(final String path) {
		final File file = new File(path);
		if (!file.isDirectory()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Return true if the path denotated by the string is a directory and exist.
	 * 
	 * @param path
	 *            a value of type 'String' : a path
	 * @return a value of type 'boolean': true if this path is a directory
	 */
	static public boolean dirExists(final String path) {
		final File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return extension value of a filename.
	 * 
	 * @param filename
	 *            a <code>String</code> value : The filename
	 * @return a <code>String</code> value : extention or null
	 */
	static public String extension(final String filename) {
		final int ind = filename.lastIndexOf(".");
		if (ind > 0) {
			return filename.substring(ind + 1);
		} else {
			return null;
		}
	}

	/**
	 * Check if filename extention is ext
	 * 
	 * @param filename
	 *            a value of type 'String'
	 * @param ext
	 *            a value of type 'string'
	 * @return a value of type 'boolean'
	 */
	static public boolean extentionIs(final String filename, final String ext) {
		if (filename.endsWith("." + ext)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return first component of a dotted string.
	 * 
	 * @param name
	 *            a <code>String</code> value : a dotted String i.e. a.b
	 * @return a <code>String</code> value : first component of the dotted
	 *         String, or the String it-self
	 */
	public static String firstComponent(final String name) {
		return StringUtil.firstComponent(name, ".");
	}

	/**
	 * Return first component of a string composed by element separated by <sep>
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @param sep
	 *            a <code>String</code> value : separator value
	 * @return a <code>String</code> value : first component or String it-self
	 */
	public static String firstComponent(final String name, final String sep) {
		final int indice = name.indexOf(sep);
		if (indice >= 0) {
			final String withoutSep = name.substring(0, indice);
			return withoutSep;
		}
		return name;
	}

	/**
	 * Returns a none XML version of the string (ie replace the special
	 * characters like &lt;&gt;&amp;&quot; by their "real" value).
	 * 
	 * @param s
	 *            the string to convert
	 */
	public static String fromXML(final String s) {
		if (s == null) {
			return "";
		}
		final int slength = s.length();
		final StringBuilder result = new StringBuilder(slength);
		char c = 0;
		for (int i = 0; i < slength; i++) {
			c = s.charAt(i);
			if (c == '&') { // Maybe we start an global entity
				if (StringUtil.is("lt;", s, i + 1)) {
					result.append("<");
					i += 3;
				} else if (StringUtil.is("gt;", s, i + 1)) {
					result.append(">");
					i += 3;
				} else if (StringUtil.is("amp;", s, i + 1)) {
					result.append("&");
					i += 4;
				} else if (StringUtil.is("apos;", s, i + 1)) {
					result.append("\'");
					i += 5;
				} else if (StringUtil.is("#37;", s, i + 1)) {
					result.append("%");
					i += 4;
				} else if (StringUtil.is("quot;", s, i + 1)) {
					// We start a 'string' ...
					// So, scan for end indice of string
					result.append("\"");
					i += 5;
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	/**
	 * Return packageName of a given class.
	 * 
	 * @param clazz
	 *            a <code>Class</code> value
	 * @return a <code>String</code> value
	 */
	static public String getPackageName(final Class<?> clazz) {
		return clazz.getPackage().getName();
	}

	/**
	 * Return the directory part of a filename
	 * 
	 * @param str
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String getPath(final String str) {
		return StringUtil.removeAllAfterLast(str, File.separator);
	}

	/**
	 * Return the directory part of a filename
	 * 
	 * @param str
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String getPath(final String str, final String model) {
		return StringUtil.removeAllAfterLast(str, model);
	}

	public static boolean is(final String pattern, final String str,
			final int indice) {
		return (str.indexOf(pattern, indice) == (indice));
	}

	/**
	 * Generate a list of all the components of a dotted string compose and
	 * return the iterator.
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @return a <code>Iterator</code> value : Iterator of a list of all the
	 *         components of the String
	 */
	public static Iterator<String> iteratorComponents(final String name) {
		return StringUtil.iteratorComponents(name, ".");
	}

	/**
	 * Generate a list of all the components of a string composed by element
	 * separated by <sep> and return the iterator.
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @param sep
	 *            a <code>String</code> value : separator value
	 * @return a <code>Iterator</code> value : Iterator of a list of all the
	 *         components of the String
	 */
	public static Iterator<String> iteratorComponents(final String name,
			final String sep) {
		final List<String> list = new ArrayList<String>();
		String components = name;
		String entry = StringUtil.firstComponent(components, sep);
		list.add(entry);
		String tail = StringUtil.removefirstComponent(components, sep);

		while (components != tail) {
			components = tail;
			entry = StringUtil.firstComponent(components, sep);
			list.add(entry);
			tail = StringUtil.removefirstComponent(components, sep);
		}

		return list.iterator();
	}

	/**
	 * Converts a string written in java syntax (ex : \"...) in a normal string.
	 * 
	 * @param s
	 *            a <code>String</code> value
	 * @return a <code>String</code> value
	 */
	public static String javaToNormalSyntax(String s) {
		int index = s.indexOf('\\');
		while (index >= 0) {
			char escapeChar = '\0';
			switch (s.charAt(index + 1)) {
				case 'b':
					escapeChar = '\b';
					break;
				case 't':
					escapeChar = '\t';
					break;
				case 'n':
					escapeChar = '\n';
					break;
				case 'f':
					escapeChar = '\f';
					break;
				case 'r':
					escapeChar = '\r';
					break;
				case '\"':
					escapeChar = '\"';
					break;
				case '\'':
					escapeChar = '\'';
					break;
				case '\\':
					escapeChar = '\\';
					break;
			}
			s = s.substring(0, index) + escapeChar + s.substring(index + 2);
			index = s.indexOf("\\", index + 1);
		}
		return s;
	}

	/**
	 * Return last component of a dotted string.
	 * 
	 * @param name
	 *            a <code>String</code> value : a dotted String i.e. a.b
	 * @return a <code>String</code> value : last component of the dotted
	 *         String, or the String it-self
	 */
	public static String lastComponent(final String name) {
		return StringUtil.lastComponent(name, ".");
	}

	/**
	 * Return last component of a string composed by element separated by <sep>
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @param sep
	 *            a <code>String</code> value : separator value
	 * @return a <code>String</code> value : last component or String it-self
	 */
	public static String lastComponent(final String name, final String sep) {
		final int indice = name.lastIndexOf(sep);
		if (indice >= 0) {
			final String withoutSep = name.substring(indice + 1);
			return withoutSep;
		}
		return name;
	}

	/**
	 * Change all "." in a package name into "/", thus it's a path !!
	 * 
	 * @param packageName
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String packageToPath(final String packageName) {
		return packageName.replace('.', File.separatorChar);
	}

	/**
	 * Remove all char after last occurence of "model"
	 * 
	 * @param str
	 *            a value of type 'String'
	 * @param model
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static String removeAllAfterLast(final String str, final String model) {
		final int pos = str.lastIndexOf(model);
		if (pos < 0) {
			return null;
		}
		String res = str.substring(0, pos);
		if (!res.endsWith(File.separator)) {
			res += File.separator;
		}
		return res;
	}

	/**
	 * Remove first component of a dotted string and return.
	 * 
	 * @param name
	 *            a <code>String</code> value : a dotted String i.e. a.b
	 * @return a <code>String</code> value : String without the first component
	 *         of the dotted String, or the String it-self
	 */
	public static String removefirstComponent(final String name) {
		return StringUtil.removefirstComponent(name, ".");
	}

	/**
	 * Remove first component of a string composed by element separated by <sep>
	 * and return.
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @param sep
	 *            a <code>String</code> value : separator value
	 * @return a <code>String</code> value : String without the first component
	 *         of the String, or String it-self
	 */
	public static String removefirstComponent(final String name,
			final String sep) {
		final int indice = name.indexOf(sep);
		if (indice >= 0) {
			final String withoutSep = name.substring(indice + 1);
			return withoutSep;
		}
		return name;
	}

	/**
	 * Remove last component of a dotted string and return.
	 * 
	 * @param name
	 *            a <code>String</code> value : a dotted String i.e. a.b
	 * @return a <code>String</code> value : String without the last component
	 *         of the dotted String, or the String it-self
	 */
	public static String removelastComponent(final String name) {
		return StringUtil.removelastComponent(name, ".");
	}

	/**
	 * Remove last component of a string composed by element separated by <sep>
	 * and return.
	 * 
	 * @param name
	 *            a <code>String</code> value : a String
	 * @param sep
	 *            a <code>String</code> value : separator value
	 * @return a <code>String</code> value : String without the last component
	 *         of the String, or the String it-self
	 */
	public static String removelastComponent(final String name, final String sep) {
		final int indice = name.lastIndexOf(sep);
		if (indice >= 0) {
			final String withoutSep = name.substring(0, indice);
			return withoutSep;
		}
		return name;
	}

	/**
	 * Remove quote (' and ") on a string
	 * 
	 * @param src
	 *            a <code>String</code> value : a string
	 * @return a <code>String</code> value : a new string where ' and " were
	 *         removed
	 */
	public static String removeQuote(final String src) {
		String cpy = src;
		if (src != null) {
			if (((src.charAt(0) == '\'') && (src.charAt(src.length() - 1) == '\''))
					|| ((src.charAt(0) == '\"') && (src
							.charAt(src.length() - 1) == '\"'))) {
				cpy = src.substring(1, src.length() - 1);
			}
		}
		return cpy;
	}

	/**
	 * Replace <var> by <value> in a string.
	 * 
	 * @param str
	 *            a <code>String</code> value
	 * @param var
	 *            a <code>String</code> value
	 * @param value
	 *            a <code>String</code> value
	 * @return a <code>String</code> value
	 */
	static public String replaceVar(final String str, final String var,
			final String value) {
		final int pos = str.indexOf("%" + var + "%");
		if (pos >= 0) {
			final int len = var.length() + 2;
			return str.substring(0, pos) + value + str.substring(pos + len);
		} else {
			return str;
		}
	}

	/**
	 * Return the substring located between two sunstring.
	 * 
	 * @param str
	 *            a value of type 'String'
	 * @param begin
	 *            a value of type 'String'
	 * @param end
	 *            a value of type 'String'
	 * @return a value of type 'String'
	 */
	static public String stringBetween(final String str, final String begin,
			final String end) {
		final int indStart = str.indexOf(begin);
		if (indStart > 0) {
			final String res = str.substring(indStart);
			final int indEnd = str.indexOf(end);
			if (indEnd > 0) {
				final String res2 = res.substring(0, indEnd);
				return res2;
			} else {
				return res;
			}
		} else {
			return null;
		}
	}

	/**
	 * Converts the first character of the given string to upper case.
	 * 
	 * @param s
	 *            a <code>String</code> value
	 * @return a <code>String</code> value
	 */
	public static String toInitialUpperCase(final String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * Returns an XML version of the string (with entities to quote special
	 * characters like &lt;&gt;&amp;&quot;').
	 * 
	 * @param s
	 *            the string to convert
	 */
	public static String toXML(final String s) {
		if (s == null) {
			return "";
		}
		final int slength = s.length();
		final StringBuilder result = new StringBuilder(slength);
		for (int i = 0; i < slength; i++) {
			final char c = s.charAt(i);
			switch (c) {
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '&':
					result.append("&amp;");
					break;
				case '"':
					result.append("&quot;");
					break;
				case '\'':
					result.append("&apos;");
					break;
				case '%':
					result.append("&#37;");
					break;
				default:
					if ((c == '\n') || (c == '\t') || (c == '\r')) {
						result.append(c);
					} else if ((c < ' ') || (c > 127)) {
						result.append("&#");
						result.append(String.valueOf((int) c));
						result.append(";");
					} else {
						result.append(c);
					}
			}
		}
		return result.toString();
	}
}
