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

import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.EOVPersonnelActuelEns;
import org.utt.fwkuttcompetences.serveur.modele.grhum.EOPersonne;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleCompetenceCapacite;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleCompetenceDomaine;

import com.sun.jdi.connect.Connector.SelectedArgument;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.foundation.ERXArrayUtilities;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 21 mars 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceDomaineCtrl extends
		ModuleCtrl<ModuleCompetenceDomaine> {

	private NSArray lesDomaines;
	private EODomaine itemDomaine;
	private EODomaine selectionDomaine;

	private NSArray lesPersonnes;
	private EOPersonne unePersonne;
	private EOPersonne personneDomaine;

	private boolean isStructure;
	private boolean isIndividu;
	private String estStructure;
	private String estIndividu;
	private boolean tiersTypePers = false;

	// Matiere
	private NSArray<EOMatiere> lesMatieres = null;
	private EOMatiere uneMatiere;
	private EOMatiere leCentreMatiere;
	private NSArray<EOMatiere> lesSousMatieres;
	private EOMatiere uneSousMatiere;
	private EOMatiere leSousCentreMatiere;

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
	private String tilteLbale = "Ajouter un ou plusieurs tier(s) ˆ la liste";

	/**
	 * @param component
	 */
	public ModuleCompetenceDomaineCtrl(ModuleCompetenceDomaine component) {
		super(component);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the lesDomaines
	 */
	public NSArray lesDomaines() {
		if (lesDomaines == null) {
			NSMutableArray<EODomaine> tmp = new NSMutableArray<EODomaine>();
			NSArray<EOCompetence> competences = null;
			EOEditingContext ec = wocomponent().competence().editingContext();
			competences = EOCompetence.fetchCompetences(ec, wocomponent()
					.mySession().TODAY);
			competences = ERXArrayUtilities.arrayWithoutDuplicateKeyValue(
					competences, EOCompetence.DOMAINE_KEY);
			if (competences != null && competences.count() > 0
					&& wocomponent().isStandAlone()) {
				NSArray domaines = (NSArray<EODomaine>) competences
						.valueForKey(EOCompetence.DOMAINE_KEY);
				domaines = ERXArrayUtilities
						.filteredArrayWithQualifierEvaluation(
								domaines,
								EODomaine.DATE_FIN
										.isNull()
										.or(EODomaine.DATE_FIN
												.greaterThanOrEqualTo(wocomponent()
														.mySession().TODAY)));
				tmp.addObjectsFromArray(domaines);
			} else {
				tmp.addObjectsFromArray(EODomaine.fetchDomaines(ec,
						wocomponent().mySession().TODAY));
			}

			EODomaine unDomaine = selectionDomaine();
			if (unDomaine != null && !tmp.contains(unDomaine)) {
				tmp.addObject(unDomaine);
			}
			if (tmp != null && tmp.count() > 0) {
				ERXArrayUtilities
						.sortArrayWithKey(tmp, EODomaine.LBL_COURT_KEY);
				lesDomaines = ERXArrayUtilities.arrayWithoutDuplicates(tmp);
			}
		}
		return lesDomaines;
	}

	/**
	 * @return the lesMatieres
	 */
	public NSArray<EOMatiere> lesMatieres() {
		if (lesMatieres == null) {
			lesMatieres = new NSArray<EOMatiere>();
			NSMutableArray<EOMatiere> tmp = new NSMutableArray<EOMatiere>();
			NSArray<EOCompetence> competences = null;
			EOEditingContext ec = wocomponent().competence().editingContext();
			EODomaine domaine = wocomponent().competence().domaine();
			if (domaine != null) {
				competences = EOUtilities.objectsMatchingKeyAndValue(ec,
						EOCompetence.ENTITY_NAME, EOCompetence.DOMAINE_KEY,
						domaine);
			} else {
				competences = EOCompetence.fetchCompetences(ec, wocomponent()
						.mySession().TODAY);
			}
			EODomaine domaines = ((NSArray<EODomaine>) competences
					.valueForKey(EOCompetence.DOMAINE_KEY)).lastObject();
			if (domaines != null && domaine != null) {
				tmp.addObjectsFromArray(EOMatiere.fetchMatieresFromDomaine(ec,
						domaines, wocomponent().mySession().TODAY));
			} else {
				if (selectionDomaine() != null)
					tmp.addObjectsFromArray(EOMatiere.fetchMatieresFromDomaine(
							ec, selectionDomaine(),
							wocomponent().mySession().TODAY));
			}

			EOMatiere uneMatiere = leCentreMatiere();
			if (uneMatiere != null && !tmp.contains(uneMatiere)) {
				tmp.addObject(uneMatiere);
			}
			if (tmp != null && tmp.count() > 0) {
				ERXArrayUtilities
						.sortArrayWithKey(tmp, EOMatiere.LBL_COURT_KEY);
				lesMatieres = ERXArrayUtilities.arrayWithoutDuplicates(tmp);
			}
		}
		return lesMatieres;
	}

	/**
	 * @param lesDomaines
	 *            the lesDomaines to set
	 */
	public void setLesDomaines(NSArray<EODomaine> lesDomaines) {
		this.lesDomaines = lesDomaines;
	}

	/**
	 * @return the itemDomaine
	 */
	public EODomaine getItemDomaine() {
		return itemDomaine;
	}

	/**
	 * @param itemDomaine
	 *            the itemDomaine to set
	 */
	public void setItemDomaine(EODomaine itemDomaine) {
		this.itemDomaine = itemDomaine;
	}

	/**
	 * @return the selectionDomaine
	 */
	public EODomaine selectionDomaine() {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			selectionDomaine = competence.domaine();
		}
		return selectionDomaine;
	}

	/**
	 * @param selectionDomaine
	 *            the selectionDomaine to set
	 */
	public void setSelectionDomaine(EODomaine selectionDomaine) {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			competence.setDomaineRelationship(selectionDomaine);
			lesDomaines = null;
		}
		this.selectionDomaine = selectionDomaine;
	}

	/**
	 * @return the lesPersonnes
	 */
	public NSArray lesPersonnes() {
		return lesPersonnes;
	}

	/**
	 * @param lesPersonnes
	 *            the lesPersonnes to set
	 */
	public void setLesPersonnes(NSArray lesPersonnes) {
		this.lesPersonnes = lesPersonnes;
	}

	/**
	 * @return the unePersonne
	 */
	public EOPersonne unePersonne() {
		return unePersonne;
	}

	/**
	 * @param unePersonne
	 *            the unePersonne to set
	 */
	public void setUnePersonne(EOPersonne unePersonne) {
		this.unePersonne = unePersonne;
	}

	/**
	 * @return the personneDomaine
	 */
	public EOPersonne personneDomaine() {
		return personneDomaine;
	}

	/**
	 * @param personneDomaine
	 *            the personneDomaine to set
	 */
	public void setPersonneDomaine(EOPersonne personneDomaine) {
		this.personneDomaine = personneDomaine;
	}

	/**
	 * @return the isStructure
	 */
	public boolean isStructure() {
		return isStructure;
	}

	/**
	 * @param isStructure
	 *            the isStructure to set
	 */
	public void setStructure(boolean isStructure) {
		this.isStructure = isStructure;
	}

	/**
	 * @return the isIndividu
	 */
	public boolean isIndividu() {
		return isIndividu;
	}

	/**
	 * @param isIndividu
	 *            the isIndividu to set
	 */
	public void setIndividu(boolean isIndividu) {
		this.isIndividu = isIndividu;
	}

	/**
	 * @return the estStructure
	 */
	public String estStructure() {
		return estStructure;
	}

	/**
	 * @param estStructure
	 *            the estStructure to set
	 */
	public void setEstStructure(String estStructure) {
		this.estStructure = estStructure;
	}

	/**
	 * @return the estIndividu
	 */
	public String estIndividu() {
		return estIndividu;
	}

	/**
	 * @param estIndividu
	 *            the estIndividu to set
	 */
	public void setEstIndividu(String estIndividu) {
		this.estIndividu = estIndividu;
	}

	/**
	 * @return the tiersTypePers
	 */
	public boolean tiersTypePers() {
		return tiersTypePers;
	}

	/**
	 * @param tiersTypePers
	 *            the tiersTypePers to set
	 */
	public void setTiersTypePers(boolean tiersTypePers) {
		this.tiersTypePers = tiersTypePers;
	}

	public WOActionResults actComp() {
		EOCompetence competence = wocomponent().competence();
		if (competence != null && selectionDomaine() != null) {
			wocomponent().mySession().setListTiers(null);
			wocomponent().setTiersDisplayGroup(new ERXDisplayGroup());
			wocomponent().setTiersTypeIndiv(selectionDomaine().isIndividu());
			wocomponent().setTiersTypeStr(selectionDomaine().isStructure());
			wocomponent().setTiersTypeUv(selectionDomaine().isUv());
			if (selectionDomaine().isPartenaire()) {
				wocomponent().setTitre("Recherche d'entreprises");
				setTiersTypePers(selectionDomaine().isPartenaire());
				setTilteLbale("Ajouter un ou plusieurs partenaire(s) ˆ la liste");
			}
			if (selectionDomaine().isEnseignement()) {
				wocomponent().setTitre("Recherche des Uvs");
				//setTiersTypePers(selectionDomaine().isEnseignement());
				setTiersTypePers(false);
				setTilteLbale("Ajouter un ou plusieurs uv(s) ˆ la liste");
			}
			if (selectionDomaine().isChercheur()) {
				wocomponent().setQualifierForIndividu(
						wocomponent().qualifierIndividusChercheurs());
				wocomponent().setTitre("Recherche de chercheurs");
				setTiersTypePers(selectionDomaine().isChercheur());
				setTilteLbale("Ajouter un ou plusieurs chercheur(s) ˆ la liste");
			}
			if (selectionDomaine().isEnseignant()) {
				wocomponent().setQualifierForIndividu(
						wocomponent().qualifierIndividusEnseignant());
				wocomponent().setTitre("Recherche d'enseignants");
				setTiersTypePers(selectionDomaine().isEnseignant());
				setTilteLbale("Ajouter un ou plusieurs enseignant(s) ˆ la liste");
			}
			wocomponent().mySession()
					.setDomaineSelectionnee(selectionDomaine());
			setLeCentreMatiere(null);
			setLeCentreTerrain(null);
			setLeCentreCapacite(null);
			setLesMatieres(null);
			setLesTerrains(null);
			setLesCapacites(null);
			wocomponent().mySession().setListTiers(null);
			wocomponent().setTiersDisplayGroup(new ERXDisplayGroup());
			lesMatieres();
			lesTerrains();
			lesCapacites();
			wocomponent().tiersDisplayGroup();
			wocomponent().mySession().setListCaps(null);
			wocomponent().setSelectedDomaine(selectionDomaine());
		}
		return null;
	}

	/**
	 * @param lesMatieres
	 *            the lesMatieres to set
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
	 * @param uneMatiere
	 *            the uneMatiere to set
	 */
	public void setUneMatiere(EOMatiere uneMatiere) {
		this.uneMatiere = uneMatiere;
	}

	/**
	 * @return the leCentreMatiere
	 */
	public EOMatiere leCentreMatiere() {
		if (leCentreMatiere == null) {
			EOCompetence competence = wocomponent().competence();
			ec = competence.editingContext();
			if (competence != null && competence.matiere() != null) {
				Integer supId = competence.matiere().matIdSup();
				EOMatiere pere = EOMatiere.PerePourNumero(ec, supId,
						wocomponent().mySession().TODAY);
				if (pere == null) {
					leCentreMatiere = competence.matiere();
				} else {
					leCentreMatiere = pere;
					leSousCentreMatiere = competence.matiere();
					wocomponent().refreshSousMat();
				}
			}
		}
		return leCentreMatiere;
	}

	/**
	 * @param leCentreMatiere
	 *            the leCentreMatiere to set
	 */
	public void setLeCentreMatiere(EOMatiere leCentreMatiere) {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			competence.setMatiereRelationship(leCentreMatiere);
			lesMatieres = null;
		}
		this.leCentreMatiere = leCentreMatiere;
	}

	/**
	 * @return the lesSousMatieres
	 */
	public NSArray<EOMatiere> getLesSousMatieres() {
		return lesSousMatieres;
	}

	/**
	 * @param lesSousMatieres
	 *            the lesSousMatieres to set
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
	 * @param uneSousMatiere
	 *            the uneSousMatiere to set
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
	 * @param leSousCentreMatiere
	 *            the leSousCentreMatiere to set
	 */
	public void setLeSousCentreMatiere(EOMatiere leSousCentreMatiere) {
		this.leSousCentreMatiere = leSousCentreMatiere;
	}

	// Matiere et S/Matiere
	public void actMatComp() {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			// EODomaine dom =
			// this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setMatiereRelationship(this.leCentreMatiere);
		}
	}

	public WOActionResults actSmatComp() {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			// EODomaine dom =
			// this.selectionDomaine.localInstanceIn(competence.editingContext());
			competence.setMatiereRelationship(this.leSousCentreMatiere);
		}
		return null;
	}

	EOEditingContext ec = null;

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
			// NSArray<EORepartCompCapacite> rccs = null;
			NSArray<EORepartDomCapacite> rdcs = null;
			EODomaine domaine = wocomponent().competence().domaine();
			if (domaine != null) {
				competences = EOUtilities.objectsMatchingKeyAndValue(ec,
						EOCompetence.ENTITY_NAME, EOCompetence.DOMAINE_KEY,
						wocomponent().competence().domaine());
			} else {
				competences = EOCompetence.fetchCompetences(ec, wocomponent()
						.mySession().TODAY);
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
				if (wocomponent().selectedDomaine() != null)
					tmp.addObjectsFromArray(EOCapacite
							.fetchCapacitesFromDomaine(ec, wocomponent()
									.selectedDomaine(), wocomponent()
									.mySession().TODAY));
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
	 * @param lesCapacites
	 *            the lesCapacites to set
	 */
	public void setLesCapacites(NSArray lesCapacites) {
		this.lesCapacites = lesCapacites;
	}

	// Terrain

	/**
	 * @return the lesTerrains
	 */
	public NSArray lesTerrains() {
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
				competences = EOCompetence.fetchCompetences(ec, wocomponent()
						.mySession().TODAY);
			}

			EODomaine domaines = ((NSArray<EODomaine>) competences
					.valueForKey(EOCompetence.DOMAINE_KEY)).lastObject();
			if (domaines != null && domaine != null) {
				tmp.addObjectsFromArray(EOTerrain.fetchTerrainsFromDomaine(ec,
						domaines, wocomponent().mySession().TODAY));
			} else {
				if (wocomponent().selectedDomaine() != null)
					tmp.addObjectsFromArray(EOTerrain.fetchTerrainsFromDomaine(
							ec, wocomponent().selectedDomaine(), wocomponent()
									.mySession().TODAY));
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
		if (leCentreTerrain == null) {
			EOCompetence competence = wocomponent().competence();
			ec = competence.editingContext();
			if (competence != null && competence.terrain() != null) {
				Integer supId = competence.terrain().terrIdSup();
				EOTerrain pere = EOTerrain.perePourNumero(ec, supId,
						wocomponent().mySession().TODAY);
				if (pere == null) {
					leCentreTerrain = competence.terrain();
				} else {
					leCentreTerrain = pere;
					leSousCentreTerrain = competence.terrain();
					wocomponent().refreshSousTerr();
				}
			}
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
		if (leCentreCapacite == null) {
			EOCompetence competence = wocomponent().competence();
			if (competence != null) {
				leCentreCapacite = competence.capacite();
			}
		}
		return leCentreCapacite;
	}

	/**
	 * @param leCentreCapacite
	 *            the leCentreCapacite to set
	 */
	public void setLeCentreCapacite(EOCapacite leCentreCapacite) {
		EOCompetence competence = wocomponent().competence();
		if (competence != null) {
			competence.setCapaciteRelationship(leCentreCapacite);
			lesCapacites = null;
		}
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

	/**
	 * @return the tilteLbale
	 */
	public String tilteLbale() {
		return tilteLbale;
	}

	/**
	 * @param tilteLbale the tilteLbale to set
	 */
	public void setTilteLbale(String tilteLbale) {
		this.tilteLbale = tilteLbale;
	}
}
