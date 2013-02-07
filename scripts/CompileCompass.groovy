includeTargets << new File("${sassPluginDir}/scripts/_CompassImpl.groovy")
target(default: "Compile using Compass") {
    println "Compass compile starting"
    depends(compassCompile)
}
