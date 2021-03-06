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

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.cocktail.fwkcktlacces.server.handler.JarResourceRequestHandler;
import org.cocktail.fwkcktlajaxwebext.serveur.CocktailAjaxApplication;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlMailBus;
import org.cocktail.fwkcktlwebapp.server.CktlWebApplication;
import org.cocktail.fwkcktlwebapp.server.ModuleRegister;
import org.cocktail.fwkcktlwebapp.server.util.EOModelCtrl;
import org.cocktail.fwkcktlwebapp.server.version.A_CktlVersion;
import org.utt.fwkuttcompetences.serveur.exception.FactoryException;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EORfcctParametres;
import org.utt.rfcct.serveur.components.GestionGroupe;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSSet;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestampFormatter;
import com.woinject.WOInject;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXMessageEncoding;
import er.extensions.eof.ERXEC;
import er.extensions.foundation.ERXProperties;

@SuppressWarnings("all")
public class Application extends CocktailAjaxApplication {
	public static final String TYPE_APP_STR = "RFCCT"; // ID de l'application
	                                                  // dans JefyAdmin/GD
	private static final String CONFIG_FILE_NAME = VersionMe.APPLICATIONINTERNALNAME
			+ ".config";
	private static final String CONFIG_TABLE_NAME = "FwkCktlWebApp_GrhumParametres";
	private static final String MAIN_MODEL_NAME = "FwkUttCompetences";
	private static final String PARAMETRES_TABLE_NAME = EORfcctParametres.ENTITY_NAME;
	private final Logger logger = ERXApplication.log;

	public static final String DEFAULT_TIMESTAMP_FORMATTER = "%d/%m/%Y à %Hh%M";

	private NSMutableDictionary appParametres;

	public static RfcctParamManager rfcctParamManager = new RfcctParamManager();

	/**
	 * Mettre a true pour que votre application renvoie les informations de
	 * collecte au serveur de collecte de Cocktail.
	 */
	public static boolean APP_SHOULD_SEND_COLLECTE = false;
	/**
	 * Liste des parametres obligatoires (dans fichier de config ou table
	 * grhum_parametres) pour que l'application se lance. Si un des parametre
	 * n'est pas initialise, il y a une erreur bloquante.
	 */
	public static final String[] MANDATORY_PARAMS = new String[] { "HOST_MAIL",
			"ADMIN_MAIL" };

	/**
	 * Liste des parametres optionnels (dans fichier de config ou table
	 * grhum_parametres). Si un des parametre n'est pas initialise, il y a un
	 * warning.
	 */
	public static final String[] OPTIONAL_PARAMS = new String[] {};
	public static final String CONFIG_C_STRUCTURE_LIST_RFCCT_USERS_KEY = "C_STRUCTURE_LIST_RFCCT_USERS";
	public static final String CONFIG_C_STRUCTURE_ADMIN_RFCCT_KEY = "C_STRUCTURE_ADMIN_RFCCT";
	public static final String CONFIG_LIST_TYPE_TIER = "LIST_TYPE_TIER";
	public static final String CONFIG_GD_ENABLE = "ENABLE_NEW_GD";
	private Boolean _newGd;

	/**
	 * boolean qui indique si on se trouve en mode developpement ou non. Permet
	 * de desactiver l'envoi de mail lors d'une exception par exemple
	 */
	public static boolean isModeDebug = false;

	public static Application app() {
		return (Application) application();
	}

	private Version _appVersion;

	public static NSTimeZone ntz = null;
	/**
	 * Formatteur a deux decimales e utiliser pour les donnees numeriques non
	 * monetaires.
	 */
	public NSNumberFormatter app2DecimalesFormatter;
	/**
	 * Formatteur a 5 decimales a utiliser pour les pourcentages dans la
	 * repartition.
	 */
	public NSNumberFormatter app5DecimalesFormatter;

	/**
	 * Formatteur de dates.
	 */
	public NSTimestampFormatter appDateFormatter;

	/**
	 * Liste des emails des utilisateurs connectes.
	 */
	private static NSMutableArray utilisateurs; // Liste des emails des
	// utilisateurs connectes

