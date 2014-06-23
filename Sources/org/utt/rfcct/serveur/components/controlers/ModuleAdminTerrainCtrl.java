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

import org.cocktail.fwkcktlpersonneguiajax.serveur.controleurs.GroupeAdminFormCtrl.RepartTypeGroupeDGDelegate;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomTerrain;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryTerrain;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleAdminTerrain;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXArrayUtilities;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 28 mars 2013
 */
@SuppressWarnings("all")
public class ModuleAdminTerrainCtrl extends ModuleCtrl<ModuleAdminTerrain> {

	// Les domaines
	private NSArray<EODomaine> lesDomaines;
	private EODomaine itemDomaine;
	private NSMutableArray<EODomaine> checkDomaines;
	private NSMutableArray<EODomaine> removedDomaines;

	// Terrain
	private NSArray lesTerrains = null;
	private EOTerrain unTerrain;
	private EOTerrain selectedTerrain;
	// Terrain Pere
	private EOTerrain unTerrainPere;
	private EOTerrain selectedTerrainPere;
	// Creation Terrain
	private String unTerrainlblCourt;
	private String unTerrainlblLong;
	private NSTimestamp unTerraindateFin;

	// Activiter modification
	private boolean activUpdateTerrain = false;
	private boolean lockFieldTerrain = false;
	EOEditingContext ec;
	private Integer persId = null;

	/**
	 * @param component
	 */
	public ModuleAdminTerrainCtrl(ModuleAdminTerrain component) {
		super(component);
		// TODO Auto-generated constructor stub
		ec = ERXEC.newEditingContext();
	}

	/**
	 * @return the lesTerrains
	 */
	public NSArray lesTerrains() {
		if (lesTerrains == null) {
			NSMutableArray<EOTerrain> tmp = new NSMutableArray<EOTerrain>();
			NSMutableArray<EOTerrain> tmp2 = new NSMutableArray<EOTerrain>();
			NSArray<EODomaine> domaines = null;
			NSArray<EORepartDomTerrain> rdts = null;
			domaines = EODomaine.fetchDomaines(ec, wocomponent().mySession().TODAY);
			if (!wocomponent().droitTout())
				domaines = filtreDomaine(domaines).immutableClone();
			domaines = ERXArrayUtilities.arrayWithoutDuplicateKeyValue(
					domaines, EODomaine.REPART_DOM_TERRAINS_KEY);
			rdts = (NSArray<EORepartDomTerrain>) domaines
					.valueForKey(EODomaine.REPART_DOM_TERRAINS_KEY);
			rdts = ERXArrayUtilities.arrayWithoutDuplicateKeyValue(rdts,
					EORepartDomTerrain.TERRAIN_KEY);
			if (rdts != null && rdts.count() > 0 && verifTer(rdts)) {
				NSArray tt = (NSArray<EOTerrain>) rdts
						.valueForKey(EORepartDomTerrain.TERRAIN_KEY);
				for (int t = 0; t < tt.count(); t++) {
					Object obj = (NSMutableArray) tt.objectAtIndex(t);
					// Correction bug plusieurs objet dans un objet
					if (((NSMutableArray) obj).count() > 1) {
						for (Object ob : (NSMutableArray) obj) {
							EOTerrain tr = (EOTerrain) ob;
							if (tr != null)
								tmp2.addObject(tr);
						}
					} else {
						EOTerrain tr = (EOTerrain) ((NSMutableArray) obj)
								.lastObject();
						if (tr != null)
							tmp2.addObject(tr);
					}
				}
				NSArray tmp3 = tmp2.immutableClone();
				tmp.addObjectsFromArray(tmp3);
			} else {
				tmp.addObjectsFromArray(EOTerrain.fetchLesTerrains(ec, wocomponent().mySession().TODAY));
			}

			EOTerrain terrain = selectedTerrain();
			if (terrain != null && !tmp.contains(terrain)) {
				tmp.addObject(terrain);
			}
			if (tmp != null && tmp.count() > 0) {
				ERXArrayUtilities
						.sortArrayWithKey(tmp, EOTerrain.LBL_COURT_KEY);
				lesTerrains = ERXArrayUtilities.arrayWithoutDuplicates(tmp);
			}
		}
		return lesTerrains;
	}

