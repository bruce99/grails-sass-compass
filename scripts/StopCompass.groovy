includeTargets << new File("${sassPluginDir}/scripts/_CompassImpl.groovy")
target(default: "Stop Compass") {
    println "Stopping Compass"
    depends(stopCompass)
}