	/**
	 * ID du container permettant d'afficher les messages via pnotify
	 */
	private static final String MESSAGE_CONTAINER_ID = "Rfcct_MessageContainer";

	/**
	 * Fonction JS permettant l'update du container de messages
	 */
	private static final String ON_FAILURE_MESSAGE = "function () {"
			+ MESSAGE_CONTAINER_ID + "Update();}";
	private static final String ON_MESSAGE_UPDATE = "function () {"
			+ MESSAGE_CONTAINER_ID + "Update();}";

	public static String ServerBdIds;

	/**
	 * Liste des c_structure des groupes dont les membres ont le droit d'accéder
	 * à RFCCT. cf {@link Application#CONFIG_C_STRUCTURE_LIST_RFCCT_USERS_KEY}
	 */
	private NSSet<String> cStructuresAccesRfcct;
	private String _cStructureAdmin;
	private NSSet<String> _listTypeTiers;

	public static void main(String[] argv) {
		//ERXApplication.main(argv, Application.class);
		WOInject.init("org.utt.rfcct.serveur.Application", argv);
	}

	public Application() {
		super();
		/* ** put your initialization code in here ** */
		registerRequestHandler(new JarResourceRequestHandler(),  "_wr_");
		setAllowsConcurrentRequestHandling(false);
		setDefaultRequestHandler(requestHandlerForKey(directActionRequestHandlerKey()));
		setPageRefreshOnBacktrackEnabled(true);
		WOMessage.setDefaultEncoding("UTF-8");
		WOMessage.setDefaultURLEncoding("UTF-8");
		ERXMessageEncoding.setDefaultEncoding("UTF8");
		ERXMessageEncoding.setDefaultEncodingForAllLanguages("UTF8");
		WOResponse.setDefaultEncoding("UTF-8");
		utilisateurs = new NSMutableArray();
		ERXEC.setDefaultFetchTimestampLag(0);
		logger.info("Bienvenue sur " + name());
		//setSessionTimeOut(86400);
		// On charge le chemin vers la conf jasper...
		URL urlToJasperProperties = resourceManager().pathURLForResourceNamed(
				"Reports/jasperreports.properties", "app", null);
		System.setProperty("net.sf.jasperreports.properties",
				urlToJasperProperties.toString());
	}

	@Override
	public A_CktlVersion appCktlVersion() {
		return new Version();
	}

	@Override
	public A_CktlVersion appCktlVersionDb() {
		return new VersionDatabase();
	}

	public void initApplication() {
		System.out.println("Lancement de l'application serveur " + this.name()
				+ "...");
		super.initApplication();
		CktlLog.rawLog("WOFrameworksBaseURL: "+frameworksBaseURL());
		// Afficher les infos de connexion des modeles de donnees
		rawLogModelInfos();
		// Verifier la coherence des dictionnaires de connexion des modeles de
		// donnees
		boolean isInitialisationErreur = !checkModel();
		isModeDebug = (Application.isDevelopmentModeSafe() || ERXProperties
				.booleanForKeyWithDefault("MODE_DEBUG", false));
		Application.APP_SHOULD_SEND_COLLECTE = !Application
				.isDevelopmentModeSafe();
		setPageCacheSize(10);
	}

	/**
	 * Execute les operations au demarrage de l'application, juste apres
	 * l'initialisation standard de l'application.
	 */
	public void startRunning() {
		initFormatters();
		initTimeZones();
		this.appDateFormatter = new NSTimestampFormatter();
		this.appDateFormatter.setPattern("%d/%m/%Y");

		// EOStructureForGroupeSpec.getGroupeDefaut(ERXEC.newEditingContext());

		/**
		 * Prefetch dans le sharedEditingContext des nomenclatures communes a
		 * toute l'appli Il est necessaire de declarer dans l'eomodel, l'entite
		 * a prefetecher via l'inspecteur: 'Share all objects' --> creation d'un
		 * fetchspecificationnamed 'FetchAll' Il est indispensable d'utiliser
		 * l'api 'bindObjectsWithFetchSpecification'
		 */
	}

