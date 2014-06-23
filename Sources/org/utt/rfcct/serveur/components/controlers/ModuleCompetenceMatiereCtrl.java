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

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleCompetenceMatiere;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.foundation.NSArray;


/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *26 mars 2013
 */
public class ModuleCompetenceMatiereCtrl extends ModuleCtrl<ModuleCompetenceMatiere> {
	
	//Matiere
	private NSArray<EOMatiere> lesMatieres;
	private EOMatiere uneMatiere;
	private EOMatiere leCentreMatiere;
	private NSArray<EOMatiere> lesSousMatieres;
	private EOMatiere uneSousMatiere;
	private EOMatiere leSousCentreMatiere;
	
	//Terrain
	private NSArray<EOTerrain> lesTerrains;
	private EOTerrain unTerrain;
	private EOTerrain leCentreTerrain;
	private NSArray<EOTerrain> lesSousTerrains;
	private EOTerrain uneSousTerrain;
	private EOTerrain leSousCentreTerrain;

	/**
	 * @param component
	 */
	public ModuleCompetenceMatiereCtrl(ModuleCompetenceMatiere component) {
		super(component);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the lesMatieres
	 */
	public NSArray<EOMatiere> lesMatieres() {
		return lesMatieres;
	}

	/**
	 * @param lesMatieres the lesMatieres to set
	 */
	public void setLesMatieres(NSArray<EOMatiere> lesMatieres) {
		this.lesMatieres = lesMatieres;
	}

	/**
	 * @return the uneMatiere
	 */
	public EOMatiere getUneMatiere() {
		return uneMatiere;
	}

	/**
	 * @param uneMatiere the uneMatiere to set
	 */
	public void setUneMatiere(EOMatiere uneMatiere) {
		this.uneMatiere = uneMatiere;
	}

	/**
	 * @return the leCentreMatiere
	 */
	public EOMatiere getLeCentreMatiere() {
		return leCentreMatiere;
	}

	/**
	 * @param leCentreMatiere the leCentreMatiere to set
	 */
	public void setLeCentreMatiere(EOMatiere leCentreMatiere) {
		this.leCentreMatiere = leCentreMatiere;
	}

	/**
	 * @return the lesSousMatieres
	 */
	public NSArray<EOMatiere> getLesSousMatieres() {
		return lesSousMatieres;
	}

	/**
	 * @param lesSousMatieres the lesSousMatieres to set
	 */
	public void setLesSousMatieres(NSArray<EOMatiere> lesSousMatieres) {
		this.lesSousMatieres = lesSousMatieres;
	}

	/**
	 * @return the uneSousMatiere
	 */
	public EOMatiere getUneSousMatiere() {
		return uneSousMatiere;
	}

	/**
	 * @param uneSousMatiere the uneSousMatiere to set
	 */
	public void setUneSousMatiere(EOMatiere uneSousMatiere) {
		this.uneSousMatiere = uneSousMatiere;
	}

	/**
	 * @return the leSousCentreMatiere
	 */
	public EOMatiere getLeSousCentreMatiere() {
		return leSousCentreMatiere;
	}

	/**
	 * @param leSousCentreMatiere the leSousCentreMatiere to set
	 */
	public void setLeSousCentreMatiere(EOMatiere leSousCentreMatiere) {
		this.leSousCentreMatiere = leSousCentreMatiere;
	}

	/**
	 * @return the lesTerrains
	 */
	public NSArray<EOTerrain> getLesTerrains() {
		return lesTerrains;
	}

	/**
	 * @param lesTerrains the lesTerrains to set
	 */
	public void setLesTerrains(NSArray<EOTerrain> lesTerrains) {
		this.lesTerrains = lesTerrains;
	}

	/**
	 * @return the unTerrain
	 */
	public EOTerrain getUnTerrain() {
		return unTerrain;
	}

	/**
	 * @param unTerrain the unTerrain to set
	 */
	public void setUnTerrain(EOTerrain unTerrain) {
		this.unTerrain = unTerrain;
	}

	/**
	 * @return the leCentreTerrain
	 */
	public EOTerrain getLeCentreTerrain() {
		return leCentreTerrain;
	}

	/**
	 * @param leCentreTerrain the leCentreTerrain to set
	 */
	public void setLeCentreTerrain(EOTerrain leCentreTerrain) {
		this.leCentreTerrain = leCentreTerrain;
	}

	/**
	 * @return the lesSousTerrains
	 */
	public NSArray<EOTerrain> getLesSousTerrains() {
		return lesSousTerrains;
	}

	/**
	 * @param lesSousTerrains the lesSousTerrains to set
	 */
	public void setLesSousTerrains(NSArray<EOTerrain> lesSousTerrains) {
		this.lesSousTerrains = lesSousTerrains;
	}

	/**
	 * @return the uneSousTerrain
	 */
	public EOTerrain getUneSousTerrain() {
		return uneSousTerrain;
	}

	/**
	 * @param uneSousTerrain the uneSousTerrain to set
	 */
	public void setUneSousTerrain(EOTerrain uneSousTerrain) {
		this.uneSousTerrain = uneSousTerrain;
	}

	/**
	 * @return the leSousCentreTerrain
	 */
	public EOTerrain getLeSousCentreTerrain() {
		return leSousCentreTerrain;
	}

	/**
	 * @param leSousCentreTerrain the leSousCentreTerrain to set
	 */
	public void setLeSousCentreTerrain(EOTerrain leSousCentreTerrain) {
		this.leSousCentreTerrain = leSousCentreTerrain;
	}
	
	//Matiere et S/Matiere
	public void actMatComp() {
		EOCompetence competence = wocomponent().competence();
		if(competence != null) {
			//EODomaine dom = this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setMatiereRelationship(this.leCentreMatiere);
		}
	}
	
	public WOActionResults actSmatComp() {
		EOCompetence competence = wocomponent().competence();
		if(competence != null) {
			//EODomaine dom = this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setMatiereRelationship(this.leSousCentreMatiere);
		}
		return null;
	}
	
	//Terrain et S/Terrain
	public void actTerrComp() {
		EOCompetence competence = wocomponent().competence();
		if(competence != null) {
			//EODomaine dom = this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setTerrainRelationship(this.leCentreTerrain);
		}
	}
	
	public WOActionResults actSterrComp() {
		EOCompetence competence = wocomponent().competence();
		if(competence != null) {
			//EODomaine dom = this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setTerrainRelationship(this.leSousCentreTerrain);
		}
		return null;
	}
}
