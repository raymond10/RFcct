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

import javax.media.jai.UntiledOpImage;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTypeTiers;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryDomaine;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleAdminDomaine;
import org.utt.rfcct.serveur.utils.NettoieEOEditingContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.ajax.AjaxUpdateContainer;
import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXArrayUtilities;
import er.extensions.foundation.ERXPatcher.DynamicElementsPatches.SubmitButton;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 28 mars 2013
 */
@SuppressWarnings("all")
public class ModuleAdminDomaineCtrl extends ModuleCtrl<ModuleAdminDomaine> {

	// Domaine
	private NSArray lesDomaines;
	private EODomaine unDomaine;
	private EODomaine selectedDomaine;
	private EODomaine unDomaineMatiere;
	private EODomaine selectedDomaineMatiere;
	// Creation Domaine
	private NSArray typeTiers;
	private EOTypeTiers unTypeTiers;
	private EOTypeTiers selectedTypeTiers;

	// Activiter modification
	private boolean activUpdateDomaine = false;
	private boolean lockFieldDomaine = false;
	private Integer persId = null;

	/**
	 * @param component
	 */
	public ModuleAdminDomaineCtrl(ModuleAdminDomaine component) {
		super(component);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the lesDomaines
	 */
	public NSArray lesDomaines() {
		if (lesDomaines == null)
			lesDomaines = EODomaine.fetchAllEODomaines(wocomponent().domaine()
					.editingContext(), new NSArray(
					new Object[] { EODomaine.DOM_ID_ASC }));
		return lesDomaines;
	}

	/**
	 * @param lesDomaines
	 *            the lesDomaines to set
	 */
	public void setLesDomaines(NSArray<EODomaine> lesDomaines) {
		this.lesDomaines = lesDomaines;
	}

	/**
	 * @return the unDomaine
	 */
	public EODomaine getUnDomaine() {
		return unDomaine;
	}

	/**
	 * @param unDomaine
	 *            the unDomaine to set
	 */
	public void setUnDomaine(EODomaine unDomaine) {
		this.unDomaine = unDomaine;
	}

	/**
	 * @return the selectedDomaine
	 */
	public EODomaine selectedDomaine() {
		return selectedDomaine;
	}

	/**
	 * @param selectedDomaine
	 *            the selectedDomaine to set
	 */
	public void setSelectedDomaine(EODomaine selectedDomaine) {
		this.selectedDomaine = selectedDomaine;
	}

	/**
	 * @return the typeTiers
	 */
	public NSArray<EOTypeTiers> typeTiers() {
		if (typeTiers == null)
			typeTiers = EOTypeTiers.fetchTypeTierActuel(wocomponent()
					.domaine().editingContext(), wocomponent().mySession().TODAY);
		return typeTiers;
	}

	/**
	 * @param typeTiers
	 *            the typeTiers to set
	 */
	public void setTypeTiers(NSArray<EOTypeTiers> typeTiers) {
		this.typeTiers = typeTiers;
	}

	/**
	 * @return the unTypeTiers
	 */
	public EOTypeTiers unTypeTiers() {
		return unTypeTiers;
	}

	/**
	 * @param unTypeTiers
	 *            the unTypeTiers to set
	 */
	public void setUnTypeTiers(EOTypeTiers unTypeTiers) {
		this.unTypeTiers = unTypeTiers;
	}

	/**
	 * @return the selectedTypeTiers
	 */
	public EOTypeTiers selectedTypeTiers() {
		if (selectedTypeTiers == null) {
			EODomaine domaine = wocomponent().domaine();
			EOEditingContext ec = domaine.editingContext();
			if (domaine != null) {
				selectedTypeTiers = domaine.eoTypeTiers();
			}
		}
		return selectedTypeTiers;
	}

	/**
	 * @param selectedTypeTiers
	 *            the selectedTypeTiers to set
	 */
	public void setSelectedTypeTiers(EOTypeTiers selectedTypeTiers) {
		EODomaine domaine = wocomponent().domaine();
		if (domaine != null) {
			this.selectedTypeTiers = selectedTypeTiers;
			if (selectedTypeTiers != null) {
				domaine.setEoTypeTiersRelationship(selectedTypeTiers);
				domaine.setTypeTiers(selectedTypeTiers.libelle());
			}
		}
	}

	/**
	 * @return the activUpdateDomaine
	 */
	public boolean isActivUpdateDomaine() {
		return activUpdateDomaine;
	}

	/**
	 * @param activUpdateDomaine
	 *            the activUpdateDomaine to set
	 */
	public void setActivUpdateDomaine(boolean activUpdateDomaine) {
		this.activUpdateDomaine = activUpdateDomaine;
	}

	/**
	 * @return the lockFieldDomaine
	 */
	public boolean isLockFieldDomaine() {
		return lockFieldDomaine;
	}

	/**
	 * @param lockFieldDomaine
	 *            the lockFieldDomaine to set
	 */
	public void setLockFieldDomaine(boolean lockFieldDomaine) {
		this.lockFieldDomaine = lockFieldDomaine;
	}

	/**
	 * @return the unDomaineMatiere
	 */
	public EODomaine getUnDomaineMatiere() {
		return unDomaineMatiere;
	}

	/**
	 * @param unDomaineMatiere
	 *            the unDomaineMatiere to set
	 */
	public void setUnDomaineMatiere(EODomaine unDomaineMatiere) {
		this.unDomaineMatiere = unDomaineMatiere;
	}

	/**
	 * @return the selectedDomaineMatiere
	 */
	public EODomaine selectedDomaineMatiere() {
		return selectedDomaineMatiere;
	}

	/**
	 * @param selectedDomaineMatiere
	 *            the selectedDomaineMatiere to set
	 */
	public void setSelectedDomaineMatiere(EODomaine selectedDomaineMatiere) {
		this.selectedDomaineMatiere = selectedDomaineMatiere;
	}

	// Edit Domaine
	public WOActionResults editDomaine() {
		EODomaine domaine = wocomponent().domaine();
		EOEditingContext ec = domaine.editingContext();
		if (selectedDomaine() != null) {
			wocomponent().mySession().setDomaineSelectionnee(selectedDomaine());
			wocomponent().setDomaine(selectedDomaine());
			setSelectedTypeTiers(null);
			setActivUpdateDomaine(true);
			setLockFieldDomaine(true);
		} else {
			if (ec != null) {
				ec = NettoieEOEditingContext.cleanEC(ec);
			} else {
				ec = wocomponent().edc();
			}
			FactoryDomaine fd = new FactoryDomaine(ec);
			domaine = fd.createdDomaine();
			if (domaine == null)
				domaine = fd.creerDomaineVierge(persId());
			wocomponent().resetDomaineField();
			wocomponent().setDomaine(domaine);
			setSelectedTypeTiers(null);
			setUnTypeTiers(selectedTypeTiers());
			setActivUpdateDomaine(false);
			setLockFieldDomaine(false);
		}
		return null;
	}

	// Editer domaine champs actif
	public WOActionResults editDomaineFiled() {
		setLockFieldDomaine(false);
		EODomaine domaine = wocomponent().domaine();
		setSelectedTypeTiers(domaine.eoTypeTiers());
		return null;
	}

	// Suppression de domaine
	public WOActionResults deleteDomaine() {
		setActivUpdateDomaine(true);
		setLockFieldDomaine(true);
		EODomaine domaine = wocomponent().domaine();
		wocomponent().setDomaine(domaine);
		EOEditingContext ec = domaine.editingContext();
		try {
			ec = NettoieEOEditingContext.cleanEC(ec);
			if (domaine.repartDomMatieres().count() != 0
					|| domaine.repartDomTerrains().count() != 0
					|| domaine.repartDomCapacites().count() != 0)
				throw new Exception(
						"Suppression impossble, il existe des donn�es li�es � ce domaine");
			ec.deleteObject(domaine);
			ec.saveChanges();
			wocomponent().resetDomaineField();
			editDomaine();
			wocomponent().mySession().addSimpleInfoMessage("Confirmation",
					"Domaine supprim� dans le r�f�rentiel");
			wocomponent().context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(wocomponent().myApp()
					.messageContainerID(), wocomponent().context());
		} catch (Exception e) {
			e.printStackTrace();
			wocomponent().mySession().addSimpleErrorMessage("ALERTE",
					e.toString());
			wocomponent().context().response().setStatus(500);
			AjaxUpdateContainer.updateContainerWithID(wocomponent().myApp()
					.messageContainerID(), wocomponent().context());
			return null;
		}
		return null;
	}

	// Annuler domaine champs actif
	public WOActionResults annulerDomaineFiled() {
		setLockFieldDomaine(true);
		return null;
	}

	/**
	 * @return the persId
	 */
	public Integer persId() {
		if (persId == null) {
			if (wocomponent().myApp().newGestionDroitsEnabled()) {
				persId = wocomponent().myGdUser().getPersId();
			} else {
				persId = wocomponent().myJefyUser().getPersId();
			}
		}
		return persId;
	}

	/**
	 * @param persId
	 *            the persId to set
	 */
	public void setPersId(Integer persId) {
		this.persId = persId;
	}
}
