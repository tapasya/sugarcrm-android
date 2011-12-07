SugarCRM For Android:
A SugarCRM client for android that aims to support SugarCRM versions 5.x(starting v5.5.3)  and  6.x (v6.3.0 being the latest)

Please check-out the following video on youtube to see how it works:
http://www.youtube.com/watch?v=mEvCvkdGBBE&feature=related	
	
Supported features:
	¥	Modules - Accounts, Contacts,, Leads, Opportunities, Calls, Meetings
	¥	Related Relationships for the modules	
	¥	Ability to access, create, edit and delete a record in a module.
	¥	Links for Phone, website url, email address and billing address 
	¥	Search feature within a module and across modules
	¥	Recent records to quickly navigate to the recently visited records of various modules.
	¥	Shortcut for the app can be added on the Home screen
	¥	Offline support for the synced records	


To get started:

Environment:
	¥	Eclipse
	¥	Android SDK (v2.1 - v4.0)
	¥	ADT Plugin
	
Domain
	¥	Go through the SugarCRM Demo,Trial at http://www.sugarcrm.com/crm/
	

Read the instructions at http://developer.android.com/sdk/ and set up your environment

Coding Guidelines
	¥	Follow Android Java conventions for Coding Style.
	¥	Import the formatting rules "eclipse_formatter.xml" and import order -"sugarcrm.importorder" by enabling project specific settings.(available in CVS)
	¥	Find Bugs is integrated with maven build, but you can install the eclipse plugin (http://findbugs.sourceforge.net/manual/eclipse.html) and proactively fix bugs before check-in.

Testing
	¥	unit testing, instrumentation testing on android
	¥	Go to sugarcrm-android/tests folder and run "ant coverage". Coverage report will be under coverage/coverage.html.


SugarCRM Rest API:
	¥	Install Sugar CRM (say v5.5.2) or access one of the available instances
	¥	Rest API: http://<yourSugarCRMInstance>/service/v2/rest.php





