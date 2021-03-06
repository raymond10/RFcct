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
package org.utt.rfcct.serveur;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORedirect;
import com.webobjects.appserver.WORequest;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.extensions.eof.ERXEC;

import org.cocktail.fwkcktljefyadmin.common.metier.EOUtilisateur;
import org.cocktail.fwkcktlpersonne.common.metier.EOCompte;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonne.common.metier.PersonneDelegate;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlWebAction;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlLogin;
import org.cocktail.fwkcktlwebapp.server.components.CktlLoginResponder;
import org.cocktail.fwkcktlwebapp.server.components.CktlWebPage;
import org.utt.fwkuttcompetences.serveur.CompetenceApplicationUser;
import org.utt.fwkuttcompetences.serveur.RFcctApplicationUser;
import org.utt.fwkuttcompetences.serveur.modele.grhum.EORepartAssociation;
import org.utt.rfcct.serveur.components.GestionGroupe;
import org.utt.rfcct.serveur.components.LoginCAS;
import org.utt.rfcct.serveur.components.Main;
import org.utt.rfcct.serveur.components.MessagePage;
import org.utt.rfcct.serveur.components.Timeout;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 14 mars 2013
 */
@SuppressWarnings("all")
public class DirectAction extends CktlWebAction {

	private String loginComment;
	EOUtilisateur utilisateur = new EOUtilisateur();
	EOEditingContext ec = ERXEC.newEditingContext();

	public DirectAction(WORequest request) {
		super(request);
	}

	// Page de d�marrage
	public WOActionResults defaultAction() {
		if (useCasService())
			return loginCASPage();
		else
			return pageWithName(Main.class.getName());
	}

	// Cas
	public WOActionResults loginCASPage() {
		String url = DirectAction.getLoginActionURL(this.context(), false,
				null, true, null);
		WORedirect page = (WORedirect) pageWithName(WORedirect.class.getName());
		page.setUrl(url);
		return page;
	}

	// Deconnection
	public WOActionResults quitterAction() {
		if (useCasService())
			return linkCas();
		else
			return laSession().onQuitter();
	}

	// link cas
	public WOActionResults linkCas() {
		LoginCAS page = (LoginCAS) pageWithName(LoginCAS.class.getName());
		page.setTitleComment(loginComment());
		page.setCASLoginLink(casLoginLink());
		laSession().terminate();
		laSession().defaultEditingContext().revert();
		return page;
	}

	//
	public WOActionResults loginResultAction() {
		Session sess = (Session) cktlApp.sessionStore().checkOutSessionWithID(
				(String) request().formValueForKey("amp;wosid"),
				context().request());
		return loginCasSuccessPage(request().formValueForKey("netid")
				.toString());
	}

	// Session de d�marrage
	protected Session laSession() {
		Session cngSession = (Session) existingSession();
		if (cngSession == null) {
			cngSession = (Session) session();
		}
		return cngSession;
	}

	public Application rfcctApp() {
		return (Application) WOApplication.application();
	}

	// Page valide dans la session
	public WOActionResults sessionPageAction() {
		WOComponent next = null;
		if (laSession().getPageName() != null) {
			next = pageWithName(laSession().getPageName());
			laSession().setPageName(null);
		} else
			next = (GestionGroupe) pageWithName(GestionGroupe.class.getName());

		return next;
	}

