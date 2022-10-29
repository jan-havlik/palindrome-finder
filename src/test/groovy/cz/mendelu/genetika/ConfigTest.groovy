package cz.mendelu.genetika

/**
 * Created by xkoloma1 on 25.02.2016.
 */
class ConfigTest extends GroovyTestCase {

    void "test Server.time.format: Correct format"() {
        String time = Config.server.time().format(new Date());
        assert time != null;
    }
}
