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
package org.utt.rfcct.serveur.components.assistants;

import org.apache.log4j.Logger;
import org.utt.fwkuttcompetences.serveur.CompetenceApplicationUser;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.rfcct.serveur.components.BaseComponent;
import org.utt.rfcct.serveur.components.assistants.modules.IModuleAssistant;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;

import er.ajax.AjaxUpdateContainer;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 21 mars 2013
 */
@SuppressWarnings("all")
public abstract class Assistant extends BaseComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1762727242354718536L;
	private static final Logger LOG = Logger.getLogger(Assistant.class);
	public static final String MODULES_BDG = "modules";
	public static final String ETAPES_BDG = "etapes";
	public static final String ACTIVE_MODULE_IDX_BDG = "indexModuleActif";
	public static final String ON_FAILURE_UPDTCONTAINER_ID_BDG = "onFailureUpdateContainerID";
	public static final String ACTION_TERMINER_BDG = "terminer";
	public static final String LIBELLE_TERMINER_BDG = "libelleTerminer";
	public static final String ACTION_ANNULER_BDG = "annuler";
	public static final String LIBELLE_ANNULER_BDG = "libelleAnnuler";
	public static final String APPLICATION_USER_BDG = "applicationUser";
	public static final String EDITING_CONTEXT_BDG = "editingContext";
	public static final String UTILISATEUR_PERS_ID_BDG = "utilisateurPersId";
	public static final String ASSISTANT_EDIT_MODE_VALUE = "editMode";
	public static final String ACTION_APRES_TERMINER_BDG = "apresTerminer";
	public static final String COMPETENCES_BDG = "competences";

	private NSArray<String> modules;
	private EOEditingContext editingContext;
	private CompetenceApplicationUser applicationUser;
	private Integer utilisateurPersId;

	private Integer indexModuleActif;
	private String libelleAnnuler;
	private String libelleTerminer;

	private IModuleAssistant module;

	public NSArray<String> etapes;
	public String uneEtape;

	private String failureMessage;
	private String onFailureUpdateContainerID;

	private boolean isEditMode;
	private boolean wantRefreshEtapeModule;
	private NSMutableArray<EOCompetence> competences;

	/**
	 * @param context
	 */
	public Assistant(WOContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void reset() {
		modules = null;
		editingContext = null;
		utilisateurPersId = null;
		indexModuleActif = null;
		module = null;
		failureMessage = null;
		isEditMode = false;
		setWantRefreshEtapeModule(true);
	}

	public String moduleName() {
		String moduleName = null;
		if (modules() != null && modules().count() > 0) {
			moduleName = (String) modules().objectAtIndex(
					indexModuleActif().intValue());
		}
		return moduleName;
	}

	/**
	 * @return the modules
	 */
	public NSArray<String> modules() {
		modules = (NSArray<String>) valueForBinding(MODULES_BDG);
		return modules;
	}

	/**
	 * @param modules
	 *            the modules to set
	 */
	public void setModules(NSArray<String> modules) {
		this.modules = modules;
	}

	public Integer indexModuleActif() {
		indexModuleActif = (Integer) valueForBinding(ACTIVE_MODULE_IDX_BDG);
		if (indexModuleActif == null && !etapes().isEmpty()) {
			setIndexModuleActif(0);
		}
		return indexModuleActif;
	}

	/**
	 * @param indexModuleActif
	 *            the indexModuleActif to set
	 */
	public void setIndexModuleActif(Integer indexModuleActif) {
		this.indexModuleActif = indexModuleActif;
		setValueForBinding(indexModuleActif, ACTIVE_MODULE_IDX_BDG);
		if (indexModuleActif() != null && indexModuleActif() == 0) {
			reset();
		}
	}

	/**
	 * @return the competences
	 */
	public NSMutableArray<EOCompetence> competences() {
		competences = (NSMutableArray<EOCompetence>) valueForBinding(COMPETENCES_BDG);
		return competences;
	}

	/**
	 * @param competences
	 *            the competences to set
	 */
	public void setCompetences(NSMutableArray<EOCompetence> competences) {
		this.competences = competences;
		setValueForBinding(competences, COMPETENCES_BDG);
	}

	/**
	 * @return the etapes
	 */
	public NSArray<String> etapes() {
		etapes = (NSArray<String>) valueForBinding(ETAPES_BDG);
		return etapes;
	}

	/**
	 * @param etapes
	 *            the etapes to set
	 */
	public void setEtapes(NSArray<String> etapes) {
		this.etapes = etapes;
	}

	public String styleForEtape() {
		String styleForEtape = null;
		if (etapes().indexOf(uneEtape) == indexModuleActif().intValue()) {
			styleForEtape = "etape selected";
		} else if (etapes().indexOf(uneEtape) < indexModuleActif().intValue()) {
			styleForEtape = "etape passed";
		} else {
			styleForEtape = "etape";
		}
		return styleForEtape;
	}

	public boolean isEtapeActive() {
		return etapes().indexOf(uneEtape) == indexModuleActif().intValue();
	}

	public WOActionResults annuler() {
		setFailureMessage(null);
		setIndexModuleActif(null);
		return performParentAction(ACTION_ANNULER_BDG);
	}

	public WOActionResults terminer() {
		WOActionResults result = null;
		try {
			if (module.valider()) {
				result = performParentAction(ACTION_TERMINER_BDG);
				setFailureMessage(null);
				mySession().resetModule();
			} else {
				context().response().setStatus(500);
			}
		} catch (NSForwardException e) {
			LOG.error(e.getMessage(), e);
			context().response().setStatus(500);
			if (e.originalException() != null)
				mySession().addSimpleErrorMessage(
						e.originalException().getMessage(),
						e.originalException());
		} catch (Exception e1) {
			LOG.error(e1.getMessage(), e1);
			context().response().setStatus(500);
			mySession().addSimpleErrorMessage(e1.getMessage(), e1);
		}
		return result;
	}

	public boolean isPrecedentDisabled() {
		boolean boolIndex = indexModuleActif().intValue() <= 0;
		// boolean boolModule = module().isPrecedentDisabled();
		boolean precedent = boolIndex;
		// || boolModule;
		return precedent;
	}

	public boolean isPrecedentEnabled() {
		return !isPrecedentDisabled();
	}

	public boolean isSuivantDisabled() {
		int modul = modules().count() - 1;
		boolean boolIndex = indexModuleActif().intValue() >= modul;
		// boolean boolModule = module().isSuivantDisabled();
		boolean suivant = boolIndex;
		// || boolModule;
		return suivant;
	}

	public boolean isSuivantEnabled() {
		return !isSuivantDisabled();
	}

	public boolean isTerminerDisabled() {
		boolean isTerminerDisabled = module().isTerminerDisabled();
		return isTerminerDisabled;
	}

	public boolean isTerminerEnabled() {
		return !isTerminerDisabled();
	}

	// Bouton precedent
	public WOActionResults precedent() {
		try {
			module().onPrecedent();
			int newIndex = indexModuleActif().intValue() - 1;
			if (newIndex >= 0) {
				setIndexModuleActif(Integer.valueOf(newIndex));
			}
			context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
		} catch (Exception e) {
			context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
		}
		return null;
	}

	// Bouton suivant
	public WOActionResults suivant() {
		try {
			module().onSuivant();
			if (module().valider()) {
				int newIndex = indexModuleActif().intValue() + 1;
				if (newIndex <= modules().count() - 1) {
					setIndexModuleActif(Integer.valueOf(newIndex));
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
			mySession().addSimpleErrorMessage("RFCCT", e.toString());
			context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
		}
		return null;
	}

	public WOComponent assistant() {
		return this;
	}

	/**
	 * @return the module
	 */
	public IModuleAssistant module() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(IModuleAssistant module) {
		this.module = module;
	}

	public String onFailure() {
		String onFailure = null;
		if (failureMessage() != null && (onFailureUpdateContainerID() != null)) {
			onFailure = "function ()" + onFailureUpdateContainerID()
					+ "Update();}";
		}
		return onFailure;
	}

	/**
	 * @return the failureMessage
	 */
	public String failureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
		if (failureMessage != null) {
			mySession().addSimpleErrorMessage("Erreur", failureMessage);
		} // else {
			// mySession().addSimpleErrorMessage("","");
			// }
	}

	/**
	 * @return the libelleAnnuler
	 */
	public String libelleAnnuler() {
		if (hasBinding(LIBELLE_ANNULER_BDG)) {
			libelleAnnuler = (String) valueForBinding(LIBELLE_ANNULER_BDG);
		} else {
			libelleAnnuler = "Annuler";
		}
		return libelleAnnuler;
	}

	/**
	 * @param libelleAnnuler
	 *            the libelleAnnuler to set
	 */
	public void setLibelleAnnuler(String libelleAnnuler) {
		this.libelleAnnuler = libelleAnnuler;
	}

	/**
	 * @return the libelleTerminer
	 */
	public String libelleTerminer() {
		if (hasBinding(LIBELLE_TERMINER_BDG)) {
			libelleTerminer = (String) valueForBinding(LIBELLE_TERMINER_BDG);
		} else {
			libelleTerminer = "Terminer";
		}
		return libelleTerminer;
	}

	/**
	 * @param libelleTerminer
	 *            the libelleTerminer to set
	 */
	public void setLibelleTerminer(String libelleTerminer) {
		this.libelleTerminer = libelleTerminer;
	}

	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}

	/**
	 * @param editingContext
	 *            the editingContext to set
	 */
	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
	}

	/**
	 * @return the utilisateurPersId
	 */
	public Integer utilisateurPersId() {
		if (hasBinding(UTILISATEUR_PERS_ID_BDG)) {
			utilisateurPersId = (Integer) valueForBinding(UTILISATEUR_PERS_ID_BDG);
		}
		return utilisateurPersId;
	}

	/**
	 * @param utilisateurPersId
	 *            the utilisateurPersId to set
	 */
	public void setUtilisateurPersId(Integer utilisateurPersId) {
		this.utilisateurPersId = utilisateurPersId;
		if (hasBinding(UTILISATEUR_PERS_ID_BDG)) {
			setValueForBinding(utilisateurPersId, UTILISATEUR_PERS_ID_BDG);
		}
	}

	public CompetenceApplicationUser applicationUser() {
		if (hasBinding(APPLICATION_USER_BDG)) {
			CompetenceApplicationUser appUser = (CompetenceApplicationUser) valueForBinding(APPLICATION_USER_BDG);
			applicationUser = appUser;
		}
		return applicationUser;
	}

	public void setApplicationUser(CompetenceApplicationUser applicationUser) {
		this.applicationUser = applicationUser;
		if (canSetValueForBinding(APPLICATION_USER_BDG))
			setValueForBinding(applicationUser, APPLICATION_USER_BDG);
	}

	/**
	 * @return the onFailureUpdateContainerID
	 */
	public String onFailureUpdateContainerID() {
		if (hasBinding(ON_FAILURE_UPDTCONTAINER_ID_BDG)) {
			onFailureUpdateContainerID = stringValueForBinding(
					ON_FAILURE_UPDTCONTAINER_ID_BDG, null);
		}
		return onFailureUpdateContainerID;
	}

	/**
	 * @param onFailureUpdateContainerID
	 *            the onFailureUpdateContainerID to set
	 */
	public void setOnFailureUpdateContainerID(String onFailureUpdateContainerID) {
		this.onFailureUpdateContainerID = onFailureUpdateContainerID;
		if (canSetValueForBinding(ON_FAILURE_UPDTCONTAINER_ID_BDG)) {
			setValueForBinding(onFailureUpdateContainerID,
					ON_FAILURE_UPDTCONTAINER_ID_BDG);
		}
	}

	/**
	 * @return the isEditMode
	 */
	public boolean isEditMode() {
		return isEditMode;
	}

	/**
	 * @param isEditMode
	 *            the isEditMode to set
	 */
	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	public WOActionResults selectEtape() {
		WOActionResults result = null;
		try {
			if (module.valider()) {
				int newIndex = etapes().indexOf(uneEtape); // TODO : stabiliser
															// en vŽrifiant que
															// uneEtape n'est
															// pas nulle....
				if (newIndex <= modules().count() - 1) {
					setIndexModuleActif(Integer.valueOf(newIndex));
					context().response().setStatus(500);
					AjaxUpdateContainer.updateContainerWithID(myApp()
							.messageContainerID(), context());
				}
				//context().response().setStatus(500);
				//AjaxUpdateContainer.updateContainerWithID(myApp()
					//	.messageContainerID(), context());
			} else {
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
			}
		} catch (NSForwardException e) {
			LOG.error(e.getMessage(), e);
			context().response().setStatus(500);
			if (e.originalException() != null)
				mySession().addSimpleErrorMessage(
						e.originalException().getMessage(),
						e.originalException());
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
		} catch (Exception e1) {
			LOG.error(e1.getMessage(), e1);
			context().response().setStatus(500);
			mySession().addSimpleErrorMessage(e1.getMessage(), e1);
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
		}
		return result;
	}

	/**
	 * @return the wantRefreshEtapeModule
	 */
	public boolean wantRefreshEtapeModule() {
		return wantRefreshEtapeModule;
	}

	/**
	 * @param wantRefreshEtapeModule
	 *            the wantRefreshEtapeModule to set
	 */
	public void setWantRefreshEtapeModule(boolean wantRefreshEtapeModule) {
		this.wantRefreshEtapeModule = wantRefreshEtapeModule;
	}

	public WOActionResults apresTerminer() {
		return performParentAction(ACTION_APRES_TERMINER_BDG);
	}

}
