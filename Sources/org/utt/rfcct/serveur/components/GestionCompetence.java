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
package org.utt.rfcct.serveur.components;

import java.util.Enumeration;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCompetenceTiers;
import org.utt.fwkuttcompetences.serveur.utils.process.ProcessResfreshVM;
import org.utt.rfcct.serveur.components.assistants.ApresEnregistrement;
import org.utt.rfcct.serveur.utils.VerifyBeforeCreate;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 21 mars 2013
 */
@SuppressWarnings("all")
public class GestionCompetence extends BaseComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841972661505668913L;
	private EOEditingContext editingContext;
	private EOCompetence competence;
	private NSArray<String> modules;
	private NSArray<String> etapes;
	private Integer utilisateurPersId;
	private Integer persId = null;

	public GestionCompetence(WOContext context) {
		super(context);
		NSLog.out.appendln("GestionCompetence");
	}

	public String idAssistantCompetence() {
		return "AssistantCompetenceContainer";
	}

	/**
	 * @return the competence
	 */
	public EOCompetence competence() {
		// if (competence == null)
		// competence = mySession().competence();
		return competence;
	}

	/**
	 * @param competence
	 *            the competence to set
	 */
	public void setCompetence(EOCompetence competence) {
		this.competence = competence;
	}

	/**
	 * @return the utilisateurPersId
	 */
	public Integer utilisateurPersId() {
		if (utilisateurPersId == null)
			utilisateurPersId = persId();
		return utilisateurPersId;
	}

	/**
	 * @param utilisateurPersId
	 *            the utilisateurPersId to set
	 */
	public void setUtilisateurPersId(Integer utilisateurPersId) {
		this.utilisateurPersId = utilisateurPersId;
	}

	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		if (editingContext == null) {
			editingContext = ERXEC.newEditingContext();
		}
		return editingContext;
	}

	/**
	 * @param editingContext
	 *            the editingContext to set
	 */
	public void setEditingContext(EOEditingContext editingContext) {
		this.editingContext = editingContext;
	}

	public String moduleName() {
		String moduleName = null;
		if (modules() != null && modules().count() > 0) {
			moduleName = (String) modules()
					.objectAtIndex(
							mySession().indexModuleActifCreationCompetence()
									.intValue());
		}
		return moduleName;
	}

	/**
	 * @return the modules
	 */
	public NSArray<String> modules() {
		if (competence() != null) {
			modules = new NSArray<String>(new String[] {
					"ModuleCompetenceDomaine", "ModuleCompetenceResume" });
			// "ModuleCompetenceMatiere",
		}
		mySession().setModulesCreationCompetence(modules);
		return modules;
	}

	/**
	 * @param modules
	 *            the modules to set
	 */
	public void setModules(NSArray<String> modules) {
		this.modules = modules;
		mySession().setModulesCreationCompetence(modules);
	}

	/**
	 * @return the etapes
	 */
	public NSArray<String> etapes() {
		if (etapes == null) {
			etapes = new NSArray<String>(new String[] { "Comp&eacute;tence",
					"R&eacute;sum&eacute;" });
			// "Mati&egrave;re - Terrain",
		}
		return etapes;
	}

	public WOActionResults annuler() {
		GestionGroupe nextPage = (GestionGroupe) pageWithName(GestionGroupe.class.getName());
		if (editingContext() != null) {
			editingContext().revert();
		}
		mySession().setIndexModuleActifCreationCompetence(null);
		mySession().getUiMessages().removeAllObjects();
		return nextPage;
	}

	public WOActionResults apresTerminer() {
		ApresEnregistrement nextPage = (ApresEnregistrement) pageWithName(ApresEnregistrement.class
				.getName());
		nextPage.setCompetence(competence());
		return nextPage;
	}

	// Bouton valider/Enregsitrement en mode assistant
	public WOActionResults terminer() {
		EOCompetence competence = competence();
		EOEditingContext ec = competence.editingContext();
		boolean isCreation = competence.primaryKey() == null;
		WOComponent accueil = null;
		if (competence != null) {
			try {
				if (!VerifyBeforeCreate.ControlDuplicate(competence, mySession())) {
					if (ec.hasChanges()) {
						ec.saveChanges();
						// Enregistrement des tiers
						FactoryCompetenceTiers fct = new FactoryCompetenceTiers(
								ec, true);
						fct.enregistrerCompetenceTiers(competence, mySession()
								.tiersDg().allObjects(), mySession().dataBus());
						//forcer le rafraichissement de la VM après enregistrement
						ProcessResfreshVM.refreshMaterializedView(mySession().dataBus());
						mySession().addSimpleInfoMessage("RFCCT",
								"La compétence a bien été créée");
						ERXRedirect redirect = (ERXRedirect) pageWithName(ERXRedirect.class
								.getName());
						accueil = pageWithName(GestionGroupe.class.getName());
						redirect.setComponent(accueil);
						context().response().setStatus(500);
						mySession().reset();
						return redirect;
					}
				} else {
					context().response().setStatus(500);					
				}
			} catch (ValidationException e2) {
				mySession().addSimpleErrorMessage(e2.getLocalizedMessage(), e2);
				context().response().setStatus(500);
				ec.revert();
				myLogger().info(e2.getMessage(), e2);
				e2.printStackTrace();
			} catch (Exception e) {
				mySession()
						.addSimpleErrorMessage(
								"Une erreur est survenue lors de l'enregistrement en base",
								e);
				context().response().setStatus(500);
				ec.revert();
				myLogger().error(e.getMessage(), e);
				edc().revert();
				e.printStackTrace();
			}

		}
		return accueil;
	}

	public String messageContainerID() {
		return uniqueDomId() + "_GestComptence_rfcct_MessageContainer";
	}
}
