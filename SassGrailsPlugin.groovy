import au.com.unico.utils.Compass
import grails.util.Environment
class SassGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "lib/**"
    ]

    //def watchedResources = "file:./src/stylesheets/*"

    // TODO Fill in these fields
    def title = "JRuby Compass Integration" // Headline display name of the plugin
    def author = "Bryce Gibson"
    def authorEmail = "bryce@gibson-consulting.com.au"
    def description = '''\
A plugin for using Compass/Sass in Grails using the JRuby library (not executable) which is pulled in with Ivy.
Hence there is a lower installation overhead.
'''

    // URL to the plugin's documentation
    //def documentation = "http://grails.org/plugin/sass"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        if(Environment.current != Environment.DEVELOPMENT) return // No need to run the dynamic compilation stuff in prod/test
        //def grailsApplication = ref('grailsApplication')
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
        ConfigObject config
        try {
            config = new ConfigSlurper().parse(classLoader.loadClass('SassConfig'))
            application.config.sassConfig = config.sassConfig
            Compass.setConfig(application.config)
            Compass.guard() //Start the compass live recompile process

        } catch (Exception e) {
            log.error "Error setting SassPlugin config", e
        }
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        //Compass.compile()
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }
    //Broken...
    def onConfigChange = { event ->
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
        ConfigObject config
        try {
            config = new ConfigSlurper().parse(classLoader.loadClass('SassConfig'))
            application.config.sassConfig = config.sassConfig
            Compass.stopGuard()
            Compass.setConfig(application.config)
            Compass.guard()
        } catch (Exception e) {
            log.error "Error in SassPlugin config change.", e
        }
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        Compass.stopGuard()
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
