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

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartDomCapacite;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCapacite;
import org.utt.rfcct.serveur.components.assistants.modules.ModuleAdminCapacite;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXArrayUtilities;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 17 mai 2013
 */
@SuppressWarnings("all")
public class ModuleAdminCapaciteCtrl extends ModuleCtrl<ModuleAdminCapacite> {

	// Les domaines
	private NSArray<EODomaine> lesDomaines;
	private EODomaine itemDomaine;
	private NSMutableArray<EODomaine> checkDomaines;
	private NSMutableArray<EODomaine> removedDomaines;

	// Capacite
	private NSArray lesCapacites = null;
	private EOCapacite uneCapacite;
	private EOCapacite selectedCapacite;
	// Creation Capacite
	private String uneCapacitelblCourt;
	private String uneCapacitelblLong;
	private NSTimestamp uneCapacitedateFin;

	// Activiter modification
	private boolean activUpdateCapacite = false;
	private boolean lockFieldCapacite = false;
	EOEditingContext ec;
	private Integer persId = null;

	/**
	 * @param component
	 */
	public ModuleAdminCapaciteCtrl(ModuleAdminCapacite component) {
		super(component);
		// TODO Auto-generated constructor stub
		ec = ERXEC.newEditingContext();
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

	/**
	 * @return the lesCapacites
	 */
	public NSArray lesCapacites() {
		if (lesCapacites == null) {
			NSMutableArray<EOCapacite> tmp = new NSMutableArray<EOCapacite>();
			NSMutableArray<EOCapacite> tmp2 = new NSMutableArray<EOCapacite>();
			NSArray<EODomaine> domaines = null;
			NSArray<EORepartDomCapacite> rdcs = null;
			domaines = EODomaine.fetchDomaines(ec, wocomponent().mySession().TODAY);
			if (!wocomponent().droitTout())
				domaines = filtreDomaine(domaines).immutableClone();
			domaines = ERXArrayUtilities.arrayWithoutDuplicateKeyValue(
					domaines, EODomaine.REPART_DOM_CAPACITES_KEY);
			rdcs = (NSArray<EORepartDomCapacite>) domaines
					.valueForKey(EODomaine.REPART_DOM_CAPACITES_KEY);
			rdcs = ERXArrayUtilities.arrayWithoutDuplicateKeyValue(rdcs,
					EORepartDomCapacite.CAPACITE_KEY);
			if (rdcs != null && rdcs.count() > 0 && verifCap(rdcs)) {
				NSArray tt = (NSArray<EOCapacite>) rdcs
						.valueForKey(EORepartDomCapacite.CAPACITE_KEY);
				for (int t = 0; t < tt.count(); t++) {
					Object obj = (NSMutableArray) tt.objectAtIndex(t);
					// Correction bug plusieur objet dans un objet
					if (((NSMutableArray) obj).count() > 1) {
						for (Object ob : (NSMutableArray) obj) {
							EOCapacite cp = (EOCapacite) ob;
							if (cp != null)
								tmp2.addObject(cp);
						}
					} else {
						EOCapacite cp = (EOCapacite) ((NSMutableArray) obj)
								.lastObject();
						if (cp != null)
							tmp2.addObject(cp);
					}
				}
				NSArray tmp3 = tmp2.immutableClone();
				tmp.addObjectsFromArray(tmp3);
			} else {
				tmp.addObjectsFromArray(EOCapacite.fetchCapacites(ec, wocomponent().mySession().TODAY));
			}
			EOCapacite capacite = selectedCapacite();
			if (capacite != null && !tmp.contains(capacite)) {
				tmp.addObject(capacite);
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
	public void setLesCapacites(NSArray<EOCapacite> lesCapacites) {
		this.lesCapacites = lesCapacites;
	}

	/**
	 * @return the uneCapacite
	 */
	public EOCapacite uneCapacite() {
		return uneCapacite;
	}

	/**
	 * @param unCapacite
	 *            the unCapacite to set
	 */
	public void setUneCapacite(EOCapacite uneCapacite) {
		this.uneCapacite = uneCapacite;
	}

	/**
	 * @return the selectedCapacite
	 */
	public EOCapacite selectedCapacite() {
		return selectedCapacite;
	}

	/**
	 * @param selectedCapacite
	 *            the selectedCapacite to set
	 */
	public void setSelectedCapacite(EOCapacite selectedCapacite) {
		this.selectedCapacite = selectedCapacite;
	}

	/**
	 * @return the unCapacitelblCourt
	 */
	public String unCapacitelblCourt() {
		return uneCapacitelblCourt;
	}

	/**
	 * @param unCapacitelblCourt
	 *            the unCapacitelblCourt to set
	 */
	public void setUneCapacitelblCourt(String uneCapacitelblCourt) {
		this.uneCapacitelblCourt = uneCapacitelblCourt;
	}

	/**
	 * @return the unCapacitelblLong
	 */
	public String unCapacitelblLong() {
		return uneCapacitelblLong;
	}

	/**
	 * @param unCapacitelblLong
	 *            the unCapacitelblLong to set
	 */
	public void setUneCapacitelblLong(String uneCapacitelblLong) {
		this.uneCapacitelblLong = uneCapacitelblLong;
	}

	/**
	 * @return the unCapacitedateFin
	 */
	public NSTimestamp unCapacitedateFin() {
		return uneCapacitedateFin;
	}

	/**
	 * @param unCapacitedateFin
	 *            the unCapacitedateFin to set
	 */
	public void setUnCapacitedateFin(NSTimestamp unCapacitedateFin) {
		this.uneCapacitedateFin = unCapacitedateFin;
	}

	/**
	 * @return the activUpdateCapacite
	 */
	public boolean isActivUpdateCapacite() {
		return activUpdateCapacite;
	}

	/**
	 * @param activUpdateCapacite
	 *            the activUpdateCapacite to set
	 */
	public void setActivUpdateCapacite(boolean activUpdateCapacite) {
		this.activUpdateCapacite = activUpdateCapacite;
	}

	/**
	 * @return the lockFieldCapacite
	 */
	public boolean isLockFieldCapacite() {
		return lockFieldCapacite;
	}

	/**
	 * @param lockFieldCapacite
	 *            the lockFieldCapacite to set
	 */
	public void setLockFieldCapacite(boolean lockFieldCapacite) {
		this.lockFieldCapacite = lockFieldCapacite;
	}

	/**
	 * @return the uneCapacitedateFin
	 */
	public NSTimestamp uneCapacitedateFin() {
		return uneCapacitedateFin;
	}

	/**
	 * @param uneCapacitedateFin
	 *            the uneCapacitedateFin to set
	 */
	public void setUneCapacitedateFin(NSTimestamp uneCapacitedateFin) {
		this.uneCapacitedateFin = uneCapacitedateFin;
	}

	// Edit Capacite
	public WOActionResults editCapacite() {
		if (selectedCapacite() != null) {
			checkDomaines = null;
			removedDomaines = null;
			wocomponent().setCapacite(selectedCapacite());
			EOCapacite capacite = wocomponent().capacite();
			if (capacite.repartDomCapacites().count() > 0) {
				for (EORepartDomCapacite rdc : capacite.repartDomCapacites()) {
					checkDomaines().addObject(rdc.domaine());
				}
			}
			setActivUpdateCapacite(true);
			setLockFieldCapacite(true);
		} else {
			FactoryCapacite fp = new FactoryCapacite(wocomponent().edc());
			EOCapacite capacite = fp.creerCapaciteVierge(persId());
			wocomponent().resetUneCapaciteField();
			wocomponent().setCapacite(capacite);
			setActivUpdateCapacite(false);
			setLockFieldCapacite(false);
			checkDomaines = null;
			removedDomaines = null;
		}
		return wocomponent().submit();
	}

	// Editer capacite champs actif
	public WOActionResults editCapaciteFiled() {
		setLockFieldCapacite(false);
		EOCapacite capacite = wocomponent().capacite();
		// setSelectedDomaineCapacite(capacite
		// .repartDomCapacites(EORepartDomCapacite.CAPACITE.eq(capacite))
		// .lastObject().domaine());
		return wocomponent().submit();
	}

	// Annuler capacite champs actif
	public WOActionResults annulerCapaciteFiled() {
		setLockFieldCapacite(true);
		return wocomponent().submit();
	}

	protected boolean verifCap(NSArray rdcs) {
		boolean retour = false;
		NSArray tt = (NSArray<EOCapacite>) rdcs
				.valueForKey(EORepartDomCapacite.CAPACITE_KEY);
		for (int t = 0; t < tt.count(); t++) {
			EOCapacite cp = (EOCapacite) ((NSMutableArray) tt.objectAtIndex(t))
					.lastObject();
			retour = cp != null;
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
