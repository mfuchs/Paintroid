#!groovy

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
}

node {
    stage('Static Analysis') {
        git branch: 'JENKINS-278_2', url: 'https://github.com/mfuchs/Paintroid.git'
        pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "pmd.xml",        unHealthy: '', unstableTotalAll: '0'
    }
}
