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

import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.utils.factory.FactoryCompetence;
import org.utt.rfcct.serveur.components.Competence;
import org.utt.rfcct.serveur.components.assistants.template.TemplateCompTierBase;
import org.utt.rfcct.serveur.components.commons.Tabs;
import org.utt.rfcct.serveur.components.commons.Tabs.Tab;
import org.utt.rfcct.serveur.components.controlers.ModuleCompetenceTiersPrincipaleCtrl;
import org.utt.rfcct.serveur.components.windows.PersonneMultipleSrchPage;
import org.utt.rfcct.serveur.components.windows.Tiers.PersonneTierSrchPage;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXEC;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 4 juil. 2013
 */
@SuppressWarnings("all")
public class ModuleCompetenceTiersPrincipale extends ModuleAssistant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8126119555573338021L;
	private ModuleCompetenceTiersPrincipaleCtrl ctrl;
	private boolean isResetAjoutPersonnesDialog;
	private EOEditingContext editingContextForAddPerson;
	private ERXDisplayGroup selectedPersonnesDisplayGroup;
	private boolean tiersTypeIndiv = false;
	private boolean tiersTypeStr = false;
	private boolean tiersTypeUv = false;
	private EOQualifier qualifierForIndividu;
	private IPersonne selectedTier;
	private String titre;
	private NSMutableArray competenceList;
	private EOCompetence competenceItem;
	public int indexCompetences;
	private String font;
	// Tabs
	private Tabs dynamicTabs;
	private Tab selectTab;
	private Tab genTab;

	// AnotherTab
	private NSArray<NSDictionary> _tabs;
	private NSDictionary _tabSelection;
	Object avoidNullComponent;
	private Boolean refresh;
	Integer count;

	public ModuleCompetenceTiersPrincipale(WOContext context) {
		super(context);
		NSLog.out.appendln("ModuleCompetenceTiersPrincipale");
		count = 0;
		refresh = false;
	}
	
	@Override
	public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
		if(refresh()) {
			setRefresh(false);
		}
		super.appendToResponse(woresponse, wocontext);
	}

	/**
	 * @return the ctrl
	 */
	public ModuleCompetenceTiersPrincipaleCtrl ctrl() {
		if (ctrl == null)
			ctrl = new ModuleCompetenceTiersPrincipaleCtrl(this);
		return ctrl;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(ModuleCompetenceTiersPrincipaleCtrl ctrl) {
		this.ctrl = ctrl;
	}

	// Module
	public boolean isTerminerDisabled() {
		NSMutableArray<EOCompetence> competences = myCompetences();
		boolean isTerminerDisabled = competences.count() > 0;
		return isTerminerDisabled;
	}

	public String TRTypeTierId() {
		// TODO
		return "TRTypeTier";
	}

	public String TRTiersId() {
		// TODO
		return "TRTiers";
	}

	public String TRTiersSelectionId() {
		// TODO
		return "TRTiersSelection";
	}

	// Id de la fen�tre de recherche de personne
	public String personnesDialogId() {
		return "PersDiag_" + uniqueDomId();
	}

	public String TRDomaineId() {
		return "TRDomaine";
	}

	public String TRComponentId() {
		return "TRComponent";
	}

	// Gestion comp�tences
	public String tabsId() {
		return "Tabs_" + uniqueDomId();
	}

	public String libelle() {
		String lbl = "";
		lbl = competenceItem().domaine().lblCourt();
		return lbl;
	}

	public String dynaTabId() {
		String lbl = "";
		lbl = "DynaTab_" + indexCompetences + 1;
		return lbl;
	}

	/*
	 * public String dynaTabId() { return "DynaTab_" + compId() + uniqueDomId();
	 * }
	 */

	public String updateContainerJsAferHide() {
		String func = "function() { TRTiersSelectionUpdate(); TRDomaineUpdate();";
		String updateId = null;
		// stringValueForBinding(BDG_UPDATE_CONTAINER_ID, )
		if (updateId != null) {
			func = func + updateId + "Update(); }";
		} else {
			func = func + " }";
		}
		return func;
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

	/**
	 * @return the editingContextForAddPerson
	 */
	public EOEditingContext editingContextForAddPerson() {
		if (editingContextForAddPerson == null) {
			editingContextForAddPerson = ERXEC.newEditingContext();
		}
		return editingContextForAddPerson;
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
	 * @return the selectedTier
	 */
	public IPersonne selectedTier() {
		return selectedTier;
	}

	/**
	 * @param selectedTier
	 *            the selectedTier to set
	 */
	public void setSelectedTier(IPersonne selectedTier) {
		this.selectedTier = selectedTier;
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

	/**
	 * @return the competenceItem
	 */
	public EOCompetence competenceItem() {
		return competenceItem;
	}

	/**
	 * @param competenceItem
	 *            the competenceItem to set
	 */
	public void setCompetenceItem(EOCompetence competenceItem) {
		this.competenceItem = competenceItem;
	}

	/**
	 * @return the font
	 */
	public String font() {
		return font;
	}

	/**
	 * @param font
	 *            the font to set
	 */
	public void setFont(String font) {
		this.font = font;
	}

	public boolean activeGenerate() {
		return (mySession().leTier() != null && ctrl().checkDomaines().count() > 0);
	}

	// Advanced dynamic Tabs
	/**
	 * @return the dynamicTabs
	 */
	public Tabs dynamicTabs() {
		if (dynamicTabs == null)
			dynamicTabs = new Tabs();
		return dynamicTabs;
	}

	/**
	 * @param dynamicTabs
	 *            the dynamicTabs to set
	 */
	public void setDynamicTabs(Tabs dynamicTabs) {
		this.dynamicTabs = dynamicTabs;
	}

	/**
	 * @return the selectTab
	 */
	public Tab selectTab() {
		return selectTab;
	}

	/**
	 * @param selectTab
	 *            the selectTab to set
	 */
	public void setSelectTab(Tab selectTab) {
		this.selectTab = selectTab;
	}

	/**
	 * @return the genTab
	 */
	public Tab genTab() {
		return genTab;
	}

	/**
	 * @param genTab
	 *            the genTab to set
	 */
	public void setGenTab(Tab genTab) {
		this.genTab = genTab;
	}

	public void createTabs() {
		for (EOCompetence competenceItem : competences()) {
			setCompetenceItem(competenceItem);
			setGenTab(new Tab(competenceItem().domaine().lblCourt(),
					competenceItem().domaine().lblCourt() + "_"
							+ competenceItem().domaine().primaryKey()));
			genTab().setTabCompetence(competenceItem());
			genTab().setTabDomaine(competenceItem().domaine());
			dynamicTabs().addTab(genTab());
		}
		dynamicTabs().setSelectedTab(dynamicTabs().tabs().get(0));
	}

	//

	// Basic dynamic tabs

	/**
	 * @return the _tabs
	 */
	public NSArray<NSDictionary> tabs() {
		if (_tabs == null) {
			NSMutableArray<NSDictionary> tabs = new NSMutableArray<NSDictionary>();
			TemplateCompTierBase template = new TemplateCompTierBase(context());
			template.setCompetence(null);
			template.setDomaine(null);
			for (EOCompetence competenceItem : competences()) {
				EODomaine domaineItem = competenceItem().domaine();
				EOEditingContext ec = competenceItem().editingContext();
				setCompetenceItem(competenceItem);
				template.setCompetence(competenceItem());
				template.setDomaine(domaineItem);
				NSMutableDictionary<Object, String> competencesTab = new NSMutableDictionary<Object, String>(
						competenceItem().domaine().lblCourt(), "tabLabel");
				competencesTab.takeValueForKey(template.getClass().getName(),
						"wocomponentName");
				avoidNullComponent = template.getClass().getName();
				competencesTab.takeValueForKey(competenceItem(), "competence");
				competencesTab.takeValueForKey(domaineItem, "domaine");
				competencesTab.takeValueForKey(ec, "context");
				tabs.addObject(competencesTab);
			}
			_tabs = tabs.immutableClone();
			//if(count > 0) {
			//	count = 0;
			//	refresh = false;
			//}
		}
		return _tabs;
	}

	/**
	 * @param _tabs
	 *            the _tabs to set
	 */
	public void setTabs(NSArray<NSDictionary> _tabs) {
		this._tabs = _tabs;
	}

	/**
	 * @return the _tabSelection
	 */
	public NSDictionary tabSelection() {
		return _tabSelection;
	}

	/**
	 * @param _tabSelection
	 *            the _tabSelection to set
	 */
	public void setTabSelection(NSDictionary _tabSelection) {
		this._tabSelection = _tabSelection;
		// String tabLabelString = (String)
		// _tabSelection.valueForKey("tabLabel");
	}

	public Object selectedTabsWOComponent() {
		Object wocomponent = null;
		try {
			wocomponent = tabSelection().valueForKey("wocomponentName");
		} catch (Exception e) {
			// Corrige le bug nullPointer exception sur le nom du composant
			wocomponent = avoidNullComponent;
		}
		return wocomponent;
	}

	public Object selectedTabCompetence() {
		Object competence = competence();
		try {
			competence = tabSelection().valueForKey("competence");
		} catch (Exception e) {

		}
		return competence;
	}

	public Object selectedTabDomaine() {
		Object domaine = competence().domaine();
		try {
			domaine = tabSelection().valueForKey("domaine");
		} catch (Exception e) {

		}
		return domaine;
	}

	public Object selectedTabContext() {
		Object context = edc();
		try {
			context = tabSelection().valueForKey("context");
		} catch (Exception e) {

		}
		return context;
	}

	//
	public boolean butonTierEnable() {
		return ctrl().tiersTypePers() && isInAssistant();
	}

	// Affichage de la fen�tre dev recherche de personne
	public WOActionResults openAjouterPersonnes() {
		setResetAjoutPersonnesDialog(true);
		PersonneTierSrchPage tier = (PersonneTierSrchPage) pageWithName(PersonneTierSrchPage.class
				.getName());
		tier.setEditingContext(editingContextForAddPerson());
		tier.setSelectedPersonnesDisplayGroup(selectedPersonnesDisplayGroup());
		tier.setTiersIndiv(tiersTypeIndiv());
		tier.setTiersStr(tiersTypeStr());
		tier.setQualifierForIndividu(qualifierForIndividu());
		tier.setResetAjoutPersonnesDialog(true);
		tier.setPersId(persId());
		tier.setTitre(titre());
		return tier;
	}

	public WOActionResults refreshTiers() {
		return null;
	}

	// G�n�ration des comp�tences
	public WOActionResults compGenerate() {
		mySession().setCompetences(null);
		setCompetences(null);
		setDynamicTabs(null);
		_tabs = null;
		if (ctrl().checkDomaines().count() > 0) {
			refresh = true;
			for (EODomaine domaine : ctrl().checkDomaines()) {
				FactoryCompetence fc = new FactoryCompetence(
						ERXEC.newEditingContext(), true);
				try {
					EOCompetence competence = fc
							.creerCompetenceVierge(persId());
					EOEditingContext ec = competence.editingContext();
					competence.setDomaine(domaine.localInstanceIn(ec));
					competence.setDomaineRelationship(domaine
							.localInstanceIn(ec));
					competences().addObject(competence);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mySession().setCompetences(competences());
			createTabs();
		}
		count++;
		return null;
	}

	public String labelTier() {
		String label = "Tier (Enseignants, Chercheurs, Entreprises)";
		if (ctrl().selectionTypeTier() != null
				&& ctrl().selectionTypeTier().isIndividu())
			label = "Enseignants, Chercheurs...";
		if (ctrl().selectionTypeTier() != null
				&& (ctrl().selectionTypeTier().isStructure()))
			label = "Entreprises, Partenaires ...";
		if (ctrl().selectionTypeTier() != null
				&& ctrl().selectionTypeTier().isUv())
			label = "Unit� de valeurs ...";
		return label;
	}
	
	/**
	 * @return the refresh
	 */
	public Boolean refresh() {
		return refresh;
	}

	/**
	 * @param refresh the refresh to set
	 */
	public void setRefresh(Boolean refresh) {
		this.refresh = refresh;
	}
}
