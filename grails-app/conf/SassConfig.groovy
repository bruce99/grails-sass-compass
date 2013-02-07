sassConfig {
    //Compass config - is turned (by hand) into a Ruby map for Compass (hence it won't support all options unless you add them to the Ruby)
    compass {
        project_path = '.'
        sass_path = 'src/sass'
        css_path = 'web-app/css'
        cache_path = '.sass-cache-plugin' 
    }
    //Gems to be downloaded and required
    gems = ['sass', 'compass', 'guard']
    //Libraries that have different names to their gem that need to be required
    requires = ['sass/plugin']
    lib_path = "./lib"
}
