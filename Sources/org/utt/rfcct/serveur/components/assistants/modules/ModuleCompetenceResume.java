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
package org.utt.rfcct.serveur.components.assistants.modules;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.ajax.AjaxUpdateContainer;

import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.utils.tiers.Tiers;
import org.utt.rfcct.serveur.components.BaseComponent;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *22 mars 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceResume extends ModuleAssistant {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4487009712523148017L;
	private Tiers currentTiers;
	private EOCapacite currentCap;
	public static final String BINDING_TBV_HEIGHT = "tbvHeight";
	public static final String BINDING_TBV_WIDTH = "tbvWidth";
	public static final String BINDING_TBV_DISPLAY_FILTER = "displayFilter";
	//Tiers
	private NSMutableDictionary<String, CktlAjaxTableViewColumn> _colonnesMap = new NSMutableDictionary<String, CktlAjaxTableViewColumn>();
	private NSArray<CktlAjaxTableViewColumn> colonnes;
	public static final String COL_TIERS_NUM_KEY = Tiers.TIERS_NUMERO_KEY;
	public static final String COL_TIERS_LIB_KEY = Tiers.TIERS_LIBELLE_KEY;
	public static final String LE_TIERS = "currentTiers.";
	/**
	 * Tableau contenant les clés identifiant les colonnes à afficher par
	 * défaut.
	 */
	public static NSArray<String> DEFAULT_COLONNES_KEYS = new NSArray<String>(
			new String[] { COL_TIERS_NUM_KEY, COL_TIERS_LIB_KEY });
	//Capacite
	private NSMutableDictionary<String, CktlAjaxTableViewColumn> _capColonnesMap = new NSMutableDictionary<String, CktlAjaxTableViewColumn>();
	private NSArray<CktlAjaxTableViewColumn> capColonnes;
	public static final String COL_CAP_NUM_KEY = EOCapacite.CAP_NUMERO_KEY;
	public static final String COL_CAP_LBL_LONG_KEY = EOCapacite.LBL_LONG_KEY;
	public static final String COL_CAP_LBL_COURT_KEY = EOCapacite.LBL_COURT_KEY;
	public static final String LE_CAP = "currentCap.";
	/**
	 * Tableau contenant les clés identifiant les colonnes à afficher par
	 * défaut.
	 */
	public static NSArray<String> DEFAULT_CAP_COLONNES_KEYS = new NSArray<String>(
			new String[] { COL_CAP_NUM_KEY, COL_CAP_LBL_LONG_KEY,
					COL_CAP_LBL_COURT_KEY });

	public ModuleCompetenceResume(WOContext context) {
		super(context, null);
		NSLog.out.appendln("ModuleCompetenceResume");
    }
	
	public void onPrecedent() {
	}
	
	public void onSuivant() {
	}
	
	/*public boolean isSuivantDisabled() {
		if (competence() == null )
			return false;
		return true;
	}*/
	
	/**
	 * @return the currentTiers
	 */
	public Tiers currentTiers() {
		return currentTiers;
	}

	/**
	 * @param currentTiers the currentTiers to set
	 */
	public void setCurrentTiers(Tiers currentTiers) {
		this.currentTiers = currentTiers;
	}

	/**
	 * @return the currentCap
	 */
	public EOCapacite currentCap() {
		return currentCap;
	}

	/**
	 * @param currentCap the currentCap to set
	 */
	public void setCurrentCap(EOCapacite currentCap) {
		this.currentCap = currentCap;
	}

	@Override
	public boolean valider() {
		boolean validate = true;
		return validate;
	}
	
	// Tableau d'affichage des tiers
	public String tbvHeight() {
		return stringValueForBinding(BINDING_TBV_HEIGHT, "200");
	}

	public String tbvWidth() {
		return stringValueForBinding(BINDING_TBV_WIDTH, "auto");
	}

	public boolean hasFiltre() {
		return booleanValueForBinding(BINDING_TBV_DISPLAY_FILTER, Boolean.TRUE);
	}

	public String tiersTbvId() {
		return "TiersTbv_" + uniqueDomId();
	}

	public String toolbarContId() {
		return "ToolbarCont_" + uniqueDomId();
	}

	public WOActionResults refreshToolbar() {
		AjaxUpdateContainer.updateContainerWithID(toolbarContId(), context());
		return null;
	}
	
	public String capTbvId() {
		return "capTbv_" + uniqueDomId();
	}

	// Definiton des colones du tableau des tiers(libelle==>valeur)
	public NSArray<CktlAjaxTableViewColumn> colonnes() {
		if (colonnes == null) {
			CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
			col0.setLibelle("Numéro");
			col0.setOrderKeyPath(COL_TIERS_NUM_KEY);
			CktlAjaxTableViewColumnAssociation asso0 = new CktlAjaxTableViewColumnAssociation(
					LE_TIERS + COL_TIERS_NUM_KEY, "Aucun numéro");
			col0.setAssociations(asso0);
			_colonnesMap.takeValueForKey(col0, COL_TIERS_NUM_KEY);
			CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
			col1.setLibelle("Libellé");
			col1.setOrderKeyPath(COL_TIERS_LIB_KEY);
			col1.setRowCssStyle("white-space: normal;");
			// col1.setRowCssClass("alignToCenter");
			CktlAjaxTableViewColumnAssociation asso1 = new CktlAjaxTableViewColumnAssociation(
					LE_TIERS + COL_TIERS_LIB_KEY, "Aucun libellé");
			col1.setAssociations(asso1);
			_colonnesMap.takeValueForKey(col1, COL_TIERS_LIB_KEY);
			NSMutableArray<CktlAjaxTableViewColumn> res = new NSMutableArray<CktlAjaxTableViewColumn>();
			NSArray<String> keys = DEFAULT_COLONNES_KEYS;
			// Generation des colones du tableau
			for (int i = 0; i < keys.count(); i++) {
				res.addObject(_colonnesMap.objectForKey((String) keys
						.objectAtIndex(i)));
			}
			colonnes = res.immutableClone();
		}
		return colonnes;
	}
	
	// Definiton des colones du tableau des capacites(libelle==>valeur)
		public NSArray<CktlAjaxTableViewColumn> capColonnes() {
			if (capColonnes == null) {
				CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
				// Colonne 1
				col0.setLibelle("Numéro");
				col0.setOrderKeyPath(COL_CAP_NUM_KEY);
				CktlAjaxTableViewColumnAssociation asso0 = new CktlAjaxTableViewColumnAssociation(
						LE_CAP + COL_CAP_NUM_KEY, "Aucun numéro");
				col0.setAssociations(asso0);
				_capColonnesMap.takeValueForKey(col0, COL_CAP_NUM_KEY);
				// Colonne 2
				CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
				col1.setLibelle("Libellé long");
				col1.setOrderKeyPath(COL_CAP_LBL_LONG_KEY);
				col1.setRowCssStyle("white-space: normal;");
				CktlAjaxTableViewColumnAssociation asso1 = new CktlAjaxTableViewColumnAssociation(
						LE_CAP + COL_CAP_LBL_LONG_KEY, "Aucun libellé long");
				col1.setAssociations(asso1);
				_capColonnesMap.takeValueForKey(col1, COL_CAP_LBL_LONG_KEY);
				// Colonne 3
				CktlAjaxTableViewColumn col2 = new CktlAjaxTableViewColumn();
				col2.setLibelle("Libellé court");
				col2.setOrderKeyPath(COL_CAP_LBL_COURT_KEY);
				col2.setRowCssStyle("white-space: normal;");
				CktlAjaxTableViewColumnAssociation asso2 = new CktlAjaxTableViewColumnAssociation(
						LE_CAP + COL_CAP_LBL_COURT_KEY, "Aucun libellé court");
				col2.setAssociations(asso2);
				_capColonnesMap.takeValueForKey(col2, COL_CAP_LBL_COURT_KEY);
				// Generation des colones du tableau
				NSMutableArray<CktlAjaxTableViewColumn> res = new NSMutableArray<CktlAjaxTableViewColumn>();
				NSArray<String> keys = DEFAULT_CAP_COLONNES_KEYS;
				for (int i = 0; i < keys.count(); i++) {
					res.addObject(_capColonnesMap.objectForKey((String) keys
							.objectAtIndex(i)));
				}
				capColonnes = res.immutableClone();
			}
			return capColonnes;
		}
}
