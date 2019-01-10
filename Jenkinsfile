#!groovy

pipeline {
    agent any

    stages {
        stage('Static Analysis') {
            steps {
                echo 'Hello World!'
            }

            post {
                always {
                    pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "pmd.xml",        unHealthy: '', unstableTotalAll: '0'
                    checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
//                    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
//                    recordIssues(tools: [androidLint(pattern: "$reports/build/reports/lint*.xml"),
//                                         checkStyle(pattern: "$reports/build/reports/checkstyle.xml"),
//                                         pmdParser(pattern: "$reports/build/reports/pmd.xml")])

                }
            }
        }
    }
}
