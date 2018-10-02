#!groovy

pipeline {
	agent any

	options {
		timeout(time: 2, unit: 'HOURS')
		timestamps()
		buildDiscarder(logRotator(numToKeepStr: '30'))
	}

	stages {
		stage('Unit and Device tests') {
			steps {
			    echo "hello world"
			}

			post {
				always {
					publishCoverage adapters: [jacocoAdapter("report*.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD')

				}
			}
		}
	}

}
