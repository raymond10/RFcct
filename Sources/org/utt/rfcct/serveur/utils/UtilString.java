/**
 * 
 */
package org.utt.rfcct.serveur.utils;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 22 nov. 2013
 *
 */
public class UtilString {

	/**
	 * Remplace une sous-chaine par une autre. C'est un proxy pour la methode replace de r.univlr.cri.util.StringCtrl.
	 * 
	 * @param string Chaine entiere.
	 * @param string2 Sous-chaine e remplacer.
	 * @param string3 Sous-chaine de remplacement.
	 * @return La chaine modifiee.
	 */
	public static String replaceStringByAnother(String s, String what, String byWhat) {
		StringBuffer sb;
		int i;

		//		  if ((s == null) || (what == null)) return s;
		sb = new StringBuffer();
		if (byWhat == null)
			byWhat = "";
		do {
			i = s.indexOf(what);
			if (i >= 0) {
				sb.append(s.substring(0, i));
				sb.append(byWhat);
				s = s.substring(i + what.length());
			}
		} while (i != -1);
		sb.append(s);
		return sb.toString();
		//return StringCtrl.replace(str, replaceWhat, byWhat);
	}

	/**
	 * Parcourt une chaine et remplace les identifiants (encadres par des caracteres %) contenus dans la chaine par les valeurs correspondantes de la
	 * map.<br>
	 * Exemple:<br>
	 * <code>
	 * Hashtable table = new Hashtable();<br>
	 * table.put("nom", "XIV");<br>
	 * table.put("prenom", "Louis");<br>
	 * String res = replaceWithValuesFromMap("Bonjour %nom% %prenom%!", table);<br>
	 * </code>
	 */
	public static String replaceWithValuesFromMap(String str, Map<String, Object> map) {
		String res = str;
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			res = UtilString.replaceStringByAnother(res, "%" + key + "%", map.get(key).toString());
		}
		return res;
	}

}