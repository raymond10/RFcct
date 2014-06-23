package org.utt.rfcct.serveur.components.tree;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTypeTiers;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOVDataRealCompTree;
import org.utt.rfcct.serveur.components.controlers.CompetencesAjaxGroupeTreeViewCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXQ;

@SuppressWarnings("all")
public class CompetencesAjaxGroupeTreeView extends ATreeViewComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -631939898496384784L;
	/**
	 * Facultatif. Binding pour specifier un NSDictionary de paires
	 * "Libelle filtre"/Qualifier. Les Libelles apparaissent dans une liste
	 * deroulante, le qualifier correspondant au libelle selectionne est ensuite
	 * applique au treeView. Les qualifier doivent êre applicable aux objets
	 * EOVDataRealCompTree.
	 */
	public static final String USER_FILTERS_DICTIONARY_BDG = "userFiltersDictionary";
	/**
	 * Liste des type de tiers en entrée de filtre
	 */
	public static final String USER_TYPETIER_LIST_BDG = "userTypeTierList";/**
	 * Liste de domaine en entrée de filtre
	 */
	public static final String USER_DOMAINE_LIST_BDG = "userDomaineList";
	/**
	 * Facultatif. Binding pour specifier s'il faut demander une confirmation
	 * avant la selection.
	 */
	public static final String CONFIRM_BEFORE_SELECTION_BDG = "confirmBeforeSelection";
	/**
	 * Facultatif. Binding spécifiant l'action a exécuter après sélection d'un
	 * groupe
	 */
	public static final String ON_SUCCESS_SELECT_BDG = "onSuccessSelect";
	/** Facultatif. Binding boolean spécifiant si l'arbre doit être reseté */
	public static final String RESET_BDG = "reset";
	/**
	 * Facultatif. Binding spécifiant la classe css à appliquer sur le gorupe
	 * sélectionné
	 */
	public static final String CSS_SELECTED_GROUPE_BDG = "cssSelectedGroupe";
	/** Facultatif. Controleur a utiliser. */
	public static final String CTRL_BDG = "ctrl";
	/** Facultatif. */
	public static final String EXPAND_TO_LEVEL_BDG = "expandToLevel";
	/** Facultatif. */
	public static final String USER_EDITINGCONTEXT_BDG = "editingContext";
	
	private NSArray<EOTypeTiers> listTypeTiers;
	private EOTypeTiers unTypeTier, unTypeTierSelectionne;
	private NSArray<EODomaine> listDomaines;
	private EODomaine unDomaine, unDomaineSelectionne;
	private NSDictionary filtres;
	private String unFiltre, unFiltreSelectionne;
	private EOQualifier userFiltreQualifier;
	private CompetencesAjaxGroupeTreeViewCtrl ctrl;
	private EOVDataRealCompTree selection;
	private String filtreTexte;
	private boolean _firstRender = true;
	private int groupeIndex;
	private EOEditingContext editingContext;

	public CompetencesAjaxGroupeTreeView(WOContext context) {
		super(context);
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	@Override
	public void appendToResponse(WOResponse response, WOContext context) {
		if (ctrl().getWocomponent() == null) {
			ctrl().setWocomponent(this);
		}
		if (hasBinding(RESET_BDG)) {
			if (Boolean.TRUE.equals(valueForBinding(RESET_BDG))) {
				resetTree();
				expandToLevel();
			}
			if (canSetValueForBinding(RESET_BDG)) {
				setValueForBinding(Boolean.FALSE, RESET_BDG);
			}
		}
		if (_firstRender) {
			expandToLevel();
		}
		super.appendToResponse(response, context);
		_firstRender = false;
	}

	private void expandToLevel() {
		if (hasBinding(EXPAND_TO_LEVEL_BDG)) {
			Integer level = (Integer) valueForBinding(EXPAND_TO_LEVEL_BDG);
			ctrl().getMyTreeModel().setRootTreeNode(ctrl().rootGroupe());
			ctrl().expandAllNodesToLevel(level);
		}
	}

	private void resetTree() {
		ctrl().setRootGroupe(null);
		ctrl().rootGroupe().setChildren(null);
		expandToLevel();
		EOVDataRealCompTree groupe = selection();
		if (groupe != null)
			ctrl().selectObjectInTree(ctrl().rootGroupe(), groupe);
	}

	public CompetencesAjaxGroupeTreeViewCtrl ctrl() {
		if (ctrl == null) {
			if (hasBinding(CTRL_BDG)) {
				ctrl = (CompetencesAjaxGroupeTreeViewCtrl) valueForBinding(CTRL_BDG);
			}
			if (ctrl == null) {
				ctrl = new CompetencesAjaxGroupeTreeViewCtrl(this);
			}
		}
		return ctrl;
	}

	public WOActionResults filtrer() {
		String filtre = unFiltreSelectionne();
		if (filtre != null) {
			if (filtre.equalsIgnoreCase("Tous")) {
				setUserFiltreQualifier(null);
			} else {
				setUserFiltreQualifier((EOQualifier) filtres
						.objectForKey(filtre));
			}
		} else {
			setUserFiltreQualifier(null);
		}
		resetTree();
		return null;
	}

	public WOActionResults resetFilters() {
		filtreTexte = null;
		setUnFiltreSelectionne(null);
		setUserFiltreQualifier(null);
		listTypeTiers = null;
		setUnTypeTierSelectionne(null);
		setUnDomaineSelectionne(null);
		resetTree();
		return null;
	}

	public WOActionResults selectionner() {
		ctrl().afficherGroupeSelectionne();
		return (WOActionResults) valueForBinding(ON_SUCCESS_SELECT_BDG);
	}

	public String cssClassForSelectedLink() {
		if (hasBinding(CSS_SELECTED_GROUPE_BDG) && selection() != null
				&& selection().equals(ctrl().unGroupe().object())) {
			return (String) valueForBinding(CSS_SELECTED_GROUPE_BDG);
		} else {
			return String.valueOf("");
		}
	}

	public NSDictionary filtres() {
		if (filtres == null) {
			filtres = (NSDictionary) valueForBinding(USER_FILTERS_DICTIONARY_BDG);
		}
		return filtres;
	}
	
	/**
	 * @return the editingContext
	 */
	public EOEditingContext editingContext() {
		if(editingContext == null)
			editingContext = (EOEditingContext) valueForBinding(USER_EDITINGCONTEXT_BDG);
		return editingContext;
	}

	public NSArray<EOTypeTiers> listTypeTiers() {
		if(listTypeTiers == null) {
			listTypeTiers = EOTypeTiers.fetchTypeTierActuel(editingContext(), mySession().TODAY);
		}
		return listTypeTiers;
	}
	
	public NSArray<EODomaine> listDomaines() {
		if(unTypeTierSelectionne() == null) {
			listDomaines = EODomaine.fetchDomaines(editingContext(), mySession().TODAY);
		} else {
			listDomaines = EODomaine.fetchDomainesPourTiers(editingContext(), mySession().TODAY, unTypeTierSelectionne().typeTiersId());
		}
		return listDomaines;
	}

	public boolean isFiltrageAvailable() {
		boolean isFiltrageAvailable = hasBinding(USER_FILTERS_DICTIONARY_BDG);
		if (isFiltrageAvailable) {
			NSDictionary dicoFiltres = filtres();
			isFiltrageAvailable = dicoFiltres.count() > 0 ? true : false;
		}
		return isFiltrageAvailable;
	}

	public void setUserFiltreQualifier(EOQualifier userFiltreQualifier) {
		this.userFiltreQualifier = userFiltreQualifier;
	}

	public EOQualifier getUserFiltreQualifier() {
		return userFiltreQualifier;
	}

	private EOQualifier getTexteQualifier() {
		if (filtreTexte != null)
			return ERXQ.contains(EOVDataRealCompTree.COMPX_LIBELLE_KEY,
					filtreTexte);
		else
			return null;
	}

	public EOQualifier getQualifier() {
		EOQualifier qual = getTreeQualifier();
		if (qual != null) {
			if (getUserFiltreQualifier() != null) {
				qual = ERXQ.and(qual, getUserFiltreQualifier());
			}
		} else {
			qual = getUserFiltreQualifier();
		}
		EOQualifier qualTexte = getTexteQualifier();
		if (qualTexte != null) {
			qual = ERXQ.and(qual, qualTexte);
		}
		return qual;
	}
	
	public EOQualifier getRootQualifer() {
		EOQualifier qual = EOVDataRealCompTree.COMPX_ID_PERE.isNull();
		if(unDomaineSelectionne() != null) {
			NSArray<String> listDomeId = new NSArray<String>(unDomaineSelectionne().primaryKey());
			qual = ERXQ.and(qual, EOVDataRealCompTree.COMPX_ID.in(listDomeId));
		} else {
			if(unTypeTierSelectionne() != null) {
				NSMutableArray<String> listIds = new NSMutableArray<String>(); 
				for(EODomaine domaine: listDomaines()) {
					listIds.addObjects(domaine.primaryKey());
				}
				qual = ERXQ.and(qual, EOVDataRealCompTree.COMPX_ID.in(listIds.immutableClone()));
			}
		}
		return qual;
	}

	public NSArray getTreeExclusions() {
		return (NSArray) valueForBinding(TREE_EXCLUSIONS_BDG);
	}

	public EOVDataRealCompTree treeRootObject() {
		return (EOVDataRealCompTree) valueForBinding(TREE_ROOT_OBJECT_BDG);
	}

	public Boolean isGroupeSelectionnable() {
		EOVDataRealCompTree tmpTree = null;
		if(ctrl().unGroupe().object() instanceof EOVDataRealCompTree) {
			tmpTree = (EOVDataRealCompTree) ctrl().unGroupe().object();
		}
		if (tmpTree.competenceId() != null && droitTout()) {
			return true;
		}
		return (tmpTree.competenceId() != null && peutConsulterCompetence());
	}

	public String selectionOnClickBefore() {
		if (hasBinding(CONFIRM_BEFORE_SELECTION_BDG)) {
			if (((Boolean) valueForBinding(CONFIRM_BEFORE_SELECTION_BDG))
					.booleanValue()) {
				return ("confirm('Voulez-vous selectionner le groupe "
						+ jsEncode(ctrl().unGroupeLibelle()) + " ?')");
			}
		}
		return null;
	}

	public String filtrePopUpID() {
		return getComponentId() + "_filtrePopUp";
	}

	public String unAjaxTreeID() {
		return getComponentId() + "_ajaxTree";
	}

	public String containerAjaxTreeID() {
		return getComponentId() + "_containerAjaxTree";
	}

	public String groupeLinkId() {
		EOVDataRealCompTree groupe = (EOVDataRealCompTree) ctrl().unGroupe()
				.object();
		return getComponentId() + "_groupeLink_" + groupe.compxId();
	}

	/**
	 * @return the unFiltreSelectionne
	 */
	public String unFiltreSelectionne() {
		return unFiltreSelectionne;
	}

	/**
	 * @param unFiltreSelectionne
	 *            the unFiltreSelectionne to set
	 */
	public void setUnFiltreSelectionne(String unFiltreSelectionne) {
		this.unFiltreSelectionne = unFiltreSelectionne;
	}

	/**
	 * @return the unFiltre
	 */
	public String unFiltre() {
		return unFiltre;
	}

	/**
	 * @param unFiltre
	 *            the unFiltre to set
	 */
	public void setUnFiltre(String unFiltre) {
		this.unFiltre = unFiltre;
	}

	/**
	 * @return the unTypeTier
	 */
	public EOTypeTiers unTypeTier() {
		return unTypeTier;
	}

	/**
	 * @param unTypeTier the unTypeTier to set
	 */
	public void setUnTypeTier(EOTypeTiers unTypeTier) {
		this.unTypeTier = unTypeTier;
	}

	/**
	 * @return the unTypeTierSelectionne
	 */
	public EOTypeTiers unTypeTierSelectionne() {
		return unTypeTierSelectionne;
	}

	/**
	 * @param unTypeTierSelectionne the unTypeTierSelectionne to set
	 */
	public void setUnTypeTierSelectionne(EOTypeTiers unTypeTierSelectionne) {
		this.unTypeTierSelectionne = unTypeTierSelectionne;
	}

	/**
	 * @return the unDomaine
	 */
	public EODomaine unDomaine() {
		return unDomaine;
	}

	/**
	 * @param unDomaine the unDomaine to set
	 */
	public void setUnDomaine(EODomaine unDomaine) {
		this.unDomaine = unDomaine;
	}

	/**
	 * @return the unDomaineSelectionne
	 */
	public EODomaine unDomaineSelectionne() {
		return unDomaineSelectionne;
	}

	/**
	 * @param unDomaineSelectionne the unDomaineSelectionne to set
	 */
	public void setUnDomaineSelectionne(EODomaine unDomaineSelectionne) {
		this.unDomaineSelectionne = unDomaineSelectionne;
	}

	/**
	 * @param ctrl
	 *            the ctrl to set
	 */
	public void setCtrl(CompetencesAjaxGroupeTreeViewCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public EOVDataRealCompTree selection() {
		if (selection != valueForBinding(SELECTION_BDG))
			selection = (EOVDataRealCompTree) valueForBinding(SELECTION_BDG);
		return selection;
	}

	/**
	 * @param selection
	 */
	public void setSelection(EOVDataRealCompTree selection) {
		this.selection = selection;
		setValueForBinding(selection,
				CompetencesAjaxGroupeTreeView.SELECTION_BDG);
	}

	public String getFiltreTexte() {
		return filtreTexte;
	}

	public void setFiltreTexte(String filtreTexte) {
		this.filtreTexte = filtreTexte;
	}

	/**
	 * @return the groupeIndex
	 */
	public int getGroupeIndex() {
		return groupeIndex;
	}

	/**
	 * @param groupeIndex
	 *            the groupeIndex to set
	 */
	public void setGroupeIndex(int groupeIndex) {
		this.groupeIndex = groupeIndex;
	}

	/**
	 * On met la sélection en gras et on enlève le bold sur toutes les autres
	 * lignes
	 * 
	 * @return onCompleteGroupeSelection
	 */
	public String onCompleteGroupeSelection() {
		String cssSelectedClass = (String) valueForBinding(CSS_SELECTED_GROUPE_BDG);
		String onCompleteGroupeSelection = "function(){";
		onCompleteGroupeSelection += "var selections = $$('a[id^="
				+ getComponentId()
				+ "_groupeLink_]'); selections.each(function(e){e.removeClassName('"
				+ cssSelectedClass + "');});";
		onCompleteGroupeSelection += "$('" + groupeLinkId()
				+ "').addClassName('" + cssSelectedClass + "');";
		onCompleteGroupeSelection += "}";
		return onCompleteGroupeSelection;
	}
	
	public WOActionResults refresh() {
		listDomaines();
		return null;
	}
}