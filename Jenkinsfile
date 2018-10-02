#!groovy

pipeline {
	agent {
		docker { image 'openjdk:8-jdk' }
	}

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
					publishCoverage adapters: [jacocoAdapter("report1.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD')
					publishCoverage adapters: [jacocoAdapter("report2.xml")], sourceFileResolver: sourceFiles('STORE_ALL_BUILD')

				}
			}
		}
	}

}
