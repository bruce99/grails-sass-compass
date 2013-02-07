includeTargets << grailsScript("_GrailsSettings")

/* Load the config file and the Compass class. */
GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader())
Class Compass = loader.parseClass( "$sassPluginDir/src/groovy/au/com/unico/utils/Compass.groovy" as File)
Class CompassConfig = loader.parseClass( "$basedir/grails-app/conf/SassConfig.groovy" as File)

//Compile with compass
target(compassCompile: "Compile using Compass") {
    config = new ConfigSlurper().parse(CompassConfig)
    Compass.setConfig(config)
    Compass.compile()
}

//Stop compass - broken
target(stopCompass: "Stop Compass") {
    Compass.stopGuard()
}