	public Logger logger() {
		return logger;
	}

	public String configFileName() {
		return CONFIG_FILE_NAME;
	}

	public String configTableName() {
		return CONFIG_TABLE_NAME;
	}

	public String[] configMandatoryKeys() {
		return MANDATORY_PARAMS;
	}

	public String[] configOptionalKeys() {
		return OPTIONAL_PARAMS;
	}

	protected String parametresTableName() {
		return PARAMETRES_TABLE_NAME;
	}

	public String casLogOutURL() {
		return ERXProperties.stringForKey("CAS_LOGOUT_URL");
	}

	public String appUseCas() {
		return ERXProperties.stringForKey("APP_USE_CAS");
	}

	public String copyright() {
		return appVersion().copyright();
	}

	public A_CktlVersion appA_CktlVersion() {
		return appVersion();
	}

	public Version appVersion() {
		if (_appVersion == null) {
			_appVersion = new Version();
		}
		return _appVersion;
	}

	public String mainModelName() {
		return MAIN_MODEL_NAME;
	}
	
	public String messageContainerID() {
		return MESSAGE_CONTAINER_ID;
	}
	
	public String onFailureMessage() {
		return ON_FAILURE_MESSAGE;
	}
	
	public String onMessageUpdate() {
		return ON_MESSAGE_UPDATE;
	}

	public void initFormatters() {
		this.app2DecimalesFormatter = new NSNumberFormatter();
		this.app2DecimalesFormatter.setDecimalSeparator(",");
		this.app2DecimalesFormatter.setThousandSeparator(" ");

		this.app2DecimalesFormatter.setHasThousandSeparators(true);
		this.app2DecimalesFormatter.setPattern("#,##0.00;0,00;-#,##0.00");

		this.app5DecimalesFormatter = new NSNumberFormatter();
		this.app5DecimalesFormatter.setDecimalSeparator(",");
		this.app5DecimalesFormatter.setThousandSeparator(" ");

		this.app5DecimalesFormatter.setHasThousandSeparators(true);
		this.app5DecimalesFormatter.setPattern("##0.00000;0,00000;-##0.00000");
	}

	public NSNumberFormatter app2DecimalesFormatter() {
		return this.app2DecimalesFormatter;
	}

	public NSNumberFormatter getApp5DecimalesFormatter() {
		return this.app5DecimalesFormatter;
	}

	/**
	 * Initialise le TimeZone a utiliser pour l'application.
	 */
	protected void initTimeZones() {
		logger().info("Initialisation du NSTimeZone");
		String tz = config().stringForKey("DEFAULT_NS_TIMEZONE");
		if (tz == null || tz.equals("")) {
			CktlLog.log("Le parametre DEFAULT_NS_TIMEZONE n'est pas defini dans le fichier .config.");
			TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
			NSTimeZone.setDefaultTimeZone(NSTimeZone.timeZoneWithName(
					"Europe/Paris", false));
		} else {
			ntz = NSTimeZone.timeZoneWithName(tz, false);
			if (ntz == null) {
				CktlLog.log("Le parametre DEFAULT_NS_TIMEZONE defini dans le fichier .config n'est pas valide ("
						+ tz + ")");
				TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
				NSTimeZone.setDefaultTimeZone(NSTimeZone.timeZoneWithName(
						"Europe/Paris", false));
			} else {
				TimeZone.setDefault(ntz);
				NSTimeZone.setDefaultTimeZone(ntz);
			}
		}
		ntz = NSTimeZone.defaultTimeZone();
		logger().info(
				"NSTimeZone par defaut utilise dans l'application:"
						+ NSTimeZone.defaultTimeZone());
		NSTimestampFormatter ntf = new NSTimestampFormatter();
		logger().info(
				"Les NSTimestampFormatter analyseront les dates avec le NSTimeZone: "
						+ ntf.defaultParseTimeZone());
	}

