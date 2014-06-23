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

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomTerrain;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.rfcct.serveur.components.BaseComponent;
import org.utt.rfcct.serveur.components.assistants.Assistant;
import org.utt.rfcct.serveur.components.controlers.ModuleCompetenceDomaineCtrl;
import org.utt.rfcct.serveur.components.controlers.ModuleCompetenceMatiereCtrl;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 22 mars 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceMatiere extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2998209164909435980L;
	private ModuleCompetenceMatiereCtrl ctrl = null;

	public ModuleCompetenceMatiere(WOContext context) {
		super(context, null);
		NSLog.out.appendln("ModuleCompetenceMatiere");
	}

	/**
	 * @return the ctrl
	 */
	public ModuleCompetenceMatiereCtrl ctrl() {
		if (ctrl == null)
			ctrl = new ModuleCompetenceMatiereCtrl(this);
		refreshCombo();
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(ModuleCompetenceMatiereCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public void onPrecedent() {
	}

	public void onSuivant() {
	}

	/*
	 * public boolean isSuivantDisabled() { if (competence() == null ) return
	 * false; return true; }
	 */

	// Vérification des different champs avant de passer à la page suivante
	@Override
	public boolean valider() {
		boolean validate = false;
		EOCompetence competence = competence();
		NSArray<String> failureMessages = new NSArray<String>();
		Assistant assistant = (Assistant) parent();
		if (competence != null && assistant != null) {

			if (competence.domaine() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le domaine de compétence de la page précédente");
			}

			if (competence.matiere() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("La matière de compétence");
			}

			if (competence.terrain() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le terrain de compétence");
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
					.setFailureMessage("Veuillez renseigner les champs obligatoires (en rouge).");
		}
		if (validate)
			mySession().getUiMessages().removeAllObjects();
		return validate;
	}

	protected void refreshCombo() {
		NSMutableArray tmpMat = new NSMutableArray();
		NSMutableArray tmpTerr = new NSMutableArray();
		for (EORepartDomMatiere rdm : competence().domaine().repartDomMatieres()) {
			EOMatiere mat = rdm.matiere();
			tmpMat.addObject(mat);
		}
		ctrl.setLesMatieres(tmpMat.immutableClone());
		for (EORepartDomTerrain rdt : competence().domaine().repartDomTerrains()) {
			EOTerrain ter = rdt.terrain();
			tmpTerr.addObject(ter);
		}
		ctrl.setLesTerrains(tmpTerr.immutableClone());
	}

	public void refreshSousMat() {
		ctrl.setLesSousMatieres(EOMatiere.fetchLesFilsDuPere(competence()
				.editingContext(), Integer.valueOf(ctrl.getLeCentreMatiere()
				.primaryKey()), mySession().TODAY));
		ctrl.actMatComp();
	}

	public void refreshSousTerr() {
		ctrl.setLesSousTerrains(EOTerrain.fetchLesFilsDuPere(competence()
				.editingContext(), Integer.valueOf(ctrl.getLeCentreTerrain()
				.primaryKey()), mySession().TODAY));
		ctrl.actTerrComp();
	}
}
