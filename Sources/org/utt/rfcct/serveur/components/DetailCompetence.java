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

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlwebapp.server.CktlEOUtilities;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.rfcct.serveur.components.commons.Tabs;
import org.utt.rfcct.serveur.components.commons.Tabs.Tab;
import org.utt.rfcct.serveur.components.commons.Tabs.TabsDelegate;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSLog;

public class DetailCompetence extends BaseComponent implements TabsDelegate {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2140251156961836743L;
	private static final Logger Log = Logger.getLogger(DetailCompetence.class);
	public static final String BINDING_competence = "competence";
	private EOCompetence competence;
	private Tabs competenceTabs;
	private Tab domTiersMatTab;
	private Tab tierTab;
	private Tab selectedTab;

	public DetailCompetence(WOContext context) {
		super(context);
		NSLog.out.appendln("DetailCompetence");
	}

	@Override
	public void awake() {
		super.awake();
		if (competenceTabs == null) {
			if (multi()) {
				competenceTabs = new Tabs(domTiersMatTab = new Tab(
						"Compétences pour plusieurs tiers", "competences"));
			} else {
				competenceTabs = new Tabs(tierTab = new Tab(
						"Compétence pour un tier", "competence"));
			}
			competenceTabs.setTabsDelegate(this);
		}
	}

	/**
	 * @return the competence
	 */
	public EOCompetence competence() {
		if (competence == null && hasBinding(BINDING_competence)) {
			competence = (EOCompetence) valueForBinding(BINDING_competence);
		}
		return competence;
	}

	/**
	 * @param competence
	 *            the competence to set
	 */
	public void setCompetence(EOCompetence competence) {
		this.competence = competence;
	}

	/**
	 * @return the competenceTabs
	 */
	public Tabs competenceTabs() {
		return competenceTabs;
	}

	/**
	 * @param competenceTabs
	 *            the competenceTabs to set
	 */
	public void setCompetenceTabs(Tabs competenceTabs) {
		this.competenceTabs = competenceTabs;
	}

	/**
	 * @return the domTiersMatTab
	 */
	public Tab domTiersMatTab() {
		return domTiersMatTab;
	}

	/**
	 * @param domTiersMatTab
	 *            the domTiersMatTab to set
	 */
	public void setDomTiersMatTab(Tab domTiersMatTab) {
		this.domTiersMatTab = domTiersMatTab;
	}

	/**
	 * @return the tierTab
	 */
	public Tab getTierTab() {
		return tierTab;
	}

	/**
	 * @param tierTab
	 *            the tierTab to set
	 */
	public void setTierTab(Tab tierTab) {
		this.tierTab = tierTab;
	}

	/**
	 * @return the selectedTab
	 */
	public Tab selectedTab() {
		return selectedTab;
	}

	/**
	 * @param selectedTab
	 *            the selectedTab to set
	 */
	public void setSelectedTab(Tab selectedTab) {
		this.selectedTab = selectedTab;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.utt.rfcct.serveur.components.commons.Tabs.TabsDelegate#
	 * shouldChangeSelectedTab
	 * (org.utt.rfcct.serveur.components.commons.Tabs.Tab,
	 * org.utt.rfcct.serveur.components.commons.Tabs.Tab)
	 */
	@Override
	public boolean shouldChangeSelectedTab(Tab selected, Tab willBeSelected) {
		// TODO Auto-generated method stub
		EOEditingContext ec = selected.getEditingContext();
		boolean hasChanges = ec.hasChanges();
		if (hasChanges && Log.isDebugEnabled()) {
			// session().addSimpleInfoMessage("RFcct",
			// "Des changements n'ont pas été enregistrés sur l'onglet \"" +
			// selected.getLibelle() +
			// "\", veuillez enregistrer ou annuler ces changements");
			String message = "\n------------------------------------------------------------------------------------------"
					+ "\n"
					+ "Passage de l'onglet '"
					+ selected.getLibelle()
					+ "' à l'onglet '"
					+ willBeSelected.getLibelle()
					+ "' : "
					+ "Des changements n'ont pas été enregistrés sur le numéro compétence"
					+ competence().numeroCompetence()
					+ "\n"
					+ "------------------------------------------------------------------------------------------"
					+ "\n";
			message = message + CktlEOUtilities.printChanges(ec);
			Log.debug(message);
		}
		// AT : temporairement on laisse passer à l'onglet suivant tant que le
		// mystère des changements non voulus n'est pas résolu
		return true;
	}

	public boolean multi() {
		return competence().temMulti().equals("O");
	}

}
