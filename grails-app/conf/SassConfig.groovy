sassConfig {
    compass {
        project_path = '.'
        sass_path = 'src/sass'
        css_path = 'web-app/css'
        cache_path = '.sass-cache-plugin' 
    }
    gems = ['sass', 'compass', 'guard', 'rb-inotify']
    requires = ['sass/plugin']
}
