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
target(possiblyCompassCompile: "Possibly do a compile") {
    config = new ConfigSlurper().parse(CompassConfig)
    List src = []
    List target = [0]
    (config.sassConfig.compass['sass_path'].toString() as File).eachFileRecurse { file ->
      src << file.lastModified()
    }
    try {
      (config.sassConfig.compass['css_path'].toString() as File).eachFileRecurse { file ->
        target << file.lastModified()
      }
    } catch(all) {}
    if((grailsEnv != "development" && grailsEnv != "test") || target.max() < src.max()) {
        compassCompile()
    } else {
      println """Not doing a compass compile because the target dir is newer than the source dir.\
      Use the compile-compass task to force a compile (if desired)"""
    }
}

//Stop compass - broken
target(stopCompass: "Stop Compass") {
    Compass.stopGuard()
}
