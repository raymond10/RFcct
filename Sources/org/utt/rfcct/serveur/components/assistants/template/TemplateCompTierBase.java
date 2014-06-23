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
package org.utt.rfcct.serveur.components.assistants.template;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.ajax.AjaxUpdateContainer;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleAssistant;
import org.utt.rfcct.serveur.components.controlers.TemplateCompTierBaseCtrl;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 3 juil. 2013
 */
@SuppressWarnings("all")
public class TemplateCompTierBase extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4103044170629350893L;
	private static final String COMPETENCE_BDG = "competence";
	private static final String DOMAINE_BDG = "domaine";
	private static final String EDITING_CONTEXT_BDG = "edc";
	private static final String STRUCTURE_BDG = "structure";
	private static final String WANT_RESET = "wantReset";
	private TemplateCompTierBaseCtrl ctrl = null;
	private EOCompetence competence;
	private EODomaine domaine;

	public TemplateCompTierBase(WOContext context) {
		super(context);
		NSLog.out.appendln("TemplateCompTierBase");
	}
	
	@Override
	public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
        if (valueForBooleanBinding(WANT_RESET, false)) {
            resetState();
        }
		super.appendToResponse(woresponse, wocontext);
	}

	public String containerTemplateId() {
		// TODO
		return "containerTemplate";
	}

	public String TRMatiereId() {
		return "TRMatiere";
	}

	public String TRSMatiereId() {
		return "TRSMatiere";
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

	public boolean enabled() {
		if (isInAssistant())
			return isStandAlone();
		if (isStandAlone()) {
			boolean assist = isInAssistant();
			boolean update = (peutModifierCompetence()
					&& competence().persIdCrea() == persId()) || droitTout();
			boolean result = assist && update;
			return result;
		}
		return false;
	}

	/**
	 * @return the ctrl
	 */
	public TemplateCompTierBaseCtrl ctrl() {
		if (ctrl == null)
			ctrl = new TemplateCompTierBaseCtrl(this);
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(TemplateCompTierBaseCtrl ctrl) {
		this.ctrl = ctrl;
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

	// Bindings
	/**
	 * @return the competence
	 */
	public EOCompetence competence() {
		if (this.competence == null) {
			EOCompetence competenceFromParent = (EOCompetence) valueForBinding(COMPETENCE_BDG);
			if ((competenceFromParent != null)
					&& (competenceFromParent.editingContext() != null))
				this.competence = competenceFromParent;
		}
		return this.competence;
	}

	/**
	 * @param competence
	 *            the competence to set
	 */
	public void setCompetence(EOCompetence competence) {
		this.competence = competence;
		setValueForBinding(competence, COMPETENCE_BDG);
	}

	/**
	 * @return the domaine
	 */
	public EODomaine domaine() {
		if (this.domaine == null) {
			EODomaine domaineFromParent = (EODomaine) valueForBinding(DOMAINE_BDG);
			if ((domaineFromParent != null)
					&& (domaineFromParent.editingContext() != null))
				this.domaine = domaineFromParent;
		}
		return domaine;
	}

	/**
	 * @param domaine
	 *            the domaine to set
	 */
	public void setDomaine(EODomaine domaine) {
		this.domaine = domaine;
		setValueForBinding(domaine, DOMAINE_BDG);
	}

	public EOEditingContext editingContext() {
		return (EOEditingContext) valueForBinding(EDITING_CONTEXT_BDG);
	}
    
    public boolean wantReset() {
    	return (Boolean) valueForBinding(WANT_RESET);
	}

	public void setWantReset(Boolean value) {
		setValueForBinding(value, WANT_RESET);
	}

	// AjaxUpdate
	// Rafra”chir les s/Matieres
	public void refreshSousMat() {
		if(ctrl().leCentreMatiere() != null)
			ctrl().setLesSousMatieres(
				EOMatiere.fetchLesFilsDuPere(competence().editingContext(),
						Integer.valueOf(ctrl().leCentreMatiere().primaryKey()),
						mySession().TODAY));
	}

	// Rafraichissement des s/Terrain
	public void refreshSousTerr() {
		if (ctrl().leCentreTerrain() != null)
			ctrl().setLesSousTerrains(
					EOTerrain.fetchLesFilsDuPere(competence().editingContext(),
							Integer.valueOf(ctrl().leCentreTerrain()
									.primaryKey()), mySession().TODAY));
	}
    
    protected void resetState() {
		competence().setMatiere(null);
		competence().setTerrain(null);
		competence().setCapacite(null);
		ctrl().setLesMatieres(null);
		ctrl().setLeCentreMatiere(null);
		ctrl().setLesTerrains(null);
		ctrl().setLeCentreTerrain(null);
		ctrl().setLesCapacites(null);
		ctrl().setLeCentreCapacite(null);
    }
}
