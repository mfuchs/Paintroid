#!groovy

pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile.jenkins'
            // 'docker build' would normally copy the whole build-dir to the container, changing the
            // docker build directory avoids that overhead
            dir 'docker'
            // Pass the uid and the gid of the current user (jenkins-user) to the Dockerfile, so a
            // corresponding user can be added. This is needed to provide the jenkins user inside
            // the container for the ssh-agent to work.
            // Another way would be to simply map the passwd file, but would spoil additional information
            // Also hand in the group id of kvm to allow using /dev/kvm.
            additionalBuildArgs '--build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g) --build-arg KVM_GROUP_ID=$(getent group kvm | cut -d: -f3)'
            // Ensure that each executor has its own gradle cache to not affect other builds
            // that run concurrently.
            args '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=7G --cpus=3.5'
            label 'LimitedEmulator'
        }
    }

    stages {
        stage('Static Analysis') {
            steps {
                    sh 'Hello World!'
                    pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "pmd.xml",        unHealthy: '', unstableTotalAll: '0'
                    checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
//                    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
            }

//            post {
//                always {
////                    recordIssues(tools: [androidLint(pattern: "$reports/build/reports/lint*.xml"),
////                                         checkStyle(pattern: "$reports/build/reports/checkstyle.xml"),
////                                         pmdParser(pattern: "$reports/build/reports/pmd.xml")])
//
//                }
//            }
        }
    }
}
