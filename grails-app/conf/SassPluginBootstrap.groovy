import au.com.unico.utils.Compass
class SassPluginBootstrap {
    def init = { servletContext ->
        environments {
            development {
                println "SassBootstrap"
                Compass.guard()
            }
        }
    }
}
