/**
 * 
 */
package org.utt.rfcct.serveur.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSNumberFormatter;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 30 sept. 2013
 */
@SuppressWarnings("all")
public class Editions {
	// constantes natures de convention
	final static public int ORDRE_RESS_AFFECTEE = 1;
	final static public int ORDRE_CONV_SIMPLE = 2;
	final static public String DTD_SAX = "<!DOCTYPE encode ["
			+ "<!ENTITY nbsp \"&#160;\">" + "<!ENTITY iexcl \"&#161;\">"
			+ "<!ENTITY rsquo   \"&#8217;\">" + "<!ENTITY cent \"&#162;\">"
			+ "<!ENTITY pound \"&#163;\">" + "<!ENTITY curren \"&#164;\">"
			+ "<!ENTITY yen \"&#165;\">" + "<!ENTITY brvbar \"&#166;\">"
			+ "<!ENTITY sect \"&#167;\">" + "<!ENTITY uml \"&#168;\">"
			+ "<!ENTITY copy \"&#169;\">" + "<!ENTITY ordf \"&#170;\">"
			+ "<!ENTITY laquo \"&#171;\">" + "<!ENTITY not \"&#172;\">"
			+ "<!ENTITY shy \"&#173;\">" + "<!ENTITY reg \"&#174;\">"
			+ "<!ENTITY macr \"&#175;\">" + "<!ENTITY deg \"&#176;\">"
			+ "<!ENTITY plusmn \"&#177;\">" + "<!ENTITY sup2 \"&#178;\">"
			+ "<!ENTITY sup3 \"&#179;\">" + "<!ENTITY acute \"&#180;\">"
			+ "<!ENTITY micro \"&#181;\">" + "<!ENTITY para \"&#182;\">"
			+ "<!ENTITY middot \"&#183;\">" + "<!ENTITY cedil \"&#184;\">"
			+ "<!ENTITY sup1 \"&#185;\">" + "<!ENTITY ordm \"&#186;\">"
			+ "<!ENTITY raquo \"&#187;\">" + "<!ENTITY frac14 \"&#188;\">"
			+ "<!ENTITY frac12 \"&#189;\">" + "<!ENTITY frac34 \"&#190;\">"
			+ "<!ENTITY iquest \"&#191;\">" + "<!ENTITY Agrave \"&#192;\">"
			+ "<!ENTITY Aacute \"&#193;\">" + "<!ENTITY Acirc \"&#194;\">"
			+ "<!ENTITY Atilde \"&#195;\">" + "<!ENTITY Auml \"&#196;\">"
			+ "<!ENTITY Aring \"&#197;\">" + "<!ENTITY AElig \"&#198;\">"
			+ "<!ENTITY Ccedil \"&#199;\">" + "<!ENTITY Egrave \"&#200;\">"
			+ "<!ENTITY Eacute \"&#201;\">" + "<!ENTITY Ecirc \"&#202;\">"
			+ "<!ENTITY Euml \"&#203;\">" + "<!ENTITY Igrave \"&#204;\">"
			+ "<!ENTITY Iacute \"&#205;\">" + "<!ENTITY Icirc \"&#206;\">"
			+ "<!ENTITY Iuml \"&#207;\">" + "<!ENTITY ETH \"&#208;\">"
			+ "<!ENTITY Ntilde \"&#209;\">" + "<!ENTITY Ograve \"&#210;\">"
			+ "<!ENTITY Oacute \"&#211;\">" + "<!ENTITY Ocirc \"&#212;\">"
			+ "<!ENTITY Otilde \"&#213;\">" + "<!ENTITY Ouml \"&#214;\">"
			+ "<!ENTITY times \"&#215;\">" + "<!ENTITY Oslash \"&#216;\">"
			+ "<!ENTITY Ugrave \"&#217;\">" + "<!ENTITY Uacute \"&#218;\">"
			+ "<!ENTITY Ucirc \"&#219;\">" + "<!ENTITY Uuml \"&#220;\">"
			+ "<!ENTITY Yacute \"&#221;\">" + "<!ENTITY THORN \"&#222;\">"
			+ "<!ENTITY szlig \"&#223;\">" + "<!ENTITY agrave \"&#224;\">"
			+ "<!ENTITY aacute \"&#225;\">" + "<!ENTITY acirc \"&#226;\">"
			+ "<!ENTITY atilde \"&#227;\">" + "<!ENTITY auml \"&#228;\">"
			+ "<!ENTITY aring \"&#229;\">" + "<!ENTITY aelig \"&#230;\">"
			+ "<!ENTITY ccedil \"&#231;\">" + "<!ENTITY egrave \"&#232;\">"
			+ "<!ENTITY eacute \"&#233;\">" + "<!ENTITY ecirc \"&#234;\">"
			+ "<!ENTITY euml \"&#235;\">" + "<!ENTITY igrave \"&#236;\">"
			+ "<!ENTITY iacute \"&#237;\">" + "<!ENTITY icirc \"&#238;\">"
			+ "<!ENTITY iuml \"&#239;\">" + "<!ENTITY eth \"&#240;\">"
			+ "<!ENTITY ntilde \"&#241;\">" + "<!ENTITY ograve \"&#242;\">"
			+ "<!ENTITY oacute \"&#243;\">" + "<!ENTITY ocirc \"&#244;\">"
			+ "<!ENTITY otilde \"&#245;\">" + "<!ENTITY ouml \"&#246;\">"
			+ "<!ENTITY divide \"&#247;\">" + "<!ENTITY oslash \"&#248;\">"
			+ "<!ENTITY ugrave \"&#249;\">" + "<!ENTITY uacute \"&#250;\">"
			+ "<!ENTITY ucirc \"&#251;\">" + "<!ENTITY uuml \"&#252;\">"
			+ "<!ENTITY yacute \"&#253;\">" + "<!ENTITY thorn \"&#254;\">"
			+ "<!ENTITY yuml \"&#255;\">" + "]>";
	// symbole euro en unicode
	final static public String MONNAIE_EURO = "\u20AC";
	// symbole pourcentage en unicode
	final static public String PERCENT = "\u0025";
	// formatteurs
	final static public NSNumberFormatter indexFormatter = new NSNumberFormatter(
			"0000;-0000");
	final static public NSNumberFormatter moneyFormatter = new NSNumberFormatter(
			"#,##0.00;-#,##0.00");
	// pointeur vers l'application serveur :
	static final public Logger LOG = Logger.getLogger(Editions.class);

