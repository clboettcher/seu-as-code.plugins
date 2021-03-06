/*
 *    Copyright (C) 2015 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.seu.as.code.plugins.credentials.impl

import org.apache.commons.codec.binary.Base64
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.hamcrest.Matchers.is
import static spock.util.matcher.HamcrestSupport.expect
import static spock.util.matcher.HamcrestSupport.that

/**
 * Unit spec for the PropertyCredentials.
 *
 * @author lreimer
 */
@Stepwise
class PropertyCredentialsSpec extends Specification {

    @Shared
    File file;

    void setupSpec() {
        file = File.createTempFile("seuac-credentials", ".properties");
    }

    void cleanupSpec() {
        file.delete();
    }

    def "1. Save credentials to properties"() {
        given: "an encryptor stub and an empty crendentials store"
        Encryptor encryptor = Stub(Encryptor)
        encryptor.decrypt('value'.getBytes("UTF-8")) >> 'value'.getBytes("UTF-8")
        encryptor.encrypt('value'.getBytes("UTF-8")) >> 'value'.getBytes("UTF-8")

        PropertyCredentials props = new PropertyCredentials(file, encryptor)

        when: "we set a key value pair and save"
        props.set('key', 'value');
        props.save();

        then: "there should be a key value stored"
        expect props.get('key'), is('value')
    }

    def "2. Check credentials in properties are Bas64"() {
        given: "a fresh properties loaded from file"
        Properties properties = new Properties()
        FileInputStream fileInputStream = new FileInputStream(file)
        try {
            properties.load(fileInputStream)
        } finally {
            fileInputStream.close()
        }

        expect: "a Base64 encoded value"
        that properties.getProperty("key"), is(Base64.encodeBase64String("value".getBytes("UTF-8")))
    }
}
