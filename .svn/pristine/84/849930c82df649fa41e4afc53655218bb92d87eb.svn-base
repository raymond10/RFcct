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

import java.math.BigDecimal;

import org.cocktail.fwkcktlpersonne.common.metier.EOStructure;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.CapaciteCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.DomaineCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.Filtre;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.MatiereCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.RechercheCompetenceService;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.ResultatRechercheBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.TerrainCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.TiersCompetenceBean;
import org.utt.fwkuttcompetences.serveur.utils.FinderCompetence;

import com.google.inject.Inject;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXS;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 13 mai 2013
 */
@SuppressWarnings("all")
public class Recherche extends BaseComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7513165839102403745L;
	protected NSArray<EOCompetence> competences;
	private ERXDisplayGroup<ResultatRechercheBean> dgCompetences;
	private NSMutableDictionary dicoCompetences;
	private ResultatRechercheBean unDicoCompetence;
	private NSMutableDictionary bindings;
	private String codeClassification;

	// Entrees de la recherche
	private DomaineCompetenceBean currentDomaineDefinition;
	private NSArray<DomaineCompetenceBean> lesDomaines;
	private MatiereCompetenceBean currentMatiere;
	private NSArray<MatiereCompetenceBean> lesMatieres;
	private TerrainCompetenceBean currentTerrain;
	private NSArray<TerrainCompetenceBean> lesTerrains;
	private CapaciteCompetenceBean currentCapacite;
	private NSArray<CapaciteCompetenceBean> lesCapacites;

	@Inject
	private RechercheCompetenceService rechercheCompetenceService;
	private NSArray<ResultatRechercheBean> allResultats;
	private Filtre filtre;

	public Recherche(WOContext context) {
		super(context);
		NSLog.out.appendln("Recherche");
		filtre = new Filtre();
		// rechercher();
		// setFiltres();
	}

	public WOActionResults rechercher() {
		if (allResultats == null) {
			boolean voirToutes = peutAdminRfcct() || droitTout();
			allResultats = rechercheCompetenceService
					.competencesForUtilisateur(edc(), persId(), voirToutes);
		}
		NSArray<ResultatRechercheBean> resultatsFiltres = rechercheCompetenceService
				.filtrerCompetences(allResultats, filtre);
		dgCompetences().setObjectArray(resultatsFiltres);
		return null;
	}

	public WOActionResults nettoyer() {
		filtre = new Filtre();
		return rechercher();
	}

	// Tri sur le tableau de competence
	public String getNumeroSortKeyPath() {
		return ResultatRechercheBean.NUMERO_KEY;
	}

	public String getCommentSortKeyPath() {
		return ResultatRechercheBean.COMMENTAIRE_KEY;
	}

	public String getTierLibelleSortKeyPath() {
		return ResultatRechercheBean.TIER_KEY + "."
				+ TiersCompetenceBean.TIER_LIBELLE;
	}

	public WOActionResults rechercherOld() {
		NSArray resultats = null;
		// Traitement des bindings
		if (bindings().containsKey("domaine")) {
			NSDictionary<String, Object> domaine = (NSDictionary<String, Object>) bindings
					.objectForKey("domaine");
			Integer domId = ((BigDecimal) domaine.objectForKey("DOM_ID"))
					.intValue();
			bindings().setObjectForKey(domId, "domId");
		} else {
			bindings().removeObjectForKey("domId");
		}
		if (bindings().containsKey("matiere")) {
			NSDictionary<String, Object> matiere = (NSDictionary<String, Object>) bindings
					.objectForKey("matiere");
			Integer matId = ((BigDecimal) matiere.objectForKey("MAT_ID"))
					.intValue();
			bindings().setObjectForKey(matId, "matId");
		} else {
			bindings().removeObjectForKey("matId");
		}
		if (bindings().containsKey("terrain")) {
			NSDictionary<String, Object> terrain = (NSDictionary<String, Object>) bindings
					.objectForKey("terrain");
			Integer terrId = ((BigDecimal) terrain.objectForKey("TERR_ID"))
					.intValue();
			bindings().setObjectForKey(terrId, "terrId");
		} else {
			bindings().removeObjectForKey("terrId");
		}
		if (bindings().containsKey("capacite")) {
			NSDictionary<String, Object> capacite = (NSDictionary<String, Object>) bindings
					.objectForKey("capacite");
			Integer capId = ((BigDecimal) capacite.objectForKey("CAP_ID"))
					.intValue();
			bindings().setObjectForKey(capId, "capId");
		} else {
			bindings().removeObjectForKey("capId");
		}
		bindings().setObjectForKey(peutAdminRfcct(), "admin");
		bindings().setObjectForKey(droitTout(), "tous");
		resultats = FinderCompetence.getRawRowCompetenceForUser(edc(),
				bindings());
		dgCompetences().setObjectArray(resultats);
		return null;
	}

	private void setFiltres() {
		// On met à jour les filtres
		lesDomaines();
		lesMatieres();
		lesTerrains();
		lesCapacites();
	}

	public WOActionResults submit() {
		return null;
	}

	/**
	 * @return the dgCompetences
	 */
	public ERXDisplayGroup<ResultatRechercheBean> dgCompetences() {
		if (dgCompetences == null) {
			dgCompetences = new ERXDisplayGroup<ResultatRechercheBean>();
			dgCompetences.setSortOrderings(ERXS
					.descs(ResultatRechercheBean.NUMERO_KEY));
			dgCompetences.setSelectsFirstObjectAfterFetch(false);
		}
		return dgCompetences;
	}

	/**
	 * @param dgCompetences
	 *            the dgCompetences to set
	 */
	public void setDgCompetences(
			ERXDisplayGroup<ResultatRechercheBean> dgCompetences) {
		this.dgCompetences = dgCompetences;
	}

	/**
	 * @return the dicoCompetences
	 */
	public NSMutableDictionary dicoCompetences() {
		return dicoCompetences;
	}

	/**
	 * @param dicoCompetences
	 *            the dicoCompetences to set
	 */
	public void setDicoCompetences(NSMutableDictionary dicoCompetences) {
		this.dicoCompetences = dicoCompetences;
	}

	/**
	 * @return the unDicoCompetence
	 */
	public ResultatRechercheBean unDicoCompetence() {
		return unDicoCompetence;
	}

	/**
	 * @param unDicoCompetence
	 *            the unDicoCompetence to set
	 */
	public void setUnDicoCompetence(ResultatRechercheBean unDicoCompetence) {
		this.unDicoCompetence = unDicoCompetence;
	}

	/**
	 * @return the codeClassification
	 */
	public String codeClassification() {
		if (codeClassification == null)
			codeClassification = "COMP";
		return codeClassification;
	}

	/**
	 * @param codeClassification
	 *            the codeClassification to set
	 */
	public void setCodeClassification(String codeClassification) {
		this.codeClassification = codeClassification;
	}

	/**
	 * @return the bindings
	 */
	public NSMutableDictionary bindings() {
		if (bindings == null) {
			bindings = new NSMutableDictionary();
			bindings.setObjectForKey(persId(), "persId");
			bindings.setObjectForKey(codeClassification(), "codeClassification");
		}
		return bindings;
	}

	/**
	 * @param bindings
	 *            the bindings to set
	 */
	public void setBindings(NSMutableDictionary bindings) {
		this.bindings = bindings;
	}

	/**
	 * @return the currentDomaineDefinition
	 */
	public DomaineCompetenceBean currentDomaineDefinition() {
		return currentDomaineDefinition;
	}

	/**
	 * @param currentDomaineDefinition
	 *            the currentDomaineDefinition to set
	 */
	public void setCurrentDomaineDefinition(
			DomaineCompetenceBean currentDomaineDefinition) {
		this.currentDomaineDefinition = currentDomaineDefinition;
	}

	/**
	 * @return the lesDomaines
	 */
	public NSArray<DomaineCompetenceBean> lesDomaines() {
		// On va chercher les domaines correspondants à toutes les compétences
		if (lesDomaines == null)
			lesDomaines = rechercheCompetenceService.domaineFromResultats(dgCompetences().allObjects());
		return lesDomaines;
	}

	/**
	 * @param lesDomaines
	 *            the lesDomaines to set
	 */
	public void setLesDomaines(NSArray<DomaineCompetenceBean> lesDomaines) {
		this.lesDomaines = lesDomaines;
	}

	/**
	 * @return the currentMatiere
	 */
	public MatiereCompetenceBean currentMatiere() {
		return currentMatiere;
	}

	/**
	 * @param currentMatiere
	 *            the currentMatiere to set
	 */
	public void setCurrentMatiere(MatiereCompetenceBean currentMatiere) {
		this.currentMatiere = currentMatiere;
	}

	/**
	 * @return the lesMatieres
	 */
	public NSArray<MatiereCompetenceBean> lesMatieres() {
		if (lesMatieres == null)
			lesMatieres = rechercheCompetenceService.matiereFromResultats(dgCompetences().allObjects());
		return lesMatieres;
	}

	/**
	 * @param lesMatieres
	 *            the lesMatieres to set
	 */
	public void setLesMatieres(NSArray<MatiereCompetenceBean> lesMatieres) {
		this.lesMatieres = lesMatieres;
	}

	/**
	 * @return the currentTerrain
	 */
	public TerrainCompetenceBean currentTerrain() {
		return currentTerrain;
	}

	/**
	 * @param currentTerrain
	 *            the currentTerrain to set
	 */
	public void setCurrentTerrain(TerrainCompetenceBean currentTerrain) {
		this.currentTerrain = currentTerrain;
	}

	/**
	 * @return the lesTerrains
	 */
	public NSArray<TerrainCompetenceBean> lesTerrains() {
		if (lesTerrains == null)
			lesTerrains = rechercheCompetenceService.terrainFromResultats(dgCompetences().allObjects());
		return lesTerrains;
	}

	/**
	 * @param lesTerrains
	 *            the lesTerrains to set
	 */
	public void setLesTerrains(NSArray<TerrainCompetenceBean> lesTerrains) {
		this.lesTerrains = lesTerrains;
	}

	/**
	 * @return the currentCapacite
	 */
	public CapaciteCompetenceBean currentCapacite() {
		return currentCapacite;
	}

	/**
	 * @param currentCapacite
	 *            the currentCapacite to set
	 */
	public void setCurrentCapacite(CapaciteCompetenceBean currentCapacite) {
		this.currentCapacite = currentCapacite;
	}

	/**
	 * @return the lesCapacites
	 */
	public NSArray<CapaciteCompetenceBean> lesCapacites() {
		if (lesCapacites == null)
			lesCapacites = rechercheCompetenceService.capaciteFromResultats(dgCompetences().allObjects());
		return lesCapacites;
	}

	/**
	 * @param lesCapacites
	 *            the lesCapacites to set
	 */
	public void setLesCapacites(NSArray<CapaciteCompetenceBean> lesCapacites) {
		this.lesCapacites = lesCapacites;
	}
