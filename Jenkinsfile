#!groovy

// TODO when there are no excludes yet (i.e. first run) only run one job and nothing in parallel

class DockerParameters {
    def fileName = 'Dockerfile.jenkins'

    // 'docker build' would normally copy the whole build-dir to the container, changing the
    // docker build directory avoids that overhead
    def dir = 'docker'

    // Pass the uid and the gid of the current user (jenkins-user) to the Dockerfile, so a
    // corresponding user can be added. This is needed to provide the jenkins user inside
    // the container for the ssh-agent to work.
    // Another way would be to simply map the passwd file, but would spoil additional information
    // Also hand in the group id of kvm to allow using /dev/kvm.
    def buildArgs = '--build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g) --build-arg KVM_GROUP_ID=$(getent group kvm | cut -d: -f3)'

    // Ensure that each executor has its own gradle cache to not affect other builds
    // that run concurrently.
    def args = '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'

    def label = 'LimitedEmulator'
}

def d = new DockerParameters()

def partitionedTests = splitTests count(3)

def reports = 'Paintroid/build/reports'

// place the cobertura xml relative to the source, so that the source can be found
def javaSrc = 'Paintroid/src/main/java'

def debugApk = 'app/build/outputs/apk/debug/app-debug.apk'

def junitAndCoverage(String jacocoXmlFile, String coverageName, String javaSrcLocation) {
    // Consume all test xml files. Otherwise tests would be tracked multiple
    // times if this function was called again.
    String testPattern = '**/*TEST*.xml'
    junit testResults: testPattern, allowEmptyResults: true
    cleanWs patterns: [[pattern: testPattern, type: 'INCLUDE']]

    String coverageFile = "$javaSrcLocation/coverage_${coverageName}.xml"
    // Convert the JaCoCo coverate to the Cobertura XML file format.
    // This is done since the Jenkins JaCoCo plugin does not work well.
    // See also JENKINS-212 on jira.catrob.at
    sh "./buildScripts/cover2cover.py '$jacocoXmlFile' '$coverageFile'"
    stash includes: coverageFile, name: "coverage_${coverageName}", allowEmpty: true
}

pipeline {
    agent none

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
        issueCommentTrigger('.*test this please.*')
    }

    stages {
        stage('All') {
            parallel {
                stage('1') {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label d.label
                        }
                    }

                    stages {
                        stage('Build Debug-APK') {
                            steps {
                                echo "#### TESTS: $TESTS"
                                sh './gradlew assembleDebug'
                                archiveArtifacts debugApk
                                plot csvFileName: 'dexcount.csv', csvSeries: [[displayTableFlag: false, exclusionValues: '', file: 'Paintroid/build/outputs/dexcount/*.csv', inclusionFlag: 'OFF', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'dexcount'
                                plot csvFileName: 'apksize.csv', csvSeries: [[displayTableFlag: false, exclusionValues: 'kilobytes', file: 'Paintroid/build/outputs/apksize/*/*.csv', inclusionFlag: 'INCLUDE_BY_STRING', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'APK Size'
                            }
                        }

                        stage('Static Analysis') {
                            steps {
                                sh './gradlew pmd checkstyle lint'
                            }

                            post {
                                always {
                                    pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/pmd.xml",        unHealthy: '', unstableTotalAll: '0'
                                    checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
                                    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
                                }
                            }
                        }

                        stage('Unit Tests') {
                            steps {
                                sh './gradlew -PenableCoverage -Pjenkins jacocoTestDebugUnitTestReport'
                            }
                            post {
                                always {
                                    junitAndCoverage "$reports/jacoco/jacocoTestDebugUnitTestReport/jacocoTestDebugUnitTestReport.xml", 'unit', javaSrc
                                }
                            }
                        }
                    }

                    post {
                        always {
                            stash name: 'logParserRules', includes: 'buildScripts/log_parser_rules'
                        }
                    }
                }

                stage("Device Tests 1") {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label d.label
                        }
                    }

                    steps {
                        writeFile file: 'testexclusions.txt', text: testSet[0].join('\n')
                        sh './gradlew -PenableCoverage -Pjenkins startEmulator adbDisableAnimationsGlobally createDebugCoverageReport'
                    }
                    post {
                        always {
                            sh './gradlew stopEmulator'
                            junitAndCoverage "$reports/coverage/debug/report.xml", "device0", javaSrc
                            archiveArtifacts 'logcat.txt'
                        }
                    }
                }
                stage("Device Tests 2") {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label d.label
                        }
                    }

                    steps {
                        writeFile file: 'testexclusions.txt', text: testSet[1].join('\n')
                        sh './gradlew -PenableCoverage -Pjenkins startEmulator adbDisableAnimationsGlobally createDebugCoverageReport'
                    }
                    post {
                        always {
                            sh './gradlew stopEmulator'
                            junitAndCoverage "$reports/coverage/debug/report.xml", "device1", javaSrc
                            archiveArtifacts 'logcat.txt'
                        }
                    }
                }
                stage("Device Tests 3") {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label d.label
                        }
                    }

                    steps {
                        writeFile file: 'testexclusions.txt', text: testSet[2].join('\n')
                        sh './gradlew -PenableCoverage -Pjenkins startEmulator adbDisableAnimationsGlobally createDebugCoverageReport'
                    }
                    post {
                        always {
                            sh './gradlew stopEmulator'
                            junitAndCoverage "$reports/coverage/debug/report.xml", "device2", javaSrc
                            archiveArtifacts 'logcat.txt'
                        }
                    }
                }
            }
        }

        stage('Coverage Collection') {
            agent {
                dockerfile {
                    filename d.fileName
                    dir d.dir
                    additionalBuildArgs d.buildArgs
                    args d.args
                    label d.label
                }
            }

            steps {
                unstash 'coverage_unit'
                script {
                    for (int i = 0; i < partitionedTests.size(); ++i) {
                        unstash "coverage_device${i}"
                    }
                }
                step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: "$javaSrc/coverage*.xml", failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false, failNoReports: false])
            }
        }
    }

    post {
        always {
            node('master') {
                unstash 'logParserRules'
                step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])
            }
        }
        changed {
            node('master') {
                notifyChat()
            }
        }
    }
}
