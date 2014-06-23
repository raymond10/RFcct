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

import java.text.Format;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomTerrain;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryDomaineTerrain;
import org.utt.rfcct.serveur.components.Accueil;
import org.utt.rfcct.serveur.components.assistants.Assistant;
import org.utt.rfcct.serveur.components.controlers.ModuleAdminTerrainCtrl;
import org.utt.rfcct.serveur.utils.NettoieEOEditingContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;
import com.webobjects.foundation.NSValidation.ValidationException;

import er.ajax.AjaxUpdateContainer;
import er.extensions.appserver.ERXRedirect;
import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 28 mars 2013
 */
@SuppressWarnings("all")
public class ModuleAdminTerrain extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5735560027346093427L;
	private ModuleAdminTerrainCtrl ctrl = null;
	private String containerId;
	private boolean isResetDialogcreateTerrain;
	private NSMutableArray<EODomaine> terrainDomaines;
	public Boolean creationTerrainEnabled = null;

	public ModuleAdminTerrain(WOContext context) {
		super(context);
		NSLog.out.appendln("ModuleAdminTerrain");
	}

	/**
	 * @return the ctrl
	 */
	public ModuleAdminTerrainCtrl ctrl() {
		if (ctrl == null)
			ctrl = new ModuleAdminTerrainCtrl(this);
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(ModuleAdminTerrainCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public void onPrecedent() {
	}

	public void onSuivant() {
	}

	public String getContainerId() {
		if (containerId == null)
			containerId = ERXWOContext.safeIdentifierName(context(), true);
		return containerId;
	}

	// Id de la fenêtre creer matiere
	public String dialogcreateTerrainId() {
		return "DialogcreateTerrain_" + uniqueDomId();
	}

	/**
	 * @return the isResetDialogcreateTerrain
	 */
	public boolean isResetDialogcreateTerrain() {
		return isResetDialogcreateTerrain;
	}

	/**
	 * @param isResetDialogcreateTerrain
	 *            the isResetDialogcreateTerrain to set
	 */
	public void setResetDialogcreateTerrain(boolean isResetDialogcreateTerrain) {
		this.isResetDialogcreateTerrain = isResetDialogcreateTerrain;
	}

	// Affichage de la fenêtre creation terrain
	public WOActionResults openCreateTerrainWdows() {
		setResetDialogcreateTerrain(true);
		return null;
	}

	public WOActionResults refreshTerrains() {
		return null;
	}

	public boolean validerTerrain() {
		boolean validate = false;
		NSArray<String> failureMessages = new NSArray<String>();
		Assistant assistant = (Assistant) parent();
		EOTerrain terrain = terrain();
		if (terrain != null && assistant != null) {
			if (ctrl().checkDomaines().isEmpty()) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le(s) domaine(s) de définition");
			}

			if (ctrl().getSelectedTerrainPere() != null)
				terrain.setTerrainPere(ctrl().getSelectedTerrainPere()
						.localInstanceIn(terrain.editingContext()));

			if (terrain.lblLong() == null || terrain.lblLong().equals("")) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Libellé long");
			}

			if (terrain.lblCourt() == null || terrain.lblCourt().equals("")) {
				failureMessages = failureMessages
						.arrayByAddingObject("Le Libellé court");
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
					.setFailureMessage("Veuillez renseigner les champs obligatoires du domaine (en rouge).");
		}
		if (validate) {
			mySession().getUiMessages().removeAllObjects();
			setTerrain(terrain);
		}
		return validate;
	}

	// Boutons enregistrer
	public WOActionResults unTerrainEnregistrer() {
		currentDomaineTerrain();
		EOTerrain terrain = terrain();
		EOEditingContext ec = terrain.editingContext();
		boolean modification = false;
		int tmp = 0;
		try {
			if (validerTerrain()) {
				// En modification?
				if (EOUtilities.primaryKeyForObject(ec, terrain()) != null) {
					modification = true;
					terrain().setDateModif(new NSTimestamp());
					terrain().setPersIdModif(utilisateurPersId());
					ec = NettoieEOEditingContext.cleanEC(ec);
					if (ctrl().getUnTerraindateFin() != null)
						terrain().setDateFin(ctrl().getUnTerraindateFin());
					// Update
					ec.saveChanges();
					// On compare les domaines de l'origine et ceux de la
					// modification
					if (terrainDomaines().count() > ctrl().checkDomaines()
							.count()) {
						for (EODomaine domaine : ctrl().removedDomaines()) {
							// On retire les domaines deselectionnes
							terrainDomaines().removeObject(domaine);
						}
						tmp = terrainDomaines().count();
					}
					// On procède à l'enregistrement des modifications des
					// domaines
					for (EODomaine domaine : ctrl().checkDomaines()) {
						if (terrainDomaines().contains(domaine)) {
							FactoryDomaineTerrain fdt = new FactoryDomaineTerrain(
									ec, true);
							fdt.enregistrementDomaineTerrain(domaine, terrain);
							terrainDomaines().removeObject(domaine);

						} else {
							if (tmp != 0 && terrainDomaines().count() == 1) {
								domaine = terrainDomaines().lastObject();
							}
							FactoryDomaineTerrain fdt = new FactoryDomaineTerrain(
									ec, true);
							fdt.suppressionDomaineTerrain(domaine, terrain);

						}
						if (ctrl().removedDomaines() != null
								&& ctrl().removedDomaines().count() != 0) {
							for (EODomaine domaineToRemove : ctrl()
									.removedDomaines()) {
								FactoryDomaineTerrain fdt = new FactoryDomaineTerrain(
										ec, true);
								fdt.supprimerLeDomaineTerrain(domaineToRemove,
										terrain);
							}
							ctrl().removedDomaines().removeAllObjects();
						}
					}
					resetTerrainField();
					ctrl().editTerrain();
					mySession().addSimpleInfoMessage("Confirmation",
							"Terrain modifié dans le référentiel");
				} else {
					//Nouveau terrain
					if (ctrl().getUnTerraindateFin() != null)
						terrain().setDateFin(ctrl().getUnTerraindateFin());
					if (ec.hasChanges()) {
						ec.saveChanges();
						for (EODomaine domaine : ctrl().checkDomaines()) {
							FactoryDomaineTerrain fdt = new FactoryDomaineTerrain(
									ec, true);
							fdt.enregistrementDomaineTerrain(domaine, terrain);
						}
						resetTerrainField();
						ctrl().editTerrain();
						mySession().addSimpleInfoMessage("Confirmation",
								"Terrain enregistré dans le référentiel");
					}
				}
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
			} else {
				context().response().setStatus(500);
				AjaxUpdateContainer.updateContainerWithID(myApp()
						.messageContainerID(), context());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mySession().addSimpleErrorMessage("ALERTE", e.toString());
			context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(myApp()
					.messageContainerID(), context());
			return null;
		}
		return null;
	}

	public void resetTerrainField() {
		ctrl().setSelectedTerrain(null);
		ctrl().setSelectedTerrainPere(null);
		ctrl().setUnTerrainPere(null);
		ctrl().setCheckDomaines(null);
		ctrl().setRemovedDomaines(null);
		ctrl().setLesDomaines(null);
		ctrl().setLesTerrains(null);
	}

	// Retourne la clé primaire
	protected static Long singleLongPrimaryKeyForObject(EOEnterpriseObject eo) {
		EOEditingContext ec = eo.editingContext();
		if (ec == null) {
			// you don't have an EC! Bad EO. We can do nothing.
			return null;
		}
		EOGlobalID gid = ec.globalIDForObject(eo);
		if (gid.isTemporary()) {
			// no pk yet assigned
			return null;
		}
		EOKeyGlobalID kGid = (EOKeyGlobalID) gid;
		NSArray keyValues = kGid.keyValuesArray();
		if (keyValues.count() != 1
				|| !(keyValues.objectAtIndex(0) instanceof Long))
			return null;

		return (Long) keyValues.objectAtIndex(0);

	}

	public NSTimestamp terrainDateFin() {
		return terrain().dateFin();
	}

	public void setTerrainDateFin(NSTimestamp date) {
		try {
			terrain().setDateFin(date);
		} catch (ValidationException e) {
			mySession().addSimpleErrorMessage("RFCCT", e.getMessage());
		}
	}

	/**
	 * @return the terrainDomaines
	 */
	public NSMutableArray<EODomaine> terrainDomaines() {
		if (terrainDomaines == null)
			terrainDomaines = new NSMutableArray<EODomaine>();
		return terrainDomaines;
	}

	/**
	 * @param terrainDomaines
	 *            the terrainDomaines to set
	 */
	public void setTerrainDomaines(NSMutableArray terrainDomaines) {
		this.terrainDomaines = terrainDomaines;
	}

	public NSMutableArray<EODomaine> currentDomaineTerrain() {
		EOTerrain terrain = terrain();
		if (terrain.repartDomTerrains().count() > 0) {
			for (EORepartDomTerrain rdt : terrain.repartDomTerrains()) {
				if (!terrainDomaines().contains(rdt.domaine()))
					terrainDomaines().addObject(rdt.domaine());
			}

		}
		return terrainDomaines();
	}

	public boolean isCreationTerrainEnabled() {
		if (creationTerrainEnabled == null) {
			creationTerrainEnabled = creerUnTerrain() || droitTout();
		}
		return creationTerrainEnabled;
	}
}