	/***************************************** < Utiles > **************************************************/

	/**
	 * Retourne le chemin absolu du repertoire contenant les ressources de
	 * l'appli.
	 */
	@SuppressWarnings("unused")
	static private String getAppResourcesDir() {
		String resourcesDir = WOApplication.application().path()
				+ "/Contents/Resources/";
		LOG.debug("Repertoire des ressources de l'application : "
				+ resourcesDir);
		return resourcesDir;
	}

	/**
	 * Sauvegarde d'un fichier dans le repertoire temporaire du systeme (il est
	 * remplace s'il existe deja)
	 */
	static public String saveInTempFile(final StringWriter sw,
			final String fileName) throws IOException {
		String temporaryDir = getLocalTempDirectoryPath();

		// si le fichier existe deja, il est remplace sauvagement :
		File f = new File(temporaryDir + fileName);
		if (f.exists()) {
			f.delete();
		}
		if (f.createNewFile()) {
			new FileWriter(f);
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(sw.toString());
			out.close();
		}
		return f.getAbsolutePath();
	}

	/**
	 * Recuperation du chemin complet du repertoire temporaire du systeme
	 * courant.
	 */
	static private String getLocalTempDirectoryPath() {
		String temporaryDir = System.getProperty("java.io.tmpdir");
		if (!temporaryDir.endsWith(File.separator)) {
			temporaryDir = temporaryDir + File.separator;
		}
		LOG.info("Repertoire temporaire de ce systeme : " + temporaryDir);
		return temporaryDir;
	}

	/**
	 * Retourne une string ou divers caractères qui posent problème sont
	 * remplaces par leur code
	 * 
	 * @param str
	 *            ou obj chaine ou objet a traiter
	 * @return chaine "netoyee"
	 */
	/*
	 * static public String cleanedString(String str) { return
	 * str.replaceAll("&"
	 * ,"&#38;").replaceAll("<","&#60;").replaceAll(">","&#62;"
	 * ).replaceAll("\r\n","\n");//.replaceAll("\n"," - "); }
	 */

	static public String cleanedString(Object obj) {
		if (obj == null)
			return "";
		String tmp = obj.toString().replace("&eacute;", "\u00E9");
		tmp.replaceAll("&", "&#38;").replaceAll("<", "&#60;")
				.replaceAll(">", "&#62;").replaceAll("\r\n", "\n")
				.replaceAll("¿", "&#39;");
		return tmp;
	}

	static public String cleanKoma(Object obj) {
		if (obj == null)
			return null;
		String tmp = obj.toString().replaceAll("¿", "'");
		return tmp;
	}

	static public String escapeSpecialChars(String val) {
		StringBuffer response = null;
		if ((val == null) || (val.equals("")))
			return "";
		if ((val != null) && (!(val.equals("")))) {
			response = new StringBuffer();
			int l = val.length();
			char c = ' ';
			for (int i = 0; i < l; ++i) {
				c = val.charAt(i);
				if (c == '<')
					response = response.append("&#x3C;");
				else if (c == '>')
					response = response.append("&#x3E;");
				else if (c == '&')
					response = response.append("&#x26;");
				else if (c == '"')
					response = response.append("&#x22;");
				else if (c == '\'')
					response = response.append("&#x0027;");
				else if (c == '\t')
					response = response.append("&#x9;");
				else if (c == '\r') {
					response = response.append("&#x2028;");
				} else if (c > 255) {
					response = response.append("&#" + c + ";");
				} else if (c == '\n')
					response = response.append("&#x2028;");
				else if (c < ' ') {
					response = response.append("&#xA0;");
				} else if ((c >= '') && (c < 160))
					response = response.append("&#xA0;");
				else {
					response = response.append(c);
				}
			}
		}
		if (response != null) {
			return response.toString();
		}
		return "";
	}

}
