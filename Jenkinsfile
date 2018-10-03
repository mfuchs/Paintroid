#!groovy

pipeline {
	agent {
		label 'Slave4'
	}

	options {
		timeout(time: 2, unit: 'HOURS')
		timestamps()
		buildDiscarder(logRotator(numToKeepStr: '30'))
	}

	stages {

		stage('Unit and Device tests') {
			steps {
			    sh "cp 2/report.xml ."
			}

			post {
				always {
					publishCoverage adapters: [jacocoAdapter("**/report.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD')
				}
			}
		}
	}

}
