/**
 * 
 */
package org.utt.rfcct.serveur.utils;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCapacite;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOMatiere;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORepartCompTiers;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTerrain;
import org.utt.fwkuttcompetences.serveur.utils.tiers.Tiers;
import org.utt.rfcct.serveur.Session;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 21 nov. 2013
 * 
 */
@SuppressWarnings("all")
public class VerifyBeforeCreate {

	/**
	 * 
	 */
	public VerifyBeforeCreate() {
		// TODO Auto-generated constructor stub
	}

	public static Boolean ControlDuplicate(EOCompetence competence, Session sess) {
		Boolean errorMessage = false;
		EODomaine domaine = competence.domaine();
		EOMatiere matiere = competence.matiere();
		EOTerrain terrain = competence.terrain();
		EOCapacite capacite = competence.capacite();
		EOCompetence exitCompetence = null;
		NSMutableArray lesTiers = new NSMutableArray();
		// vérifier la matiere, le terrain et la capacite dans le domaine pour
		// le tier
		if (competence.temMulti().equals("O")) {
			NSArray<Tiers> tiers = sess.tiersDg().allObjects();
			for (Tiers tier : tiers) {
				exitCompetence = EOCompetence
						.fetchEOCompetence(
								sess.defaultEditingContext(),
								EOCompetence.DOMAINE
										.eq(domaine)
										.and(EOCompetence.MATIERE
												.eq(matiere)
												.and(EOCompetence.TERRAIN
														.eq(terrain)
														.and(EOCompetence.REPART_COMP_TIERS
																.dot(EORepartCompTiers.TIERS_NUMERO
																		.eq(tier.tiersNumero()))))));
				if (exitCompetence != null) {
					errorMessage = true;
					sess.addSimpleErrorMessage(
							"Duplication de compétence dans le domaine : "+domaine.lblCourt(),
							"Une compétence similaire existe déjà pour : "
									+ tier.tiersNumero() + " - "
									+ tier.tiersLibelle());
				}
			}
		} else {
			exitCompetence = EOCompetence
					.fetchEOCompetence(
							sess.defaultEditingContext(),
							EOCompetence.DOMAINE
									.eq(domaine)
									.and(EOCompetence.MATIERE
											.eq(matiere)
											.and(EOCompetence.TERRAIN
													.eq(terrain)
													.and(EOCompetence.REPART_COMP_TIERS
															.dot(EORepartCompTiers.TIERS_NUMERO
																	.eq(sess.leTier()
																			.tiersNumero()))))));
			if (exitCompetence != null) {
				errorMessage = true;
				sess.addSimpleErrorMessage("Duplication de compétence dans le domaine : "+domaine.lblCourt(),
						"Une compétence similaire existe déjà pour : "
								+ sess.leTier().tiersNumero() + " - "
								+ sess.leTier().tiersLibelle());
				sess.setErrorMessage("Une compétence similaire existe déjà pour : "
								+ sess.leTier().tiersNumero() + " - "
								+ sess.leTier().tiersLibelle());
			}
		}
		return errorMessage;
	}

}
