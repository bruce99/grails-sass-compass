package au.com.unico.utils

import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class CompassTests {

    @Before
	void setUp() {
        //TODO
    }
	
	void testCompassConfig() {
        def config = ['sassConfig':['compass':['project_path':'.', 'sass_path':'test', 'css_path':'web-app/css', 'cache_path':'.sass-cache-test'],'gems':["'compass'", "'sass'", "'susy'"],'requires':['sass/plugin']]]
        assertFalse Compass.getConfig().equals(config)
        Compass.setConfig(config)
		assertEquals Compass.getConfig(), config
	}
	
}
