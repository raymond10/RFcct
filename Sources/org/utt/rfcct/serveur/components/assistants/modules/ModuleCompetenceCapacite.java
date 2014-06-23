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

import java.util.Enumeration;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.ajax.AjaxUpdateContainer;
import er.ajax.AjaxUtils;
import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXArrayUtilities;

import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktldroitsutils.common.util.MyDateCtrl;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.rfcct.serveur.components.Accueil;
import org.utt.rfcct.serveur.components.BaseComponent;
import org.utt.rfcct.serveur.components.assistants.Assistant;
import org.utt.rfcct.serveur.components.controlers.ModuleCompetenceCapaciteCtrl;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 22 mars 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceCapacite extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9146190847199411836L;
	public static final String BINDING_TBV_HEIGHT = "tbvHeight";
	public static final String BINDING_TBV_WIDTH = "tbvWidth";
	public static final String BINDING_TBV_DISPLAY_FILTER = "displayFilter";
	public static final String BDG_UPDATE_CONTAINER_ID = "updateContainerID";

	private ModuleCompetenceCapaciteCtrl ctrl = null;
	private ERXDisplayGroup capDisplayGroup = null;
	private EOCapacite currentCap;
	private NSMutableDictionary<String, CktlAjaxTableViewColumn> _colonnesMap = new NSMutableDictionary<String, CktlAjaxTableViewColumn>();
	private NSArray<CktlAjaxTableViewColumn> colonnes;
	public static final String COL_CAP_NUM_KEY = EOCapacite.CAP_NUMERO_KEY;
	public static final String COL_CAP_LBL_LONG_KEY = EOCapacite.LBL_LONG_KEY;
	public static final String COL_CAP_LBL_COURT_KEY = EOCapacite.LBL_COURT_KEY;
	public static final String LE_CAP = "currentCap.";
	private Integer persId = null;
	private Boolean droitTout = null;
	private Boolean modifier = null;
	/**
	 * Tableau contenant les clés identifiant les colonnes à afficher par
	 * défaut.
	 */
	public static NSArray<String> DEFAULT_COLONNES_KEYS = new NSArray<String>(
			new String[] { COL_CAP_NUM_KEY, COL_CAP_LBL_LONG_KEY,
					COL_CAP_LBL_COURT_KEY });

	public ModuleCompetenceCapacite(WOContext context) {
		super(context);
		NSLog.out.appendln("ModuleCompetenceCapacite");
	}

	/**
	 * @return the ctrl
	 */
	public ModuleCompetenceCapaciteCtrl ctrl() {
		if (ctrl == null)
			ctrl = new ModuleCompetenceCapaciteCtrl(this);
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(ModuleCompetenceCapaciteCtrl ctrl) {
		this.ctrl = ctrl;
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

	public String capTbvId() {
		return "capTbv_" + uniqueDomId();
	}

	public String TRCapaciteSelectionId() {
		return "TRCapaciteSelection";
	}

	public String TRTerrainId() {
		return "TRTerrain";
	}

	public String TRSTerrainId() {
		return "TRSTerrain";
	}

	public String TRCapaciteId() {
		return "TRCapacite";
	}

	public String ContainerCapaciteCommentaireId() {
		return "ContainerCapaciteCommentaire";
	}

	public WOActionResults refreshToolbar() {
		AjaxUpdateContainer.updateContainerWithID(TRCapaciteSelectionId(),
				context());
		return null;
	}

	public WOActionResults refreshContainerCapaciteCommentaire() {
		AjaxUpdateContainer.updateContainerWithID(
				ContainerCapaciteCommentaireId(), context());
		return null;
	}

	public WOActionResults refreshTRTerrain() {
		AjaxUpdateContainer.updateContainerWithID(TRTerrainId(), context());
		return null;
	}

	public WOActionResults refreshTRSTerrain() {
		AjaxUpdateContainer.updateContainerWithID(TRSTerrainId(), context());
		return null;
	}

	public WOActionResults refreshTRCapacite() {
		AjaxUpdateContainer.updateContainerWithID(TRCapaciteId(), context());
		return null;
	}

	public String globalRefresh() {
		String func = "function() {" + ContainerCapaciteCommentaireId()
				+ "Update(); " + TRTerrainId() + "Update(); " + TRSTerrainId()
				+ "Update(); " + TRCapaciteId() + "Update(); }";
		return func;
	}

	/**
	 * @return the capDisplayGroup
	 */
	/*
	public ERXDisplayGroup capDisplayGroup() {
		if (capDisplayGroup == null) {
			capDisplayGroup = new ERXDisplayGroup();
			capDisplayGroup.setDelegate(this);
			EOCompetence competence = competence();
			EOEditingContext ec = competence.editingContext();
			if (competence != null
					&& EOUtilities.primaryKeyForObject(ec, competence) != null) {
				NSArray<EORepartCompCapacite> rccs = null;
				//EORepartCompCapacite
				//		.fetchCurrentCapsFromCompetence(ec, competence);
				mySession().setListCaps(new NSMutableArray<EOCapacite>());
				rccs = ERXArrayUtilities.arrayWithoutDuplicateKeyValue(rccs,
						EORepartCompCapacite.CAPACITE_KEY);
				NSArray<EOCapacite> capacites = (NSArray<EOCapacite>) rccs
						.valueForKey(EORepartCompCapacite.CAPACITE_KEY);
				for (EOCapacite capacite : capacites) {
					if (mySession().listCaps() == null)
						mySession().setListCaps(
								new NSMutableArray<EOCapacite>());
					if (!mySession().listCaps().contains(capacite))
						mySession().listCaps().addObject(capacite);
					capDisplayGroup.setObjectArray(mySession().listCaps()
							.immutableClone());
				}
			}
		}
		return capDisplayGroup;
	}
	
	*/

	/**
	 * @param capDisplayGroup
	 *            the capDisplayGroup to set
	 */
	public void setCapDisplayGroup(ERXDisplayGroup capDisplayGroup) {
		this.capDisplayGroup = capDisplayGroup;
	}

	/**
	 * @return the currentCap
	 */
	public EOCapacite currentCap() {
		return currentCap;
	}

	/**
	 * @param currentCap
	 *            the currentCap to set
	 */
	public void setCurrentCap(EOCapacite currentCap) {
		this.currentCap = currentCap;
	}

	public void onPrecedent() {
	}

	public void onSuivant() {
	}

	/*
	 * public boolean isSuivantDisabled() { if (competence() == null ) return
	 * false; return true; }
	 */

	@Override
	public boolean valider() {
		boolean validate = false;
		EOCompetence competence = competence();
		NSArray<String> failureMessages = new NSArray<String>();
		Assistant assistant = (Assistant) parent();
		if (competence != null && assistant != null) {
			if (competence.domaine() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le domaine de compétence de la 1ère étape");
			}
			/*
			if (capDisplayGroup().allObjects().isEmpty()) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le(s) capacité(s) pour la compétence");
			} else {
				if (mySession().capDg() == null)
					mySession().setCapDg(new ERXDisplayGroup());
				mySession().capDg().setObjectArray(
						capDisplayGroup().allObjects());
			}
*/
			if (failureMessages.count() == 0) {
				validate = true;
				assistant.setFailureMessage(null);
			} else {
				assistant.setFailureMessage("Veuillez renseigner "
						+ failureMessages.componentsJoinedByString(", ") + ".");
			}

		} else {
			assistant
					.setFailureMessage("Veuillez renseigner les champs obligatoires (en rouge).");
		}
		if (validate)
			mySession().getUiMessages().removeAllObjects();
		return validate;
	}

	public boolean validerModifier() {
		boolean validate = false;
		EOCompetence competence = competence();
		EOEditingContext ec = competence.editingContext();
		NSArray<String> failureMessages = new NSArray<String>();
		if (competence != null
				&& EOUtilities.primaryKeyForObject(ec, competence) != null) {
			if (competence.domaine() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le domaine de compétence de la 1er onglet");
			}
/*
			if (capDisplayGroup().allObjects().isEmpty()) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le(s) capacité(s) pour la compétence");
			} else {
				if (mySession().capDg() == null)
					mySession().setCapDg(new ERXDisplayGroup());
				mySession().capDg().setObjectArray(
						capDisplayGroup().allObjects());
			}
*/
			if (failureMessages.count() == 0) {
				validate = true;
				setFailureMessage(null);
			} else {
				setFailureMessage("Veuillez renseigner "
						+ failureMessages.componentsJoinedByString(", ") + ".");
			}

		} else {
			setFailureMessage("Veuillez renseigner les champs obligatoires (en rouge).");
		}
		if (validate)
			mySession().getUiMessages().removeAllObjects();
		return validate;
	}

	// Definiton des colones du tableau(libelle==>valeur)
	public NSArray<CktlAjaxTableViewColumn> colonnes() {
		if (colonnes == null) {
			CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
			// Colonne 1
			col0.setLibelle("Numéro");
			col0.setOrderKeyPath(COL_CAP_NUM_KEY);
			CktlAjaxTableViewColumnAssociation asso0 = new CktlAjaxTableViewColumnAssociation(
					LE_CAP + COL_CAP_NUM_KEY, "Aucun numéro");
			col0.setAssociations(asso0);
			_colonnesMap.takeValueForKey(col0, COL_CAP_NUM_KEY);
			// Colonne 2
			CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
			col1.setLibelle("Libellé long");
			col1.setOrderKeyPath(COL_CAP_LBL_LONG_KEY);
			col1.setRowCssStyle("white-space: normal;");
			CktlAjaxTableViewColumnAssociation asso1 = new CktlAjaxTableViewColumnAssociation(
					LE_CAP + COL_CAP_LBL_LONG_KEY, "Aucun libellé long");
			col1.setAssociations(asso1);
			_colonnesMap.takeValueForKey(col1, COL_CAP_LBL_LONG_KEY);
			// Colonne 3
			CktlAjaxTableViewColumn col2 = new CktlAjaxTableViewColumn();
			col2.setLibelle("Libellé court");
			col2.setOrderKeyPath(COL_CAP_LBL_COURT_KEY);
			col2.setRowCssStyle("white-space: normal;");
			CktlAjaxTableViewColumnAssociation asso2 = new CktlAjaxTableViewColumnAssociation(
					LE_CAP + COL_CAP_LBL_COURT_KEY, "Aucun libellé court");
			col2.setAssociations(asso2);
			_colonnesMap.takeValueForKey(col2, COL_CAP_LBL_COURT_KEY);
			// Generation des colones du tableau
			NSMutableArray<CktlAjaxTableViewColumn> res = new NSMutableArray<CktlAjaxTableViewColumn>();
			NSArray<String> keys = DEFAULT_COLONNES_KEYS;
			for (int i = 0; i < keys.count(); i++) {
				res.addObject(_colonnesMap.objectForKey((String) keys
						.objectAtIndex(i)));
			}
			colonnes = res.immutableClone();
		}
		return colonnes;
	}

	// Rafraichissement des s/Terrain
	public void refreshSousTerr() {
		if (ctrl.leCentreTerrain() != null)
			ctrl.setLesSousTerrains(EOTerrain.fetchLesFilsDuPere(competence()
					.editingContext(), Integer.valueOf(ctrl.leCentreTerrain()
					.primaryKey()), mySession().TODAY));
	}

	public NSTimestamp competenceDateFin() {
		return competence().dateFin();
	}

	public void setCompetenceDateFin(NSTimestamp date) {
		try {
			competence().setDateFin(date);
		} catch (ValidationException e) {
			mySession().addSimpleErrorMessage("RFCCT", e.getMessage());
		}
	}

	// Bouton supprimer capacite dans le tableau dynamique (mode standAlone ou
	// assistant)
	public WOActionResults supprimerUneCap() {

		/*
		EOCapacite uneCapaciteAsupprimer = (EOCapacite) capDisplayGroup()
				.selectedObject();
		if (uneCapaciteAsupprimer != null) {
			EOCompetence competence = competence();
			EOEditingContext ec = competence.editingContext();
			if (competence != null
					&& EOUtilities.primaryKeyForObject(ec, competence) != null) {
				EORepartCompCapacite repartCompCapAsupprimer = EORepartCompCapacite
						.fetchLaRepartitionCap(
								ERXEC.newEditingContext(),
								Integer.valueOf(uneCapaciteAsupprimer
										.primaryKey()),
								Integer.valueOf(competence.primaryKey()))
						.lastObject();
				FactoryCompetenceCapacite fcc = new FactoryCompetenceCapacite(
						repartCompCapAsupprimer.editingContext(), true);
				try {
					fcc.suppressionCompetenceCapacite(repartCompCapAsupprimer);
					mySession().addSimpleInfoMessage("RFCCT",
							"capacité supprimée");
					context().response().setStatus(500);
					AjaxUpdateContainer.updateContainerWithID(myApp()
							.messageContainerID(), context());
				} catch (Exception e) {
					mySession().addSimpleErrorMessage("Erreur",
							e.getLocalizedMessage());
					context().response().setStatus(500);
					AjaxUpdateContainer.updateContainerWithID(myApp()
							.messageContainerID(), context());
					e.printStackTrace();
				}
				setCapDisplayGroup(null);

			} else {
				mySession().listCaps().removeObject(uneCapaciteAsupprimer);
				capDisplayGroup().setObjectArray(mySession().listCaps());
				mySession().addSimpleInfoMessage("RFCCT", "capacité supprimée");
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
			}
		}
		*/
		return null;
	}

	// Bouton annuler en mode sandAlone
	public WOActionResults annuler() {
		capDisplayGroup = null;
		mySession().setListCaps(null);
		editingContext().revert();
		mySession().addSimpleInfoMessage("RFCCT",
				"Les changements ont bien été annulés");
		return null;
	}

	// Bouton enregistrer onglet terrain, cap, comment date en mode stanAlone
	public WOActionResults enregistrer() {
		EOCompetence competence = competence();
		EOEditingContext ec = competence.editingContext();
		competence.setPersIdModif(persId());
		competence.setDateModif(MyDateCtrl.now());
		if (competence != null) {
			try {
				if (ec.hasChanges() && validerModifier()) {
					ec.updatedObjects();
					ec.saveChanges();
					// Enregistrement des capacites
					/*
					FactoryCompetenceCapacite fcc = new FactoryCompetenceCapacite(
							ec, true);
					fcc.enregistrementCompetenceCapacite(competence,
							mySession().capDg().allObjects());
							*/
					mySession().addSimpleInfoMessage(
							"RFCCT",
							"Competence ' " + competence().numeroCompetence()
									+ " ' modifiée avec succès");
				}
			} catch (ValidationException e2) {
				mySession().addSimpleErrorMessage(e2.getLocalizedMessage(), e2);
				context().response().setStatus(500);
				myLogger().info(e2.getMessage(), e2);
			} catch (Exception e) {
				mySession()
						.addSimpleErrorMessage(
								"Une erreur est survenue lors de l'enregistrement en base",
								e);
				context().response().setStatus(500);
				ec.revert();
				myLogger().error(e.getMessage(), e);
				throw new NSForwardException(e,
						"Une erreur est survenue lors de l'enregistrement en base");
			}

		}
		return null;
	}

	public boolean enabled() {
		if (isInAssistant())
			return isStandAlone();
		if (isStandAlone()) {
			boolean assist = isInAssistant();
			boolean update = (modifier()
					|| competence().persIdCrea() == persId() || droitTout());
			boolean result = assist && update;
			return result;
		}
		return false;
	}

	public boolean disabled() {
		return !enabled();
	}

	/**
	 * @return the persId
	 */
	public Integer persId() {
		if (persId == null) {
			if (myApp().newGestionDroitsEnabled()) {
				persId = myGdUser().getPersId();
			} else {
				persId = myJefyUser().getPersId();
			}
		}
		return persId;
	}

	/**
	 * @param persId
	 *            the persId to set
	 */
	public void setPersId(Integer persId) {
		this.persId = persId;
	}

	/**
	 * @return the droitTout
	 */
	public Boolean droitTout() {
		if (droitTout == null) {
			if (myApp().newGestionDroitsEnabled()) {
				droitTout = myGdUser().getCompetenceAutorisationCache()
						.gDRfcctSuperUser();
			} else {
				droitTout = myJefyUser().hasDroitSuperUser();
			}
		}
		return droitTout;
	}

	/**
	 * @param droitTout
	 *            the droitTout to set
	 */
	public void setDroitTout(Boolean droitTout) {
		this.droitTout = droitTout;
	}

	/**
	 * @return the modifier
	 */
	public Boolean modifier() {
		if (modifier == null) {
			if (myApp().newGestionDroitsEnabled()) {
				modifier = myGdUser().getCompetenceAutorisationCache()
						.gDRfcctModificationComptence();
			} else {
				modifier = myJefyUser().hasDroitModificationCompetence();
			}
		}
		return modifier;
	}

	/**
	 * @param modifier
	 *            the modifier to set
	 */
	public void setModifier(Boolean modifier) {
		this.modifier = modifier;
	}
}