	/**
	 * Retourne le mot de passe du super-administrateur. Il permet de se
	 * connecter a l'application avec le nom d'un autre utilisateur
	 * (l'authentification local et non celle CAS doit etre activee dans ce
	 * cas).
	 */
	public String getRootPassword() {
		// passpar2
		return "HO4LI8hKZb81k";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public WOResponse handleException(Exception anException, WOContext context) {
		logger().error("Message d'erreur :" + anException.getMessage(),
				anException);
		WOResponse response = null;
		if (context != null && context.hasSession()) {
			Session session = (Session) context.session();
			session.setGeneralErrorMessage(anException.getMessage());
			sendMessageErreur(anException, context, session);
			String error = createMessageErreur(anException, context, session);
			response = createResponseInContext(context);
			session.setGeneralErrorMessage(error);
			NSMutableDictionary formValues = new NSMutableDictionary();
			formValues.setObjectForKey(session.sessionID(), "wosid");
			String applicationExceptionUrl = context
					.directActionURLForActionNamed("applicationException",
							formValues);
			response.appendContentString("<script>document.location.href='"
					+ applicationExceptionUrl + "';</script>");
			cleanInvalidEOFState(anException, context);
			GestionGroupe nextPage = (GestionGroupe) pageWithName(GestionGroupe.class.getName(), context);
			nextPage.setIsOpenFenetreException(true);
			response = nextPage.generateResponse();
			return response;
		} else {
			return super.handleException(anException, context);
		}
	}

	@SuppressWarnings("rawtypes")
	private String createMessageErreur(Exception anException,
			WOContext context, Session session) {
		String contenu;
		// Si c'est une erreur de config, on affiche pas tout le tsoin tsoin,
		// juste une info claire
		if (anException instanceof FactoryException) {
			contenu = anException.getMessage();
			session.setGeneralErrorMessage(contenu);
		} else if (anException instanceof NSForwardException
				&& ((NSForwardException) anException).originalException() instanceof FactoryException) {
			Throwable cause = ((NSForwardException) anException)
					.originalException();
			contenu = cause != null ? cause.getLocalizedMessage() : null;
		} else {
			NSDictionary extraInfo = extraInformationForExceptionInContext(
					anException, context);
			contenu = "Date : "
					+ DateCtrl.dateToString(DateCtrl.now(), "%d/%m/%Y %H:%M")
					+ "\n";
			contenu += "OS: " + System.getProperty("os.name") + "\n";
			contenu += "Java vm version: "
					+ System.getProperty("java.vm.version") + "\n";
			contenu += "WO version: " + ERXProperties.webObjectsVersion()
					+ "\n\n";
			contenu += "User agent: "
					+ context.request().headerForKey("user-agent") + "\n\n";
			contenu += "Utilisateur(PersId): "
					+ session.nomAndPrenom()
					+ " (" + session.persId() + ")"
					+ "\n";

			contenu += "\n\nException : " + "\n";
			if (anException instanceof InvocationTargetException) {
				contenu += getMessage(anException, extraInfo) + "\n";
				anException = (Exception) anException.getCause();
			}
			contenu += getMessage(anException, extraInfo) + "\n";
			contenu += "\n\n";
		}
		return contenu;
	}

	private void sendMessageErreur(Exception anException, WOContext context,
			Session session) {
		try {
			CktlMailBus cmb = session.mailBus();
			String smtpServeur = config().stringForKey("HOST_MAIL");
			String destinataires = config().stringForKey("ADMIN_MAIL");

			if (cmb != null && smtpServeur != null
					&& smtpServeur.equals("") == false && destinataires != null
					&& destinataires.equals("") == false) {
				String objet = "[RFCCT]:Exception:[";
				objet += VersionMe.txtAppliVersion() + "]";
				String contenu = createMessageErreur(anException, context,
						session);
				session.setGeneralErrorMessage(contenu);
				boolean retour = false;
				if (isModeDebug) {
					logger().info(
							"!!!!!!!!!!!!!!!!!!!!!!!! MODE DEVELOPPEMENT : pas de mail !!!!!!!!!!!!!!!!");
					retour = false;
				} else {
					retour = cmb.sendMail(destinataires, destinataires, null,
							objet, contenu);
				}
				if (!retour) {
					logger().warn(
							"!!!!!!!!!!!!!!!!!!!!!!!! IMPOSSIBLE d'ENVOYER le mail d'exception !!!!!!!!!!!!!!!!");
					logger().warn("\nMail:\n\n" + contenu);

				}
			} else {
				logger().warn(
						"!!!!!!!!!!!!!!!!!!!!!!!! IMPOSSIBLE d'ENVOYER le mail d'exception !!!!!!!!!!!!!!!!");
				logger().warn(
						"Veuillez verifier que les parametres HOST_MAIL et ADMIN_MAIL sont bien renseignes");
				logger().warn("HOST_MAIL = " + smtpServeur);
				logger().warn("ADMIN_MAIL = " + destinataires);
				logger().warn("cmb = " + cmb);
				logger().warn("\n\n\n");
			}
		} catch (Exception e) {
			logger().error("\n\n\n");
			logger().error(
					"!!!!!!!!!!!!!!!!!!!!!!!! Exception durant le traitement d'une autre exception !!!!!!!!!!!!!!!!");
			logger().error(e.getMessage(), e);
			super.handleException(e, context);
			logger().error("\n");
			logger().error(
					"Message Exception originale: " + anException.getMessage());
			logger().error(
					"Stack Exception dans exception: "
							+ anException.getStackTrace());
		}

	}

	public String _rewriteURL(String url) {
		String processedURL = url;
		if (url != null && _replaceApplicationPathPattern != null
				&& _replaceApplicationPathReplace != null) {
			processedURL = processedURL.replaceFirst(
					_replaceApplicationPathPattern,
					_replaceApplicationPathReplace);
		}
		return processedURL;
	}

	private void cleanInvalidEOFState(Exception e, WOContext ctx) {
		if (e instanceof IllegalStateException
				|| e instanceof EOGeneralAdaptorException) {
			Session sess = (Session) ctx.session();
			sess.defaultEditingContext().invalidateAllObjects();
		}
	}

	@SuppressWarnings("rawtypes")
	protected String getMessage(Throwable e, NSDictionary extraInfo) {
		String message = "";
		if (e != null) {
			message = stackTraceToString(e, false) + "\n\n";
			message += "Info extra :\n";
			if (extraInfo != null) {
				message += NSPropertyListSerialization
						.stringFromPropertyList(extraInfo) + "\n\n";
			}
		}
		return message;
	}

	/**
	 * permet de recuperer la trace d'une exception au format string message +
	 * trace
	 * 
	 * @param e
	 * @return
	 */
	public static String stackTraceToString(Throwable e, boolean useHtml) {
		String tagCR = "\n";
		if (useHtml) {
			tagCR = "<br>";
		}
		String stackStr = e + tagCR + tagCR;
		StackTraceElement[] stack = e.getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			stackStr += (stack[i]).toString() + tagCR;
		}
		return stackStr;
	}

