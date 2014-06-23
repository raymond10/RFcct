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
package org.utt.rfcct.serveur.components.controlers;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleCompetenceCapacite;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.foundation.ERXArrayUtilities;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 9 avr. 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceCapaciteCtrl extends
		ModuleCtrl<ModuleCompetenceCapacite> {

	// Terrain
	private NSArray lesTerrains;
	private EOTerrain unTerrain;
	private EOTerrain leCentreTerrain;
	private NSArray<EOTerrain> lesSousTerrains;
	private EOTerrain uneSousTerrain;
	private EOTerrain leSousCentreTerrain;

	// Capacite
	private NSArray lesCapacites;
	private EOCapacite uneCapacite;
	private EOCapacite leCentreCapacite;

	EOEditingContext ec = null;
	
	private EODomaine domaineEncours;

	/**
	 * @param component
	 */
	public ModuleCompetenceCapaciteCtrl(ModuleCompetenceCapacite component) {
		super(component);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the domaineEncours
	 */
	public EODomaine domaineEncours() {
		return domaineEncours;
	}

	/**
	 * @param domaineEncours the domaineEncours to set
	 */
	public void setDomaineEncours(EODomaine domaineEncours) {
		this.domaineEncours = domaineEncours;
	}

	/**
	 * @return the lesCapacites
	 */
	public NSArray lesCapacites() {
		ec = wocomponent().competence().editingContext();		
		if (lesCapacites == null) {
			lesCapacites = new NSArray();
			NSMutableArray<EOCapacite> tmp = new NSMutableArray<EOCapacite>();
			NSMutableArray<EOCapacite> tmp2 = new NSMutableArray<EOCapacite>();
			NSArray<EOCompetence> competences = null;
			/*NSArray<EORepartCompCapacite> rccs = null;*/
			NSArray<EORepartDomCapacite> rdcs = null;
			EODomaine domaine = wocomponent().competence().domaine();
			if (domaine != null) {
				competences = EOUtilities.objectsMatchingKeyAndValue(ec,
						EOCompetence.ENTITY_NAME, EOCompetence.DOMAINE_KEY,
						wocomponent().competence().domaine());
			} else {
				competences = EOUtilities.objectsForEntityNamed(ec,
						EOCompetence.ENTITY_NAME);
			}
			EODomaine domaines = ((NSArray<EODomaine>) competences
					.valueForKey(EOCompetence.DOMAINE_KEY)).lastObject();
			if (domaines != null && domaine != null) {
				rdcs = (NSArray<EORepartDomCapacite>) domaines
						.valueForKey(EODomaine.REPART_DOM_CAPACITES_KEY);
				if (rdcs != null && rdcs.count() > 0) {
					NSArray tt = (NSArray<EOCapacite>) rdcs
							.valueForKey(EORepartDomCapacite.CAPACITE_KEY);
					for (int t = 0; t < tt.count(); t++) {
						EOCapacite cp = (EOCapacite) tt.objectAtIndex(t);
						tmp2.addObject(cp);
					}
					NSArray tmp3 = tmp2.immutableClone();
					tmp3 = ERXArrayUtilities
							.filteredArrayWithQualifierEvaluation(
									tmp3,
									EOCapacite.DATE_FIN
											.isNull()
											.or(EOCapacite.DATE_FIN
													.greaterThanOrEqualTo(wocomponent()
															.mySession().TODAY)));
					tmp.addObjectsFromArray(tmp3);
				}
			} else {
				if(wocomponent().selectedDomaine() != null)
					tmp.addObjectsFromArray(EOCapacite.fetchCapacitesFromDomaine(
						ec, wocomponent().selectedDomaine(), wocomponent().mySession().TODAY));
			}

			EOCapacite uneCapacite = leCentreCapacite();
			if (uneCapacite != null && !tmp.contains(uneCapacite)) {
				tmp.addObject(uneCapacite);
			}
			if (tmp != null && tmp.count() > 0) {
				ERXArrayUtilities.sortArrayWithKey(tmp,
						EOCapacite.LBL_COURT_KEY);
				lesCapacites = ERXArrayUtilities.arrayWithoutDuplicates(tmp);
			}
		}
		return lesCapacites;
	}

	/**
	 * @param lesCapacites the lesCapacites to set
	 */
	public void setLesCapacites(NSArray lesCapacites) {
		this.lesCapacites = lesCapacites;
	}

	public WOActionResults actCap() {
		if (leCentreCapacite != null) {
			if (wocomponent().mySession().listCaps() == null)
				wocomponent().mySession().setListCaps(
						new NSMutableArray<EOCapacite>());
			/*
			if (wocomponent().capDisplayGroup() == null)
				wocomponent().setCapDisplayGroup(new ERXDisplayGroup());
			if (!wocomponent().mySession().listCaps()
					.contains(leCentreCapacite))
				wocomponent().mySession().listCaps()
						.addObject(leCentreCapacite);
			wocomponent().capDisplayGroup().setObjectArray(
					wocomponent().mySession().listCaps().immutableClone());
					*/
		}
		return null;
	}

	// Terrain

	/**
	 * @return the lesTerrains
	 */
	public NSArray lesTerrains() {
		if (wocomponent().isInAssistant())
			resetIt();
		ec = wocomponent().competence().editingContext();
		if (lesTerrains == null) {
			lesTerrains = new NSArray();
			NSMutableArray<EOTerrain> tmp = new NSMutableArray<EOTerrain>();
			NSArray<EOCompetence> competences = null;
			EODomaine domaine = wocomponent().competence().domaine();
			if (domaine != null) {
				competences = EOUtilities.objectsMatchingKeyAndValue(ec,
						EOCompetence.ENTITY_NAME, EOCompetence.DOMAINE_KEY,
						wocomponent().competence().domaine());
			} else {
				competences = EOUtilities.objectsForEntityNamed(ec,
						EOCompetence.ENTITY_NAME);
			}

			EODomaine domaines = ((NSArray<EODomaine>) competences
					.valueForKey(EOCompetence.DOMAINE_KEY)).lastObject();
			if (domaines != null && domaine != null) {
				tmp.addObjectsFromArray(EOTerrain.fetchTerrainsFromDomaine(ec, domaines, wocomponent().mySession().TODAY));
			} else {
				if(wocomponent().selectedDomaine() != null)
					tmp.addObjectsFromArray(EOTerrain.fetchTerrainsFromDomaine(ec,
							wocomponent().selectedDomaine(), wocomponent().mySession().TODAY));
			}
			EOTerrain unTerrain = leCentreTerrain();
			if (unTerrain != null && !tmp.contains(unTerrain)) {
				tmp.addObject(unTerrain);
			}
			if (tmp != null && tmp.count() > 0) {
				ERXArrayUtilities
						.sortArrayWithKey(tmp, EOTerrain.LBL_COURT_KEY);
				lesTerrains = ERXArrayUtilities.arrayWithoutDuplicates(tmp);
			}
		}
		wocomponent().refreshTRTerrain();
		return lesTerrains;
	}

	/**
	 * @param lesTerrains
	 *            the lesTerrains to set
	 */
	public void setLesTerrains(NSArray<EOTerrain> lesTerrains) {
		this.lesTerrains = lesTerrains;
	}

	/**
	 * @return the unTerrain
	 */
	public EOTerrain unTerrain() {
		return unTerrain;
	}

	/**
	 * @param unTerrain
	 *            the unTerrain to set
	 */
	public void setUnTerrain(EOTerrain unTerrain) {
		this.unTerrain = unTerrain;
	}

	/**
	 * @return the leCentreTerrain
	 */
	public EOTerrain leCentreTerrain() {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			leCentreTerrain = competence.terrain();
		}
		return leCentreTerrain;
	}

	/**
	 * @param leCentreTerrain
	 *            the leCentreTerrain to set
	 */
	public void setLeCentreTerrain(EOTerrain leCentreTerrain) {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			competence.setTerrainRelationship(leCentreTerrain);
			lesTerrains = null;
		}
		this.leCentreTerrain = leCentreTerrain;
	}

	/**
	 * @return the lesSousTerrains
	 */
	public NSArray<EOTerrain> lesSousTerrains() {
		return lesSousTerrains;
	}

	/**
	 * @param lesSousTerrains
	 *            the lesSousTerrains to set
	 */
	public void setLesSousTerrains(NSArray<EOTerrain> lesSousTerrains) {
		this.lesSousTerrains = lesSousTerrains;
	}

	/**
	 * @return the uneSousTerrain
	 */
	public EOTerrain uneSousTerrain() {
		return uneSousTerrain;
	}

	/**
	 * @param uneSousTerrain
	 *            the uneSousTerrain to set
	 */
	public void setUneSousTerrain(EOTerrain uneSousTerrain) {
		this.uneSousTerrain = uneSousTerrain;
	}

	/**
	 * @return the leSousCentreTerrain
	 */
	public EOTerrain leSousCentreTerrain() {
		return leSousCentreTerrain;
	}

	/**
	 * @return the uneCapacite
	 */
	public EOCapacite uneCapacite() {
		return uneCapacite;
	}

	/**
	 * @param uneCapacite
	 *            the uneCapacite to set
	 */
	public void setUneCapacite(EOCapacite uneCapacite) {
		this.uneCapacite = uneCapacite;
	}

	/**
	 * @return the leCentreCapacite
	 */
	public EOCapacite leCentreCapacite() {
		return leCentreCapacite;
	}

	/**
	 * @param leCentreCapacite
	 *            the leCentreCapacite to set
	 */
	public void setLeCentreCapacite(EOCapacite leCentreCapacite) {
		this.leCentreCapacite = leCentreCapacite;
	}

	/**
	 * @param leSousCentreTerrain
	 *            the leSousCentreTerrain to set
	 */
	public void setLeSousCentreTerrain(EOTerrain leSousCentreTerrain) {
		this.leSousCentreTerrain = leSousCentreTerrain;
	}

	// Terrain et S/Terrain
	public WOActionResults actSterrComp() {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			// EODomaine dom =
			// this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setTerrainRelationship(this.leSousCentreTerrain);
		}
		return null;
	}
	
	public void resetIt() {
		EOCompetence competence = wocomponent().competence();
		if(this.domaineEncours == null || this.domaineEncours != wocomponent().selectedDomaine()) {
			this.domaineEncours = wocomponent().selectedDomaine();
			competence.setTerrainRelationship(null);
			lesTerrains = null;
			leCentreTerrain = null;
			unTerrain = null;
			lesSousTerrains = null;
			leSousCentreTerrain = null;
			lesCapacites = null;
			leCentreCapacite = null;
			uneCapacite = null;
			wocomponent().setCapDisplayGroup(null);
		}
	}

}