	/**
	 * @param lesTerrains
	 *            the lesTerrains to set
	 */
	public void setLesTerrains(NSArray lesTerrains) {
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
	 * @return the selectedTerrain
	 */
	public EOTerrain selectedTerrain() {
		return selectedTerrain;
	}

	/**
	 * @param selectedTerrain
	 *            the selectedTerrain to set
	 */
	public void setSelectedTerrain(EOTerrain selectedTerrain) {
		this.selectedTerrain = selectedTerrain;
	}

	/**
	 * @return the unTerrainlblCourt
	 */
	public String getUnTerrainlblCourt() {
		return unTerrainlblCourt;
	}

	/**
	 * @param unTerrainlblCourt
	 *            the unTerrainlblCourt to set
	 */
	public void setUnTerrainlblCourt(String unTerrainlblCourt) {
		this.unTerrainlblCourt = unTerrainlblCourt;
	}

	/**
	 * @return the unTerrainlblLong
	 */
	public String getUnTerrainlblLong() {
		return unTerrainlblLong;
	}

	/**
	 * @param unTerrainlblLong
	 *            the unTerrainlblLong to set
	 */
	public void setUnTerrainlblLong(String unTerrainlblLong) {
		this.unTerrainlblLong = unTerrainlblLong;
	}

	/**
	 * @return the unTerraindateFin
	 */
	public NSTimestamp getUnTerraindateFin() {
		return unTerraindateFin;
	}

	/**
	 * @param unTerraindateFin
	 *            the unTerraindateFin to set
	 */
	public void setUnTerraindateFin(NSTimestamp unTerraindateFin) {
		this.unTerraindateFin = unTerraindateFin;
	}

	/**
	 * @return the unTerrainPere
	 */
	public EOTerrain getUnTerrainPere() {
		return unTerrainPere;
	}

	/**
	 * @param unTerrainPere
	 *            the unTerrainPere to set
	 */
	public void setUnTerrainPere(EOTerrain unTerrainPere) {
		this.unTerrainPere = unTerrainPere;
	}

	/**
	 * @return the selectedTerrainPere
	 */
	public EOTerrain getSelectedTerrainPere() {
		return selectedTerrainPere;
	}

	/**
	 * @param selectedTerrainPere
	 *            the selectedTerrainPere to set
	 */
	public void setSelectedTerrainPere(EOTerrain selectedTerrainPere) {
		this.selectedTerrainPere = selectedTerrainPere;
	}

	/**
	 * @return the activUpdateTerrain
	 */
	public boolean isActivUpdateTerrain() {
		return activUpdateTerrain;
	}

	/**
	 * @param activUpdateTerrain
	 *            the activUpdateTerrain to set
	 */
	public void setActivUpdateTerrain(boolean activUpdateTerrain) {
		this.activUpdateTerrain = activUpdateTerrain;
	}

	/**
	 * @return the lockFieldTerrain
	 */
	public boolean isLockFieldTerrain() {
		return lockFieldTerrain;
	}

	/**
	 * @param lockFieldTerrain
	 *            the lockFieldTerrain to set
	 */
	public void setLockFieldTerrain(boolean lockFieldTerrain) {
		this.lockFieldTerrain = lockFieldTerrain;
	}

	/**
	 * @return the lesDomaines
	 */
	public NSArray lesDomaines() {
		if (lesDomaines == null) {
			lesDomaines = EODomaine.fetchDomaines(ec, wocomponent().mySession().TODAY);
			if (!wocomponent().droitTout())
				lesDomaines = filtreDomaine(lesDomaines).immutableClone();
		}
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
	 * @return the itemDomaine
	 */
	public EODomaine itemDomaine() {
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
	 * @return the checkDomaines
	 */
	public NSMutableArray<EODomaine> checkDomaines() {
		if (checkDomaines == null)
			checkDomaines = new NSMutableArray<EODomaine>();
		return checkDomaines;
	}

	/**
	 * @param checkDomaines
	 *            the checkDomaines to set
	 */
	public void setCheckDomaines(NSMutableArray<EODomaine> checkDomaines) {
		this.checkDomaines = checkDomaines;
	}

	/**
	 * @return the removedDomaines
	 */
	public NSMutableArray<EODomaine> removedDomaines() {
		if (removedDomaines == null)
			removedDomaines = new NSMutableArray<EODomaine>();
		return removedDomaines;
	}

	/**
	 * @param removedDomaines
	 *            the removedDomaines to set
	 */
	public void setRemovedDomaines(NSMutableArray<EODomaine> removedDomaines) {
		this.removedDomaines = removedDomaines;
	}

	// Activ/Desactiv le checkBox
	public void setDomaineSelected(boolean selected) {
		if (selected) {
			checkDomaines().addObject(itemDomaine());
			removedDomaines().removeObject(itemDomaine());
		} else {
			checkDomaines().removeObject(itemDomaine());
			removedDomaines().addObject(itemDomaine());
		}
	}

	public boolean isDomaineSelected() {
		return checkDomaines().containsObject(itemDomaine());
	}

	// Edit Terrain
	public WOActionResults editTerrain() {
		if (selectedTerrain() != null) {
			checkDomaines = null;
			wocomponent().setTerrain(selectedTerrain());
			EOTerrain terrain = wocomponent().terrain();
			if (terrain.repartDomTerrains().count() > 0) {
				for (EORepartDomTerrain rdt : terrain.repartDomTerrains()) {
					checkDomaines().addObject(rdt.domaine());
				}

			}
			setSelectedTerrainPere(terrain.terrainPere());
			setUnTerrainPere(terrain.terrainPere());
			setActivUpdateTerrain(true);
			setLockFieldTerrain(true);
		} else {
			FactoryTerrain ft = new FactoryTerrain(wocomponent().edc());
			EOTerrain terrain = ft.creerTerrainVierge(persId());
			wocomponent().resetTerrainField();
			wocomponent().setTerrain(terrain);
			setActivUpdateTerrain(false);
			setLockFieldTerrain(false);
			checkDomaines = null;
		}
		return wocomponent().submit();
	}

	// Editer terrain champs actif
	public WOActionResults editTerrainFiled() {
		setLockFieldTerrain(false);
		EOTerrain terrain = wocomponent().terrain();
		// setSelectedDomaineTerrain(terrain
		// .repartDomTerrains(EORepartDomTerrain.TERRAIN.eq(terrain))
		// .lastObject().domaine());
		return wocomponent().submit();
	}

	// Annuler terrain champs actif
	public WOActionResults annulerTerrainFiled() {
		setLockFieldTerrain(true);
		return wocomponent().submit();
	}

	// Verif indispensable pour eviter un nullpointerException
	protected boolean verifTer(NSArray rdts) {
		boolean retour = false;
		NSArray tt = (NSArray<EOTerrain>) rdts
				.valueForKey(EORepartDomTerrain.TERRAIN_KEY);
		for (int t = 0; t < tt.count(); t++) {
			EOTerrain tr = (EOTerrain) ((NSMutableArray) tt.objectAtIndex(t))
					.lastObject();
			retour = tr != null;
			break;
		}
		return retour;
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

	protected NSMutableArray<EODomaine> filtreDomaine(
			NSArray<EODomaine> Domaines) {
		NSMutableArray<EODomaine> domaines = new NSMutableArray<EODomaine>();
		for (EODomaine domaine : Domaines) {
			if (wocomponent().accesDomaineEnseignement()
					&& domaine.isIndividu()) {
				if (!domaines.contains(domaine))
					domaines.addObject(domaine);
			}

			if (wocomponent().accesDomaineRecherche() && domaine.isIndividu()) {
				if (!domaines.contains(domaine))
					domaines.addObject(domaine);
			}

			if (wocomponent().accesDomaineEntreprise() && domaine.isStructure()) {
				if (!domaines.contains(domaine))
					domaines.addObject(domaine);
			}

			if (wocomponent().accesDomaineUv() && domaine.isUv()) {
				if (!domaines.contains(domaine))
					domaines.addObject(domaine);
			}
		}
		return domaines;
	}

}