	public boolean _isSupportedDevelopmentPlatform() {
		return (super._isSupportedDevelopmentPlatform() || System.getProperty(
				"os.name").startsWith("Win"));
	}

	@Override
	public WOResponse handleSessionRestorationErrorInContext(WOContext context) {
		WOResponse response;
		response = createResponseInContext(context);
		String sessionExpiredUrl = context.directActionURLForActionNamed(
				"sessionExpired", null);
		response.appendContentString("<script>document.location.href='"
				+ sessionExpiredUrl + "';</script>");

		return response;
	}

	@SuppressWarnings("rawtypes")
	public NSMutableArray utilisateurs() {
		return utilisateurs;
	}

	@SuppressWarnings("rawtypes")
	public static String serverBDId() {
		if (ServerBdIds == null) {
			NSMutableArray<String> serverDBIds = new NSMutableArray<String>();
			final NSMutableDictionary mdlsDico = EOModelCtrl.getModelsDico();
			final Enumeration mdls = mdlsDico.keyEnumerator();
			while (mdls.hasMoreElements()) {
				final String mdlName = (String) mdls.nextElement();
				String serverDBId = EOModelCtrl
						.bdConnexionServerId((EOModel) mdlsDico
								.objectForKey(mdlName));
				if (!serverDBIds.containsObject(serverDBId)) {
					serverDBIds.addObject(serverDBId);
				}
			}
			ServerBdIds = serverDBIds.componentsJoinedByString(",");
		}
		return ServerBdIds;
	}

