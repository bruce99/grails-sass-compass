includeTargets << new File ("${sassPluginDir}/scripts/_CompassImpl.groovy")
includeTargets << grailsScript("_GrailsArgParsing")
//Compile compass when the Grails compile task starts
def th
eventCompileStart = { kind ->
   println "Compass compile starting ${new Date()}"
   th = Thread.start { compassCompile() }
}
eventCompileEnd = {
   println "About to wait on Compass"
   th.join()
   println "Compass complete ${new Date()}"
}

//Broken...
eventExiting = {
    stopCompass()
}
