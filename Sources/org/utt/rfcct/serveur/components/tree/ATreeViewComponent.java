/**
 * 
 */
package org.utt.rfcct.serveur.components.tree;

import org.utt.rfcct.serveur.components.BaseComponent;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 19 ao�t 2013
 */
public class ATreeViewComponent extends BaseComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6840460204232517770L;
	public static final String SELECTION_BDG = "selection";

	public static final String TREE_VIEW_TITLE_BDG = "treeViewTitle";
	public static final String TREE_VIEW_WIDTH_BDG = "treeViewWidth";
	public static final String TREE_VIEW_HEIGHT_BDG = "treeViewHeight";
	public static final String TREE_VIEW_CLASS_NAME_BDG = "treeViewClassName";
	public static final String TREE_VIEW_SHOWROOT_BDG = "treeViewShowRoot";
	public static final String TREE_VIEW_ALLEXPANDED_BDG = "treeViewAllExpanded";

	/**
	 * Facultatif. Binding pour specifier le qualifier a appliquer pour
	 * recuperer les enfants d'un noeud. Si non null, le qualifier se rajoute a
	 * l'eventuel filtre selectionne par l'utilisateur.
	 */
	public static final String TREE_QUALIFIER_BDG = "treeQualifier";

	/** Facultatif. Binding pour specifier la racine de l'arbre. */
	public static final String TREE_ROOT_OBJECT_BDG = "treeRootObject";

	/**
	 * Facultatif. Permet de specifier un NSArray d'objets a exclure de
	 * l'affichage de l'arbre.
	 */
	public static final String TREE_EXCLUSIONS_BDG = "treeExclusions";

	public static final String TREE_COLLAPSED_IMG_BDG = "treeCollapsedImage";
	public static final String TREE_COLLAPSED_IMG_FWK_BDG = "treeCollapsedImageFramework";
	public static final String TREE_EXPANDED_IMG_BDG = "treeExpandedImage";
	public static final String TREE_EXPANDED_IMG_FWK_BDG = "treeExpandedImageFramework";
	public static final String TREE_LEAF_IMG_BDG = "treeLeafImage";
	public static final String TREE_LEAF_IMG_FWK_BDG = "treeLeafImageFramework";

	protected String collapsedImage, collapsedImageFramework;
	protected String expandedImage, expandedImageFramework;
	protected String leafImage, leafImageFramework;
	protected EOQualifier treeQualifier;

	private Boolean isTreeViewOpened = Boolean.FALSE;

	public ATreeViewComponent(WOContext context) {
		super(context);
	}

	/**
	 * @return the collapsedImage
	 */
	public String getTreeCollapsedImage() {
		if (collapsedImage == null) {
			collapsedImage = (String) valueForBinding(TREE_COLLAPSED_IMG_BDG);
			if (collapsedImage == null) {
				collapsedImage = "images/ico_node_collapsed_gris_16.png";
				collapsedImageFramework = "FwkCktlAjaxWebExt.framework";
			}
		}

		return collapsedImage;
	}

	public boolean getShowRoot() {
		boolean returShowRoot = false;
		if (hasBinding(TREE_VIEW_SHOWROOT_BDG)) {
			returShowRoot = (Boolean) valueForBinding(TREE_VIEW_SHOWROOT_BDG);
		}
		return returShowRoot;
	}

	public boolean getAllExpanded() {
		boolean returAllExpanded = false;
		if (hasBinding(TREE_VIEW_ALLEXPANDED_BDG)) {
			returAllExpanded = (Boolean) valueForBinding(TREE_VIEW_ALLEXPANDED_BDG);
		}
		return returAllExpanded;
	}

	/**
	 * @param collapsedImage
	 *            the collapsedImage to set
	 */
	public void setTreeCollapsedImage(String collapsedImage) {
		this.collapsedImage = collapsedImage;
	}

	/**
	 * @return the collapsedImageFramework
	 */
	public String getTreeCollapsedImageFramework() {
		if (collapsedImageFramework == null) {
			collapsedImageFramework = (String) valueForBinding(TREE_COLLAPSED_IMG_FWK_BDG);
		}

		return collapsedImageFramework;
	}

	/**
	 * @param collapsedImageFramework
	 *            the collapsedImageFramework to set
	 */
	public void setTreeCollapsedImageFramework(String collapsedImageFramework) {
		this.collapsedImageFramework = collapsedImageFramework;
	}

	/**
	 * @return the expandedImage
	 */
	public String getTreeExpandedImage() {
		if (expandedImage == null) {
			expandedImage = (String) valueForBinding(TREE_EXPANDED_IMG_BDG);
			if (expandedImage == null) {
				expandedImage = "images/ico_node_expanded_gris_16.png";
				expandedImageFramework = "FwkCktlAjaxWebExt.framework";
			}
		}
		return expandedImage;
	}

	/**
	 * @param expandedImage
	 *            the expandedImage to set
	 */
	public void setTreeExpandedImage(String expandedImage) {
		this.expandedImage = expandedImage;
	}

	/**
	 * @return the expandedImageFramework
	 */
	public String getTreeExpandedImageFramework() {
		if (expandedImageFramework == null) {
			expandedImageFramework = (String) valueForBinding(TREE_EXPANDED_IMG_FWK_BDG);
		}
		return expandedImageFramework;
	}

	/**
	 * @param expandedImageFramework
	 *            the expandedImageFramework to set
	 */
	public void setTreeExpandedImageFramework(String expandedImageFramework) {
		this.expandedImageFramework = expandedImageFramework;
	}

	/**
	 * @return the leafImage
	 */
	public String getTreeLeafImage() {
		if (leafImage == null) {
			leafImage = (String) valueForBinding(TREE_LEAF_IMG_BDG);
			if (leafImage == null) {
				leafImage = "images/ico_node_leaf_gris_16.png";
				leafImageFramework = "FwkCktlAjaxWebExt.framework";
			}
		}
		return leafImage;
	}

	/**
	 * @param leafImage
	 *            the leafImage to set
	 */
	public void setTreeLeafImage(String leafImage) {
		this.leafImage = leafImage;
	}

	/**
	 * @return the leafImageFramework
	 */
	public String getTreeLeafImageFramework() {
		if (leafImageFramework == null) {
			leafImageFramework = (String) valueForBinding(TREE_LEAF_IMG_FWK_BDG);
		}
		return leafImageFramework;
	}

	/**
	 * @param leafImageFramework
	 *            the leafImageFramework to set
	 */
	public void setTreeLeafImageFramework(String leafImageFramework) {
		this.leafImageFramework = leafImageFramework;
	}

	public EOQualifier getTreeQualifier() {
		if (treeQualifier == null) {
			treeQualifier = (EOQualifier) valueForBinding(TREE_QUALIFIER_BDG);
		}
		return treeQualifier;
	}

	public void setTreeViewQualifier(EOQualifier treeViewQualifier) {
		this.treeQualifier = treeViewQualifier;
	}

	/**
	 * @return the isTreeViewOpened
	 */
	public Boolean isTreeViewOpened() {
		return isTreeViewOpened;
	}

	/**
	 * @param isTreeViewOpened
	 *            the isTreeViewOpened to set
	 */
	public void setIsTreeViewOpened(Boolean isTreeViewOpened) {
		this.isTreeViewOpened = isTreeViewOpened;
	}

	public WOActionResults openTreeView() {
		setIsTreeViewOpened(Boolean.TRUE);
		return null;
	}

}
