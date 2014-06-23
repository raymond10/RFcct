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

import java.util.Enumeration;

import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktldroitsutils.common.util.MyDateCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.utt.fwkuttcompetences.serveur.modele.grhum.EOIndividuUlr;
import org.utt.fwkuttcompetences.serveur.modele.grhum.EOStructureUlr;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartCompTiers;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCompetenceTiers;
import org.utt.fwkuttcompetences.serveur.utils.tiers.Tiers;
import org.utt.rfcct.serveur.components.Accueil;
import org.utt.rfcct.serveur.components.assistants.Assistant;
import org.utt.rfcct.serveur.components.controlers.ModuleCompetenceDomaineCtrl;
import org.utt.rfcct.serveur.components.windows.PersonneMultipleSrchPage;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.appserver.ERXRedirect;
import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 28 mars 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceDomaine extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3376948892086834536L;
	public ModuleCompetenceDomaineCtrl ctrl = null;
	private ERXDisplayGroup selectedPersonnesDisplayGroup;
	private ERXDisplayGroup tiersDisplayGroup = null;
	public static final String BDG_UPDATE_CONTAINER_ID = "updateContainerID";
	private String containerId;
	private Tiers currentTiers;
	private boolean isResetAjoutPersonnesDialog;
	private EOEditingContext editingContextForAddPerson;
	public static final String BINDING_TBV_HEIGHT = "tbvHeight";
	public static final String BINDING_TBV_WIDTH = "tbvWidth";
	public static final String BINDING_TBV_DISPLAY_FILTER = "displayFilter";
	private NSMutableDictionary<String, CktlAjaxTableViewColumn> _colonnesMap = new NSMutableDictionary<String, CktlAjaxTableViewColumn>();
	private NSArray<CktlAjaxTableViewColumn> colonnes;
	public static final String COL_TIERS_NUM_KEY = Tiers.TIERS_NUMERO_KEY;
	public static final String COL_TIERS_LIB_KEY = Tiers.TIERS_LIBELLE_KEY;
	public static final String LE_TIERS = "currentTiers.";
	private boolean tiersTypeIndiv = false;
	private boolean tiersTypeStr = false;
	private boolean tiersTypeUv = false;
	private EOQualifier qualifierForIndividu;
	private String titre;
	private Boolean tierActivate = null;
	private Boolean multi = null;
	private Boolean update = null;

	/**
	 * Tableau contenant les clés identifiant les colonnes à afficher par
	 * défaut.
	 */
	public static NSArray<String> DEFAULT_COLONNES_KEYS = new NSArray<String>(
			new String[] { COL_TIERS_NUM_KEY, COL_TIERS_LIB_KEY });

	public ModuleCompetenceDomaine(WOContext context) {
		super(context);
		NSLog.out.appendln("ModuleCompetenceDomaine");
		mySession().setDomaineSelectionnee(null);
	}

	// Id de la fenêtre de recherche de personne
	public String personnesDialogId() {
		return "PersDiag_" + uniqueDomId();
	}

	/**
	 * @return the ctrl
	 */
	public ModuleCompetenceDomaineCtrl ctrl() {
		if (ctrl == null)
			ctrl = new ModuleCompetenceDomaineCtrl(this);
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(ModuleCompetenceDomaineCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public void onPrecedent() {
	}

	public void onSuivant() {
		/*
		 * ModuleCompetenceCapacite next = (ModuleCompetenceCapacite)
		 * pageWithName(ModuleCompetenceCapacite.class.getName());
		 * if(fromPrecedent()) { next.reset();
		 * next.synchronizesVariablesWithBindings(); next.doNothing();
		 * next.submit(); }
		 */
	}

	public String updateContainerJsAferHide() {
		String func = "function() { TRTiersSelectionUpdate(); TRMatiereUpdate(); TRTerrainUpdate(); TRCapaciteUpdate();";
		String updateId = null;
		// stringValueForBinding(BDG_UPDATE_CONTAINER_ID, )
		if (updateId != null) {
			func = func + updateId + "Update(); }";
		} else {
			func = func + " }";
		}
		// assistant().isPrecedentDisabled();
		return func;
	}

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
						.arrayByAddingObject("Le domaine de compétence");
			}

			if (tiersDisplayGroup().allObjects().isEmpty()) {
				if (competence.domaine() == null) {
					failureMessages = failureMessages
							.arrayByAddingObject("Le(s) tiers pour la compétence");
				} else {
					failureMessages = failureMessages
							.arrayByAddingObject("Le(s) "
									+ competence.domaine().typeTiers()
									+ "(s) pour la compétence");
				}
			} else {
				if (mySession().tiersDg() == null)
					mySession().setTiersDg(new ERXDisplayGroup());
				mySession().tiersDg().setObjectArray(
						tiersDisplayGroup().allObjects());
			}

			if (competence.matiere() == null) {
				if (competence.domaine() == null) {
					failureMessages = failureMessages
							.arrayByAddingObject("La matière de la compétence");
				} else {
					failureMessages = failureMessages
							.arrayByAddingObject(competence.domaine()
									.lblMatiere() + " de la compétence");
				}
			}

			if (competence.terrain() == null) {
				if (competence.domaine() == null) {
					failureMessages = failureMessages
							.arrayByAddingObject("Le terrain de la compétence");
				} else {
					failureMessages = failureMessages
							.arrayByAddingObject(competence.domaine()
									.lblTerrain() + " de la compétence");
				}
			}

			if (competence.capacite() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("La capacité de la compétence");
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

	public boolean validerModifier() {
		boolean validate = false;
		EOCompetence competence = competence();
		EOEditingContext ec = competence.editingContext();
		NSArray<String> failureMessages = new NSArray<String>();
		if (competence != null
				&& EOUtilities.primaryKeyForObject(ec, competence) != null) {
			if (competence.domaine() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le domaine de compétence");
			}

			if (tiersDisplayGroup().allObjects().isEmpty()) {
				if (competence.domaine() == null) {
					failureMessages = failureMessages
							.arrayByAddingObject("Le(s) tiers pour la compétence");
				} else {
					failureMessages = failureMessages
							.arrayByAddingObject("Le(s) "
									+ competence.domaine().typeTiers()
									+ "(s) pour la compétence");
				}
			} else {
				if (mySession().tiersDg() == null)
					mySession().setTiersDg(new ERXDisplayGroup());
				mySession().tiersDg().setObjectArray(
						tiersDisplayGroup().allObjects());
			}

			if (competence.matiere() == null) {
				if (competence.domaine() == null) {
					failureMessages = failureMessages
							.arrayByAddingObject("La matière de la compétence");
				} else {
					failureMessages = failureMessages
							.arrayByAddingObject(competence.domaine()
									.lblMatiere() + " de la compétence");
				}
			}

			if (competence.terrain() == null) {
				if (competence.domaine() == null) {
					failureMessages = failureMessages
							.arrayByAddingObject("Le terrain de la compétence");
				} else {
					failureMessages = failureMessages
							.arrayByAddingObject(competence.domaine()
									.lblTerrain() + " de la compétence");
				}
			}

			if (competence.capacite() == null) {
				failureMessages = failureMessages
						.arrayByAddingObject("La capacité de compétence");
			}

			if (failureMessages.count() == 0) {
				validate = true;
				setFailureMessage(null);
			} else {
				setFailureMessage("Veuillez renseigner "
						+ failureMessages.componentsJoinedByString(", ") + ".");
			}

		} else {
			setFailureMessage("Veuillez renseigner les champs obligatoires (en rouge).");
		}
		if (validate)
			mySession().getUiMessages().removeAllObjects();
		return validate;
	}

	/**
	 * @return the isResetAjoutPersonnesDialog
	 */
	public boolean isResetAjoutPersonnesDialog() {
		return isResetAjoutPersonnesDialog;
	}

	/**
	 * @param isResetAjoutPersonnesDialog
	 *            the isResetAjoutPersonnesDialog to set
	 */
	public void setResetAjoutPersonnesDialog(boolean isResetAjoutPersonnesDialog) {
		this.isResetAjoutPersonnesDialog = isResetAjoutPersonnesDialog;
	}

	public ERXDisplayGroup selectedPersonnesDisplayGroup() {
		if (selectedPersonnesDisplayGroup == null)
			selectedPersonnesDisplayGroup = new ERXDisplayGroup();
		return selectedPersonnesDisplayGroup;
	}

	/**
	 * @param selectedPersonnesDisplayGroup
	 *            the selectedPersonnesDisplayGroup to set
	 */
	public void setSelectedPersonnesDisplayGroup(
			ERXDisplayGroup selectedPersonnesDisplayGroup) {
		this.selectedPersonnesDisplayGroup = selectedPersonnesDisplayGroup;
	}

	/**
	 * @return the tiersDisplayGroup
	 */
	public ERXDisplayGroup tiersDisplayGroup() {
		if (tiersDisplayGroup == null) {
			tiersDisplayGroup = new ERXDisplayGroup();
			tiersDisplayGroup.setDelegate(this);
			EOCompetence competence = competence();
			EOEditingContext ec = competence.editingContext();
			if (competence != null
					&& EOUtilities.primaryKeyForObject(ec, competence) != null) {
				NSArray<EORepartCompTiers> tierss = EORepartCompTiers
						.fetchCurrentTiersFromCompetence(ec, competence);
				mySession().setListTiers(new NSMutableArray<Tiers>());
				for (EORepartCompTiers tier : tierss) {
					Tiers tmp = new Tiers();
					if (competence.domaine().isIndividu()) {
						setTiersTypeIndiv(competence.domaine().isIndividu());
						EOIndividuUlr indiv = EOIndividuUlr.individuPourPersId(
								ec, tier.tiersNumero());
						if (indiv != null) {
							tmp.setTiersInvididu(indiv);
							tmp.setTiersLibelle(tmp.tiersInvididu()
									.nomAffichage()
									+ " "
									+ tmp.tiersInvididu().prenomAffichage());
							tmp.setTiersNumero(indiv.persId());
							tmp.setTiersDetails(indiv.toPersonne().persType());
						}
					}

					if (competence.domaine().isStructure()) {
						setTiersTypeStr(competence.domaine().isStructure());
						EOStructureUlr str = EOStructureUlr
								.structurePourPersId(ec, tier.tiersNumero());
						if (str != null) {
							tmp.setTiersStructure(str);
							tmp.setTiersLibelle(tmp.tiersStructure()
									.llStructure());
							tmp.setTiersNumero(str.persId());
							tmp.setTiersDetails(str.toPersonne().persType());
						}
					}

					if (competence.domaine().isUv()) {
						setTiersTypeUv(competence.domaine().isUv());
					}
					if (!mySession().listTiers().contains(tmp))
						mySession().listTiers().addObject(tmp);
				}
				if (mySession().listTiers() != null)
					tiersDisplayGroup.setObjectArray(mySession().listTiers()
							.immutableClone());
				tiersDisplayGroup.setSelectsFirstObjectAfterFetch(true);
				tiersDisplayGroup.sortOrderings();
				tiersDisplayGroup.setSelectedObjects(NSArray.EmptyArray);
				ctrl().setTiersTypePers(tiersTypeIndiv() || tiersTypeStr());
			}
		}
		return tiersDisplayGroup;
	}

	/**
	 * @param tiersDisplayGroup
	 *            the tiersDisplayGroup to set
	 */
	public void setTiersDisplayGroup(ERXDisplayGroup tiersDisplayGroup) {
		this.tiersDisplayGroup = tiersDisplayGroup;
	}

	/**
	 * @return the currentTiers
	 */
	public Tiers getCurrentTiers() {
		return currentTiers;
	}

	/**
	 * @param currentTiers
	 *            the currentTiers to set
	 */
	public void setCurrentTiers(Tiers currentTiers) {
		this.currentTiers = currentTiers;
	}

	/**
	 * @return the tiersTypeIndiv
	 */
	public boolean tiersTypeIndiv() {
		return tiersTypeIndiv;
	}

	/**
	 * @param tiersTypeIndiv
	 *            the tiersTypeIndiv to set
	 */
	public void setTiersTypeIndiv(boolean tiersTypeIndiv) {
		this.tiersTypeIndiv = tiersTypeIndiv;
	}

	/**
	 * @return the tiersTypeStr
	 */
	public boolean tiersTypeStr() {
		return tiersTypeStr;
	}

	/**
	 * @param tiersTypeStr
	 *            the tiersTypeStr to set
	 */
	public void setTiersTypeStr(boolean tiersTypeStr) {
		this.tiersTypeStr = tiersTypeStr;
	}

	/**
	 * @return the tiersTypeUv
	 */
	public boolean tiersTypeUv() {
		return tiersTypeUv;
	}

	/**
	 * @param tiersTypeUv
	 *            the tiersTypeUv to set
	 */
	public void setTiersTypeUv(boolean tiersTypeUv) {
		this.tiersTypeUv = tiersTypeUv;
	}

	/**
	 * @return the editingContextForAddPerson
	 */
	public EOEditingContext editingContextForAddPerson() {
		if (editingContextForAddPerson == null) {
			editingContextForAddPerson = ERXEC.newEditingContext();
		}
		return editingContextForAddPerson;
	}

	// Tableau d'affichage des tiers
	public String tbvHeight() {
		return stringValueForBinding(BINDING_TBV_HEIGHT, "200");
	}

	public String tbvWidth() {
		return stringValueForBinding(BINDING_TBV_WIDTH, "auto");
	}

	public boolean hasFiltre() {
		return booleanValueForBinding(BINDING_TBV_DISPLAY_FILTER, Boolean.TRUE);
	}

	public String tiersTbvId() {
		return "TiersTbv_" + uniqueDomId();
	}

	public String ContainerDomainesTiersEtMatieresId() {
		return "ContainerDomainesTiersEtMatieres";
	}

	public String TRDomaineId() {
		return "TRDomaine";
	}

	public String TRTiersId() {
		return "TRTiers";
	}

	public String TRTiersSelectionId() {
		return "TRTiersSelection";
	}

	public String TRMatiereId() {
		return "TRMatiere";
	}

	public String TRSMatiereId() {
		return "TRSMatiere";
	}

	public WOActionResults refreshToolbar() {
		AjaxUpdateContainer.updateContainerWithID(TRTiersSelectionId(),
				context());
		return null;
	}

	public String globalRefresh() {
		String func = "function() {" + ContainerDomainesTiersEtMatieresId()
				+ "Update(); " + TRTerrainId() + "Update(); " + TRSTerrainId()
				+ "Update(); " + TRCapaciteId() + "Update(); }";
		return func;
	}

	// Definiton des colones du tableau(libelle==>valeur)
	public NSArray<CktlAjaxTableViewColumn> colonnes() {
		if (colonnes == null) {
			CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
			col0.setLibelle("Numéro");
			col0.setOrderKeyPath(COL_TIERS_NUM_KEY);
			CktlAjaxTableViewColumnAssociation asso0 = new CktlAjaxTableViewColumnAssociation(
					LE_TIERS + COL_TIERS_NUM_KEY, "Aucun numéro");
			col0.setAssociations(asso0);
			_colonnesMap.takeValueForKey(col0, COL_TIERS_NUM_KEY);
			CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
			col1.setLibelle("Libellé");
			col1.setOrderKeyPath(COL_TIERS_LIB_KEY);
			col1.setRowCssStyle("white-space: normal;");
			// col1.setRowCssClass("alignToCenter");
			CktlAjaxTableViewColumnAssociation asso1 = new CktlAjaxTableViewColumnAssociation(
					LE_TIERS + COL_TIERS_LIB_KEY, "Aucun libellé");
			col1.setAssociations(asso1);
			_colonnesMap.takeValueForKey(col1, COL_TIERS_LIB_KEY);
			NSMutableArray<CktlAjaxTableViewColumn> res = new NSMutableArray<CktlAjaxTableViewColumn>();
			NSArray<String> keys = DEFAULT_COLONNES_KEYS;
			// Generation des colones du tableau
			for (int i = 0; i < keys.count(); i++) {
				res.addObject(_colonnesMap.objectForKey((String) keys
						.objectAtIndex(i)));
			}
			colonnes = res.immutableClone();
		}
		return colonnes;
	}

	// Affichage de la fenêtre dev recherche de personne
	public WOActionResults openAjouterPersonnes() {
		setResetAjoutPersonnesDialog(true);
		PersonneMultipleSrchPage detailPers = (PersonneMultipleSrchPage) pageWithName(PersonneMultipleSrchPage.class
				.getName());
		detailPers.setEditingContext(editingContextForAddPerson());
		detailPers
				.setSelectedPersonnesDisplayGroup(selectedPersonnesDisplayGroup());
		detailPers.setTiersIndiv(tiersTypeIndiv());
		detailPers.setTiersStr(tiersTypeStr());
		detailPers.setQualifierForIndividu(qualifierForIndividu());
		detailPers.setResetAjoutPersonnesDialog(true);
		detailPers.setPersId(persId());
		detailPers.setTitre(titre());
		return detailPers;
	}

	public WOActionResults refreshTiers() {
		updateDisplayGroup();
		return null;
	}

	// Update des données du tableau dynamique de tiers
	public void updateDisplayGroup() {
		if (mySession().listTiers() != null) {
			if (tiersDisplayGroup() == null)
				setTiersDisplayGroup(new ERXDisplayGroup());
			tiersDisplayGroup().setObjectArray(
					mySession().listTiers().immutableClone());
		}
		tiersDisplayGroup().setSelectsFirstObjectAfterFetch(true);
		tiersDisplayGroup().sortOrderings();
		tiersDisplayGroup().setSelectedObjects(NSArray.EmptyArray);
	}

	// Rafra√Æchir les s/Matieres
	public void refreshSousMat() {
		if (ctrl().leCentreMatiere() != null)
			ctrl().setLesSousMatieres(
					EOMatiere.fetchLesFilsDuPere(competence().editingContext(),
							Integer.valueOf(ctrl().leCentreMatiere()
									.primaryKey()), mySession().TODAY));
	}

	// Bouton supprimer tiers dans le tableau dynamique (mode standAlone ou
	// assistant)
	public WOActionResults supprimerUnTiers() {
		Tiers unTiersAsupprimer = (Tiers) tiersDisplayGroup().selectedObject();
		if (unTiersAsupprimer != null) {
			EOCompetence competence = competence();
			EOEditingContext ec = competence.editingContext();
			if (competence != null
					&& EOUtilities.primaryKeyForObject(ec, competence) != null) {
				EORepartCompTiers repartCompTiersAsupprimer = EORepartCompTiers
						.fetchLaRepartitionTiers(ERXEC.newEditingContext(),
								Integer.valueOf(competence.primaryKey()),
								unTiersAsupprimer.tiersNumero()).lastObject();
				FactoryCompetenceTiers fct = new FactoryCompetenceTiers(
						repartCompTiersAsupprimer.editingContext(), true);
				try {
					fct.supprimerCompetenceTiers(repartCompTiersAsupprimer,
							mySession().dataBus());
					setTiersDisplayGroup(null);
					mySession().addSimpleInfoMessage("RFCCT", "tiers supprimé");
					context().response().setStatus(500);
					AjaxUpdateContainer.updateContainerWithID(myApp()
							.messageContainerID(), context());
				} catch (Exception e) {
					mySession().addSimpleErrorMessage("Erreur",
							e.getLocalizedMessage());
					context().response().setStatus(500);
					AjaxUpdateContainer.updateContainerWithID(myApp()
							.messageContainerID(), context());
					e.printStackTrace();
				}
			} else {
				mySession().listTiers().removeObject(unTiersAsupprimer);
				tiersDisplayGroup().setObjectArray(mySession().listTiers());
				mySession().addSimpleInfoMessage("RFCCT", "tiers supprimé");
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
			}
		}
		return null;
	}

	// Bouton annuler en mode sandAlone
	public WOActionResults annuler() {
		tiersDisplayGroup = null;
		mySession().setListTiers(null);
		editingContext().revert();
		mySession().addSimpleInfoMessage("RFCCT",
				"Les changements ont bien été annulés");
		return null;
	}

	// Bouton enregistrer onglet domaine, tiers, matiere en mode sandAlone
	public WOActionResults enregistrer() {
		EOCompetence competence = competence();
		EOEditingContext ec = competence.editingContext();
		competence.setPersIdModif(persId());
		competence.setDateModif(MyDateCtrl.now());
		if (competence != null) {
			try {
				if (ec.hasChanges() && validerModifier()) {
					ec.updatedObjects();
					ec.saveChanges();
					// Enregistrement des tiers
					FactoryCompetenceTiers fct = new FactoryCompetenceTiers(ec);
					fct.enregistrerCompetenceTiers(competence, mySession()
							.tiersDg().allObjects(), mySession().dataBus());
					mySession().addSimpleInfoMessage(
							"RFCCT",
							"Competence ' " + competence().numeroCompetence()
									+ " ' modifiée avec succès");
				}
			} catch (ValidationException e2) {
				mySession().addSimpleErrorMessage(e2.getLocalizedMessage(), e2);
				context().response().setStatus(500);
				ec.revert();
				myLogger().info(e2.getMessage(), e2);
				e2.printStackTrace();
			} catch (Exception e) {
				mySession()
						.addSimpleErrorMessage(
								"Une erreur est survenue lors de l'enregistrement en base",
								e);
				context().response().setStatus(500);
				ec.revert();
				myLogger().error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		return null;
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

	public boolean enabled() {
		if (isInAssistant())
			return isStandAlone();
		if (isStandAlone()) {
			boolean assist = isInAssistant();
			boolean update = (peutModifierCompetence() && competence()
					.persIdCrea() == persId()) || droitTout();
			boolean result = assist && update;
			return result;
		}
		return false;
	}

	public String capTbvId() {
		return "capTbv_" + uniqueDomId();
	}

	public String TRCapaciteSelectionId() {
		return "TRCapaciteSelection";
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

	public String ContainerCapaciteCommentaireId() {
		return "ContainerCapaciteCommentaire";
	}

	public WOActionResults refreshContainerCapaciteCommentaire() {
		AjaxUpdateContainer.updateContainerWithID(
				ContainerCapaciteCommentaireId(), context());
		return null;
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

	// Rafraichissement des s/Terrain
	public void refreshSousTerr() {
		if (ctrl().leCentreTerrain() != null)
			ctrl().setLesSousTerrains(
					EOTerrain.fetchLesFilsDuPere(competence().editingContext(),
							Integer.valueOf(ctrl().leCentreTerrain()
									.primaryKey()), mySession().TODAY));
	}

	/**
	 * @return the qualifierForIndividu
	 */
	public EOQualifier qualifierForIndividu() {
		return qualifierForIndividu;
	}

	/**
	 * @param qualifierForIndividu
	 *            the qualifierForIndividu to set
	 */
	public void setQualifierForIndividu(EOQualifier qualifierForIndividu) {
		this.qualifierForIndividu = qualifierForIndividu;
	}

	/**
	 * @return the titre
	 */
	public String titre() {
		return titre;
	}

	/**
	 * @param titre
	 *            the titre to set
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}

	public boolean tierActivate() {
		tierActivate = ctrl().tiersTypePers() && multi();
		return tierActivate;
	}

	public boolean multi() {
		if (multi == null) {
			EOCompetence competence = competence();
			Boolean cre = mySession().getTemcreate();
			Boolean upd = mySession().getTemupdate();
			if ((cre != null && !cre) && (upd != null && upd)) {
				multi = (competence.temMulti().equals("O") && update())
						|| (competence.temMulti().equals("O") && droitTout());
			} else {
				multi = competence.temMulti().equals("O");
			}
		}
		return multi;
	}

	public boolean update() {
		if (update == null) {
			EOCompetence competence = competence();
			update = peutModifierCompetence()
					&& ((competence.persIdCrea() != null) && (competence
							.persIdCrea() == persId()));
		}
		return update;
	}

	public String labelTier() {
		String label = "Tier (Enseignants, Chercheurs, Entreprises)";
		if (ctrl().selectionDomaine() != null
				&& (ctrl().selectionDomaine().isChercheur() || ctrl()
						.selectionDomaine().isEnseignant()))
			label = "Enseignants, Chercheurs...";
		if (ctrl().selectionDomaine() != null
				&& (ctrl().selectionDomaine().isPartenaire()))
			label = "Entreprises, Partenaires ...";
		if (ctrl().selectionDomaine() != null
				&& (ctrl().selectionDomaine().isEnseignement()))
			label = "Unité de valeurs ...";
		return label;
	}
}
