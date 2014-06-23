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

import java.text.Format;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.rfcct.serveur.components.Accueil;
import org.utt.rfcct.serveur.components.assistants.Assistant;
import org.utt.rfcct.serveur.components.controlers.ModuleAdminDomaineCtrl;
import org.utt.rfcct.serveur.components.windows.CreerDomaine;
import org.utt.rfcct.serveur.utils.NettoieEOEditingContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXRedirect;
import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 28 mars 2013
 */
@SuppressWarnings("all")
public class ModuleAdminDomaine extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5947911373912696658L;
	private ModuleAdminDomaineCtrl ctrl = null;
	private String containerId;
	private boolean isResetDialogcreateDomaine;
	private boolean isResetDialogcreateMatiere;
	private Boolean creationDomaine = null;
	private Boolean droitTout = null;

	public ModuleAdminDomaine(WOContext context) {
		super(context);
		NSLog.out.appendln("ModuleAdminDomaine");
		mySession().setDomaineSelectionnee(null);
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
	 * @return the ctrl
	 */
	public ModuleAdminDomaineCtrl ctrl() {
		if (ctrl == null)
			ctrl = new ModuleAdminDomaineCtrl(this);
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(ModuleAdminDomaineCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public void onPrecedent() {
	}

	public void onSuivant() {
	}

	// Vérification des different champs avant Enregistrement
	public boolean validerDomaine() {
		boolean validate = false;
		NSArray<String> failureMessages = new NSArray<String>();
		Assistant assistant = (Assistant) parent();
		EODomaine domaine = domaine();
		if (domaine != null && assistant != null) {
			if (domaine.eoTypeTiers() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Type de tiers");
			}

			if (domaine.lblLong() == null || domaine.lblLong().equals("")) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Libellé long");
			}

			if (domaine.lblCourt() == null || domaine.lblCourt().equals("")) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Libellé court");
			}

			if (domaine.lblMatiere() == null || domaine.lblMatiere().equals("")) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Libellé de la matière");
			}

			if (domaine.lblTerrain() == null || domaine.lblTerrain().equals("")) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Libellé du terrain");
			}

			if (failureMessages.count() == 0) {
				validate = true;
				assistant.setFailureMessage(null);
			} else {
				assistant.setFailureMessage("Veuillez renseigner "
						+ failureMessages.componentsJoinedByString(", ") + ".");
			}

		} else {
			assistant
					.setFailureMessage("Veuillez renseigner les champs obligatoires du domaine (en rouge).");
		}
		if (validate) {
			mySession().getUiMessages().removeAllObjects();
			setDomaine(domaine);
		}
		return validate;
	}

	// Id de la fenêtre creer dommaine
	public String dialogcreateDomaineId() {
		return "DialogcreateDomaine_" + uniqueDomId();
	}

	// Id de la fenêtre creer matiere
	public String dialogcreateMatiereId() {
		return "DialogcreateMatiere_" + uniqueDomId();
	}

	/**
	 * @return the isResetDialogcreateDomaine
	 */
	public boolean isResetDialogcreateDomaine() {
		return isResetDialogcreateDomaine;
	}

	/**
	 * @param isResetDialogcreateDomaine
	 *            the isResetDialogcreateDomaine to set
	 */
	public void setResetDialogcreateDomaine(boolean isResetDialogcreateDomaine) {
		this.isResetDialogcreateDomaine = isResetDialogcreateDomaine;
	}

	/**
	 * @return the isResetDialogcreateMatiere
	 */
	public boolean isResetDialogcreateMatiere() {
		return isResetDialogcreateMatiere;
	}

	/**
	 * @param isResetDialogcreateMatiere
	 *            the isResetDialogcreateMatiere to set
	 */
	public void setResetDialogcreateMatiere(boolean isResetDialogcreateMatiere) {
		this.isResetDialogcreateMatiere = isResetDialogcreateMatiere;
	}

	// Affichage de la fenêtre creation domaine
	public WOActionResults openCreateDomaineWdows() {
		setResetDialogcreateDomaine(true);
		CreerDomaine openDomaine = (CreerDomaine) pageWithName(CreerDomaine.class
				.getName());
		openDomaine.setDomaine(domaine());
		return openDomaine;
	}

	public WOActionResults refreshDomaines() {
		return null;
	}

	// Affichage de la fenêtre creation matiere
	public WOActionResults openCreateMatiereWdows() {
		setResetDialogcreateMatiere(true);
		return null;
	}

	public WOActionResults refreshMatieres() {
		return null;
	}

	// Boutons enregistrer
	public WOActionResults unDomaineEnregistrer() {
		EODomaine domaine = domaine();
		EOEditingContext ec = domaine.editingContext();
		try {
			if (validerDomaine()) {
				// En modification?
				if (EOUtilities.primaryKeyForObject(ec, domaine()) != null) {
					domaine().setDateModif(new NSTimestamp());
					domaine().setPersIdModif(utilisateurPersId());
					ec = NettoieEOEditingContext.cleanEC(ec);
					ec.saveChanges();
					resetDomaineField();
					ctrl().editDomaine();
					mySession().addSimpleInfoMessage("Confirmation",
							"Domaine modifié dans le référentiel");
				} else {
				//Nouveau domaine
					if (ec.hasChanges()) {
						ec.saveChanges();
						resetDomaineField();
						ctrl().editDomaine();
						mySession().addSimpleInfoMessage("Confirmation",
								"Domaine enregistré dans le référentiel");
					}
				}
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
			} else {
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
			}
		} catch (Exception e) {
			e.printStackTrace();
			mySession().addSimpleErrorMessage("ALERTE", e.toString());
			context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
			return null;
		}
		return null;
	}

	public void resetDomaineField() {
		ctrl().setSelectedDomaine(null);
		ctrl().setLesDomaines(null);
		ctrl().setTypeTiers(null);
	}

	public void resetUneMatiereField() {
		ctrl().setSelectedDomaineMatiere(null);
		ctrl().setUnDomaineMatiere(null);
	}

	public NSTimestamp domaineDateFin() {
		return domaine().dateFin();
	}

	public void setDomaineDateFin(NSTimestamp date) {
		try {
			domaine().setDateFin(date);
		} catch (ValidationException e) {
			mySession().addSimpleErrorMessage("RFCCT", e.getMessage());
		}
	}

	public NSTimestamp matiereDateFin() {
		return matiere().dateFin();
	}

	public void setMatiereDateFin(NSTimestamp date) {
		try {
			matiere().setDateFin(date);
		} catch (ValidationException e) {
			mySession().addSimpleErrorMessage("RFCCT", e.getMessage());
		}
	}

	// Retourne la clé primaire
	protected static Integer singleIntPrimaryKeyForObject(EOEditingContext ec,
			EOMatiere eo) {
		// EOEditingContext ec = ec;
		if (ec == null) {
			// you don't have an EC! Bad EO. We can do nothing.
			return null;
		}
		EOGlobalID gid = ec.globalIDForObject(eo);
		if (gid.isTemporary()) {
			// no pk yet assigned
			return null;
		}
		EOKeyGlobalID kGid = (EOKeyGlobalID) gid;
		NSArray keyValues = kGid.keyValuesArray();
		if (keyValues.count() != 1
				|| !(keyValues.objectAtIndex(0) instanceof Integer))
			return null;

		return (Integer) keyValues.objectAtIndex(0);
	}

	public boolean isCreationDomaineEnabled() {
		if (creationDomaine == null) {
			if (myApp().newGestionDroitsEnabled()) {
				creationDomaine = myGdUser().getCompetenceAutorisationCache()
						.gDRfcctCreationDomaine() || droitTout();
			} else {
				creationDomaine = myJefyUser().hasDroitCreationDomaine()
						|| droitTout();
			}
		}
		return creationDomaine;
	}
}