	public String getDefaultTimestampFormatter() {
		return DEFAULT_TIMESTAMP_FORMATTER;
	}

	/**
	 * @return the cStructuresAccesGrhum : liste des cStructures dont les
	 *         membres ont accès à RFCCT
	 */
	public NSSet<String> getCStructuresAccesRfcct() {
		if (cStructuresAccesRfcct == null) {
			String cStructuresListeParam = ERXProperties.stringForKey(
					CONFIG_C_STRUCTURE_LIST_RFCCT_USERS_KEY);
			if (!MyStringCtrl.isEmpty(cStructuresListeParam)) {
				cStructuresAccesRfcct = new NSSet<String>(
						cStructuresListeParam.split(","));
			}
		}
		return cStructuresAccesRfcct;
	}

	/**
	 * cStruture des membres Admin
	 * 
	 * @return
	 */
	public String cStructureAdmin() {
		if (_cStructureAdmin == null)
			_cStructureAdmin = ERXProperties.stringForKey(
					CONFIG_C_STRUCTURE_ADMIN_RFCCT_KEY);
		return _cStructureAdmin;
	}
	
	/**
	 * _newGd
	 * @return
	 */
	public boolean newGestionDroitsEnabled() {
		if(_newGd == null)
			_newGd = ERXProperties.booleanForKeyWithDefault(CONFIG_GD_ENABLE, false);
		return _newGd;
	}

	/**
	 * @return the _listTypeTiers
	 */
	public NSSet<String> listTypeTiers() {
		if(_listTypeTiers == null) {
			String listTypeTiersParam = ERXProperties.stringForKey(
					CONFIG_LIST_TYPE_TIER);
			if (!MyStringCtrl.isEmpty(listTypeTiersParam)) {
				_listTypeTiers = new NSSet<String>(
						listTypeTiersParam.split(","));
			}			
		}
		return _listTypeTiers;
	}

	// PARAMETRES
	/**
	 * Récupére une valeur dans la table parametresTableName(). Si elle n'existe
	 * pas dans la table, va la chercher dans le configFileName(), et si
	 * n'existe pas va dans la table configTableName()
	 * 
	 * @param paramKey
	 *            La clé à rechercher
	 * @return La première valeur associée à la clé paramkey.
	 * @see appParametres
	 */
	public String getParam(String paramKey) {
		NSArray a = (NSArray) appParametres().valueForKey(paramKey);
		String res = null;
		if (a == null || a.count() == 0) {
			// recherche dans le configFileName()
			res = config().stringForKey(paramKey);
		} else {
			// recherche dans le configTableName()
			res = (String) a.objectAtIndex(0);
		}
		return res;
	}

	/**
	 * Récupère x valeur(s) dans la table parametresTableName(). Si elle
	 * n'existe pas dans la table, va la chercher dans le configFileName(), et
	 * si n'existe pas va dans la table configTableName()
	 * 
	 * @param paramKey
	 *            La clé à rechercher
	 * @return La(les) valeur(s) associée(s) à la clé paramkey.
	 * @see appParametres
	 */
	public NSArray getParams(String paramKey) {
		NSArray a = (NSArray) appParametres().valueForKey(paramKey);
		if (a == null || a.count() == 0) {
			// recherche dans le configFileName()/configTableName()
			a = config().valuesForKey(paramKey);
		}
		if (a == null) {
			a = new NSArray();
		}
		return a;
	}

