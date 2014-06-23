/**
 * 
 */
package org.utt.rfcct.serveur.components.controlers;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTypeTiers;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryTypeTier;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleAdminTypeTier;
import org.utt.rfcct.serveur.utils.NettoieEOEditingContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.ajax.AjaxUpdateContainer;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 8 nov. 2013
 * 
 */
@SuppressWarnings("all")
public class ModuleAdminTypeTierCtrl extends ModuleCtrl<ModuleAdminTypeTier> {

	// TypesTiers
	private NSArray<EOTypeTiers> lesTypesTiers;
	private EOTypeTiers unTypeTier;
	private EOTypeTiers selectedTypeTier;

	// Activiter modification
	private boolean activUpdateTypeTier = false;
	private boolean lockFieldTypeTier = false;
	private Integer persId = null;

	public ModuleAdminTypeTierCtrl(ModuleAdminTypeTier component) {
		super(component);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the lesTypesTiers
	 */
	public NSArray<EOTypeTiers> lesTypesTiers() {
		if (lesTypesTiers == null)
			lesTypesTiers = EOTypeTiers.fetchTypeTierActuel(wocomponent()
					.typeTier().editingContext(), wocomponent().mySession().TODAY);
		return lesTypesTiers;
	}

	/**
	 * @param lesTypesTiers
	 *            the lesTypesTiers to set
	 */
	public void setLesTypesTiers(NSArray<EOTypeTiers> lesTypesTiers) {
		this.lesTypesTiers = lesTypesTiers;
	}

	/**
	 * @return the unTypeTier
	 */
	public EOTypeTiers unTypeTier() {
		return unTypeTier;
	}

	/**
	 * @param unTypeTier
	 *            the unTypeTier to set
	 */
	public void setUnTypeTier(EOTypeTiers unTypeTier) {
		this.unTypeTier = unTypeTier;
	}

	/**
	 * @return the selectedTypeTier
	 */
	public EOTypeTiers selectedTypeTier() {
		return selectedTypeTier;
	}

	/**
	 * @param selectedTypeTier
	 *            the selectedTypeTier to set
	 */
	public void setSelectedTypeTier(EOTypeTiers selectedTypeTier) {
		this.selectedTypeTier = selectedTypeTier;
	}

	/**
	 * @return the activUpdateTypeTier
	 */
	public boolean isActivUpdateTypeTier() {
		return activUpdateTypeTier;
	}

	/**
	 * @param activUpdateTypeTier
	 *            the activUpdateTypeTier to set
	 */
	public void setActivUpdateTypeTier(boolean activUpdateTypeTier) {
		this.activUpdateTypeTier = activUpdateTypeTier;
	}

	/**
	 * @return the lockFieldTypeTier
	 */
	public boolean isLockFieldTypeTier() {
		return lockFieldTypeTier;
	}

	/**
	 * @param lockFieldTypeTier
	 *            the lockFieldTypeTier to set
	 */
	public void setLockFieldTypeTier(boolean lockFieldTypeTier) {
		this.lockFieldTypeTier = lockFieldTypeTier;
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

	/** Gestion des donnees **/

	// Edit TypeTier
	public WOActionResults editTypeTier() {
		EOTypeTiers typeTier = wocomponent().typeTier();
		typeTier.__setEditingContext(wocomponent().edc());
		EOEditingContext ec = typeTier.editingContext();
		if (selectedTypeTier() != null) {
			wocomponent().setTypeTier(selectedTypeTier());
			setLockFieldTypeTier(true);
			setActivUpdateTypeTier(true);
		} else {
			if (ec != null)
				ec = NettoieEOEditingContext.cleanEC(ec);
			FactoryTypeTier ftt = new FactoryTypeTier(ec);
			typeTier = ftt.createTypeTierVierge(persId());
			wocomponent().resetTypeTierField();
			wocomponent().setTypeTier(typeTier);
			setLockFieldTypeTier(false);
			setActivUpdateTypeTier(false);
		}
		return null;
	}

	// Editer typeTier champs actif
	public WOActionResults editTypeTierFiled() {
		setLockFieldTypeTier(false);
		return null;
	}

	// Suppression de typeTier
	public WOActionResults deleteTypeTier() {
		setLockFieldTypeTier(true);
		setActivUpdateTypeTier(true);
		EOTypeTiers typeTier = wocomponent().typeTier();
		EOEditingContext ec = typeTier.editingContext();
		try {
			ec = NettoieEOEditingContext.cleanEC(ec);
			if (typeTier.domaines().count() != 0)
				throw new Exception(
						"Suppression impossble, il existe des domaines liées à ce type de tier");
			ec.deleteObject(typeTier);
			ec.saveChanges();
			wocomponent().resetTypeTierField();
			editTypeTier();
			wocomponent().mySession().addSimpleInfoMessage("Confirmation",
					"Type de tier supprimé dans le référentiel");
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

	// Annuler typeTier champs actif
	public WOActionResults annulerTypeTierFiled() {
		setLockFieldTypeTier(true);
		return null;
	}
}