/*
	public String labelForCurrentDomaine() {
		// TODO
		return (String) currentDomaineDefinition().objectForKey(
				EODomaine.LBL_LONG_COLKEY);
	}

	public String labelForCurrentMatiere() {
		// TODO
		return (String) currentMatiere()
				.objectForKey(EOMatiere.LBL_LONG_COLKEY);
	}

	public String labelForCurrentTerrain() {
		// TODO
		return (String) currentTerrain()
				.objectForKey(EOTerrain.LBL_LONG_COLKEY);
	}

	public String labelForCurrentCapacite() {
		// TODO
		return (String) currentCapacite().objectForKey(
				EOCapacite.LBL_LONG_COLKEY);
	}
*/
	public Filtre getFiltre() {
		return filtre;
	}

	// Css validite
	public String cssClassForUnDicoCompetence() {
		String css = "";
		/*Object date = unDicoCompetence.objectForKey("DATEVALID");
		if (date != null && !date.equals(NSKeyValueCoding.NullValue)) {
			css = "valid";
		}*/
		return css;
	}

	// Consulter une competence
	public WOActionResults consulterCompetence() {
		Competence nextPage = null;
		ResultatRechercheBean leDicoCompetence = dgCompetences()
				.selectedObject();
		if (leDicoCompetence != null) {
			EOCompetence competence = EOCompetence.competencePourNumero(edc(),
					leDicoCompetence.getCompId());
			if (competence != null && competence.isConsultablePar(user())) {
				nextPage = (Competence) pageWithName(Competence.class.getName());
				nextPage.setCompetence(competence);
				nextPage.setTemcreate(false);
				nextPage.setTemupdate(true);
				mySession().setPageCompetence(nextPage);
			} else {
				mySession()
						.setMessageErreur(
								"Vous n'ètes pas autorisé(e) à consulter cette competence");
			}
		}
		if(nextPage == null)
			return nextPage;
		ERXRedirect redirect = (ERXRedirect) pageWithName(ERXRedirect.class
				.getName());
		redirect.setComponent(nextPage);
		return redirect;
	}

	private NSArray<Integer> lesNumeros() {
		NSArray numeros = (NSArray) dgCompetences().allObjects().valueForKey(
				"COMP_ID");
		return numeros;
	}
}
