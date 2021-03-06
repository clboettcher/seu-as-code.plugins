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
package de.qaware.seu.as.code.plugins.git

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.expect
import static spock.util.matcher.HamcrestSupport.that

/**
 * Basic test specification for the GitCloneTask.
 *
 * @author lreimer
 */
class GitCloneTaskSpec extends Specification {
    static final String TEST_GIT_CLONE = 'testGitClone'
    Project project
    File directory

    def setup() {
        project = ProjectBuilder.builder().build()
        directory = File.createTempDir()
    }

    void cleanup() {
        directory.deleteDir()
    }

    def "Define GitCloneTask"() {
        expect: "the clone task to be undefined"
        that project.tasks.findByName(TEST_GIT_CLONE), is(nullValue())

        when: "we defined and configure the clone task"
        project.task(TEST_GIT_CLONE, type: GitCloneTask) {
            url = "https://github.com/qaware/QAseuac.git"
            directory = this.directory
            branch = 'TEST'
            username = 'user'
            password = 'secret'
        }

        then: "we expect to find the task correctly configured"
        GitCloneTask task = project.tasks.findByName(TEST_GIT_CLONE)

        expect task, notNullValue()
        expect task.group, equalTo('Version Control')
        expect task.url, equalTo('https://github.com/qaware/QAseuac.git')
        expect task.branch, equalTo('TEST')
        expect task.username, equalTo('user')
        expect task.password, equalTo('secret')
        expect task.directory, notNullValue()
    }

    def "Invoke doClone"() {
        expect: "the clone task to be undefined"
        that project.tasks.findByName(TEST_GIT_CLONE), is(nullValue())

        when: "we create the task and invoke clone"
        project.configurations.create('jgit')
        GitCloneTask task = project.task(TEST_GIT_CLONE, type: GitCloneTask) {
            url = "https://github.com/qaware/QAseuac.git"
            directory = this.directory
        }
        task.doClone()

        then: "the task is defined but threw an exception"
        expect project.tasks.testGitClone, notNullValue()
        thrown(GradleException)
    }

    def "Invoke doClone with single task"() {
        expect:
        that project.tasks.findByName(TEST_GIT_CLONE), is(nullValue())

        when:
        project.configurations.create('jgit')
        GitCloneTask task = project.task(TEST_GIT_CLONE, type: GitCloneTask) {
            url = "https://github.com/qaware/QAseuac.git"
            directory = this.directory
            singleBranch = true
            branch = "refs/heads/base-plugin"
        }
        task.doClone()

        then:
        expect project.tasks.testGitClone, notNullValue()
        thrown(GradleException)
    }
}
