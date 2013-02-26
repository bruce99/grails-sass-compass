includeTargets << new File ("${sassPluginDir}/scripts/_CompassImpl.groovy")
includeTargets << grailsScript("_GrailsArgParsing")
//Compile compass when the Grails compile task starts
def th = null
eventCompileStart = { kind ->
    println "Compass compile starting ${new Date()}"
    th = Thread.start { possiblyCompassCompile() }
}
eventCompileEnd = {
    println "About to wait on Compass"
    if(th) {
      th.join()
    }
    println "Compass complete ${new Date()}"
}

//Broken...
eventExiting = {
    stopCompass()
}
