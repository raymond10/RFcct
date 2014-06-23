package org.utt.rfcct.serveur.components;

import org.cocktail.fwkcktljefyadmin.common.metier.EOUtilisateur;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTypeTiers;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOVDataRealCompTree;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.ResultatRechercheBean;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCapacite;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCompetence;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryDomaine;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryMatiere;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryTerrain;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryTypeTier;
import org.utt.rfcct.serveur.components.commons.EODataForGroupeSpec;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXRedirect;
import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 23 août 2013
 */
@SuppressWarnings("all")
public class GestionGroupe extends BaseComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1548360048092977383L;
	private EOEditingContext gestionGroupeEc = ERXEC.newEditingContext();
	private EOVDataRealCompTree dataTree;
	private NSDictionary<String, EOQualifier> filters;
	private Boolean isOpenFenetreException = Boolean.FALSE;

	private Boolean creerUneCompetence = null;
	private Boolean accessBackOffice = null;
	private Boolean rechercheAvance = null;
	private EOUtilisateur appUtilisateur;

	public GestionGroupe(WOContext context) {
		super(context);
		NSLog.out.appendln("GestionGroupe");
	}

	@Override
	public EOEditingContext edc() {
		return gestionGroupeEc;
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

	public WOComponent retourGestionGroupe() {
		GestionGroupe tree = (GestionGroupe) mySession().getSavedPageWithName(
				GestionGroupe.class.getName());
		mySession().reset();
		tree.setIsOpenFenetreException(Boolean.FALSE);
		return tree;
	}

	/**
	 * @return the dataTree
	 */
	public EOVDataRealCompTree getDataTree() {
		return dataTree;
	}

	/**
	 * @param dataTree
	 *            the dataTree to set
	 */
	public void setDataTree(EOVDataRealCompTree dataTree) {
		this.dataTree = dataTree;
	}

	public boolean unGroupeEstSelectionne() {
		return getDataTree() != null;
	}

	/**
	 * @return the filters
	 */
	public NSDictionary<String, EOQualifier> filters() {
		if (filters == null) {
			filters = 
				new NSMutableDictionary<String, EOQualifier>();
			EOQualifier qual = EODataForGroupeSpec.QUAL_GROUPES_DOMAINES;
			filters.put("Domaines", qual);
			qual = EODataForGroupeSpec.QUAL_GROUPES_COMPETENCES;
			filters.put("Compétences", qual);
			qual = EODataForGroupeSpec.QUAL_GROUPES_MATIERES;
			filters.put("Matières", qual);
			qual = EODataForGroupeSpec.QUAL_GROUPES_TERRAINS;
			filters.put("Terrains", qual);
			qual = EODataForGroupeSpec.QUAL_GROUPES_CAPACITES;
			filters.put("Capacités", qual);
		}
		return filters;
	}

	/**
	 * @param filters
	 *            the filters to set
	 */
	public void setFilters(NSDictionary<String, EOQualifier> filters) {
		this.filters = filters;
	}

	public WOActionResults onSelectGroupe() {
		edc().revert();
		Competence nextPage = null;
		if (getDataTree().competenceId() != null) {
			EOCompetence competence = EOCompetence.competencePourNumero(edc(), getDataTree().competenceId());
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

	public boolean isCreerUneCompetenceDisabled() {
		if (creerUneCompetence == null) {
			creerUneCompetence = peutCreerCompetence() || droitTout();
		}
		return !creerUneCompetence;
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

	public boolean isRechercheAvanceDisabled() {
		if (rechercheAvance == null)
			rechercheAvance = peutConsulterCompetence() || droitTout();
		return !rechercheAvance;
	}
	

    //Accueil
	public WOActionResults accueil() {
		mySession().reset();
		return pageWithName(Accueil.class.getName());
	}

	// Creer une competence pour des tiers
	public WOActionResults creerCompetence() {
		resetSessionState();
		GestionCompetence nextPage = (GestionCompetence) pageWithName(GestionCompetence.class
				.getName());
		FactoryCompetence fc = new FactoryCompetence(ERXEC.newEditingContext());
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
		FactoryCompetence fc = new FactoryCompetence(ERXEC.newEditingContext());
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

	// Acceder au backOffice
	public WOActionResults accederAdmin() {
		resetSessionState();
		GestionAdministration adminPage = (GestionAdministration) pageWithName(GestionAdministration.class
				.getName());
		FactoryTypeTier ftt = new FactoryTypeTier(ERXEC.newEditingContext());
		FactoryDomaine fd = new FactoryDomaine(ERXEC.newEditingContext());
		FactoryMatiere fm = new FactoryMatiere(ERXEC.newEditingContext());
		FactoryTerrain ft = new FactoryTerrain(ERXEC.newEditingContext());
		FactoryCapacite fp = new FactoryCapacite(ERXEC.newEditingContext());
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

	// Recherche avancee
	public WOActionResults afficherRechercheAvancee() {
		Recherche nextPage = (Recherche) pageWithName(Recherche.class.getName());
		nextPage.rechercher();
		return nextPage;
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

	private void resetSessionState() {
		mySession().setIndexModuleActifCreationCompetence(0);
		mySession().setIndexModuleActifGestionAdministration(0);
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
}