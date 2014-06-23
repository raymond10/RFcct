/*******************************************************************************
 * Copyright (c) Raymond NANEON  - 2013.
 * Universite de Technologie de Troyes - CEDRE (www.utt.fr), since 1993.
 * This software is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or 
 * redistribute the software under the terms of the CeCILL license as 
 * circulated by CEA, CNRS and INRIA at the following URL 
 * "http://www.cecill.info". 
 * As a counterpart to the access to the source code and rights to copy, modify 
 * and redistribute granted by the license, users are provided only with a 
 * limited warranty and the software's author, the holder of the economic 
 * rights, and the successive licensors have only limited liability. In this 
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user 
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is 
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the 
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally, 
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge 
 * of the CeCILL license and that you accept its terms.
 * 
 * Do not remove this copyright message
 * 
 * Contributors:
 *     Raymond NANEON - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.utt.rfcct.serveur.utils;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktljefyadmin.common.metier.EOFonction;
import org.cocktail.fwkcktljefyadmin.common.metier.EOTypeApplication;
import org.cocktail.fwkcktljefyadmin.common.metier.EOUtilisateur;
import org.cocktail.fwkcktljefyadmin.common.metier.EOUtilisateurFonction;
import org.utt.fwkuttcompetences.serveur.CompetenceApplicationUser;
import org.utt.fwkuttcompetences.serveur.exception.FactoryException;
import org.utt.fwkuttcompetences.serveur.utils.Finder;
import org.utt.fwkuttcompetences.serveur.utils.FinderFonction;
import org.utt.fwkuttcompetences.serveur.utils.FinderTypeApplication;
import org.utt.rfcct.serveur.Session;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *18 mars 2013
 */

@SuppressWarnings("all")
public class FinderUtilisateurFonction extends Finder {
	/**
	 * @param ec
	 * @param entityName
	 */
	protected FinderUtilisateurFonction(EOEditingContext ec, String entityName) {
		super(ec, entityName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Recherche d'un utilisateurFonction pour un utilisateur et un libelle de
	 * fonction <BR>
	 * 
	 * @param ed
	 *            editingContext dans lequel se fait le fetch
	 * @param utilisateur
	 *            utilisateur pour lequel trouver la fonction associee
	 * @param libelleFonction
	 *            String contenant le libelle de la fonction a rechercher
	 * @return un EOUtilisateurFonction
	 */
	private static final Session sess = new Session();
	private final static Logger LOG = Logger.getLogger(FinderUtilisateurFonction.class);

	public static final EOUtilisateurFonction getUtilisateurFonction(
			EOEditingContext ed, EOUtilisateur utilisateur,
			String libelleFonction) {
		NSMutableDictionary mesBindings = new NSMutableDictionary();
		if (utilisateur == null) {
			utilisateur = sess.utilisateur();
		} else {
			sess.setUtilisateur(utilisateur);
		}
		EOTypeApplication typeApplication = FinderTypeApplication
				.getTypeApplication(ed,
						CompetenceApplicationUser.TYAP_STR_ID_APP);
		if (typeApplication == null) {
			throw new FactoryException("Type application "
					+ CompetenceApplicationUser.TYAP_STR_ID_APP
					+ " non trouvé ! (utilisateur:" + utilisateur + ", ed:"
					+ ed + "fonction:" + libelleFonction + ", edc=" + ed + ")");
		}

		EOFonction fonction = FinderFonction.getFonction(ed, libelleFonction,
				typeApplication);
		if (fonction == null)
			return null;
		// Correction bug concernant les users non prÔøΩsents dans jefyAdmin (user
		// n'ayant pas de rÔøΩle dÔøΩfinit pour une application) - 21/01/2013
		if (utilisateur == null) {
			return null;
		}
		mesBindings.setObjectForKey(utilisateur, "utilisateur");
		mesBindings.setObjectForKey(fonction, "fonction");
		EOUtilisateurFonction result = FinderUtilisateurFonction
				.getUtilisateurFontion(ed, mesBindings);
		return result;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return sess;
	}

	/**
	 * Recherche d'un utilisateurFonction par la fetchSpecification Recherche.<BR>
	 * Bindings pris en compte : utilisateur, fonction <BR>
	 * 
	 * @param ed
	 *            editingContext dans lequel se fait le fetch
	 * @param bindings
	 *            dictionnaire contenant les bindings
	 * @return un EOUtilisateurFonction
	 */
	public static final EOUtilisateurFonction getUtilisateurFontion(
			EOEditingContext ed, NSDictionary<String, Object> bindings) {
		NSArray larray = FinderUtilisateurFonction.getUtilisateurFonctions(ed,
				bindings);

		if (larray != null && larray.count() == 1)
			return (EOUtilisateurFonction) larray.objectAtIndex(0);
		return null;
	}

	/**
	 * Recherche des utilisateurFonction d'un utilisateur par la
	 * fetchSpecification Recherche.<BR>
	 * Bindings pris en compte : utilisateur, fonction <BR>
	 * 
	 * @param ed
	 *            editingContext dans lequel se fait le fetch
	 * @param bindings
	 *            dictionnaire contenant les bindings
	 * @return un NSArray de EOUtilisateurFonction
	 */
	public static final NSArray getUtilisateurFonctions(EOEditingContext ed,
			NSDictionary<String, Object> bindings) {
		NSArray result = null;
		try {
			result = EOUtilities.objectsWithFetchSpecificationAndBindings(ed,
					EOUtilisateurFonction.ENTITY_NAME, "Recherche", bindings);
		} catch (Exception e) {
			LOG.warn("Impossible d'initialiser le user, la raison probable est que l'utilisateur "
							+ "n'est pas enregistré dans JEFY_ADMIN");
/*			sess.addSimpleErrorMessage("ALERTE",
			 "Attention les droits d'utilisation n'ont pu être initialisés, les fonctions des menus RFCCT seront limitées.");*/
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.utt.rfcct.serveur.utils.Finder#clearAllCriteria()
	 */
	@Override
	public void clearAllCriteria() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.utt.rfcct.serveur.utils.Finder#canFind()
	 */
	@Override
	public boolean canFind() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.utt.rfcct.serveur.utils.Finder#getCurrentWarningMessage()
	 */
	@Override
	public String getCurrentWarningMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
