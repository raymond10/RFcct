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

import org.cocktail.fwkcktljefyadmin.common.metier.EOUtilisateur;
import org.utt.fwkuttcompetences.serveur.exception.FactoryException;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTypeTiers;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.Filtre;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.RechercheCompetenceService;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.ResultatRechercheBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.TiersCompetenceBean;
import org.utt.fwkuttcompetences.serveur.utils.FinderCompetence;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCapacite;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCompetence;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryDomaine;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryMatiere;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryTerrain;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryTypeTier;
import org.utt.rfcct.serveur.components.controlers.AccueilCtrl;

import com.google.inject.Inject;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEditingContext.EditingContextEvent;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXS;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 18 mars 2013
 */
@SuppressWarnings("all")
public class Accueil extends BaseComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6268797678243725388L;

	private String codeClassification;
	private Integer numeroCompetence;
	private Boolean isOpenFenetreException = Boolean.FALSE;
	
	private ERXDisplayGroup<ResultatRechercheBean> dgCompetences;
	// private NSDictionary<String, Object> currentCompetence;
	private ResultatRechercheBean currentCompetence;
	@Inject
	private RechercheCompetenceService rechercheCompetenceService;
	private Filtre filtre;
	private EOUtilisateur appUtilisateur;
	private AccueilCtrl ctrl;
	private Boolean creerUneCompetence = null;
	private Boolean accessBackOffice = null;
	private Boolean rechercheAvance = null;

	public Accueil(WOContext context) {
		super(context);
		NSLog.out.appendln("Accueil");
		filtre = new Filtre();
		ctrl = new AccueilCtrl(this);
	}

	/**
	 * @return the ctrl
	 */
	public AccueilCtrl ctrl() {
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(AccueilCtrl ctrl) {
		this.ctrl = ctrl;
	}

	/**
	 * @return the currentCompetence
	 */
	public ResultatRechercheBean getCurrentCompetence() {
		return currentCompetence;
	}

	/**
	 * @param currentCompetence
	 *            the currentCompetence to set
	 */
	public void setCurrentCompetence(ResultatRechercheBean currentCompetence) {
		this.currentCompetence = currentCompetence;
	}

	/**
	 * @return the isOpenFenetreException
	 */
	public Boolean isOpenFenetreException() {
		return isOpenFenetreException;
	}

	/**
	 * @param isOpenFenetreException
	 *            the isOpenFenetreException to set
	 */
	public void setIsOpenFenetreException(Boolean isOpenFenetreException) {
		this.isOpenFenetreException = isOpenFenetreException;
	}

	/**
	 * @return the codeClassification
	 */
	public String getCodeClassification() {
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
	 * @return the numeroCompetence
	 */
	public Integer numeroCompetence() {
		return numeroCompetence;
	}

	/**
	 * @param numeroCompetence
	 *            the numeroCompetence to set
	 */
	public void setNumeroCompetence(Integer numeroCompetence) {
		this.numeroCompetence = numeroCompetence;
	}

	public ERXDisplayGroup<ResultatRechercheBean> dgCompetences() {
		if (dgCompetences == null) {
			dgCompetences = new ERXDisplayGroup<ResultatRechercheBean>();
			dgCompetences.setSelectsFirstObjectAfterFetch(false);
			boolean voirToutes = peutAdminRfcct() || droitTout();
			NSArray<ResultatRechercheBean> resultats = rechercheCompetenceService
					.competencesForUtilisateur(edc(), persId(), voirToutes);
			/*
			 * NSMutableDictionary<String, Object> bindings = new
			 * NSMutableDictionary<String, Object>();
			 * bindings.setObjectForKey(persId(), "persId");
			 * bindings.setObjectForKey(getCodeClassification(),
			 * "codeClassification"); bindings.setObjectForKey(peutAdminRfcct(),
			 * "admin"); bindings.setObjectForKey(droitTout(), "tous");
			 * NSArray<NSDictionary<Object, Object>> resultats =
			 * FinderCompetence .getRawRowCompetenceForUser(edc(), bindings);
			 */
			// FinderCompetence.lesDicCompetences(edc(), ctrl.competences());
			dgCompetences.setSortOrderings(ERXS
					.descs(ResultatRechercheBean.NUMERO_KEY));
			dgCompetences.setObjectArray(resultats);
		}
		return dgCompetences;
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

	// Consulter une competence
	public WOActionResults consulterCompetence() {
		Competence nextPage = null;
		ResultatRechercheBean resultat = dgCompetences().selectedObject();
		if (resultat != null) {
			EOCompetence competence = EOCompetence.competencePourNumero(edc(), resultat.getCompId());
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

	// Creer une competence pour des tiers
	public WOActionResults creerCompetence() {
		resetSessionState();
		GestionCompetence nextPage = (GestionCompetence) pageWithName(GestionCompetence.class
				.getName());
		FactoryCompetence fc = new FactoryCompetence(edc());
		try {
			EOCompetence competence = fc.creerCompetenceVierge(persId());
			competence.setTemMulti("O");
			mySession().setCompetence(competence);
			mySession().setTemcreate(true);
			mySession().setTemupdate(false);
			nextPage.setCompetence(competence);
		} catch (Exception e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
		return nextPage;
	}

	// Creer des compétences pour un tier
	public WOActionResults creerTiersCompetences() {
		resetSessionState();
		GestionCompetencesTiers nextPage = (GestionCompetencesTiers) pageWithName(GestionCompetencesTiers.class
				.getName());
		FactoryCompetence fc = new FactoryCompetence(edc());
		try {
			EOCompetence competence = fc.creerCompetenceVierge(persId());
			mySession().setCompetence(competence);
			mySession().setTemcreate(true);
			mySession().setTemupdate(false);
			nextPage.setCompetence(competence);
		} catch (Exception e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
		return nextPage;
	}

	public boolean isCreerUneCompetenceDisabled() {
		if (creerUneCompetence == null) {
			creerUneCompetence = peutCreerCompetence() || droitTout();
		}
		return !creerUneCompetence;
	}

	// Acceder au backOffice
	public WOActionResults accederAdmin() {
		resetSessionState();
		GestionAdministration adminPage = (GestionAdministration) pageWithName(GestionAdministration.class
				.getName());
		FactoryTypeTier ftt = new FactoryTypeTier(edc());
		FactoryDomaine fd = new FactoryDomaine(edc());
		FactoryMatiere fm = new FactoryMatiere(edc());
		FactoryTerrain ft = new FactoryTerrain(edc());
		FactoryCapacite fp = new FactoryCapacite(edc());
		try {
			EOTypeTiers typeTier = ftt.createTypeTierVierge(persId());
			EODomaine domaine = fd.creerDomaineVierge(persId());
			EOMatiere matiere = fm.creerMatiereVierge(persId());
			EOTerrain terrain = ft.creerTerrainVierge(persId());
			EOCapacite capacite = fp.creerCapaciteVierge(persId());
			mySession().setTypeTier(typeTier);
			adminPage.setTypeTier(typeTier);
			mySession().setDomaine(domaine);
			adminPage.setDomaine(domaine);
			mySession().setMatiere(matiere);
			adminPage.setMatiere(matiere);
			mySession().setTerrain(terrain);
			adminPage.setTerrain(terrain);
			mySession().setCapacite(capacite);
			adminPage.setCapacite(capacite);
		} catch (Exception e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
		return adminPage;
	}

	public boolean isAccessBackOfficeDisabled() {
		if (accessBackOffice == null)
			accessBackOffice = (droitTout())
					|| (peutAdminRfcct() && accesDomaineEnseignement())
					|| (peutAdminRfcct() && accesDomaineRecherche())
					|| (peutAdminRfcct() && accesDomaineEntreprise())
					|| (peutAdminRfcct() && knowDomaineEnseignement())
					|| (peutAdminRfcct() && knowDomaineRecherche());
		return !accessBackOffice;
	}
	
	public WOActionResults treeGroupe() {
		mySession().reset();
		GestionGroupe nextPage = (GestionGroupe) pageWithName(GestionGroupe.class.getName());
		return nextPage;
	}

	// Recherche avancee
	public WOActionResults afficherRechercheAvancee() {
		Recherche nextPage = (Recherche) pageWithName(Recherche.class.getName());
		nextPage.rechercher();
		return nextPage;
	}

	public boolean isRechercheAvanceDisabled() {
		if (rechercheAvance == null)
			rechercheAvance = peutConsulterCompetence() || droitTout();
		return !rechercheAvance;
	}

	// Editions
	public WOActionResults afficherLesEditions() {
	      GestionEditions gestionEditions = (GestionEditions) pageWithName(GestionEditions.class.getName());
	      return gestionEditions;
	}

	// Outils
	public WOActionResults afficherLesOutils() {
		return null;
	}

	// Css validite
	public String cssClassForCurrentCompetence() {
		String css = "";
		/*
		 * Object date = currentCompetence.objectForKey("DATEVALID"); if (date
		 * != null && !date.equals(NSKeyValueCoding.NullValue)) { css = "valid";
		 * }
		 */
		return css;
	}

	// Rechercher une competence
	private WOComponent traiterResultatsRechercheSimple(
			NSArray<ResultatRechercheBean> resultats) {
		Competence nextPage = null;
		if (resultats != null && resultats.size() == 1) {
			EOCompetence competence = (EOCompetence) EOUtilities
					.objectWithPrimaryKeyValue(edc(), EOCompetence.ENTITY_NAME,
							resultats.lastObject().getCompId());
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
		} else {
			mySession().setMessageErreur("Aucune competence trouvée");
		}
		return nextPage;
	}

	public WOActionResults rechercherUneCompetence() {
		WOComponent nextPage = null;
		if (filtre.getFiltreCompId() != null) {
			NSArray<ResultatRechercheBean> resultats = rechercheCompetenceService
					.filtrerCompetences(dgCompetences().allObjects(), filtre);
			nextPage = traiterResultatsRechercheSimple(resultats);
		} else {
			mySession().setMessageErreur("Vous devez saisir un numéro");
		}
		return nextPage;
	}

	public WOActionResults rechercherUneCompetenceOld() {
		Competence nextPage = (Competence) pageWithName(Competence.class
				.getName());
		if (numeroCompetence() != null) {
			FinderCompetence fc = new FinderCompetence(edc());
			fc.setCompetenceNumero(numeroCompetence());
			NSArray result;
			EOCompetence competence = null;
			try {
				result = fc.find();
				competence = (EOCompetence) result.lastObject();
			} catch (FactoryException e) {
				mySession().setMessageErreur(e.getMessage());
				e.printStackTrace();
			}
			if (competence != null && competence.isConsultablePar(user())) {
				nextPage.setCompetence(competence);
				mySession().setPageCompetence(nextPage);
			} else {
				if (competence == null) {
					mySession().setMessageErreur("Aucune competence trouvée");
				} else {
					mySession()
							.setMessageErreur(
									"Vous n'ètes pas autorisé(e) à consulter cette competence");
				}
				nextPage = null;
			}
		} else {
			mySession().setMessageErreur("Vous devez saisir un numéro");
			nextPage = null;
		}
		return nextPage;
	}

	public WOComponent retourAccueil() {
		Accueil accueil = (Accueil) mySession().getSavedPageWithName(
				Accueil.class.getName());
		mySession().reset();
		accueil.setIsOpenFenetreException(Boolean.FALSE);
		return accueil;
	}

	/**
	 * @return the appUtilisateur
	 */
	public EOUtilisateur getAppUtilisateur() {
		return appUtilisateur;
	}

	/**
	 * @param appUtilisateur
	 *            the appUtilisateur to set
	 */
	public void setAppUtilisateur(EOUtilisateur appUtilisateur) {
		this.appUtilisateur = appUtilisateur;
		if (mySession().utilisateur() == null) {
			mySession().setUtilisateur(appUtilisateur);
		}
	}

	private void resetSessionState() {
		mySession().setIndexModuleActifCreationCompetence(0);
		mySession().setIndexModuleActifGestionAdministration(0);
	}

	public Filtre getFiltre() {
		return filtre;
	}
}