	/**
	 * CAS : traitement authentification OK
	 */
	public WOActionResults loginCasSuccessPage(String netid) {
		try {
			Boolean canUse = null;
			IPersonne pers = PersonneDelegate.fetchPersonneForLogin(ec, netid);
			if (pers == null) {
				throw new Exception(
						"Impossible de recuperer un objet personne associe au login "
								+ netid);
			}
			String erreur = laSession().setConnectedUser(netid);
			if (erreur != null)
				throw new Exception(erreur);
			// Verification de la validit� du login
			EOCompte cpt = EOCompte.compteForLogin(ec, netid);
			if (cpt != null && cpt.isValideAsString().equals("N"))
				throw new Exception(
						"Vous n'�tes pas autoris�(e) � utiliser cette application, votre login est invalide");
			/** V�rifier la validit� de la personne connect� en RH **/
			// V�rifier que l'individu est bien affect� dans un service
			if (!EORepartAssociation.validiteService(ec, pers.persId()))
					/*|| !EORepartAssociation.validitePole(ec, pers.persId())
					|| !EORepartAssociation.validiteEquipe(ec, pers.persId()))*/
				throw new Exception(
						"Vous n'�tes pas autoris�(e) � utiliser cette application, vous n'appartenez � aucun service");

			if (rfcctApp().newGestionDroitsEnabled()) {
				RFcctApplicationUser rfcctGdUser = new RFcctApplicationUser(
						pers.persId());
				canUse = rfcctGdUser.getCompetenceAutorisationCache()
						.gDRfcctAcces();
				laSession().setGdApplicationUser(rfcctGdUser);
			} else {
				CompetenceApplicationUser appUser = new CompetenceApplicationUser(
						ec, Application.TYPE_APP_STR, pers.persId());
				canUse = appUser.hasDroitAccesRfcct();
				laSession().setApplicationUser(appUser);
			}
			CktlWebPage page = null;
			if (!canUse) {
				page = (CktlWebPage) pageWithName(MessagePage.class.getName());
				((MessagePage) page)
						.setMessage("Vous n'�tes pas autoris�(e) � utiliser cette application !");
			} else {
				NSArray utilisateurs = EOUtilities.objectsMatchingKeyAndValue(
						ec, EOUtilisateur.ENTITY_NAME, "persId", pers.persId());
				if (utilisateurs != null && utilisateurs.count() == 1) {
					utilisateur = (EOUtilisateur) utilisateurs.lastObject();
					laSession().setUtilisateur(utilisateur);
				}
				GestionGroupe nextPage = (GestionGroupe) cktlApp.pageWithName(
						GestionGroupe.class.getName(), laSession().context());
				nextPage.setAppUtilisateur(utilisateur);
				return nextPage;

			}
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			return getErrorPage(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cocktail.fwkcktlwebapp.server.CktlWebAction#loginCasFailurePage(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public WOActionResults loginCasFailurePage(String errorMessage, String arg1) {
		// TODO Auto-generated method stub
		WORequest request = context().request();
		String login = StringCtrl.normalize((String) request
				.formValueForKey("identifiant"));
		CktlLog.log("loginCasFailurePage : " + errorMessage + " (" + arg1 + ")");
		StringBuffer msg = new StringBuffer();
		String message = "Une erreur s'est produite lors de l'authentification de l'utilisateur "
				+ login + "&nbsp;:<br><br>";
		msg.append(message);
		if (errorMessage != null)
			msg.append("&nbsp;:<br><br>").append(errorMessage);
		return getErrorPage(msg.toString());
	}

	/**
	 * Retourne la directAction attendue d'apres son nom <code>daName</code>. Si
	 * rien n'a ete trouve, alors une page d'avertissement est affichee.
	 */
	public WOActionResults performActionNamed(String aName) {
		WOActionResults result = null;
		try {
			result = super.performActionNamed(aName);
		} catch (Exception e) {
			result = getErrorPage("DirectAction introuvable : \"" + aName
					+ "\"");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cocktail.fwkcktlwebapp.server.CktlWebAction#loginCasSuccessPage(java
	 * .lang.String, com.webobjects.foundation.NSDictionary)
	 */
	@Override
	public WOActionResults loginCasSuccessPage(String arg0, NSDictionary arg1) {
		// TODO Auto-generated method stub
		return loginCasSuccessPage(arg0);
	}

	/**
	 * CAS : page par defaut si CAS n'est pas parametre
	 */
	public WOActionResults loginNoCasPage() {
		return pageWithName(Main.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cocktail.fwkcktlwebapp.server.CktlWebAction#loginNoCasPage(com.webobjects
	 * .foundation.NSDictionary)
	 */
	@Override
	public WOActionResults loginNoCasPage(NSDictionary arg0) {
		// TODO Auto-generated method stub
		return loginNoCasPage();
	}

	// Validation login/mdp
	public WOActionResults validerLoginAction() {
		if (useCasService()) {
			throw new IllegalAccessError(
					"Vous n'�tes pas autoris� � acc�der � cette URL");
		}
		WOActionResults page = null;
		WORequest request = context().request();
		String login = StringCtrl.normalize((String) request
				.formValueForKey("identifiant"));
		String password = StringCtrl.normalize((String) request
				.formValueForKey("mot_de_passe"));
		String messageErreur = null;
		CktlLoginResponder loginResponder = getNewLoginResponder(null);
		CktlUserInfo loggedUserInfo = new CktlUserInfoDB(cktlApp.dataBus());
		if (login.length() == 0) {
			messageErreur = "Vous devez renseigner l'identifiant.";
		} else if (!loginResponder.acceptLoginName(login)) {
			messageErreur = "Vous n'�tes pas autoris� � utiliser cette application";
		} else {
			if (password == null)
				password = "";
			loggedUserInfo.setRootPass(loginResponder.getRootPassword());
			loggedUserInfo.setAcceptEmptyPass(loginResponder
					.acceptEmptyPassword());
			loggedUserInfo.compteForLogin(login, password, true);
			if (loggedUserInfo.errorCode() != CktlUserInfo.ERROR_NONE) {
				if (loggedUserInfo.errorMessage() != null)
					messageErreur = loggedUserInfo.errorMessage();
				CktlLog.rawLog(">> Erreur | " + loggedUserInfo.errorMessage());
			}
		}
		EOCompte cpt = EOCompte.compteForLogin(ec, login);
		if (cpt != null && cpt.isValideAsString().equals("N"))
			messageErreur = "Vous n'�tes pas autoris�(e) � utiliser cette application, votre login est invalide";
		/** V�rifier la validit� de la personne connect� en RH **/
		// V�rifier que l'individu est bien affect� dans un service
		if (!EORepartAssociation.validiteService(ec, loggedUserInfo.persId()
				.intValue()))/* || !EORepartAssociation.validitePole(ec, loggedUserInfo.persId()
						.intValue()) || !EORepartAssociation.validiteEquipe(ec, loggedUserInfo.persId()
								.intValue()))*/
			messageErreur = "Vous n'�tes pas autoris�(e) � utiliser cette application, vous n'appartenez � aucun service";

		if (messageErreur == null) {
			laSession().setConnectedUserInfo(loggedUserInfo);
			String erreur = laSession()
					.setConnectedUser(loggedUserInfo.login());
			if (erreur != null) {
				messageErreur = erreur;
			} else {
				Integer persId = Integer.valueOf(loggedUserInfo.persId()
						.intValue());
				Boolean canUse = null;
				if (rfcctApp().newGestionDroitsEnabled()) {
					RFcctApplicationUser rfcctGdUser = new RFcctApplicationUser(
							persId);
					canUse = rfcctGdUser.getCompetenceAutorisationCache()
							.gDRfcctAcces();
					laSession().setGdApplicationUser(rfcctGdUser);
				} else {
					CompetenceApplicationUser appUser = new CompetenceApplicationUser(
							ec, Application.TYPE_APP_STR, persId);
					canUse = appUser.hasDroitAccesRfcct();
					laSession().setApplicationUser(appUser);
				}
				if (!canUse) {
					page = (CktlWebPage) pageWithName(MessagePage.class
							.getName());
					((MessagePage) page)
							.setMessage("Vous n'�tes pas autoris�(e) � utiliser cette application !");
				} else {
					NSArray utilisateurs = EOUtilities
							.objectsMatchingKeyAndValue(ec,
									EOUtilisateur.ENTITY_NAME, "persId", persId);
					if (utilisateurs != null && utilisateurs.count() == 1) {
						utilisateur = (EOUtilisateur) utilisateurs.lastObject();
						laSession().setUtilisateur(utilisateur);
					}
				}
			}
		}

		if (messageErreur != null) {
			page = (MessagePage) pageWithName(MessagePage.class.getName());
			((MessagePage) page).setMessage(messageErreur);
			return page;
		}

		return loginResponder.loginAccepted(null);

	}

	public String loginComment() {
		return loginComment;
	}

	public void setLoginComment(String comment) {
		loginComment = comment;
	}

	private String casLoginLink() {
		return null;
	}

	/**
	 * affiche une page avec un message d'erreur
	 */
	private WOComponent getErrorPage(String errorMessage) {
		System.out.println("ERREUR = " + errorMessage);
		CktlAlertPage page = (CktlAlertPage) cktlApp.pageWithName(
				CktlAlertPage.class.getName(), context());
		page.showMessage(null, "ERREUR", errorMessage, null, null, null,
				CktlAlertPage.ERROR, null);
		return page;
	}

	public WOActionResults mailUsersAction() {
		String destinataire = cktlApp.config().stringForKey("APP_ADMIN_MAIL");
		WORequest req = request();
		if (req.formValueForKey("dest") != null) {
			destinataire = (String) req.formValueForKey("dest");
		}
		cktlApp.mailBus()
				.sendMail(
						destinataire,
						cktlApp.config().stringForKey("APP_ADMIN_MAIL"),
						null,
						"[" + cktlApp.name()
								+ "]Utilisateur connect� � l'application ",
						"Liste des emails : \n"
								+ ((Application) WOApplication.application())
										.utilisateurs()
										.componentsJoinedByString(","));
		return defaultAction();
	}

	public WOActionResults sessionExpiredAction() {
		Timeout nextPage = (Timeout) pageWithName(Timeout.class.getName());
		return nextPage;
	}

	public WOActionResults applicationExceptionAction() {
		GestionGroupe nextPage = (GestionGroupe) pageWithName(GestionGroupe.class
				.getName());
		nextPage.setIsOpenFenetreException(true);
		return nextPage;
	}

	public CktlLoginResponder getNewLoginResponder(NSDictionary actionParams) {
		return new DefaultLoginResponder(actionParams);
	}

	public class DefaultLoginResponder implements CktlLoginResponder {
		private NSDictionary actionParams;

		public DefaultLoginResponder(NSDictionary actionParams) {
			this.actionParams = actionParams;
		}

		public NSDictionary actionParams() {
			return actionParams;
		}

		public WOComponent loginAccepted(CktlLogin loginComponent) {
			WOComponent nextPage = null;
			nextPage = cktlApp.pageWithName(GestionGroupe.class.getName(),
					context());
			return nextPage;
		}

		public boolean acceptLoginName(String loginName) {
			return cktlApp.acceptLoginName(loginName);
		}

		public boolean acceptEmptyPassword() {
			return cktlApp.config().booleanForKey("ACCEPT_EMPTY_PASSWORD");
		}

		public String getRootPassword() {
			return cktlApp.getRootPassword();
		}
	}
}
