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
package org.utt.rfcct.serveur.components.assistants;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.rfcct.serveur.components.GestionGroupe;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSMutableArray;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 4 juil. 2013
 */
@SuppressWarnings("all")
public class AssistantCompetenceTier extends Assistant {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7777073646878022620L;
	public static final String COMPETENCE_BDG = "competence";
	private EOCompetence competence;

	public AssistantCompetenceTier(WOContext context) {
        super(context);
		setEditMode(true);
    }

	public void appendToResponse(WOResponse response, WOContext context) {
		super.appendToResponse(response, context);
	}

	@Override
	public void reset() {
		competence = null;
		setEditMode(false);
		super.reset();
	}
	
	/**
	 * @return the competence
	 */
	public EOCompetence competence() {
		if (hasBinding(COMPETENCE_BDG)) {
			competence = (EOCompetence) valueForBinding(COMPETENCE_BDG);
		}
		return competence;
	}

	/**
	 * @param competence
	 *            the competence to set
	 */
	public void setCompetence(EOCompetence competence) {
		this.competence = competence;
		if (hasBinding(COMPETENCE_BDG)) {
			setValueForBinding(competence, COMPETENCE_BDG);
		}
	}
	
	public boolean isPrecedentEnabled() {
		boolean precedentEnabled = !isPrecedentDisabled();
		return precedentEnabled;
	}

	public boolean isSuivantEnabled() {
		boolean suivantEnabled = !isSuivantDisabled();
		return suivantEnabled;
	}

	public boolean isTerminerEnabled() {
		boolean terminerEnabled = !isTerminerDisabled();
		return terminerEnabled;
	}
	
	public boolean isTerminerDisabled() {
		NSMutableArray<EOCompetence> competences = myCompetences();
		boolean isTerminerDisabled = competences.count() > 0;
		return isTerminerDisabled;
	}

	public WOActionResults apresTerminer() {
		WOActionResults nextPage = (WOActionResults) valueForBinding(ACTION_APRES_TERMINER_BDG);
		return nextPage;
	}

	public WOActionResults apresEnregistrer() {
		ApresEnregistrement nextPage = (ApresEnregistrement) pageWithName(ApresEnregistrement.class
				.getName());
		nextPage.setCompetence(competence);
		return nextPage;
	}

	public WOActionResults annuler() {
		GestionGroupe nextPage = (GestionGroupe) pageWithName(GestionGroupe.class.getName());
		if (editingContext() != null) {
			editingContext().revert();
		} else if (competence() != null
				&& competence().editingContext() != null) {
			competence().editingContext().revert();
		}
		setIndexModuleActif(null);
		mySession().setIndexModuleActifCreationTier(null);
		mySession().getUiMessages().removeAllObjects();
		mySession().reset();
		return nextPage;
	}

	public String getUneEtapeName() {
		String etapeName = "";
		if (uneEtape != null) {
			etapeName = uneEtape;
		}
		return etapeName;
	}

	public boolean isModuleEtapeActive() {
		return (uneEtape != null) ? isEtapeActive() : false;
	}
}