	/**
	 * Récupère une valeur dans la table parametresTableName(). A n'utiliser que
	 * pour récupèrer des paramètres spécifiques à l'application, et si on ne
	 * veut pas aller chercher le paramètre ailleurs que dans la table de
	 * paramètrage de l'application.
	 * 
	 * @param paramKey
	 *            La clé à rechercher
	 * @return La première valeur associée à la clé paramkey.
	 * @see appParametres
	 */
	public String getAppParam(String paramKey) {
		NSArray a = (NSArray) appParametres().valueForKey(paramKey);
		String res = null;
		if (a != null && a.count() > 0) {
			res = (String) a.objectAtIndex(0);
		}
		return res;
	}

	/**
	 * Récupère x valeur(s) dans la table parametresTableName(). A n'utiliser
	 * que pour récupérer des paramètres spécifiques à l'application, et si on
	 * ne veut pas aller chercher les paramètres ailleurs que dans la table de
	 * paramètrage de l'application.
	 * 
	 * @param paramKey
	 *            La clé à rechercher
	 * @return La(les) valeur(s) associée(s) à la clé paramkey.
	 * @see appParametres
	 */
	public NSArray getAppParams(String paramKey) {
		NSArray a = (NSArray) appParametres().valueForKey(paramKey);
		if (a == null) {
			a = new NSArray();
		}
		return a;
	}

	public boolean getParamBoolean(String paramKey) {
		return getParamBoolean(paramKey, false);
	}

	public boolean getParamBoolean(String paramKey,
			boolean defaultValueWhenNotFound) {
		String s = getParam(paramKey);
		if (s == null) {
			return defaultValueWhenNotFound;
		}
		if (s.equals("1") || s.equalsIgnoreCase("O")
				|| s.equalsIgnoreCase("OUI") || s.equalsIgnoreCase("Y")
				|| s.equalsIgnoreCase("YES") || s.equalsIgnoreCase("TRUE")) {
			return true;
		}
		return false;
	}

	/**
	 * @return Les parametres de l'application stockés dans la table
	 *         parametresTableName()
	 */
	private NSMutableDictionary appParametres() {
		if (appParametres == null) {
			// if (mySharedEditingContext == null || parametresTableName() ==
			// null) {
			// return new NSMutableDictionary();
			// }
			if (parametresTableName() == null) {
				return new NSMutableDictionary();
			}
			appParametres = new NSMutableDictionary();
			try {
				NSArray vParam = dataBus().fetchArray(parametresTableName(),
						null, null);
				String previousParamKey = null;
				NSMutableArray a = null;
				Enumeration enumerator = vParam.objectEnumerator();
				while (enumerator.hasMoreElements()) {
					EOGenericRecord vTmpRec = (EOGenericRecord) enumerator
							.nextElement();
					if (vTmpRec.valueForKey(EORfcctParametres.PARAM_KEY_KEY) == null
							|| ((String) vTmpRec
									.valueForKey(EORfcctParametres.PARAM_KEY_KEY))
									.equals("")
							|| vTmpRec
									.valueForKey(EORfcctParametres.PARAM_VALUE_KEY) == null) {
						continue;
					}
					if (!((String) vTmpRec
							.valueForKey(EORfcctParametres.PARAM_KEY_KEY))
							.equalsIgnoreCase(previousParamKey)) {
						if (a != null && a.count() > 0) {
							appParametres.setObjectForKey(a, previousParamKey);
						}
						previousParamKey = (String) vTmpRec
								.valueForKey(EORfcctParametres.PARAM_KEY_KEY);
						a = new NSMutableArray();
					}
					if (vTmpRec.valueForKey(EORfcctParametres.PARAM_VALUE_KEY) != null) {
						a.addObject((String) vTmpRec
								.valueForKey(EORfcctParametres.PARAM_VALUE_KEY));
					}
				}
				if (a != null && a.count() > 0) {
					appParametres.setObjectForKey(a, previousParamKey);
				}
				// mySharedEditingContext.invalidateAllObjects();
			} catch (Exception e) {
				appParametres = new NSMutableDictionary();
			}
		}
		return appParametres;
	}

	@Override
	public void finishInitialization() {
		// TODO Auto-generated method stub
		super.finishInitialization();
		rfcctParamManager.checkAndInitParamsWithDefault();
	}
}
