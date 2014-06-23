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
package org.utt.rfcct.serveur.components.commons;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 11 avr. 2013
 */
public class Tabs {
	private NSMutableDictionary<String, Tab> indexedTabs = new NSMutableDictionary<String, Tab>();
	private NSMutableArray<Tab> _tabs = new NSMutableArray<Tabs.Tab>();
	private Tab selectedTab;
	private TabsDelegate tabsDelegate;

	public Tabs(Tab firstTab, Tab... otherTabs) {
		selectTab(firstTab);
		addTab(firstTab);
		for (Tab tab : otherTabs) {
			addTab(tab);
		}
	}
	
	public Tabs() {
	}

	public void selectTab(Tab tab) {
		if (selectedTab != tab) {
			if (tabsDelegate == null
					|| tabsDelegate.shouldChangeSelectedTab(selectedTab, tab)) {
				this.selectedTab = tab;
			}
		}
	}

	public void addTab(Tab tab) {
		if (tab != null) {
			indexedTabs.setObjectForKey(tab, tab.getId());
			_tabs.addObject(tab);
			tab.setTabs(this);
		}
	}

	public NSArray<Tab> tabs() {
		return _tabs;
	}

	public void setTabsDelegate(TabsDelegate tabsDelegate) {
		this.tabsDelegate = tabsDelegate;
	}

	public Tab selectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Tab selectedTab) {
		selectTab(selectedTab);
	}

	public static class Tab {

		private EOEditingContext editingContext;
		private String libelle;
		private String id;
		private Tabs tabs;
		private EOCompetence tabCompetence;
		private EODomaine tabDomaine;

		public Tab(String libelle, String id) {
			this.libelle = libelle;
			this.id = id;
		}
		
		public Tab() {
		}

		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		public String getLibelle() {
			return libelle;
		}

		/**
		 * @param libelle the libelle to set
		 */
		public void setLibelle(String libelle) {
			this.libelle = libelle;
		}

		public EOEditingContext getEditingContext() {
			if (editingContext == null)
				editingContext = ERXEC.newEditingContext();
			return editingContext;
		}

		public boolean isSelected() {
			return tabs.selectedTab != null && tabs.selectedTab == this;
		}

		public void setSelected(boolean willBeSelected) {
			if (willBeSelected) {
				tabs.selectTab(this);
			}
		}

		protected void setTabs(Tabs tabs) {
			this.tabs = tabs;
		}

		/**
		 * @return the tabCompetence
		 */
		public EOCompetence tabCompetence() {
			return tabCompetence;
		}

		/**
		 * @param tabCompetence the tabCompetence to set
		 */
		public void setTabCompetence(EOCompetence tabCompetence) {
			this.tabCompetence = tabCompetence;
		}

		/**
		 * @return the tabDomaine
		 */
		public EODomaine tabDomaine() {
			return tabDomaine;
		}

		/**
		 * @param tabDomaine the tabDomaine to set
		 */
		public void setTabDomaine(EODomaine tabDomaine) {
			this.tabDomaine = tabDomaine;
		}

	}

	public static interface TabsDelegate {
		public boolean shouldChangeSelectedTab(Tab selected, Tab willBeSelected);
	}
}
