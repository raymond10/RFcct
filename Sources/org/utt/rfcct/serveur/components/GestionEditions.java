package org.utt.rfcct.serveur.components;

import org.cocktail.fwkcktlreportingguiajax.serveur.CktlAbstractReporterAjaxProgress;
import org.cocktail.reporting.CktlAbstractReporter;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.CapaciteCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.DomaineCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.Filtre;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.MatiereCompetenceBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.RechercheCompetenceService;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.ResultatRechercheBean;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.service.recherche.TerrainCompetenceBean;

import com.google.inject.Inject;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXS;

public class GestionEditions extends BaseComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5210504244128107089L;

	protected NSArray<EOCompetence> competences;
	private ERXDisplayGroup<ResultatRechercheBean> dgCompetences;
	private ReporterAjaxProgress reporterProgress;
	private String reportFilename;
	private CktlAbstractReporter reporter;
	private NSTimestamp dateFin;

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

	public GestionEditions(WOContext context) {
		super(context);
		NSLog.out.appendln("GestionEditions");
		filtre = new Filtre();
	}

	public WOActionResults EditerCompetence() {
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

	public WOActionResults resetFiltre() {
		filtre = new Filtre();
		return doNothing();
	}

	public Filtre getFiltre() {
		return filtre;
	}

	/**
	 * @return the reporterProgress
	 */
	public ReporterAjaxProgress getReporterProgress() {
		return reporterProgress;
	}

	/**
	 * @param reporterProgress
	 *            the reporterProgress to set
	 */
	public void setReporterProgress(ReporterAjaxProgress reporterProgress) {
		this.reporterProgress = reporterProgress;
	}

	/**
	 * @return the reportFilename
	 */
	public String getReportFilename() {
		return reportFilename;
	}

	/**
	 * @param reportFilename
	 *            the reportFilename to set
	 */
	public void setReportFilename(String reportFilename) {
		this.reportFilename = reportFilename;
	}

	/**
	 * @return the reporter
	 */
	public CktlAbstractReporter getReporter() {
		return reporter;
	}

	/**
	 * @param reporter
	 *            the reporter to set
	 */
	public void setReporter(CktlAbstractReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the dateFin
	 */
	public NSTimestamp dateFin() {
		return dateFin;
	}

	/**
	 * @param dateFin
	 *            the dateFin to set
	 */
	public void setDateFin(NSTimestamp dateFin) {
		this.dateFin = dateFin;
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
			lesDomaines = rechercheCompetenceService
					.domaineFromResultats(dgCompetences().allObjects());
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
			lesMatieres = rechercheCompetenceService
					.matiereFromResultats(dgCompetences().allObjects());
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
			lesTerrains = rechercheCompetenceService
					.terrainFromResultats(dgCompetences().allObjects());
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
			lesCapacites = rechercheCompetenceService
					.capaciteFromResultats(dgCompetences().allObjects());
		return lesCapacites;
	}

	/**
	 * @param lesCapacites
	 *            the lesCapacites to set
	 */
	public void setLesCapacites(NSArray<CapaciteCompetenceBean> lesCapacites) {
		this.lesCapacites = lesCapacites;
	}

	public static class ReporterAjaxProgress extends
			CktlAbstractReporterAjaxProgress implements
			org.cocktail.reporting.jrxml.IJrxmlReportListener {

		public ReporterAjaxProgress(int maximum) {
			super(maximum);
		}

	}
}