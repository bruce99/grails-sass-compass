package au.com.unico.utils

import javax.script.*
import groovy.transform.*
import org.apache.log4j.Logger
@Singleton
class Compass {
    ScriptEngineManager manager
    ScriptEngine jRubyEngine
    GString s
    Logger log
    def config = ['sassConfig':['compass':['project_path':'.', 'sass_path':'src/sass', 'css_path':'web-app/css', 'cache_path':'.sass-cache-plugin'],'gems':["'compass'", "'sass'"],'requires':["'sass/plugin'"],'lib_path':'./lib']]
    Boolean lock = false
    Thread th
    def run = true
    /*
     * Prepare the string (of Ruby) that is executed by the jRuby engine in doCompile.
     * Also, prepare the jRuby engine (such that we only ever get one).
     * The bindings between the groovy world and jRuby worth refused to work, so it's done
     * using some basterdised GString stuff (interpolation to make Ruby data structures).
     */
    private void prepare() {
        def configStr = """
            { :project_path => '${config.sassConfig.compass.project_path}',
                :sass_path => '${config.sassConfig.compass.sass_path}',
                :css_path => '${config.sassConfig.compass.css_path}',
                :cache_path => '${config.sassConfig.compass.cache_path}' }
        """
        def libPath = config.sassConfig.lib_path ?: "./lib"
        manager = new ScriptEngineManager()
        jRubyEngine = manager.getEngineByName("jruby")
        def tmp = '$VERBOSE = nil'
        s = """
              $tmp
              config = ${configStr}

              gems = ${config.sassConfig.gems.unique()}
              requires = ${config.sassConfig.requires.unique()}
              def reqing(aList)
                aList.each { |it|
                    require it
                }
              end
              require 'rubygems'
              require 'rubygems/dependency_installer'
              begin
                Gem.path << "${libPath}"
                Gem.refresh
                reqing(gems + requires)
              rescue Exception=>e
                theGems = []
                gems.each { |gem|
                  theGems += Gem::DependencyInstaller.new({:install_dir => "${libPath}"}).install(gem)
                }
                theGems.each { |it|
                  it.activate()
                }
                reqing(gems + requires)
              end
              Compass.add_configuration(
                config,
                'custom' # A name for the configuration, can be anything you want
              )
              Compass.compiler.run()
        """
        log?.debug "jRuby script: ${s}"
    }
    /* Run the compass compile task */
    @Synchronized
    @CompileStatic
    private void doCompile() {
        if(manager == null)
            prepare()
        if(lock)
            return
        lock = true
        jRubyEngine.eval(s)
        lock = false
    }
    public static def getConfig() {
        return getInstance().@config
    }
    /* Set the config for the singleton.
     * Adds an extra set of quotes to things turned into lists because of the interpolation
     * binding solution (ie this list is "printed to a string" in the GString which is
     * the ruby string, and hence needs to be quoted twice (such that there are quotes
     * in the ruby string)
     */
    @Synchronized
    public static void setConfig(def aConfig) {
        def gems = []
        aConfig.sassConfig.gems.each {
            gems.add("'${it}'")
        }
        aConfig.sassConfig.gems = gems
        def requires = []
        aConfig.sassConfig.requires.each {
            requires.add("'${it}'")
        }
        aConfig.sassConfig.requires = requires
        getInstance().@config = aConfig
    }
    /* Compile using compass */
    @CompileStatic
    public static void compile() {
        getInstance().doCompile()
    }
    /* Run a ruby guard to watch for sass changes and recompile the sass
     * when changes occur
     */
    @Synchronized
    private void doGuard() {
        if(lock)
            return
        lock = true
        th = Thread.start {
            log?.info "Starting Compass guard process"
            config.sassConfig.gems << "'listen'"
            if(manager == null)
                doCompile()
            def guardStr = """
                require 'listen'
                listener = Listen.to('${config.sassConfig.compass.sass_path}') 
                listener.change do |modified, added, removed|
                   Compass.compiler.run()
                end
                listener.start false
                sleep 5
                listener.stop
            """
            log?.debug "jRuby guard script: ${guardStr}"
            while(run)
                jRubyEngine.eval(guardStr) 
            run = true
        }
        th.setDefaultUncaughtExceptionHandler({t,ex ->
            log?.warn("Exception thrown in Guard thread", ex)
            lock = false
        } as Thread.UncaughtExceptionHandler)
        log?.info "Compass guard process running"
    }
    /* Run the guard task */
    @CompileStatic
    public static void guard() {
        getInstance().doGuard()
    }
    /* Should stop the guarding thread - currently broken */
    @CompileStatic
    private void doStopGuard() {
        log?.info "Stopping ${th}"
        if(lock) {
            th.interrupt()
            run = false
        }
        lock = false
    }
    /* Should stop the guarding thread - currently broken */
    @CompileStatic
    public static void stopGuard() {
        getInstance().doStopGuard()
    }
}
